package de.yourdomain.frontend;

import de.monticore.ui._ast.*;

import java.util.stream.Collectors;

public class JSXExporter {

    public static String generateJSX(ASTUI ast) {
        StringBuilder out = new StringBuilder();
        out.append("export default function HomePage() {\n  return (\n    ");
        out.append(toJSXBody(ast));
        out.append("\n  );\n}");
        return out.toString();
    }

    private static String toJSXBody(ASTUI ui) {
        return ui.getUIElementList().stream()
            .map(JSXExporter::toJSX)
            .collect(Collectors.joining("\n    "));
    }

    private static String toJSX(ASTUIElement el) {
        if (el instanceof ASTVStack) {
            String children = ((ASTVStack) el).getUIElementList().stream()
                .map(JSXExporter::toJSX)
                .collect(Collectors.joining("\n      "));
            return "<div style={{ display: \"flex\", flexDirection: \"column\" }}>\n      " + children + "\n    </div>";
        }

        if (el instanceof ASTText) {
            return "<p>" + ((ASTText) el).getSTRING().replace("\"", "") + "</p>";
        }

        if (el instanceof ASTButton) {
            ASTButton btn = (ASTButton) el;
            String label = btn.getSTRING().replace("\"", "");
            String handler = btn.isPresentAction()
                ? " onClick={() => " + btn.getAction().getSTRING().replace("\"", "") + "}"
                : "";
            return "<button" + handler + ">" + label + "</button>";
        }

        return "";
    }
}
