package burptesting.tests;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.utilities.Base64DecodingOptions;
import burp.api.montoya.utilities.Base64EncodingOptions;
import burptesting.ApiAware;
import burptesting.TestOrder;
import burptesting.TestResult;

public class Base64Test implements ApiAware {
    private MontoyaApi api;

    @Override
    public void setApi(MontoyaApi api) {
        this.api = api;
    }

    @TestOrder.Order(1)
    public TestResult encodeAndDecode() {
        var base64 = api.utilities().base64Utils();
        String original = "Hello, Burp!";
        String encoded = base64.encodeToString(original);
        String decoded = base64.decode(encoded).toString();
        if (!original.equals(decoded)) {
            return new TestResult(false, "Round-trip failed: expected \"" + original + "\" but got \"" + decoded + "\"", null);
        }
        return new TestResult(true, null, null);
    }

    @TestOrder.Order(2)
    public TestResult urlSafeEncoding() {
        var base64 = api.utilities().base64Utils();
        // Use a string that produces + or / in standard base64 so the URL option is meaningful
        String input = "subjects?_d=1&foo=bar/baz";
        String encoded = base64.encodeToString(input, Base64EncodingOptions.URL);
        if (encoded.contains("+") || encoded.contains("/")) {
            return new TestResult(false, "URL-safe encoded output contains '+' or '/': " + encoded, null);
        }
        return new TestResult(true, null, null);
    }

    @TestOrder.Order(3)
    public TestResult noPaddingEncoding() {
        var base64 = api.utilities().base64Utils();
        String input = "Burp";
        String encoded = base64.encodeToString(input, Base64EncodingOptions.NO_PADDING);
        if (encoded.contains("=")) {
            return new TestResult(false, "NO_PADDING encoded output contains '=': " + encoded, null);
        }
        return new TestResult(true, null, null);
    }
}