package burptesting;

import burp.api.montoya.MontoyaApi;

import javax.swing.*;
import java.util.logging.*;

public class BurpTestingController {
    private static final Logger logger = Logger.getLogger(BurpTestingController.class.getName());

    static {
        logger.setLevel(Level.ALL);

        StreamHandler consoleHandler = new StreamHandler(System.out, new Formatter() {
            @Override
            public String format(LogRecord record) {
                return record.getMessage() + "\n";
            }
        }) {
            @Override
            public void publish(LogRecord record) {
                super.publish(record);
                flush();
            }
        };
        consoleHandler.setLevel(Level.ALL);
        logger.addHandler(consoleHandler);
    }

    private final BurpTestingModel model;
    private final BurpTestingView view;
    private final MontoyaApi api;

    public BurpTestingController(MontoyaApi api, String classFilter) {
        this.api = api;

        this.model = new BurpTestingModel(api, classFilter);
        this.view = new BurpTestingView();

        view.getRunButton().addActionListener(e -> runTestsAsync());

        runTestsAsync();
    }

    private void runTestsAsync() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                runTests();
                return null;
            }
        };
        worker.execute();
    }

    private void runTests() {
        view.clearOutput();
        try {
            var summary = model.runTests();
            summary.executions().forEach(exec -> {
                boolean success = exec.result().success();
                String resultText = success ? "PASSED" : "FAILED";
                String line = "Running " + exec.className() + "." + exec.methodName() + " " + resultText;
                logger.info(line);
                view.append(line + "\n");

                if (exec.result().message() != null) {
                    String msg = "  => " + exec.result().message();
                    logger.info(msg);
                    view.append(msg + "\n");
                }

                if (exec.stackTrace() != null) {
                    logger.severe(exec.stackTrace());
                    view.append(exec.stackTrace());
                }
            });

            String sep = "=".repeat(50);
            String summary2 = String.format("\n%s\nRun: %d  Passed: %d  Failed: %d\n%s\n%s\n%s\n",
                    sep, summary.run(), summary.passed(), summary.failed(),
                    sep, summary.failed() == 0 ? "ALL TESTS PASSED" : "TESTS FAILED", sep);
            logger.info(summary2);
            view.append(summary2);

            if (java.awt.GraphicsEnvironment.isHeadless()) {
                api.burpSuite().shutdown();
            }
        } catch (Exception ex) {
            logger.severe("Error running tests: " + ex.getMessage());
            view.append("Error running tests: " + ex.getMessage());

            if (java.awt.GraphicsEnvironment.isHeadless()) {
                api.burpSuite().shutdown();
            }
        }
    }

    public JPanel getPanel() {
        return view;
    }

    public String getCaption() {
        return view.caption();
    }
}
