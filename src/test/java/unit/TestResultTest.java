package unit;

import burptesting.TestResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestResultTest {

    @Test
    void successResult() {
        var result = new TestResult(true, null, null);
        assertTrue(result.success());
        assertNull(result.message());
        assertNull(result.throwable());
    }

    @Test
    void failureResultWithMessage() {
        var result = new TestResult(false, "something went wrong", null);
        assertFalse(result.success());
        assertEquals("something went wrong", result.message());
    }

    @Test
    void failureResultWithThrowable() {
        var cause = new RuntimeException("boom");
        var result = new TestResult(false, "exception thrown", cause);
        assertFalse(result.success());
        assertSame(cause, result.throwable());
    }
}
