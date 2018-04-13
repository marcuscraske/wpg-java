package com.worldpay.sdk.wpg.request.modification.nonbatchable;

import com.worldpay.sdk.wpg.exception.WpgErrorResponseException;
import com.worldpay.sdk.wpg.exception.WpgMalformedXmlException;
import com.worldpay.sdk.wpg.exception.WpgRequestException;
import com.worldpay.sdk.wpg.internal.xml.XmlBuildParams;
import com.worldpay.sdk.wpg.internal.xml.XmlRequest;
import com.worldpay.sdk.wpg.internal.xml.XmlResponse;
import com.worldpay.sdk.wpg.internal.xml.serializer.modification.OrderModificationSerializer;

public class CancelOrderRequest extends XmlRequest<Void>
{
    private String orderCode;

    public CancelOrderRequest() { }

    public CancelOrderRequest(String orderCode)
    {
        this.orderCode = orderCode;
    }

    @Override
    protected void validate(XmlBuildParams params)
    {
    }

    @Override
    protected void build(XmlBuildParams params)
    {
        OrderModificationSerializer.decorateForRequest(params, orderCode);
        OrderModificationSerializer.decorate(params, this);
    }

    @Override
    protected Void adapt(XmlResponse response) throws WpgRequestException, WpgErrorResponseException, WpgMalformedXmlException
    {
        return null;
    }

    public CancelOrderRequest orderCode(String orderCode)
    {
        this.orderCode = orderCode;
        return this;
    }

    public String getOrderCode()
    {
        return orderCode;
    }
}