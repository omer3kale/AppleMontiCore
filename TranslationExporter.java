package de.translation.exporter;

import de.monticore.translationdsl._parser.TranslationDSLParser;
import de.monticore.translationdsl._ast.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class TranslationExporter {

  public static void main(String[] args) throws IOException {
    Path inputFile = Paths.get("src/main/resources/strings.tra");
    Path outputDir = Paths.get("exported");
    Files.createDirectories(outputDir);

    TranslationDSLParser parser = new TranslationDSLParser();
    Optional<ASTTranslationFile> optAst = parser.parse(inputFile.toString());

    if (optAst.isEmpty()) {
      System.err.println("❌ Could not parse file: " + inputFile);
      return;
    }

    ASTTranslationFile ast = optAst.get();

    for (ASTLangBlock langBlock : ast.getLangBlockList()) {
      String lang = langBlock.getLanguage();
      Map<String, String> translations = new LinkedHashMap<>();

      for (ASTTranslation t : langBlock.getTranslationList()) {
        translations.put(t.getKey(), t.getSTRING().replaceAll("^\"|\"$", ""));
      }

      // Export JSON
      Path jsonFile = outputDir.resolve(lang + ".json");
      try (BufferedWriter writer = Files.newBufferedWriter(jsonFile)) {
        writer.write("{\n");
        int i = 0;
        for (Map.Entry<String, String> entry : translations.entrySet()) {
          writer.write("  \"" + entry.getKey() + "\": \"" + entry.getValue() + "\"");
          if (i++ < translations.size() - 1) writer.write(",");
          writer.write("\n");
        }
        writer.write("}\n");
      }

      // Export .strings (iOS)
      Path stringsFile = outputDir.resolve(lang + ".strings");
      try (BufferedWriter writer = Files.newBufferedWriter(stringsFile)) {
        for (Map.Entry<String, String> entry : translations.entrySet()) {
          writer.write("\"" + entry.getKey() + "\" = \"" + entry.getValue() + "\";\n");
        }
      }

      System.out.println("✅ Exported " + lang + " → " + jsonFile + ", " + stringsFile);
    }
  }
}
