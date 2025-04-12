package de.yourdomain.testing;

import de.monticore.visitortestdsl._parser.VisitorTestDSLParser;
import de.monticore.visitortestdsl._ast.ASTVisitorTestFile;

import java.io.*;
import java.nio.file.*;
import java.util.Optional;

public class VisitorTestRunner {

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Usage: java VisitorTestRunner <file.ui-test>");
            return;
        }

        Path input = Paths.get(args[0]);
        if (!Files.exists(input)) {
            System.err.println("File not found: " + input);
            return;
        }

        VisitorTestDSLParser parser = new VisitorTestDSLParser();
        Optional<ASTVisitorTestFile> astOpt = parser.parse(input.toString());

        if (astOpt.isEmpty()) {
            System.err.println("Could not parse test file: " + input);
            return;
        }

        ASTVisitorTestFile ast = astOpt.get();
        String javaTest = SwiftStyleTestExporter.exportJUnit(ast);
        String swiftTest = SwiftStyleTestExporter.exportSwiftTest(ast);

        Path javaOut = Paths.get("output/GeneratedVisitorTests.java");
        Path swiftOut = Paths.get("output/VisitorTests.swift");
        Files.createDirectories(javaOut.getParent());
        Files.writeString(javaOut, javaTest);
        Files.writeString(swiftOut, swiftTest);

        System.out.println("✅ Generated:");
        System.out.println("- " + javaOut.toAbsolutePath());
        System.out.println("- " + swiftOut.toAbsolutePath());
    }
}
