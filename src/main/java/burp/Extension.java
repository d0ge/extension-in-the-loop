package burp;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burptesting.BurpTestingController;

import java.io.IOException;
import java.util.jar.Manifest;

// Class name is referenced by integration.BurpExtensionIntegrationTest#buildCommand
public class Extension implements BurpExtension {

    private static String readManifestTitle() {
        try (var stream = ClassLoader.getSystemClassLoader().getResourceAsStream("META-INF/MANIFEST.MF")) {
            if (stream != null) {
                String title = new Manifest(stream).getMainAttributes().getValue("Implementation-Title");
                if (title != null) return title;
            }
        } catch (IOException ignored) {
        }
        return "extension-in-the-loop";
    }

    @Override
    public void initialize(MontoyaApi api) {
        api.extension().setName("Extension In The Loop");

        // DO NOT CHANGE
        // Required for integration tests
        String name = readManifestTitle();
        String burpTestingProperty = System.getProperty("burptesting.extension");
        String burpTestingClassProperty = System.getProperty("burp.testing.extension.class");
        if (name.equalsIgnoreCase(burpTestingProperty)) {
            BurpTestingController burpTestingController = new BurpTestingController(api, burpTestingClassProperty);
            api.userInterface().registerSuiteTab(burpTestingController.getCaption(), burpTestingController.getPanel());
        }
    }
}
