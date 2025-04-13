package de.translation.visitor;

import de.monticore.translationdsl._ast.*;
import de.monticore.translationdsl._visitor.TranslationDSLVisitor2;

import java.util.LinkedHashMap;
import java.util.Map;

public class TranslationVisitor implements TranslationDSLVisitor2 {

  private final Map<String, Map<String, String>> translations = new LinkedHashMap<>();
  private String currentLang = "";

  public Map<String, Map<String, String>> getTranslations() {
    return translations;
  }

  @Override
  public void visit(ASTLangBlock node) {
    currentLang = node.getLanguage();
    translations.putIfAbsent(currentLang, new LinkedHashMap<>());
  }

  @Override
  public void visit(ASTTranslation node) {
    translations.get(currentLang).put(
      node.getKey(),
      node.getSTRING().replaceAll("^\"|\"$", "")
    );
  }

  @Override
  public void endVisit(ASTLangBlock node) {
    currentLang = "";
  }
}
