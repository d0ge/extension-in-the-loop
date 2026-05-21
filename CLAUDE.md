# CLAUDE.md

This file provides guidance to Claude Code when working with code in this repository.

---

## Project description

This is a template project for building Burp Suite extensions.
It provides a ready-made burptesting testing framework so extension behaviour can be verified against a real running
Burp Suite instance.

- Language: Java
- Build system: Gradle
- Minimum Java version: 21
- API: Burp Suite Montoya API Interface files available at @src/main/java/burp/api/montoya

---

## Code structure

```
src/main/java/
  burp/api/montoya/      # Burp Suite Montoya API interface files 
  burp/Extension.java    # Entry point of the extension, change the BurpIntegrationTest if class name was changed
  burptesting/           # Burp Suite burptesting test framework
```

---

## Build

```sh
./gradlew jar
```

Output: `build/libs/extension-in-the-loop-<version>.jar`

---

## Testing

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
| Integration tests | `src/main/java/burptesting/tests/` |

---

## Integration test examples

Implement `BurpTestingInterface` when your test needs the real `MontoyaApi` — the framework
calls `setApi()` before running any test methods.

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

After writing a test class, register it in `BurpTestingModel.TEST_CLASSES`.
