package integration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies that Burp Suite is installed at the expected path before integration tests run.
 */
public class BurpInstallationTest {

    /**
     * Checks the bundled java binary is executable and burpsuite.jar exists.
     */
    @Test
    void burpIsInstalled() {
        BurpLocator.BurpInstallation inst = BurpLocator.get();
        assertTrue(inst.javaBinary().toFile().canExecute(),
                "Burp bundled java binary not found or not executable: " + inst.javaBinary());
        assertTrue(inst.burpJar().toFile().isFile(),
                "burpsuite.jar not found: " + inst.burpJar());
    }
}
