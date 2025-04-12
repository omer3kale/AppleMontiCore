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
            sb.append("    AST").append(type).append(" node = new AST").append(type).append("();\n");

            if (test.isPresentSetup()) {
                for (ASTSetupLine line : test.getSetup().getSetupLineList()) {
                    sb.append("    node.set").append(capitalize(line.getFieldRef().getFieldName())).append("(");
                    if (line.getValue().isPresentSTRING()) {
                        sb.append("\"").append(line.getValue().getSTRING()).append("\");
                    } else if (line.getValue().isPresentNUMBER()) {
                        sb.append(line.getValue().getNUMBER());
                    } else if (line.getValue().isPresentDOUBLE()) {
                        sb.append(line.getValue().getDOUBLE());
                    }
                    sb.append(");\n");
                }
            }

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
            sb.append("    var node = AST").append(type).append("()\n");

            if (test.isPresentSetup()) {
                for (ASTSetupLine line : test.getSetup().getSetupLineList()) {
                    sb.append("    node.").append(line.getFieldRef().getFieldName()).append(" = ");
                    if (line.getValue().isPresentSTRING()) {
                        sb.append("\"").append(line.getValue().getSTRING()).append("\"\n");
                    } else if (line.getValue().isPresentNUMBER()) {
                        sb.append(line.getValue().getNUMBER()).append("\n");
                    } else if (line.getValue().isPresentDOUBLE()) {
                        sb.append(line.getValue().getDOUBLE()).append("\n");
                    }
                }
            }

            for (ASTAssertion assertion : test.getAssertionList()) {
                sb.append("    ").append(toSwiftAssertion(assertion)).append("\n");
            }

            sb.append("  }\n\n");
        }

        sb.append("}\n");
        return sb.toString();
    }

    private static String toJavaAssertion(ASTAssertion a) {
        if (a instanceof ASTEquals eq) {
            return "assertEquals(\"" + eq.getSTRING() + "\", node.get" + capitalize(eq.getFieldRef().getFieldName()) + "());";
        } else if (a instanceof ASTContains c) {
            return "assertTrue(node.get" + capitalize(c.getFieldRef().getFieldName()) + "().contains(\"" + c.getSTRING() + "\"));";
        } else if (a instanceof ASTEmpty em) {
            return "assertTrue(node.get" + capitalize(em.getFieldRef().getFieldName()) + "().isEmpty());";
        } else if (a instanceof ASTNil nil) {
            return "assertNull(node.get" + capitalize(nil.getFieldRef().getFieldName()) + "());";
        } else if (a instanceof ASTGreater gr) {
            return "assertTrue(node.get" + capitalize(gr.getFieldRef().getFieldName()) + "() > " + gr.getNumber() + ");";
        }
        return "// unsupported assertion";
    }

    private static String toSwiftAssertion(ASTAssertion a) {
        if (a instanceof ASTEquals eq) {
            return "XCTAssertEqual(node." + eq.getFieldRef().getFieldName() + ", \"" + eq.getSTRING() + "\")";
        } else if (a instanceof ASTContains c) {
            return "XCTAssertTrue(node." + c.getFieldRef().getFieldName() + ".contains(\"" + c.getSTRING() + "\"))";
        } else if (a instanceof ASTEmpty em) {
            return "XCTAssertTrue(node." + em.getFieldRef().getFieldName() + ".isEmpty)";
        } else if (a instanceof ASTNil nil) {
            return "XCTAssertNil(node." + nil.getFieldRef().getFieldName() + ")";
        } else if (a instanceof ASTGreater gr) {
            return "XCTAssertTrue(node." + gr.getFieldRef().getFieldName() + " > " + gr.getNumber() + ")";
        }
        return "// unsupported assertion";
    }

    private static String capitalize(String fieldName) {
        return fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }
}
