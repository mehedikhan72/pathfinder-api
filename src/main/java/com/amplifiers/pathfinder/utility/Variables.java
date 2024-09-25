package com.amplifiers.pathfinder.utility;

public class Variables {

    public static class ClientSettings {

        public static final String CLIENT_BASE_URL = "https://www.pathphindr.com";
    }

    public static class ApiSettings {

        public static final String API_BASE_URL = "https://api.pathphindr.com/";
    }

    public static class PaginationSettings {

        public static final Integer NUM_GIGS_PER_PAGE = 10;
        public static final Integer NUM_ENROLLMENTS_PER_PAGE = 5;
        public static final Integer NUM_NOTIFICATIONS_PER_PAGE = 5;
        public static final Integer NUM_REVIEWS_PER_PAGE = 5;
        public static final Integer NUM_MESSAGES_PER_PAGE = 10;
        public static final Integer NUM_RECOMMENDED_GIGS = 5;
        public static final Integer NUM_REPORTS_PER_PAGE = 20;
    }

    public static class SslCommerzSettings {

        public static final String SSLCOMMERZ_STORE_ID = "pathf66bdad0fb5f89";
        public static final String SSLCOMMERZ_STORE_PASSWORD = "pathf66bdad0fb5f89@ssl";
        public static final boolean STORE_TEST_MODE = true;
    }
}
