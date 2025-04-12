package de.yourdomain.testing;

import de.monticore.visitortestdsl._ast.*;
import java.util.stream.Collectors;

public class SwiftStyleTestExporter {

    public static String exportJUnit(ASTVisitorTestFile ast) {
        StringBuilder sb = new StringBuilder();
        sb.append("import org.junit.jupiter.api.*;\n");
        sb.append("import static org.junit.jupiter.api.Assertions.*;\n\n");
        sb.append("public class GeneratedVisitorTests {\n\n");

        for (ASTVisitorTest test : ast.getVisitorTestList()) {
            String type = test.getTypeName();
            sb.append("  @Test\n");
            sb.append("  public void test").append(type).append("Visit() {\n");
            sb.append("    AST").append(type).append(" node = /* mock or real instance */;\n");

            for (ASTAssertion assertion : test.getAssertionList()) {
                sb.append("    ").append(toJavaAssertion(assertion)).append("\n");
            }

            sb.append("  }\n\n");
        }

        sb.append("}\n");
        return sb.toString();
    }

    public static String exportSwiftTest(ASTVisitorTestFile ast) {
        StringBuilder sb = new StringBuilder();
        sb.append("import XCTest\n\n");
        sb.append("final class VisitorTests: XCTestCase {\n\n");

        for (ASTVisitorTest test : ast.getVisitorTestList()) {
            String type = test.getTypeName();
            sb.append("  func test").append(type).append("Visit() {\n");
            sb.append("    let node = /* AST").append(type).append(" instance */\n");

            for (ASTAssertion assertion : test.getAssertionList()) {
                sb.append("    ").append(toSwiftAssertion(assertion)).append("\n");
            }

            sb.append("  }\n\n");
        }

        sb.append("}\n");
        return sb.toString();
    }

    private static String toJavaAssertion(ASTAssertion a) {
        if (a instanceof ASTEquals) {
            ASTEquals eq = (ASTEquals) a;
            return "assertEquals(\"" + eq.getSTRING() + "\", node.get" + capitalize(eq.getFieldRef().getFieldName()) + "());";
        } else if (a instanceof ASTContains) {
            ASTContains c = (ASTContains) a;
            return "assertTrue(node.get" + capitalize(c.getFieldRef().getFieldName()) + "().contains(\"" + c.getSTRING() + "\"));";
        }
        return "// unsupported assertion";
    }

    private static String toSwiftAssertion(ASTAssertion a) {
        if (a instanceof ASTEquals) {
            ASTEquals eq = (ASTEquals) a;
            return "XCTAssertEqual(node." + eq.getFieldRef().getFieldName() + ", \"" + eq.getSTRING() + "\")";
        } else if (a instanceof ASTContains) {
            ASTContains c = (ASTContains) a;
            return "XCTAssertTrue(node." + c.getFieldRef().getFieldName() + ".contains(\"" + c.getSTRING() + "\"))";
        }
        return "// unsupported assertion";
    }

    private static String capitalize(String fieldName) {
        return fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }
}
