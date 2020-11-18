import jdk.jfr.Description;
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
    final String ORDER_ID_NAME = "OrderId";
    final String PAY_INFO_NAME = "PayInfo";

    final String ERROR_CODE_ACCESS_DENIED = "ErrCode=\"ACCESS_DENIED\"";
    final String ERROR_CODE_DUPLICATE_ORDER_ID = "ErrCode=\"DUPLICATE_ORDER_ID\"";
    final String ERROR_CODE_WRONG_PARAMS = "ErrCode=\"WRONG_PARAMS\"";
    final String ERROR_CODE_AMOUNT_ERROR = "ErrCode=\"AMOUNT_ERROR\"";
    final String ERROR_CODE_WRONG_PAY_INFO = "ErrCode=\"WRONG_PAY_INFO\"";

    String orderID = orderIDBuilder();
    String duplicateOrderId = "60f02253-bea1-2563-d432-961f0ace9c943";

    String payInfoValue = encodeValue("PAN=4111111111111112;EMonth=12;EYear=22;CardHolder=Roman Miller;SecureCode=123;" +
            "OrderId=" + orderID + ";Amount=" + AMOUNT_VALUE);

    public static String orderIDBuilder() {
        //60f02253-bea1-2563-d432-961f0ace9c943
        return "60f0" + (int) (Math.random() * 10000) + "-bea1-" + (int) (Math.random() * 10000) + "-d432-961f0ace9c943";
    }

    private static String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }

    @Test
    @Description("Отправить GET запрос с корректно заполненными обязательными полями (Key, OrderID, Amount, PayInfo)")
    public void test_sendCorrectRequest() {
        given().
                queryParam(KEY_NAME, KEY_VALUE).
                queryParam(AMOUNT_NAME, AMOUNT_VALUE).
                queryParam(ORDER_ID_NAME, orderID).
                queryParam(PAY_INFO_NAME, payInfoValue).
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

    @Test
    @Description("Отправить GET запрос с корректно заполненными обязательными полями (OrderID, Amount, PayInfo) и без параметра Key")
    public void test_sendRequestWithoutKey() {
        given().
                queryParam(AMOUNT_NAME, AMOUNT_VALUE).
                queryParam(ORDER_ID_NAME, orderID).
                queryParam(PAY_INFO_NAME, payInfoValue).
                when().
                get(BASE_URL).
                then().
                log().all().
                assertThat()
                .statusCode(200)
                .and()
                .body(containsString("Block"))
                .and()
                .body(containsString("Success=\"False\""))
//                .and()
//                .body(containsString("OrderId=\"" + orderID + "\""))
                .and()
                .body(containsString(ERROR_CODE_ACCESS_DENIED));

    }

    @Test
    @Description("Отправить GET запрос с корректно заполненными обязательными полями (Key, OrderId, Amount, PayInfo). В значении OrderId передать ранее использованный номер заказа")
    public void test_sendRequestWithDuplicateOrderID() {
        given().
                queryParam(KEY_NAME, KEY_VALUE).
                queryParam(AMOUNT_NAME, AMOUNT_VALUE).
                queryParam(ORDER_ID_NAME, duplicateOrderId).
                queryParam(PAY_INFO_NAME, payInfoValue).
                when().
                get(BASE_URL).
                then().
                log().all().
                assertThat()
                .statusCode(200)
                .and()
                .body(containsString("Block"))
                .and()
                .body(containsString("Success=\"False\""))
                .and()
                .body(containsString("OrderId=\"" + duplicateOrderId + "\""))
                .and()
                .body(containsString(ERROR_CODE_DUPLICATE_ORDER_ID));

    }

    @Test
    @Description("Отправить GET запрос с корректно заполненными обязательными полями (Key, Amount, PayInfo) и без параметра OrderId")
    public void test_sendRequestWithoutOrderId() {
        given().
                queryParam(KEY_NAME, KEY_VALUE).
                queryParam(AMOUNT_NAME, AMOUNT_VALUE).
                queryParam(PAY_INFO_NAME, payInfoValue).
                when().
                get(BASE_URL).
                then().
                log().all().
                assertThat()
                .statusCode(200)
                .and()
                .body(containsString("Block"))
                .and()
                .body(containsString("Success=\"False\""))
                .and()
                .body(containsString("OrderId=\"\""))
                .and()
                .body(containsString(ERROR_CODE_WRONG_PARAMS));

    }

    @Test
    @Description("Отправить GET запрос с корректно заполненными обязательными полями (Key, OrderId, PayInfo) и без параметра Amount")
    public void test_sendRequestWithoutAmount() {
        given().
                queryParam(KEY_NAME, KEY_VALUE).
                queryParam(ORDER_ID_NAME, orderID).
                queryParam(PAY_INFO_NAME, payInfoValue).
                when().
                get(BASE_URL).
                then().
                log().all().
                assertThat()
                .statusCode(200)
                .and()
                .body(containsString("Block"))
                .and()
                .body(containsString("Success=\"False\""))
                .and()
                .body(containsString("OrderId=\"" + orderID + "\""))
                .and()
                .body(containsString(ERROR_CODE_AMOUNT_ERROR));

    }

    @Test
    @Description("Отправить GET запрос с корректно заполненными обязательными полями (Key, Amount, OrderId) и без параметра PayInfo")
    public void test_sendRequestWithoutPayInfo() {
        given().
                queryParam(KEY_NAME, KEY_VALUE).
                queryParam(AMOUNT_NAME, AMOUNT_VALUE).
                queryParam(ORDER_ID_NAME, orderID).
                when().
                get(BASE_URL).
                then().
                log().all().
                assertThat()
                .statusCode(200)
                .and()
                .body(containsString("Block"))
                .and()
                .body(containsString("Success=\"False\""))
//                .and()
//                .body(containsString("OrderId=\"" + orderID + "\""))
                .and()
                .body(containsString(ERROR_CODE_WRONG_PAY_INFO));

    }
}
