package integration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Launches Burp Suite as a subprocess with the extension loaded and asserts all
 * in-process tests (burptesting/tests) pass. Requires {@code ./gradlew jar} to have run first —
 * Gradle's test task handles this via {@code dependsOn("jar")}.
 */
public class BurpIntegrationTest {

    private static BurpLocator.BurpInstallation installation;
    private static Path extensionJar;
    private static String extensionName;

    /**
     * Resolves the Burp installation and extension jar path. Fails early with clear messages if either is missing.
     */
    @BeforeAll
    static void resolvePrerequisites() {
        installation = BurpLocator.get();

        String jarProp = System.getProperty("extensionJar");
        assertNotNull(jarProp, "System property 'extensionJar' not set — run via Gradle: ./gradlew test");
        extensionJar = Paths.get(jarProp);
        assertTrue(extensionJar.toFile().isFile(), "Extension jar does not exist: " + extensionJar);
        extensionName = System.getProperty("extensionName");
        assertNotNull(extensionName, "System property 'extensionName' not set — run via Gradle: ./gradlew test");
    }

    /**
     * Builds the command.
     * Extension class name referenced here — see Extension.java for the corresponding declaration.
     */
    private static List<String> buildCommand() {
        return List.of(
                installation.javaBinary().toString(),
                "-Djava.awt.headless=true",
                "-Xbootclasspath/a:" + extensionJar,
                "-Dburptesting.extension=" + extensionName,
                "-jar", installation.burpJar().toString(),
                "--developer-extension-class-name=Extension",
                "--use-defaults",
                "--temporary-project"
        );
    }

    /**
     * Starts Burp headlessly with the extension jar on the bootclasspath, reads stdout for test results,
     * and asserts {@code ALL TESTS PASSED}. Burp shuts itself down after tests complete.
     */
    @Test
    @Timeout(60)
    void runAllBurpIntegrationTests() throws Exception {
        ProcessBuilder pb = new ProcessBuilder(buildCommand());
        // stderr stays separate — all JVM/Swing/SLF4J noise is discarded, stdout has clean test output
        pb.redirectError(ProcessBuilder.Redirect.DISCARD);

        Process process = pb.start();
        try {
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            String out = output.toString();
            if (!out.contains("ALL TESTS PASSED")) {
                fail("Burp tests did not all pass.\n\n" + out);
            }
        } finally {
            process.destroyForcibly();
        }
    }
}
