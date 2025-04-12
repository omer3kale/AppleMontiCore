public class SwiftStyleTestExporter {

    public static String exportJUnit(ASTVisitorTestFile ast) {
        StringBuilder sb = new StringBuilder();
        sb.append("import org.junit.jupiter.api.*;\n");
        sb.append("import static org.junit.jupiter.api.Assertions.*;\n\n");
        sb.append("public class GeneratedVisitorTests {\n");

        for (ASTVisitorTest test : ast.getVisitorTestList()) {
            String type = test.getTypeName();
            sb.append("  @Test\n  public void test").append(type).append("Visit() {\n");
            sb.append("    AST").append(type).append(" node = /* mock or parse */;\n");
            for (ASTAssertion assertion : test.getAssertionList()) {
                sb.append("    ").append(toJavaAssertion(assertion)).append("\n");
            }
            sb.append("  }\n\n");
        }

        sb.append("}");
        return sb.toString();
    }

    private static String toJavaAssertion(ASTAssertion a) {
        if (a instanceof ASTEquals) {
            ASTEquals eq = (ASTEquals) a;
            return "assertEquals(\"" + eq.getSTRING() + "\", node." + eq.getFieldRef().getFieldName() + "());";
        } else if (a instanceof ASTContains) {
            ASTContains c = (ASTContains) a;
            return "assertTrue(node." + c.getFieldRef().getFieldName() + "().contains(\"" + c.getSTRING() + "\"));";
        }
        return "// unknown assertion";
    }
}
