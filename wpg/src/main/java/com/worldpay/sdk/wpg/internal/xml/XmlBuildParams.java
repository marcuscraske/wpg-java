package com.worldpay.sdk.wpg.internal.xml;

import com.worldpay.sdk.wpg.connection.GatewayContext;
import com.worldpay.sdk.wpg.connection.SessionContext;

public class XmlBuildParams
{
    private final GatewayContext gatewayContext;
    private final SessionContext sessionContext;
    private XmlBuilder xmlBuilder;
    private boolean batch;
    private XmlEndpoint endpoint;

    public XmlBuildParams(GatewayContext gatewayContext, SessionContext sessionContext, XmlBuilder xmlBuilder, XmlEndpoint endpoint, boolean batch)
    {
        this.gatewayContext = gatewayContext;
        this.sessionContext = sessionContext;
        this.xmlBuilder = xmlBuilder;
        this.endpoint = endpoint;
        this.batch = batch;
    }

    public GatewayContext gatewayContext()
    {
        return gatewayContext;
    }

    public SessionContext sessionContext()
    {
        return sessionContext;
    }

    public XmlBuilder xmlBuilder()
    {
        return xmlBuilder;
    }

    public void setBuilder(XmlBuilder xmlBuilder)
    {
        this.xmlBuilder = xmlBuilder;
    }

    public boolean isBatch()
    {
        return batch;
    }

    public XmlEndpoint getEndpoint()
    {
        return endpoint;
    }

}
