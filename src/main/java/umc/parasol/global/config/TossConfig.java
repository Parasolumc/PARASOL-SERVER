package umc.parasol.global.config;


import org.springframework.context.annotation.Configuration;

// Toss Payments에 사용할 Config 파일
@Configuration
public class TossConfig {

    public static final String ACCESS_TOKEN_API_URL = "https://api.tosspayments.com/v1/brandpay/authorizations/access-token";
    public static final String AUTO_PAYMENT_API_URL = "https://api.tosspayments.com/v1/brandpay/payments";
    public static final String PAYMENT_METHOD_API_URL = "https://api.tosspayments.com/v1/brandpay/payments/methods/";
    public static final String AUTHORIZATION = "Authorization";
    public static final String BASIC = "BASIC ";
    public static final String MEDIA_TYPE = "application/json";
    public static final String ENCODING_KEY = "dGVzdF9za19BTG5RdkRkMlZKWWJnUXFhamVlM01qN1g0MW1OOg==";
}
