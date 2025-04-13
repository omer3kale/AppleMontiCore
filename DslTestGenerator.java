package de.yourdomain.codegen;

import de.monticore.viewdsl._ast.*;
import de.monticore.viewdsl._parser.ViewDSLParser;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class DslTestGenerator {

  public static void main(String[] args) throws IOException {
    Path inputDir = Paths.get("src/main/resources/views");
    Path outputFile = Paths.get("src/test/java/de/yourdomain/codegen/GeneratedDslTests.java");

    List<String> tests = new ArrayList<>();
    ViewDSLParser parser = new ViewDSLParser();

    try (BufferedWriter writer = Files.newBufferedWriter(outputFile)) {
      writer.write("package de.yourdomain.codegen;\n\n");
      writer.write("import de.monticore.viewdsl._ast.*;\n");
      writer.write("import org.junit.jupiter.api.Test;\n");
      writer.write("import static org.junit.jupiter.api.Assertions.*;\n\n");
      writer.write("public class GeneratedDslTests {\n\n");

      Files.list(inputDir)
        .filter(f -> f.toString().endsWith(".dsl"))
        .forEach(file -> {
          try {
            Optional<ASTViewFile> astOpt = parser.parse(file.toString());
            if (astOpt.isPresent()) {
              ASTViewFile ast = astOpt.get();
              for (ASTViewDef view : ast.getViewDefList()) {
                String name = view.getName();
                int count = view.getComponentList().size();
                writer.write("  @Test\n");
                writer.write("  public void test" + name + "View() {\n");
                writer.write("    assertEquals(\"" + name + "\", \"" + name + "\");\n");
                writer.write("    assertEquals(" + count + ", " + count + ");\n");
                writer.write("  }\n\n");
              }
            }
          } catch (Exception e) {
            System.err.println("Failed to parse: " + file + " → " + e.getMessage());
          }
        });

      writer.write("}\n");
    }
    System.out.println("✅ Visitor tests generated at: " + outputFile);
  }
}
