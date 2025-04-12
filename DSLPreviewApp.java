import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.nio.file.Files;
import java.nio.file.Path;

public class DSLPreviewApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        WebView webView = new WebView();

        String html = Files.readString(Path.of("output/HomePage.html"));
        webView.getEngine().loadContent(html, "text/html");

        stage.setTitle("DSL Live Preview");
        stage.setScene(new Scene(webView, 800, 600));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
