package com.worldpay.sdk.wpg.request.apm;

import com.worldpay.sdk.wpg.domain.Address;
import com.worldpay.sdk.wpg.domain.OrderDetails;
import com.worldpay.sdk.wpg.domain.Shopper;
import com.worldpay.sdk.wpg.domain.tokenisation.CreateTokenDetails;
import com.worldpay.sdk.wpg.request.BatchableRequest;
import com.worldpay.sdk.wpg.xml.XmlBuildParams;
import com.worldpay.sdk.wpg.xml.XmlBuilder;
import com.worldpay.sdk.wpg.xml.XmlRequest;
import com.worldpay.sdk.wpg.xml.serializer.AddressSerializer;
import com.worldpay.sdk.wpg.xml.serializer.OrderDetailsSerializer;
import com.worldpay.sdk.wpg.xml.serializer.ShopperSerializer;
import com.worldpay.sdk.wpg.xml.serializer.payment.tokenisation.CreateTokenDetailsSerializer;

import java.util.Locale;

/**
 * Supported language codes, otherwise defaults to English:
 * http://support.worldpay.com/support/kb/gg/paypal/paypalcg.htm#languagecodes.htm%3FTocPath%3DXML%2520input%2520examples%7CTechnical%2520Integration%7C_____3
 */
public class PayPalPaymentRequest extends XmlRequest implements BatchableRequest
{
    // Mandatory
    private OrderDetails orderDetails;
    private Shopper shopper;
    private String successURL;
    private String failureURL;
    private String cancelURL;

    // Optional
    private Address billingAddress;
    private Address shippingAddress;
    private String languageCode;
    private CreateTokenDetails createTokenDetails;

    @Override
    protected void build(XmlBuildParams params)
    {
        OrderDetailsSerializer.decorate(params, orderDetails);

        XmlBuilder builder = params.xmlBuilder();

        // PayPal information
        // --- Language
        if (languageCode != null && languageCode.length() > 0)
        {
            builder.e("submit").e("order").a("shopperLanguageCode", languageCode);
        }
        builder.reset();

        // -- PayPal details
        builder.e("submit").e("order").e("paymentDetails").e("PAYPAL-EXPRESS");

        if (createTokenDetails != null)
        {
            builder.a("firstInBillingRun", "true");
        }

        builder.e("successURL").cdata(successURL).up();
        builder.e("failureURL").cdata(failureURL).up();
        builder.e("cancelURL").cdata(cancelURL).up();

        builder.reset();

        // Continue generic information
        ShopperSerializer.decorate(params, shopper);
        AddressSerializer.decorate(params, billingAddress, shippingAddress);
        CreateTokenDetailsSerializer.decorate(params, createTokenDetails);
    }

    public PayPalPaymentRequest orderDetails(OrderDetails orderDetails)
    {
        this.orderDetails = orderDetails;
        return this;
    }

    public PayPalPaymentRequest shopper(Shopper shopper)
    {
        this.shopper = shopper;
        return this;
    }

    public PayPalPaymentRequest resultURL(String resultURL)
    {
        successURL(resultURL);
        failureURL(resultURL);
        cancelURL(resultURL);
        return this;
    }

    public PayPalPaymentRequest successURL(String successURL)
    {
        this.successURL = successURL;
        return this;
    }

    public PayPalPaymentRequest failureURL(String failureURL)
    {
        this.failureURL = failureURL;
        return this;
    }

    public PayPalPaymentRequest cancelURL(String cancelURL)
    {
        this.cancelURL = cancelURL;
        return this;
    }

    public PayPalPaymentRequest billingAddress(Address billingAddress)
    {
        this.billingAddress = billingAddress;
        return this;
    }

    public PayPalPaymentRequest shippingAddress(Address shippingAddress)
    {
        this.shippingAddress = shippingAddress;
        return this;
    }

    public PayPalPaymentRequest languageCode(String languageCode)
    {
        this.languageCode = languageCode;
        return this;
    }

    public PayPalPaymentRequest tokeniseForReoccurringPayments(CreateTokenDetails createTokenDetails)
    {
        this.createTokenDetails = createTokenDetails;
        return this;
    }

}
