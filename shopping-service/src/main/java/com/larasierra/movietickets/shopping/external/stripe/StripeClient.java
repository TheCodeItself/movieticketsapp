package com.larasierra.movietickets.shopping.external.stripe;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class StripeClient {
    @Value("${com.larasierra.stripe_sk}")
    private String STRIPE_SK;

    @PostConstruct
    public void init() {
        Stripe.apiKey = STRIPE_SK;
    }

    public PaymentIntentInfo createPaymentIntent(String orderId, Long amount) {
        var metadata = Map.of("order_id", orderId);

        var params = PaymentIntentCreateParams.builder()
                .setAmount(amount)
                .setCurrency("usd")
                .putAllMetadata(metadata)
                .setAutomaticPaymentMethods(
                    PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                            .setEnabled(true)
                            .build()
                )
                .build();

        try {
            PaymentIntent paymentIntent = PaymentIntent.create(params);
            return new PaymentIntentInfo(paymentIntent.getId(), paymentIntent.getClientSecret());
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }

    public PaymentIntent findPayment(String paymentIntentId) {
        try {
            PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);
            switch (intent.getStatus()) {
                case "succeeded": break;
                case "requires_action": break;
                case "requires_payment_method": break;
                default: throw new RuntimeException();
            }

            return intent;
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }

    public record PaymentIntentInfo(String id, String clientSecret) { }
}
