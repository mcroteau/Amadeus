package xyz.ioc.service;

import com.google.gson.Gson;
import com.stripe.Stripe;
import com.stripe.model.Charge;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

public class StripeService {

    @Value("${stripe.api.key}")
    private String apiKey;

    public Charge charge(double amount, String stripeToken, String email){

        try {

            Stripe.apiKey = apiKey;

            int amountInCents = ((int) amount) * 100;

            Map<String, Object> chargeParams = new HashMap<String, Object>();
            chargeParams.put("amount", String.valueOf(amountInCents));
            chargeParams.put("currency", "usd");
            chargeParams.put("source", stripeToken);
            chargeParams.put("description", "Amadeus Advertisement @ 7 days for $" + amount);

            Charge charge = Charge.create(chargeParams);

            return charge;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
