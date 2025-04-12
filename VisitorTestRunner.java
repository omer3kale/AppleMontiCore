package de.yourdomain.testing;

import de.monticore.visitortestdsl._parser.VisitorTestDSLParser;
import de.monticore.visitortestdsl._ast.ASTVisitorTestFile;

import java.io.*;
import java.nio.file.*;
import java.util.Optional;

public class VisitorTestRunner {

    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length == 0) {
            System.out.println("Usage: java VisitorTestRunner <file.ui-test | folder>");
            return;
        }

        Path input = Paths.get(args[0]);
        if (!Files.exists(input)) {
            System.err.println("File not found: " + input);
            return;
        }

        if (Files.isDirectory(input)) {
            Files.list(input)
                .filter(p -> p.toString().endsWith(".ui-test"))
                .forEach(p -> {
                    try {
                        processFile(p);
                    } catch (Exception e) {
                        System.err.println("Error processing " + p + ": " + e.getMessage());
                    }
                });
        } else {
            processFile(input);
        }
    }

    private static void processFile(Path input) throws IOException, InterruptedException {
        VisitorTestDSLParser parser = new VisitorTestDSLParser();
        Optional<ASTVisitorTestFile> astOpt = parser.parse(input.toString());

        if (astOpt.isEmpty()) {
            System.err.println("Could not parse test file: " + input);
            return;
        }

        ASTVisitorTestFile ast = astOpt.get();
        String javaTest = SwiftStyleTestExporter.exportJUnit(ast);
        String swiftTest = SwiftStyleTestExporter.exportSwiftTest(ast);

        String baseName = input.getFileName().toString().replace(".ui-test", "");
        Path outputDir = Paths.get("output");
        Path javaOut = outputDir.resolve("Generated" + baseName + "Test.java");
        Path swiftOut = outputDir.resolve(baseName + "Tests.swift");

        Files.createDirectories(outputDir);
        Files.writeString(javaOut, javaTest);
        Files.writeString(swiftOut, swiftTest);

        System.out.println("✅ Generated:");
        System.out.println("- " + javaOut.toAbsolutePath());
        System.out.println("- " + swiftOut.toAbsolutePath());

        // Compile the Java test file
        System.out.println("🛠️ Compiling " + javaOut.getFileName() + "...");
        Process compile = new ProcessBuilder("javac", javaOut.toString())
            .inheritIO()
            .start();
        compile.waitFor();
    }
}
