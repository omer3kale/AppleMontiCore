package de.yourdomain.preview;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class DSLPreviewApp extends Application {

    private static final String[] PAGES = {"HomePage", "Contact"};
    private static final Path DSL_DIR = Path.of("dsl/models/");
    private static final Path HTML_DIR = Path.of("output/");
    private static final Path ERRORS_PATH = Path.of("output/ValidationErrors.swift");

    private final Map<String, TextArea> dslEditors = new HashMap<>();
    private WebView webView;
    private TextArea errorViewer;
    private TabPane editorTabs;

    @Override
    public void start(Stage stage) {
        webView = new WebView();
        errorViewer = new TextArea();
        errorViewer.setEditable(false);
        errorViewer.setStyle("-fx-font-family: monospace;");

        editorTabs = new TabPane();
        for (String page : PAGES) {
            TextArea editor = new TextArea();
            editor.setStyle("-fx-font-family: monospace;");
            editor.setWrapText(true);
            editor.setPrefWidth(400);
            dslEditors.put(page, editor);
            loadDSLSource(page);
            Tab tab = new Tab(page, editor);
            editorTabs.getTabs().add(tab);

            editor.textProperty().addListener((obs, oldText, newText) -> saveDSLAndUpdate(page));
        }

        SplitPane rightPane = new SplitPane();
        rightPane.setOrientation(javafx.geometry.Orientation.VERTICAL);
        rightPane.getItems().addAll(webView, errorViewer);
        rightPane.setDividerPositions(0.75);

        SplitPane rootPane = new SplitPane();
        rootPane.getItems().addAll(editorTabs, rightPane);
        rootPane.setDividerPositions(0.35);
        rootPane.setPadding(new Insets(10));

        stage.setTitle("DSL Tabbed Editor + Live Preview");
        stage.setScene(new Scene(rootPane, 1200, 700));
        stage.show();

        runMontiCoreParser(PAGES[0]);
        loadHtml(PAGES[0]);
        loadErrors();

        editorTabs.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            String page = newTab.getText();
            runMontiCoreParser(page);
            loadHtml(page);
        });
    }

    private void loadDSLSource(String page) {
        Path path = DSL_DIR.resolve(page + ".ui");
        try {
            String code = Files.readString(path);
            dslEditors.get(page).setText(code);
        } catch (IOException e) {
            dslEditors.get(page).setText("View {\n  VStack {\n    Text(\"Welcome\")\n  }\n}\n");
        }
    }

    private void saveDSLAndUpdate(String page) {
        Path path = DSL_DIR.resolve(page + ".ui");
        try {
            Files.writeString(path, dslEditors.get(page).getText());
            runMontiCoreParser(page);
            loadHtml(page);
            loadErrors();
        } catch (IOException e) {
            errorViewer.setText("❌ Failed to write .ui file: " + e.getMessage());
        }
    }

    private void loadHtml(String page) {
        Path htmlPath = HTML_DIR.resolve(page + ".html");
        try {
            String html = Files.readString(htmlPath);
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

    private void runMontiCoreParser(String page) {
        Path dslPath = DSL_DIR.resolve(page + ".ui");
        Path htmlPath = HTML_DIR.resolve(page + ".html");
        try {
            ProcessBuilder pb = new ProcessBuilder(
                "java", "-jar", "src/tools/monticore-cli.jar",
                "-g", "dsl/grammars/FrontendDSL.mc4",
                "-i", dslPath.toString(),
                "-o", htmlPath.toString()
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
