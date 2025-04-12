package de.yourdomain.codegen;

import de.monticore.viewdsl._ast.*;

public class ViewToJSXExporter {

  public static String toJSX(ASTHtmlElement element) {
    StringBuilder sb = new StringBuilder();
    sb.append("<").append(element.getTagName());
    if (element.isPresentHtmlAttrs()) {
      for (ASTHtmlAttr attr : element.getHtmlAttrs().getHtmlAttrList()) {
        sb.append(" ").append(attr.getName()).append("=\"").append(attr.getSTRING()).append("\"");
      }
    }
    sb.append(">");
    if (element.isPresentHtmlContent()) {
      sb.append(element.getHtmlContent().getSTRING());
    }
    sb.append("</").append(element.getTagName()).append(">");
    return sb.toString();
  }

  public static String toJSX(ASTHtmlVoid element) {
    StringBuilder sb = new StringBuilder();
    sb.append("<").append(element.getTagName());
    if (element.isPresentHtmlAttrs()) {
      for (ASTHtmlAttr attr : element.getHtmlAttrs().getHtmlAttrList()) {
        sb.append(" ").append(attr.getName()).append("=\"").append(attr.getSTRING()).append("\"");
      }
    }
    sb.append(" />");
    return sb.toString();
  }
}
