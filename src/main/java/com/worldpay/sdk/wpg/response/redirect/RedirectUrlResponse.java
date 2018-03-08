package com.worldpay.sdk.wpg.response.redirect;

import com.worldpay.sdk.wpg.connection.http.HttpResponse;
import com.worldpay.sdk.wpg.response.ResponseType;
import com.worldpay.sdk.wpg.validation.Assert;
import com.worldpay.sdk.wpg.xml.XmlBuilder;
import com.worldpay.sdk.wpg.xml.XmlResponse;

public class RedirectUrlResponse extends XmlResponse
{
    private final String url;

    public RedirectUrlResponse(HttpResponse httpResponse, XmlBuilder builder)
    {
        super(httpResponse, builder);
        this.url = builder.reset().e("reply").e("orderStatus").e("reference").cdata();
        Assert.notNull(url, "Failed to read redirect reply");
    }

    public String getUrl()
    {
        return url;
    }

    public PaymentPagesRedirectBuilder paymentPages()
    {
        return new PaymentPagesRedirectBuilder(url);
    }

    @Override
    public ResponseType getResponseType()
    {
        return ResponseType.REDIRECT;
    }

}
