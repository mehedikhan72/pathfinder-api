package com.amplifiers.pathfinder.payment;

import com.amplifiers.pathfinder.entity.enrollment.Enrollment;
import com.amplifiers.pathfinder.entity.enrollment.EnrollmentRepository;
import com.amplifiers.pathfinder.entity.gig.Gig;
import com.amplifiers.pathfinder.entity.notification.NotificationService;
import com.amplifiers.pathfinder.sslcommerz.TransactionResponseValidator;
import com.amplifiers.pathfinder.entity.transaction.Transaction;
import com.amplifiers.pathfinder.entity.transaction.TransactionRepository;
import com.amplifiers.pathfinder.entity.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private TransactionResponseValidator transactionResponseValidator;
    @Mock
    private NotificationService notificationService;
    @Mock
    private EnrollmentRepository enrollmentRepository;


    private PaymentService paymentService;

    private Enrollment enrollment;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService(transactionRepository, transactionResponseValidator, notificationService, enrollmentRepository);

        User buyer = new User();
        buyer.setFirstName("John");
        buyer.setLastName("Doe");
        buyer.setEmail("john.doe@gmail.com");

        User seller = new User();
        seller.setFirstName("Jane");
        seller.setLastName("Doe");
        seller.setEmail("jane.doe@gmail.com");

        Gig gig = new Gig();
        gig.setTitle("Test Gig");
        gig.setSeller(seller);
        gig.setPrice(100.0f);

        enrollment = new Enrollment();
        enrollment.setGig(gig);
        enrollment.setPrice(100.0f);
        enrollment.setBuyer(buyer);
        enrollment.setPaid(false);
        enrollment.setBuyerConfirmed(false);
        enrollment.setStartedAt(null);

        transaction = new Transaction();
        transaction.setTranxId("123456");
        transaction.setEnrollment(enrollment);
        transaction.setAmount(100.0f);
        transaction.setPaidAt(null);
        transaction.setPaymentConfirmed(false);
    }

    @Test
    void testIfHandleOnlinePaymentReturnsUrl() throws Exception {
        String url = paymentService.handleOnlinePayment(enrollment);
        // Assert that the URL is not null and is a valid URL
        assertTrue(url != null && url.startsWith("https://"), "The URL should start with 'https://'");
    }

    @Test
    void testPaymentSuccessWithValidPayment() throws Exception {
        Map<String, String> returnData = new HashMap<>();
        returnData.put("tran_id", "123456");

        when(transactionResponseValidator.receiveSuccessResponse(returnData)).thenReturn(true);
        when(transactionRepository.findByTranxId("123456")).thenReturn(Optional.of(transaction));

        boolean result = paymentService.paymentSuccess(returnData);

        assertTrue(result);
        assertTrue(enrollment.isPaid());
        assertTrue(enrollment.isBuyerConfirmed());
        assertTrue(transaction.isPaymentConfirmed());
        assertNotNull(enrollment.getStartedAt());
    }

    @Test
    void testPaymentSuccessWithInvalidPayment() throws Exception {
        Map<String, String> returnData = new HashMap<>();
        returnData.put("tran_id", "123456");

        when(transactionResponseValidator.receiveSuccessResponse(returnData)).thenReturn(false);

        boolean result = paymentService.paymentSuccess(returnData);

        assertFalse(result);
        assertFalse(enrollment.isPaid());
        assertFalse(enrollment.isBuyerConfirmed());
        assertFalse(transaction.isPaymentConfirmed());
        assertNull(enrollment.getStartedAt());
    }
}