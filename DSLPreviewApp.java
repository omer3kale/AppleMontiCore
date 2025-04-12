package de.yourdomain.preview;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
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
    private TextArea dslEditor;

    @Override
    public void start(Stage stage) throws Exception {
        webView = new WebView();

        dslEditor = new TextArea();
        dslEditor.setStyle("-fx-font-family: monospace;");
        dslEditor.setWrapText(true);
        dslEditor.setPrefWidth(400);
        loadDSLSource();

        errorViewer = new TextArea();
        errorViewer.setEditable(false);
        errorViewer.setStyle("-fx-font-family: monospace;");

        SplitPane rightPane = new SplitPane();
        rightPane.setOrientation(javafx.geometry.Orientation.VERTICAL);
        rightPane.getItems().addAll(webView, errorViewer);
        rightPane.setDividerPositions(0.75);

        SplitPane rootPane = new SplitPane();
        rootPane.getItems().addAll(dslEditor, rightPane);
        rootPane.setDividerPositions(0.35);
        rootPane.setPadding(new Insets(10));

        dslEditor.textProperty().addListener((obs, oldText, newText) -> saveDSLAndUpdate());

        stage.setTitle("DSL Editor + Live Preview + Errors");
        stage.setScene(new Scene(rootPane, 1200, 700));
        stage.show();

        runMontiCoreParser();
        loadHtml();
        loadErrors();
    }

    private void loadDSLSource() {
        try {
            String code = Files.readString(DSL_PATH);
            dslEditor.setText(code);
        } catch (IOException e) {
            dslEditor.setText("View {\n  VStack {\n    Text(\"Hello\")\n  }\n}\n");
        }
    }

    private void saveDSLAndUpdate() {
        try {
            Files.writeString(DSL_PATH, dslEditor.getText());
            runMontiCoreParser();
            loadHtml();
            loadErrors();
        } catch (IOException e) {
            errorViewer.setText("❌ Failed to write .ui file: " + e.getMessage());
        }
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

    public static void main(String[] args) {
        launch();
    }
}
