package com.worldpay.sdk.wpg.internal.xml.serializer;

import com.worldpay.sdk.wpg.domain.Address;
import com.worldpay.sdk.wpg.domain.CardDetails;
import com.worldpay.sdk.wpg.exception.WpgRequestException;
import com.worldpay.sdk.wpg.internal.xml.XmlBuildParams;
import com.worldpay.sdk.wpg.internal.xml.XmlBuilder;

public class CardDetailsSerializer
{

    public static void decorateOrder(XmlBuildParams params, CardDetails cardDetails)
    {
        if (cardDetails != null)
        {
            XmlBuilder builder = params.xmlBuilder();

            builder.e("paymentDetails");

            // build card element
            // TODO allow card scheme to be customised?
            builder.e("CARD-SSL")
                    .e("cardNumber")
                        .cdata(cardDetails.getCardNumber())
                        .up()
                    .e("expiryDate")
                        .e("date")
                            .a("month", String.valueOf(cardDetails.getExpiryMonth()))
                            .a("year", String.valueOf(cardDetails.getExpiryYear()))
                            .up()
                        .up()
                    .e("cardHolderName")
                        .cdata(cardDetails.getCardHolderName())
                        .up();

            // add card holder address
            if (cardDetails.getCardHolderAddress() != null)
            {
                builder.e("cardAddress");
                AddressSerializer.decorateCurrentElement(params, cardDetails.getCardHolderAddress());
                builder.up();
            }

            // reset to order element
            builder.up().up();
        }
    }

    public static CardDetails read(XmlBuilder builder) throws WpgRequestException
    {
        String cardNumber = builder.getCdata("cardNumber");
        String cardHolderName = builder.getCdata("cardHolderName");
        long cvc = builder.getCdataLong("cvc");
        String encryptedPAN = builder.getCdata("encryptedPAN");

        builder.e("expiryDate");
        long expiryMonth = builder.aToLong("month");
        long expiryYear = builder.aToLong("year");
        builder.up();

        builder.e("cardHolderAddress");
        Address cardHolderAddress = AddressSerializer.read(builder);
        builder.up();

        CardDetails result = new CardDetails(
                cardNumber, expiryMonth, expiryYear, cardHolderName, cvc, cardHolderAddress, encryptedPAN);

        return result;
    }

}
