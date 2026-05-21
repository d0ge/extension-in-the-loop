---
name: burp-extension-development
description: Use when writing, modifying, or testing Burp Suite extension code in Java with the Montoya API — adding handlers, writing integration tests, or implementing any extension feature.
---

# Burp Suite Extension Development

## Development principals
1. Think Before Coding - Don't assume. Don't hide confusion. Surface tradeoffs. Read docs first `src/main/java/burp/api/montoya`
2. Simplicity First - Minimum code that solves the problem. Nothing speculative.
3. Surgical Changes - Touch only what you must. Clean up only your own mess.
4. Goal-Driven Execution - Define success criteria. Loop until verified

## Testing principles

**Never add visibility to accommodate tests. If logic needs testing, it needs its own class.** When you feel the urge to make an internal method `public` so a test can reach it, stop — that is a design signal. The logic is in the wrong class. A class's public interface exists for its callers, not for its tests. If the logic is worth testing, it has a distinct responsibility and belongs in a dedicated class whose natural public interface is exactly what you wanted to test. Extract until each class is testable through its own boundary without concession.

**Test the real class, never copy and paste code.** Integration tests must instantiate the production class and call its public methods directly. Never reimplement the production logic inside the test — a test that duplicates what the production code does is not testing that code. If the production class has a bug, the test must catch it.

## Two Types of Tests

| Type | Location | Framework | When |
|------|----------|-----------|------|
| Integration | `src/main/java/burptesting/tests/` | Burp testing framework | Behavior inside a live Burp instance |

Add integration tests to the `src/main/java/burptesting/tests/` folder.

## Reference

Read `references/testing-patterns.md` before writing integration tests — it contains `TestResult` usage, `BurpTestingInterface` pattern, test ordering, and full worked examples.

## Quick Reference

| Need                            | Answer |
|---------------------------------|--------|
| Burp Suite Integration Tests    | Implement `BurpTestingInterface`, receive `MontoyaApi` via `setApi()` |
| Control test order within class | `@TestOrder.Order(n)` — lower runs first |
| Register a new test class       | Add to `BurpTestingModel.TEST_CLASSES` |