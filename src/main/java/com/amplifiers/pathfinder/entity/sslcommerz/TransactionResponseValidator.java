package com.amplifiers.pathfinder.entity.sslcommerz;
import com.amplifiers.pathfinder.utility.Variables.SslCommerzSettings;
import org.springframework.boot.web.server.Ssl;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * This class handles the Response parameters redirected from payment success page.
 * Validates those parameters fetched from payment page response and returns true for successful transaction
 * and false otherwise.
 */
@Component
public class TransactionResponseValidator {
    /**
     *
     * @param request
     * @return
     * @throws Exception
     * Send Received params from your success response (POST ) in this Map</>
     */
    public boolean receiveSuccessResponse(Map<String, String> request) throws Exception {

        String trxId = request.get("tran_id");
        /**
         *Get your AMOUNT and Currency FROM DB to initiate this Transaction
         */
        String amount = request.get("amount");
        String currency = "BDT";
        // Set your store Id and store password and define TestMode
        SSLCommerz sslcz = new SSLCommerz(SslCommerzSettings.SSLCOMMERZ_STORE_ID, SslCommerzSettings.SSLCOMMERZ_STORE_PASSWORD, SslCommerzSettings.STORE_TEST_MODE);

        /**
         * If following order validation returns true, then process transaction as success.
         * if this following validation returns false , then query status if failed of canceled.
         *      Check request.get("status") for this purpose
         */
        return sslcz.orderValidate(trxId, amount, currency, request);
    }
}
