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
            return "<p>" + ((ASTText) element).getSTRING() + "</p>\n";
        } else if (element instanceof ASTButton) {
            ASTButton btn = (ASTButton) element;
            String onclick = btn.isPresentAction() ? " onclick=\"" + btn.getAction().getSTRING() + "\"" : "";
            return "<button" + onclick + ">" + btn.getSTRING() + "</button>\n";
        }
        return "";
    }
}
