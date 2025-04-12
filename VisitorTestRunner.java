package de.yourdomain.testing;

import de.monticore.visitortestdsl._parser.VisitorTestDSLParser;
import de.monticore.visitortestdsl._ast.ASTVisitorTestFile;

import java.io.*;
import java.nio.file.*;
import java.util.Optional;

public class VisitorTestRunner {

    public static void main(String[] args) throws IOException, InterruptedException {
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

        Path outputDir = Paths.get("output");
        Path javaOut = outputDir.resolve("GeneratedVisitorTests.java");
        Path swiftOut = outputDir.resolve("VisitorTests.swift");

        Files.createDirectories(outputDir);
        Files.writeString(javaOut, javaTest);
        Files.writeString(swiftOut, swiftTest);

        System.out.println("✅ Generated:");
        System.out.println("- " + javaOut.toAbsolutePath());
        System.out.println("- " + swiftOut.toAbsolutePath());

        // Compile the Java test file
        System.out.println("🛠️ Compiling Java test...");
        Process compile = new ProcessBuilder("javac", javaOut.toString())
            .inheritIO()
            .start();
        compile.waitFor();

        // Run test using junit-platform-console if available
        if (Files.exists(Paths.get("output/GeneratedVisitorTests.class"))) {
            System.out.println("🚀 Running tests:");
            new ProcessBuilder("java", "-cp", "output:.", "org.junit.platform.console.ConsoleLauncher",
                    "--scan-classpath", "output",
                    "--include-classname", ".*GeneratedVisitorTests")
                .inheritIO()
                .start()
                .waitFor();
        } else {
            System.out.println("⚠️ Skipped test run (class not found)");
        }
    }
}
