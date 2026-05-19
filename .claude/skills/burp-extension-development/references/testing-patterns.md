# Integration Testing Patterns

Integration tests live in `src/main/java/burptesting/tests/` and run inside a live Burp Suite instance.

## Which Pattern to Use

| Pattern | When |
|---------|------|
| Plain class | Test only needs Montoya static factories — no live Burp state |
| `ApiAware` | Test needs real Burp state: scope, proxy history, HTTP, persistence |

## Pattern 1 — Plain class

```java
package burptesting.tests;

import burp.api.montoya.http.message.requests.HttpRequest;
import burptesting.TestResult;

public class HttpHeaderPresentTest {
    public TestResult hostHeaderPresent() {
        HttpRequest request = HttpRequest.httpRequest("GET / HTTP/1.1\r\nHost: example.com\r\n\r\n");
        boolean ok = request.hasHeader("Host");
        return new TestResult(ok, ok ? null : "Missing Host header", null);
    }
}
```

## Pattern 2 — ApiAware (live Burp state)

Implement `ApiAware` when your test needs the real `MontoyaApi`. The framework calls `setApi()` before running any test methods.

```java
public class Base64Test implements ApiAware {
    private MontoyaApi api;

    @Override
    public void setApi(MontoyaApi api) { this.api = api; }

    public TestResult encodeAndDecode() {
        var base64 = api.utilities().base64Utils();
        // use api freely here
        return new TestResult(true, null, null);
    }
}
```

See `src/main/java/burptesting/tests/Base64Test.java` for a full example.

## TestResult

```java
return new TestResult(true, null, null);                        // pass
return new TestResult(false, "Expected X but got Y", null);     // fail with message
return new TestResult(false, "Exception thrown", e);            // fail with cause
```

Constructor: `TestResult(boolean success, String failureMessage, Throwable cause)`

## Registering a Test Class

Every new test class must be added to `BurpTestingModel.TEST_CLASSES`:

```java
private static final List<Class<?>> TEST_CLASSES = List.of(
        SimpleHttpRequestTest.class,
        ScopeIsEmptyTest.class,
        MyNewTest.class   // add here
);
```

## Running and Confirming

```sh
# Run all tests
./gradlew test
```

After writing tests, run them and **confirm they fail** before implementing. After implementing, run again and **confirm they pass**.

## Controlling Test Order

Use `@TestOrder.Order(n)` when test methods within a class must run in sequence (e.g. setup then verify). Lower values run first.

