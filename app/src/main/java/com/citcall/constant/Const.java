package com.citcall.constant;

/**
 * Created by Agustya on 7/24/16.
 */
public class Const {
    public static class Value {
        public static final int CONNECTION_TIME_OUT = 30;
        public static final int DELAY = 1000;
    }

    public static class Page {
        public static final int MAIN = 1;
    }

    public static class Key {
        public static final String NUMBER = "number";
    }

    public static class Extra {
        public static final String STATUS = "status";
        public static final String RETRY = "retry";
    }

    public static class Request {
        public static final int PERMISSION_MAIN = 11;
    }

    public static class Status {
        public static final String SUCCESS = "Success";
        public static final String FAILED = "Failed";
    }

    public static class Url {
        //example BASE_URL http://citcall.com/backend/
        public static final String BASE_URL = "YOUR_URL";
        public static final String VERIFY = BASE_URL + "mval.php";
        public static final String REQUEST = BASE_URL + "mrequest.php";
    }
}
