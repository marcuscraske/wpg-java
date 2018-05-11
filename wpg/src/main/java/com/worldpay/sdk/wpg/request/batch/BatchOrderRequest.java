package com.worldpay.sdk.wpg.request.batch;

import com.worldpay.sdk.wpg.builder.RandomIdentifier;
import com.worldpay.sdk.wpg.exception.WpgErrorResponseException;
import com.worldpay.sdk.wpg.exception.WpgMalformedResponseException;
import com.worldpay.sdk.wpg.exception.WpgMalformedXmlException;
import com.worldpay.sdk.wpg.exception.WpgRequestException;
import com.worldpay.sdk.wpg.internal.xml.XmlBuildParams;
import com.worldpay.sdk.wpg.internal.xml.XmlBuilder;
import com.worldpay.sdk.wpg.internal.xml.XmlRequest;
import com.worldpay.sdk.wpg.internal.xml.XmlResponse;
import com.worldpay.sdk.wpg.internal.xml.serializer.OrderDetailsSerializer;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Not yet supported.
 *
 *
 * Allows multiple orders to be sent together, as a batch.
 *
 * Currently supports:
 * - {@link com.worldpay.sdk.wpg.request.payout.CardPayoutRequest}
 * - {@link com.worldpay.sdk.wpg.request.cse.ClientsideEncryptedCardRequest}
 * - {@link com.worldpay.sdk.wpg.request.tokenisation.TokenPaymentRequest}
 */
// TODO when building, we'll need to check all serializers are resetting correctly and not just writing to top-level
public class BatchOrderRequest extends XmlRequest<Void>
{
    private static final int ID_MIN_LEN = 1;
    private static final int ID_MAX_LEN = 25;

    private String id;
    private List<BatchOrderItem> items;

    public BatchOrderRequest()
    {
        this.id = RandomIdentifier.generate(ID_MAX_LEN);
    }

    /**
     *
     * @param id max length of 25 chars
     */
    public BatchOrderRequest(String id)
    {
        this.id = id;
    }

    @Override
    protected void validate(XmlBuildParams params)
    {
        if (this.id == null || this.id.length() < ID_MIN_LEN || this.id.length() > ID_MAX_LEN)
        {
            throw new IllegalArgumentException("Batch request ID must be " + ID_MIN_LEN + " to " + ID_MAX_LEN + " chars in length");
        }

        for (BatchOrderItem item : items)
        {
            XmlRequest request = (XmlRequest) item;
            proxyValidate(params, request);
        }
    }

    @Override
    protected void build(XmlBuildParams params)
    {
        XmlBuilder builder = params.xmlBuilder();

        OrderDetailsSerializer.decorateMerchantCode(params);

        builder.e("submit")
                .e("orderBatch")
                    .a("transactions", items.size())
                    .a("merchantBatchCode", id);

        // decorate each order
        for (BatchOrderItem item : items)
        {
            XmlRequest request = (XmlRequest) item;
            proxyBuild(params, request);
        }
    }

    @Override
    protected Void adapt(XmlResponse response) throws WpgRequestException, WpgErrorResponseException, WpgMalformedXmlException
    {
        // Just check we have the expected response
        XmlBuilder builder = response.getBuilder();

        if (!builder.hasE("reply") || !builder.hasE("batchStatus") || builder.a("status") == null)
        {
            throw new WpgMalformedResponseException(response);
        }

        return null;
    }

    @Override
    protected boolean isBatch()
    {
        return true;
    }

    public String getId()
    {
        return id;
    }

    public BatchOrderRequest id(String id)
    {
        this.id = id;
        return this;
    }

    public synchronized List<BatchOrderItem> getItems()
    {
        return Collections.unmodifiableList(items);
    }

    public synchronized BatchOrderRequest items(List<BatchOrderItem> items)
    {
        this.items = items;
        return this;
    }

    public synchronized BatchOrderRequest add(BatchOrderItem... items)
    {
        if (this.items == null)
        {
            this.items = new LinkedList();
        }

        for (BatchOrderItem item : items)
        {
            this.items.add(item);
        }

        return this;
    }

}
