package com.app.server.util.parser;

import com.app.server.models.Payment.ThirdPartyPayment;
import org.json.JSONObject;

public class ThirdPartyPaymentParser {

    public static ThirdPartyPayment convertJsonToThirdPartyPayment(JSONObject json){
        ThirdPartyPayment thirdPartyPayment = new ThirdPartyPayment(
                json.getString("userReferenceId"), json.getString("paymentType"),
                json.getString("paymentAmount"),
                json.getString("paymentCurrency"),
                json.getString("requestTime"),
                json.getString("paymentStatus"));
        return thirdPartyPayment;
    }
}
