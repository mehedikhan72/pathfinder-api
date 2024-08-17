package com.amplifiers.pathfinder.entity.sslcommerz;

import com.amplifiers.pathfinder.entity.sslcommerz.utility.ParameterBuilder;
import com.amplifiers.pathfinder.utility.Variables.SslCommerzSettings;
import org.springframework.boot.web.server.Ssl;

import java.util.Map;

/**
 * This class initiates a transaction request to SSL Commerz
 * required parameters to hit SSL Commerz payment page are constructed in a Map of String as key value pair
 * Its method initTrnxnRequest returns JSON list or String with Session key which then used to select payment option
 */
public class TransactionInitiator {
    public String initTrnxnRequest() {
        String response = "";
        try {
            /**
             * All parameters in payment order should be constructed in this follwing postData Map
             * keep an eye on success fail url correctly.
             * insert your success and fail URL correctly in this Map
             */
            Map<String, String> postData = ParameterBuilder.constructRequestParameters();
            /**
             * Provide your SSL Commerz store Id and Password by this following constructor.
             * If Test Mode then insert true and false otherwise.
             */
            SSLCommerz sslcz = new SSLCommerz(SslCommerzSettings.SSLCOMMERZ_STORE_ID, SslCommerzSettings.SSLCOMMERZ_STORE_PASSWORD, SslCommerzSettings.STORE_TEST_MODE);

            /**
             * If user want to get Gate way list then pass isGetGatewayList parameter as true
             * If user want to get URL as returned response, pass false.
             */
            response = sslcz.initiateTransaction(postData, false);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}
