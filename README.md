# Extension In The Loop

A template for building Burp Suite extensions with a built-in integration testing framework. 
It lets you verify extension behaviour against a real, running Burp instance as part of your normal Gradle build.
Inspired by [Live Testing](https://github.com/CompassSecurity/SAMLRaider/blob/master/doc/hacking.md)

## Why

Unit tests can mock the Montoya API, but they cannot catch bugs that only surface when your extension runs inside Burp — incorrect scoping, UI registration issues, or anything that depends on live Burp state. 
This template closes that gap by launching Burp headlessly during `./gradlew test` and running your test classes inside the live process.

## How to use it

**1. Write your extension**

Put your extension code in `src/main/java/`. The entry point is `Extension.java`, which implements `BurpExtension`.

**2. Write integration tests**

Add test classes to `src/main/java/burptesting/tests/`. Each public method that returns `TestResult` is treated as a test case.

- If your test only needs to construct Montoya objects, no extra setup is required.
- If your test needs the live `MontoyaApi`, implement `ApiAware` — the framework calls `setApi()` before running your tests.

```java
public class MyTest implements ApiAware {
    private MontoyaApi api;

    @Override
    public void setApi(MontoyaApi api) { this.api = api; }

    public TestResult myCheck() {
        // use api freely here
        return new TestResult(true, null, null);
    }
}
```

**3. Register your test class**

Add it to `TEST_CLASSES` in `BurpTestingModel.java`:

```java
private static final List<Class<?>> TEST_CLASSES = List.of(
        MyTest.class
);
```

**4. Run**

```sh
./gradlew test
```

Gradle builds the extension jar, launches Burp headlessly with the jar loaded, runs your test classes inside the live process, and reports results via JUnit. Burp shuts itself down automatically after the tests complete.


## Claude Code

A Claude Code skill for Burp extension development is bundled at `.claude/skills/burp-extension-development/`. 
When working in this repo with Claude Code, it is picked up automatically and guides Claude on Montoya API patterns, integration test structure, and code style.

## Requirements

- [Claude Code](https://code.claude.com/docs/en/quickstart)
- Superpowers plugin `npx claudepluginhub obra/superpowers --plugin superpowers`
- Java 21+
- Burp Suite installed at its default location for your OS:

| OS      | Path                                                |
|---------|-----------------------------------------------------|
| macOS   | `/Applications/Burp Suite.app/Contents/Resources`   |
| Windows | `C:\Program Files\BurpSuite`                        |
| Linux   | `~/BurpSuitePro`                                    |

