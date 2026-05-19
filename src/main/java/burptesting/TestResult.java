package burptesting;

public record TestResult(boolean success, String message, Throwable throwable) {
}
