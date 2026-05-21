# Integration Testing Patterns

Integration tests live in `src/main/java/burptesting/tests/` and run inside a live Burp Suite instance.

## Pattern 

Implement `BurpTestingInterface` when writing Burp Suite Integration tests. The framework calls `setApi()` before running any test methods.

```java
public class Base64Test implements BurpTestingInterface {
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

Constructor: `TestResult(boolean success, String message, Throwable throwable)`

## Registering a Test Class

Every new test class must be added to `BurpTestingModel.TEST_CLASSES`:

```java
private static final List<Class<?>> TEST_CLASSES = List.of(
        Base64Test.class,
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

