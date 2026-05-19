package burptesting;

import burp.api.montoya.MontoyaApi;
import burptesting.tests.Base64Test;
import burptesting.tests.SimpleHttpRequestTest;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class BurpTestingModel {
    private static final List<Class<?>> TEST_CLASSES = List.of(
            Base64Test.class,
            SimpleHttpRequestTest.class
    );

    private final Set<String> classFilter;
    private final MontoyaApi api;

    public BurpTestingModel(MontoyaApi api, String classFilter) {
        this.api = api;
        this.classFilter = classFilter != null ? Set.of(classFilter.split(",")) : null;
    }

    private static TestExecution executeMethod(Object instance, Method method, String className) {
        try {
            var result = (TestResult) method.invoke(instance);
            return new TestExecution(className, method.getName(), result, null);
        } catch (Throwable t) {
            return new TestExecution(className, method.getName(),
                    new TestResult(false, "Exception thrown", t),
                    stackTrace(t));
        }
    }

    private static String stackTrace(Throwable t) {
        var baos = new ByteArrayOutputStream();
        t.printStackTrace(new PrintStream(baos));
        return baos.toString();
    }

    public TestSummary runTests() {
        var testClasses = classFilter == null
                ? TEST_CLASSES
                : TEST_CLASSES.stream().filter(c -> classFilter.contains(c.getSimpleName())).toList();

        var executions = testClasses.stream()
                .flatMap(testClass -> executeClass(testClass).stream())
                .toList();

        return new TestSummary(executions);
    }

    private List<TestExecution> executeClass(Class<?> testClass) {
        try {
            var instance = testClass.getDeclaredConstructor().newInstance();
            if (instance instanceof ApiAware a) {
                a.setApi(api);
            }
            return Arrays.stream(testClass.getDeclaredMethods())
                    .filter(m -> m.canAccess(instance) && m.getReturnType().equals(TestResult.class))
                    .sorted((a, b) -> TestOrder.comparator().compare(
                            a.getAnnotation(TestOrder.Order.class),
                            b.getAnnotation(TestOrder.Order.class)))
                    .map(m -> executeMethod(instance, m, testClass.getSimpleName()))
                    .toList();
        } catch (Exception e) {
            return List.of(new TestExecution(
                    testClass.getSimpleName(), "<init>",
                    new TestResult(false, "Failed to instantiate test class", e),
                    stackTrace(e)));
        }
    }

    public record TestSummary(List<TestExecution> executions) {
        public int run() {
            return executions.size();
        }

        public int passed() {
            return (int) executions.stream().filter(e -> e.result().success()).count();
        }

        public int failed() {
            return run() - passed();
        }
    }

    public record TestExecution(String className, String methodName, TestResult result, String stackTrace) {
    }
}