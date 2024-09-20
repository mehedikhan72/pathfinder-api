package com.amplifiers.pathfinder.payment;

import com.amplifiers.pathfinder.entity.enrollment.Enrollment;
import com.amplifiers.pathfinder.entity.enrollment.EnrollmentRepository;
import com.amplifiers.pathfinder.entity.gig.Gig;
import com.amplifiers.pathfinder.entity.gig.GigRepository;
import com.amplifiers.pathfinder.entity.notification.NotificationService;
import com.amplifiers.pathfinder.entity.notification.NotificationType;
import com.amplifiers.pathfinder.entity.transaction.Transaction;
import com.amplifiers.pathfinder.entity.transaction.TransactionRepository;
import com.amplifiers.pathfinder.exception.ResourceNotFoundException;
import com.amplifiers.pathfinder.exception.ValidationException;
import com.amplifiers.pathfinder.sslcommerz.SSLCommerz;
import com.amplifiers.pathfinder.sslcommerz.TransactionResponseValidator;
import com.amplifiers.pathfinder.utility.EmailService;
import com.amplifiers.pathfinder.utility.Variables;
import com.amplifiers.pathfinder.utility.Variables.ApiSettings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final TransactionRepository transactionRepository;
    private final TransactionResponseValidator transactionResponseValidator;
    private final NotificationService notificationService;
    private final EnrollmentRepository enrollmentRepository;
    private final GigRepository gigRepository;
    private final EmailService emailService;

    private final Integer gigScoreIncrease = 10;

    // returns the URL to which the user will be redirected to make the payment
    public String handleOnlinePayment(Enrollment enrollment) throws Exception {

        String tranxId = UUID.randomUUID().toString();
        Transaction transaction = Transaction.builder()
                .tranxId(tranxId)
                .enrollment(enrollment)
                .amount(enrollment.getPrice())
                .paidAt(OffsetDateTime.now())
                .paymentConfirmed(false)
                .build();

        transactionRepository.save(transaction);

        Map<String, String> postData = constructPostData(
                enrollment.getPrice(),
                tranxId,
                enrollment.getBuyer().getFullName(),
                enrollment.getBuyer().getEmail(),
                enrollment.getGig().getTitle()
        );
        SSLCommerz sslCommerz = new SSLCommerz(
                Variables.SslCommerzSettings.SSLCOMMERZ_STORE_ID,
                Variables.SslCommerzSettings.SSLCOMMERZ_STORE_PASSWORD,
                Variables.SslCommerzSettings.STORE_TEST_MODE
        );
        String url = sslCommerz.initiateTransaction(postData, false);
        System.out.println("url - " + url);

        return url;
    }

    public Map<String, String> constructPostData(Float total, String tranxId, String cusName, String cusEmail, String gigTitle) {
        String baseUrl = ApiSettings.API_BASE_URL; //Request.Url.Scheme + "://" + Request.Url.Authority + Request.ApplicationPath.TrimEnd('/') + "/";
        String successUrl = baseUrl + "api/v1/public/enrollment/payment-success";
        String failUrl = baseUrl + "api/v1/public/enrollment/payment-fail";
        String cancelUrl = baseUrl + "api/v1/public/enrollment/payment-cancel";

        Map<String, String> postData = new HashMap<String, String>();
        postData.put("total_amount", String.valueOf(total));
        postData.put("tran_id", tranxId); // use unique tran_id for each API call
        postData.put("success_url", successUrl);
        postData.put("fail_url", failUrl);
        postData.put("cancel_url", cancelUrl);
        postData.put("cus_name", cusName);
        postData.put("cus_email", cusEmail);
        postData.put("cus_add1", "Address Line One");
        postData.put("cus_city", "Dhaka");
        postData.put("cus_postcode", "1000");
        postData.put("cus_country", "Bangladesh");
        postData.put("cus_phone", "0111111111");
        postData.put("shipping_method", "NO");
        postData.put("product_name", "Test Product");
        postData.put("product_category", "General");
        postData.put("product_profile", "General");
//        postData.put("ship_name", "ABC XY");
//        postData.put("ship_add1", "Address Line One");
//        postData.put("ship_add2", "Address Line Two");
//        postData.put("ship_city", "City Name");
//        postData.put("ship_state", "State Name");
//        postData.put("ship_postcode", "Post Code");
//        postData.put("ship_country", "Country");
        return postData;
    }

    public boolean paymentSuccess(Object request) {
        Map<String, String> returnData = (Map<String, String>) request;
        String tranxId = returnData.get("tran_id");

        System.out.println("returnData - " + returnData);
        boolean paymentValidationSuccessful;
        try {
            paymentValidationSuccessful = transactionResponseValidator.receiveSuccessResponse(returnData);
            System.out.println("Payment validation successful: " + paymentValidationSuccessful);
        } catch (Exception e) {
            System.out.println("Exception occurred while validating payment.");
            return false;
        }

        if (paymentValidationSuccessful) {
            System.out.println("Payment successful.");

            Optional<Transaction> transaction = transactionRepository.findByTranxId(tranxId);
            if (transaction.isEmpty()) {
                throw new ResourceNotFoundException("No transaction found with the transaction ID.");
            }

            transaction.get().setPaymentConfirmed(true);
            transactionRepository.save(transaction.get());

            Enrollment enrollment = transaction.get().getEnrollment();

            enrollment.setPaid(true);
            enrollment.setBuyerConfirmed(true);
            enrollment.setStartedAt(OffsetDateTime.now());

            enrollmentRepository.save(enrollment);

            Gig gig = enrollment.getGig();
            gig.setScore(gig.getScore() + gigScoreIncrease);
            gigRepository.save(gig);

            // Sending notification
            String notificationTxt = enrollment.getBuyer().getFullName()
                    + " has accepted your enrollment offer.";
            String linkSuffix = "interaction/user/" + enrollment.getBuyer().getId();
            notificationService.sendNotification(notificationTxt, enrollment.getGig().getSeller(), NotificationType.ENROLLMENT, linkSuffix);

            // updating seller n buyer thru email.
            try {
                emailService.sendEmail(enrollment.getBuyer(),
                        "Enrollment Confirmed",
                        "You have successfully confirmed the enrollment for the gig " + enrollment.getGig().getTitle() + ".\n"
                                + "Amount paid - " + enrollment.getPrice() + ".\n"
                                + "Transaction id - " + tranxId + ".\n\n"
                                + "Best,\n"
                                + "Team pathPhindr\n");
            } catch (Exception e) {
                throw new ValidationException("Email could not be sent. Please try again.");
            }

            try {
                emailService.sendEmail(enrollment.getGig().getSeller(),
                        "New Confirmed Enrollment",
                        "You have a new enrollment confirmed for the gig " + enrollment.getGig().getTitle() + ".\n"
                                + "Amount received - " + enrollment.getPrice() + ".\n"
                                + "Transaction id - " + tranxId + ".\n\n"
                                + "Best,\n"
                                + "Team pathPhindr\n");
            } catch (Exception e) {
                throw new ValidationException("Email could not be sent. Please try again.");
            }

            return true;
        } else {
            System.out.println("Payment failed.");
            return false;
        }
    }

    public void paymentFail(Object request) {
        System.out.println("Transaction deleted cause payment failed.");
    }

    public void paymentCancel(Object request) {
        System.out.println("Transaction deleted cause payment cancelled.");
    }
}
