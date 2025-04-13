package de.translation.exporter;

import de.monticore.translationdsl._parser.TranslationDSLParser;
import de.monticore.translationdsl._ast.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class TranslationValidator {

  public static void main(String[] args) throws IOException {
    Path file = Paths.get("src/main/resources/strings.tra");
    TranslationDSLParser parser = new TranslationDSLParser();
    Optional<ASTTranslationFile> optAst = parser.parse(file.toString());

    if (optAst.isEmpty()) {
      System.err.println("❌ Could not parse " + file);
      return;
    }

    ASTTranslationFile ast = optAst.get();
    Map<String, Set<String>> langKeyMap = new HashMap<>();

    for (ASTLangBlock langBlock : ast.getLangBlockList()) {
      String lang = langBlock.getLanguage();
      Set<String> keys = new TreeSet<>();
      for (ASTTranslation t : langBlock.getTranslationList()) {
        keys.add(t.getKey());
      }
      langKeyMap.put(lang, keys);
    }

    // Get full union of all keys
    Set<String> allKeys = new TreeSet<>();
    langKeyMap.values().forEach(allKeys::addAll);

    // Check for missing keys
    for (Map.Entry<String, Set<String>> entry : langKeyMap.entrySet()) {
      String lang = entry.getKey();
      Set<String> langKeys = entry.getValue();
      for (String required : allKeys) {
        if (!langKeys.contains(required)) {
          System.out.println("⚠️  Missing key '" + required + "' in [" + lang + "]");
        }
      }
    }

    System.out.println("✅ Validation complete. All languages: " + langKeyMap.keySet());
  }
}
