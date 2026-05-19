# CLAUDE.md

This file provides guidance to Claude Code when working with code in this repository.

---

## Project description

This is a template project for building Burp Suite extensions.
It provides a ready-made integration testing framework so extension behaviour can be verified against a real running
Burp Suite instance.

- Language: Java
- Build system: Gradle
- Minimum Java version: 21
- API: Burp Suite Montoya API

---

## Code structure

```
src/main/java/
  burp/api/montoya/   # Burp Suite Montoya API interface files 
  burptesting/        # Burp Suite integration test framework
  Extension.java      # Entry point of the extension, change the BurpExtensionIntegrationTest if class name was changed
```



---

## Build

```sh
./gradlew jar
```

Output: `build/libs/extension-in-the-loop-<version>.jar`

---

## Testing

### Unit tests

```sh
./gradlew test
```

Unit tests live in `src/test/java/unit/`. See `TestResultTest` for an example.

### Integration tests

Integration tests run inside a live Burp instance:

```sh
./gradlew test
```

---

## Code guidelines

| What              | Where                              |
|-------------------|------------------------------------|
| Extension code    | `src/main/java/`                   |
| Unit tests        | `src/test/java/unit/`              |
| Integration tests | `src/main/java/burptesting/tests/` |

---

## Integration test examples

### Example 1 — Pure Montoya API (no live Burp state)

Use when your test only needs to construct or inspect Montoya objects.
No `ApiAware` required.

```java
public class SimpleHttpRequestTest {
    public TestResult testMontoyaRequest() {
        HttpRequest request = HttpRequest.httpRequest("GET / HTTP/1.1\r\nHost: example.com\r\n\r\n");
        boolean success = request.hasHeader("Host");
        return new TestResult(success, null, null);
    }
}
```

### Example 2 — Live Burp state via `ApiAware`

Implement `ApiAware` when your test needs the real `MontoyaApi` — the framework
calls `setApi()` before running any test methods.

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

After writing a test class, register it in `BurpTestingModel.TEST_CLASSES`.
