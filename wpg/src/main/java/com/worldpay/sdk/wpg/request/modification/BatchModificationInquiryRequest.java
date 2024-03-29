package com.worldpay.sdk.wpg.request.modification;

import com.worldpay.sdk.wpg.domain.notification.BatchInquiry;
import com.worldpay.sdk.wpg.exception.WpgErrorResponseException;
import com.worldpay.sdk.wpg.exception.WpgMalformedException;
import com.worldpay.sdk.wpg.exception.WpgRequestException;
import com.worldpay.sdk.wpg.internal.validation.Assert;
import com.worldpay.sdk.wpg.internal.xml.XmlBuildParams;
import com.worldpay.sdk.wpg.internal.xml.XmlRequest;
import com.worldpay.sdk.wpg.internal.xml.XmlResponse;
import com.worldpay.sdk.wpg.internal.xml.XmlEndpoint;
import com.worldpay.sdk.wpg.internal.xml.adapter.BatchInquiryAdapter;
import com.worldpay.sdk.wpg.internal.xml.serializer.modification.BatchOrderModificationSerializer;

/**
 * A request to inquire about the status of a batch modification job.
 *
 * @see <a href="http://support.worldpay.com/support/kb/gg/corporate-gateway-guide/content/manage/batchedmodifications.htm">http://support.worldpay.com/support/kb/gg/corporate-gateway-guide/content/manage/batchedmodifications.htm</a>
 */
public class BatchModificationInquiryRequest extends XmlRequest<BatchInquiry>
{
    private String batchCode;

    public BatchModificationInquiryRequest() { }

    public BatchModificationInquiryRequest(String batchCode)
    {
        this.batchCode = batchCode;
    }

    /**
     * @param batchCode The identifier of the batch being inquired
     * @return Current instance
     */
    public BatchModificationInquiryRequest batchCode(String batchCode)
    {
        this.batchCode = batchCode;
        return this;
    }

    /**
     * @return The identifier of the batch being inquired
     */
    public String getBatchCode()
    {
        return batchCode;
    }

    @Override
    protected void validate(XmlBuildParams params)
    {
        Assert.notEmpty(batchCode, "Batch code is mandatory");
    }

    @Override
    protected void build(XmlBuildParams params)
    {
        BatchOrderModificationSerializer.decorateForInquiry(params, this);
    }

    @Override
    protected BatchInquiry adapt(XmlResponse response) throws WpgRequestException, WpgErrorResponseException, WpgMalformedException
    {
        BatchInquiry inquiry = BatchInquiryAdapter.read(response);
        return inquiry;
    }

    @Override
    protected XmlEndpoint getEndpoint()
    {
        return XmlEndpoint.PAYMENTS;
    }

}
