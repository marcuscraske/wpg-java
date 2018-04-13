package com.worldpay.sdk.wpg.request.modification.batchable;

import com.worldpay.sdk.wpg.domain.payment.Amount;
import com.worldpay.sdk.wpg.exception.WpgErrorResponseException;
import com.worldpay.sdk.wpg.exception.WpgMalformedXmlException;
import com.worldpay.sdk.wpg.exception.WpgRequestException;
import com.worldpay.sdk.wpg.internal.xml.XmlBuildParams;
import com.worldpay.sdk.wpg.internal.xml.XmlRequest;
import com.worldpay.sdk.wpg.internal.xml.XmlResponse;
import com.worldpay.sdk.wpg.request.modification.batchable.BatchModificationItem;

public class RefundOrderRequest extends XmlRequest<Void> implements BatchModificationItem
{
    private String orderCode;
    private String reference;
    private Amount amount;

    public RefundOrderRequest() { }

    public RefundOrderRequest(String orderCode, String reference, Amount amount)
    {
        this.orderCode = orderCode;
        this.reference = reference;
        this.amount = amount;
    }

    @Override
    protected void validate(XmlBuildParams params)
    {
    }

    @Override
    protected void build(XmlBuildParams params)
    {
        // TODO should never allow this
    }

    @Override
    protected Void adapt(XmlResponse response) throws WpgRequestException, WpgErrorResponseException, WpgMalformedXmlException
    {
        return null;
    }

    public RefundOrderRequest orderCode(String orderCode)
    {
        this.orderCode = orderCode;
        return this;
    }

    public RefundOrderRequest reference(String reference)
    {
        this.reference = reference;
        return this;
    }

    public RefundOrderRequest amount(Amount amount)
    {
        this.amount = amount;
        return this;
    }

    public String getOrderCode()
    {
        return orderCode;
    }

    public String getReference()
    {
        return reference;
    }

    public Amount getAmount()
    {
        return amount;
    }

}