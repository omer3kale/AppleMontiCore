package de.yourdomain.preview;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.*;

public class DSLPreviewApp extends Application {

    private static final Path HTML_PATH = Path.of("output/HomePage.html");
    private WebView webView;

    @Override
    public void start(Stage stage) throws Exception {
        webView = new WebView();
        loadHtml();

        stage.setTitle("DSL Live Preview");
        stage.setScene(new Scene(webView, 800, 600));
        stage.show();

        watchHtmlChanges();
    }

    private void loadHtml() {
        try {
            String html = Files.readString(HTML_PATH);
            webView.getEngine().loadContent(html, "text/html");
        } catch (IOException e) {
            webView.getEngine().loadContent("<h1>Could not load HTML</h1><p>" + e.getMessage() + "</p>");
        }
    }

    private void watchHtmlChanges() throws IOException {
        WatchService watchService = FileSystems.getDefault().newWatchService();
        Path dir = HTML_PATH.getParent();
        dir.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

        Thread watcherThread = new Thread(() -> {
            while (true) {
                try {
                    WatchKey key = watchService.take();
                    for (WatchEvent<?> event : key.pollEvents()) {
                        Path changed = dir.resolve((Path) event.context());
                        if (changed.endsWith(HTML_PATH.getFileName())) {
                            Thread.sleep(100); // slight delay for file write completion
                            javafx.application.Platform.runLater(this::loadHtml);
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
