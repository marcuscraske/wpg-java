package com.worldpay.sdk.wpg.internal.xml;

import com.worldpay.sdk.wpg.connection.GatewayContext;
import com.worldpay.sdk.wpg.connection.SessionContext;
import com.worldpay.sdk.wpg.connection.auth.UserPassAuth;
import com.worldpay.sdk.wpg.connection.factory.ConnectionFactory;
import com.worldpay.sdk.wpg.connection.http.HttpResponse;
import com.worldpay.sdk.wpg.exception.WpgConnectionException;
import com.worldpay.sdk.wpg.exception.WpgErrorResponseException;
import com.worldpay.sdk.wpg.exception.WpgMalformedXmlException;
import com.worldpay.sdk.wpg.exception.WpgRequestException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.Base64;
import java.util.Map;

public class XmlClient
{
    private static final URL SANDBOX_URL;
    private static final URL PRODUCTION_URL;

    static
    {
        try
        {
            SANDBOX_URL = new URL("https://secure-test.worldpay.com/jsp/merchant/xml/paymentService.jsp");
            PRODUCTION_URL = new URL("https://secure.worldpay.com/jsp/merchant/xml/paymentService.jsp");
        }
        catch (MalformedURLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public XmlResponse send(XmlBuildParams params)
            throws WpgRequestException, WpgConnectionException, WpgErrorResponseException
    {
        GatewayContext gatewayContext = params.gatewayContext();
        SessionContext sessionContext = params.sessionContext();
        XmlBuilder builder = params.xmlBuilder();

        ConnectionFactory connectionFactory = gatewayContext.getConnectionFactory();
        Socket socket = null;

        try
        {
            URL url = getUrl(gatewayContext);

            // build request
            byte[] request = buildRequest(url, gatewayContext, sessionContext, builder);

            // open connection
            String hostName = url.getHost();
            int port = url.getPort();

            if (port < 1)
            {
                port = 443;
            }

            socket = connectionFactory.get(gatewayContext, hostName, port);

            // write payload
            OutputStream os = socket.getOutputStream();
            os.write(request);
            os.flush();

            // handle response
            XmlResponse response = readResponse(connectionFactory, socket, sessionContext);
            return response;
        }
        catch (IOException e)
        {
            // Unexpected problem, release the socket prematurely
            if (socket != null)
            {
                connectionFactory.release(socket);
            }

            throw new WpgConnectionException(e);
        }
    }

    private byte[] buildRequest(URL url, GatewayContext gatewayContext, SessionContext sessionContext, XmlBuilder builder) throws WpgRequestException
    {
        try
        {
            String xml = builder.toString();
            byte[] payload = xml.getBytes("UTF-8");
            byte[] headers = buildHeaders(url, gatewayContext, sessionContext, payload.length);

            byte[] request = new byte[headers.length + payload.length];
            System.arraycopy(headers, 0, request, 0, headers.length);
            System.arraycopy(payload, 0, request, headers.length, payload.length);

            // TODO drop after dev
            String text = new String(request, "UTF-8");
            System.out.println(text);

            return request;
        }
        catch (UnsupportedEncodingException e)
        {
            throw new WpgRequestException("Failed to build request", e);
        }
    }

    private byte[] buildHeaders(URL url, GatewayContext gatewayContext, SessionContext sessionContext, long payloadSize) throws WpgRequestException
    {
        try
        {
            UserPassAuth auth = (UserPassAuth) gatewayContext.getAuth();
            String authHeaderValue = authHeader(auth.getUser() + ":" + auth.getPass());

            StringBuilder buff = new StringBuilder();
            buff.append("POST ").append(url.getPath()).append(" HTTP/1.1").append("\r\n");
            buff.append("Host: ").append(url.getHost()).append("\r\n");
            buff.append("Content-Type: text/xml; charset=utf-8\r\n");
            buff.append("Authorization: Basic " + authHeaderValue).append("\r\n");

            // append headers
            for (Map.Entry<String, String> header : sessionContext.getHeaders().entrySet())
            {
                buff.append(header.getKey()).append("=").append(header.getValue()).append("\r\n");
            }

            // append content/payload length
            buff.append("Content-Length: ").append(payloadSize).append("\r\n");
            buff.append("\r\n");

            byte[] headers = buff.toString().getBytes("UTF-8");
            return headers;
        }
        catch (UnsupportedEncodingException e)
        {
            throw new WpgRequestException("Failed to prepare request", e);
        }
    }

    private String authHeader(String value)
    {
        try
        {
            String encoded = Base64.getEncoder().encodeToString(value.getBytes("UTF-8"));
            return encoded;
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException("UTF-8 charset not available", e);
        }
    }

    private URL getUrl(GatewayContext gatewayContext) throws WpgRequestException
    {
        switch (gatewayContext.getEnvironment())
        {
            case PRODUCTION:
                return PRODUCTION_URL;
            case SANDBOX:
                return SANDBOX_URL;
            default:
                throw new WpgRequestException("Unknown environment for WPG - " + gatewayContext.getEnvironment());
        }
    }

    private XmlResponse readResponse(ConnectionFactory connectionFactory, Socket socket, SessionContext sessionContext)
            throws IOException, WpgRequestException, WpgErrorResponseException
    {
        // read raw response
        InputStream is = socket.getInputStream();

        ByteArrayOutputStream baos = new ByteArrayOutputStream(4096);
        byte[] buffer = new byte[4096];
        int bytesRead;
        boolean complete = false;
        HttpResponse httpResponse = null;

        while (!complete && (bytesRead = is.read(buffer)) > 0)
        {
            // write latest data read
            baos.write(buffer, 0, bytesRead);

            // parse as response and check whether full response received yet
            httpResponse = new HttpResponse(baos.toByteArray());
            complete = httpResponse.isComplete();
        }

        // release socket early as possible
        connectionFactory.release(socket);

        if (httpResponse == null || !httpResponse.isComplete())
        {
            throw new WpgRequestException("Incomplete response from gateway");
        }

        // copy cookies to session
        String cookies = httpResponse.getHeader("Set-Cookie");
        if (cookies != null)
        {
            sessionContext.addHeader("Cookies", cookies);
        }

        // deserialize
        try
        {
            String body = httpResponse.getBody();
            XmlBuilder builder = XmlBuilder.parse(body);
            XmlResponse response = new XmlResponse(httpResponse, builder);
            return response;
        }
        catch (WpgMalformedXmlException e)
        {
            throw new WpgRequestException("Failed to parse XML", e);
        }
    }

}