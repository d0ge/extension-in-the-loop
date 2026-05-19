package burptesting.tests;

import burp.api.montoya.http.message.requests.HttpRequest;
import burptesting.TestOrder;
import burptesting.TestResult;

public class SimpleHttpRequestTest {
    private final String rawRequest = "GET / HTTP/1.1\r\nHost: example.com\r\nTest: success\r\n\r\n";

    @TestOrder.Order(1)
    public TestResult testMontoyaRequest() {
        try {
            HttpRequest request = HttpRequest.httpRequest(rawRequest);
            boolean success = request.hasHeader("Test");
            return new TestResult(success, null, null);
        } catch (Exception exc) {
            return new TestResult(false, "Failed to create HttpRequest", exc);
        }
    }
}
