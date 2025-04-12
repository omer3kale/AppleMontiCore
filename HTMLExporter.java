package de.yourdomain.frontend;

import de.monticore.ui._ast.*;
import java.util.stream.Collectors;

public class HTMLExporter {

    public static String generateHTML(ASTUI ui) {
        StringBuilder out = new StringBuilder();
        for (ASTUIElement element : ui.getUIElementList()) {
            out.append(toHTML(element));
        }
        return out.toString();
    }

    private static String toHTML(ASTUIElement element) {
        if (element instanceof ASTVStack) {
            return "<div style=\"display:flex;flex-direction:column;\">\n" +
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
            return "<button" + onclick + ">" + btn.getSTRING().replace("\"", "") + "</button>\n";
        }

        return "<!-- Unknown element -->\n";
    }
} 

