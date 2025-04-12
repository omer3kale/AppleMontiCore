package de.yourdomain.preview;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.*;

public class DSLPreviewApp extends Application {

    private static final Path HTML_PATH = Path.of("output/HomePage.html");
    private static final Path DSL_PATH = Path.of("dsl/models/HomePage.ui");
    private static final Path ERRORS_PATH = Path.of("output/ValidationErrors.swift");

    private WebView webView;
    private TextArea errorViewer;

    @Override
    public void start(Stage stage) throws Exception {
        webView = new WebView();
        errorViewer = new TextArea();
        errorViewer.setEditable(false);
        errorViewer.setStyle("-fx-font-family: monospace;");

        runMontiCoreParser();
        loadHtml();
        loadErrors();

        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(webView, errorViewer);
        splitPane.setDividerPositions(0.7);
        splitPane.setPadding(new Insets(10));

        stage.setTitle("DSL Live Preview + Errors");
        stage.setScene(new Scene(splitPane, 1000, 600));
        stage.show();

        watchDslChanges();
    }

    private void loadHtml() {
        try {
            String html = Files.readString(HTML_PATH);
            webView.getEngine().loadContent(html, "text/html");
        } catch (IOException e) {
            webView.getEngine().loadContent("<h1>Could not load HTML</h1><p>" + e.getMessage() + "</p>");
        }
    }

    private void loadErrors() {
        try {
            String errors = Files.readString(ERRORS_PATH);
            errorViewer.setText(errors);
        } catch (IOException e) {
            errorViewer.setText("No ValidationErrors.swift found\n" + e.getMessage());
        }
    }

    private void runMontiCoreParser() {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                "java", "-jar", "src/tools/monticore-cli.jar",
                "-g", "dsl/grammars/FrontendDSL.mc4",
                "-i", DSL_PATH.toString(),
                "-o", HTML_PATH.toString()
            );
            pb.inheritIO().start().waitFor();
        } catch (Exception e) {
            System.err.println("Failed to run MontiCore parser: " + e.getMessage());
        }
    }

    private void watchDslChanges() throws IOException {
        WatchService watchService = FileSystems.getDefault().newWatchService();
        Path dir = DSL_PATH.getParent();
        dir.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

        Thread watcherThread = new Thread(() -> {
            while (true) {
                try {
                    WatchKey key = watchService.take();
                    for (WatchEvent<?> event : key.pollEvents()) {
                        Path changed = dir.resolve((Path) event.context());
                        if (changed.endsWith(DSL_PATH.getFileName())) {
                            Thread.sleep(200); // allow write to finish
                            runMontiCoreParser();
                            javafx.application.Platform.runLater(() -> {
                                loadHtml();
                                loadErrors();
                            });
                        }
                    }
                    key.reset();
                } catch (Exception ignored) {
                }
            }
        });
        watcherThread.setDaemon(true);
        watcherThread.start();
    }

    public static void main(String[] args) {
        launch();
    }
}
