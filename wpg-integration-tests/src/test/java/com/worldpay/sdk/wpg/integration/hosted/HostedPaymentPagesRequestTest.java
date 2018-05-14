package com.worldpay.sdk.wpg.integration.hosted;

import com.worldpay.sdk.wpg.domain.Country;
import com.worldpay.sdk.wpg.domain.Language;
import com.worldpay.sdk.wpg.domain.OrderDetails;
import com.worldpay.sdk.wpg.domain.Shopper;
import com.worldpay.sdk.wpg.domain.payment.Amount;
import com.worldpay.sdk.wpg.domain.payment.Currency;
import com.worldpay.sdk.wpg.domain.payment.PaymentMethod;
import com.worldpay.sdk.wpg.domain.redirect.RedirectUrl;
import com.worldpay.sdk.wpg.exception.WpgException;
import com.worldpay.sdk.wpg.integration.BaseIntegrationTest;
import com.worldpay.sdk.wpg.request.hosted.HostedPaymentPagesRequest;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Locale;

public class HostedPaymentPagesRequestTest extends BaseIntegrationTest
{
    private RedirectUrl redirectUrl;

    @Before
    public void generateOrderUrl() throws WpgException
    {
        OrderDetails orderDetails = new OrderDetails("test order", new Amount(Currency.GBP, 2, 1234L));
        HostedPaymentPagesRequest request = new HostedPaymentPagesRequest(orderDetails, new Shopper("test@worldpay.com"));
        redirectUrl = request.send(GATEWAY_CONTEXT);
    }

    @Test
    public void orderUrl_vanilla() throws IOException
    {
        assertStatusCode(redirectUrl.getUrl(), 200);
    }

    @Test
    public void orderUrl_malformed() throws IOException
    {
        assertStatusCode(redirectUrl.getUrl() + "&blah=ttest", 400);
    }

    @Test
    public void orderUrl_withResultUrls() throws IOException
    {
        String url = redirectUrl.paymentPages()
                .successUrl("https://success.worldpay.com")
                .cancelUrl("https://cancel.worldpay.com")
                .errorUrl("https://error.worldpay.com")
                .failureUrl("https://failure.worldpay.com")
                .pendingUrl("https://pending.worldpay.com")
                .build();

        assertStatusCode(url, 200);
    }

    @Test
    public void orderUrl_withPreferredPaymentMethod() throws IOException
    {
        String url = redirectUrl.paymentPages()
                .preferredPaymentMethod(PaymentMethod.VISA)
                .build();

        assertStatusCode(url, 200);
    }

    @Test
    public void orderUrl_withLocale() throws IOException
    {
        String url = redirectUrl.paymentPages()
                .languageAndCountry(Locale.CANADA_FRENCH)
                .build();

        assertStatusCode(url, 200);
    }

    @Test
    public void orderUrl_withCountryLanguage() throws IOException
    {
        String url = redirectUrl.paymentPages()
                .country("gb")
                .language("en")
                .build();

        assertStatusCode(url, 200);
    }

    @Test
    public void orderUrl_withCountry2() throws IOException
    {
        String url = redirectUrl.paymentPages()
                .country(Country.GREAT_BRITAIN)
                .language("en")
                .build();

        assertStatusCode(url, 200);
    }

    @Test
    public void orderUrl_allCountries() throws IOException
    {
        // TODO consider making parameitised
        for (Country country : Country.values())
        {
            String url = redirectUrl.paymentPages()
                    .country(country)
                    .build();

            assertStatusCode(url, 200);
        }
    }

    @Test
    public void orderUrl_allLanguages() throws IOException
    {
        // TODO consider making parameitised
        for (Language language : Language.values())
        {
            String url = redirectUrl.paymentPages()
                    .language(language)
                    .build();

            assertStatusCode(url, 200);
        }
    }

    @Test
    public void orderUrl_withEverything() throws IOException
    {
        String url = redirectUrl.paymentPages()
                .successUrl("https://success.worldpay.com")
                .cancelUrl("https://cancel.worldpay.com")
                .errorUrl("https://error.worldpay.com")
                .failureUrl("https://failure.worldpay.com")
                .pendingUrl("https://pending.worldpay.com")
                .preferredPaymentMethod(PaymentMethod.VISA)
                .languageAndCountry(Locale.CANADA_FRENCH)
                .build();

        assertStatusCode(url, 200);
    }

}
