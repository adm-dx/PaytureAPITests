import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class PaytureAPIBlockRequestTests {
    final String BASE_URL = "https://sandbox3.payture.com/api/Block";
    final String KEY_NAME = "Key";
    final String KEY_VALUE = "Merchant";
    final String AMOUNT_NAME = "Amount";
    final String AMOUNT_VALUE = "12345";
    final String ORDERID_NAME = "OrderId";
    final String PAYINFO_NAME = "PayInfo";
    String orderID = orderIDBuilder();
    String payInfoValue = encodeValue("PAN=4111111111111112;EMonth=12;EYear=22;CardHolder=Roman Miller;SecureCode=123;" +
            "OrderId=" + orderID + ";Amount=" + AMOUNT_VALUE);

    public static String orderIDBuilder(){
        //60f02253-bea1-2563-d432-961f0ace9c943
        return "60f0" + (int)(Math.random() * 10000) + "-bea1-" + (int)(Math.random() * 10000) + "-d432-961f0ace9c943";
    }

    private static String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }

    @Test
    public void test_sendCorrectRequest() {
        given().
                queryParam(KEY_NAME, KEY_VALUE).
                queryParam(AMOUNT_NAME, AMOUNT_VALUE).
                queryParam(ORDERID_NAME, orderID).
                queryParam(PAYINFO_NAME, payInfoValue).
        when().
                get(BASE_URL).
        then().
                log().all().
                assertThat()
                .statusCode(200)
                .and()
                .body(containsString("Block"))
                .and()
                .body(containsString("Success=\"True\""))
                .and()
                .body(containsString("OrderId=\"" + orderID + "\""))
                .and()
                .body(containsString("Amount=\"" + AMOUNT_VALUE + "\""));

    }
}
