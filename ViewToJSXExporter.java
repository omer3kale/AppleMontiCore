package de.yourdomain.codegen;

import de.monticore.viewdsl._ast.*;

import java.util.*;

public class ViewToJSXExporter {

  public static String exportView(ASTViewDef view) {
    StringBuilder sb = new StringBuilder();
    sb.append("import React from 'react';\n\n");
    sb.append("export default function ").append(view.getName()).append("() {\n");
    sb.append("  return (\n    <div>\n");

    for (ASTComponent comp : view.getComponentList()) {
      sb.append("      ").append(exportComponent(comp)).append("\n");
    }

    sb.append("    </div>\n  );\n}\n");
    return sb.toString();
  }

  public static String exportComponent(ASTComponent comp) {
    if (comp instanceof ASTHtmlElement e) return toJSX(e);
    if (comp instanceof ASTHtmlVoid v) return toJSX(v);
    if (comp instanceof ASTText t) return "<p>" + t.getSTRING() + "</p>";
    if (comp instanceof ASTImage i) return "<img src=\"" + i.getSTRINGList().get(0) + "\" alt=\"" + i.getSTRINGList().get(1) + "\" />";
    if (comp instanceof ASTButton b) return "<button onClick={() => " + b.getActionBlock().getSTRING() + "}>" + b.getSTRING() + "</button>";
    if (comp instanceof ASTTextField tf) return "<input name=\"" + tf.getName() + "\" placeholder=\"" + tf.getSTRING() + "\" />";
    if (comp instanceof ASTNavigationLink nl) return "<a href=\"" + nl.getTarget() + "\">" + nl.getLabel() + "</a>";
    if (comp instanceof ASTVStack vs) return wrapBlock("div", "flex flex-col gap-2", vs.getComponentList());
    if (comp instanceof ASTHStack hs) return wrapBlock("div", "flex flex-row gap-2", hs.getComponentList());
    return "";
  }

  private static String wrapBlock(String tag, String className, List<ASTComponent> children) {
    StringBuilder sb = new StringBuilder();
    sb.append("<").append(tag).append(" className=\"").append(className).append("\">");
    for (ASTComponent c : children) {
      sb.append(exportComponent(c));
    }
    sb.append("</").append(tag).append(">");
    return sb.toString();
  }

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
