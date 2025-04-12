package de.yourdomain.frontend;

import de.monticore.ui._ast.*;
import java.util.stream.Collectors;

public class HTMLExporter {

    public static String generateHTML(ASTUI ui) {
        StringBuilder body = new StringBuilder();
        for (ASTUIElement element : ui.getUIElementList()) {
            body.append(toHTML(element));
        }
        return wrapInHTMLTemplate(body.toString());
    }

    private static String toHTML(ASTUIElement element) {
        if (element instanceof ASTVStack) {
            return "<div style=\"display:flex;flex-direction:column;gap:1em;\">\n" +
                ((ASTVStack) element).getUIElementList().stream()
                    .map(HTMLExporter::toHTML)
                    .collect(Collectors.joining()) +
                "</div>\n";

        } else if (element instanceof ASTText) {
            return "<p>" + ((ASTText) element).getSTRING().replace("\"", "") + "</p>\n";

        } else if (element instanceof ASTButton) {
            ASTButton btn = (ASTButton) element;
            String onclick = btn.isPresentAction() ? 
                " onclick=\"" + btn.getAction().getSTRING().replace("\"", "") + "\"" : "";
            return "<button style=\"padding: 0.5em 1em;\"" + onclick + ">" + btn.getSTRING().replace("\"", "") + "</button>\n";
        }

        return "<!-- Unknown element -->\n";
    }

    private static String wrapInHTMLTemplate(String bodyContent) {
        return "<!DOCTYPE html>\n" +
               "<html lang=\"en\">\n" +
               "<head>\n" +
               "  <meta charset=\"UTF-8\">\n" +
               "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
               "  <title>Generated Page</title>\n" +
               "  <style>\n" +
               "    body { font-family: -apple-system, BlinkMacSystemFont, sans-serif; padding: 2em; }\n" +
               "    button { cursor: pointer; background-color: #007BFF; color: white; border: none; border-radius: 5px; }\n" +
               "    p { margin: 0; }\n" +
               "  </style>\n" +
               "</head>\n" +
               "<body>\n" + bodyContent +
               "</body>\n" +
               "</html>";
    }
} 
