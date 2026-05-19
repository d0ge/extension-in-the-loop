package integration;

import java.nio.file.Path;

/**
 * Resolves the Burp Suite installation at its fixed OS-specific path. Fails fast if not found.
 */
public class BurpLocator {

    /**
     * Returns the Burp installation for the current OS.
     * Throws {@link AssertionError} with a fix instruction if the expected path does not exist.
     */
    public static BurpInstallation get() {
        Path root = root();
        Path javaBinary;
        Path burpJar;

        String os = System.getProperty("os.name", "").toLowerCase();
        if (os.contains("mac")) {
            javaBinary = root.resolve("jre.bundle/Contents/Home/bin/java");
            burpJar = root.resolve("app/burpsuite.jar");
        } else if (os.contains("win")) {
            javaBinary = root.resolve("jre\\bin\\java.exe");
            burpJar = root.resolve("burpsuite.jar");
        } else {
            javaBinary = root.resolve("jre/bin/java");
            burpJar = root.resolve("burpsuite.jar");
        }

        if (!root.toFile().exists()) {
            throw new AssertionError(
                    "Burp Suite not found at: " + root + "\n" +
                            "Please ensure Burp Suite is installed at that exact path."
            );
        }

        return new BurpInstallation(javaBinary, burpJar);
    }

    /**
     * Returns the OS-specific root directory of the Burp installation.
     */
    private static Path root() {
        String os = System.getProperty("os.name", "").toLowerCase();
        if (os.contains("mac")) {
            return Path.of("/Applications/Burp Suite.app/Contents/Resources");
        } else if (os.contains("win")) {
            return Path.of("C:\\Program Files\\BurpSuite");
        } else {
            return Path.of(System.getProperty("user.home"), "BurpSuitePro");
        }
    }

    /**
     * Paths to the bundled JRE java binary and burpsuite.jar within a Burp installation.
     */
    public record BurpInstallation(Path javaBinary, Path burpJar) {
    }
}
