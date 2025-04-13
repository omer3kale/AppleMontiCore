package de.yourdomain.codegen;

import de.monticore.viewdsl._ast.*;

import java.util.*;

public class RouteExporter {

  public static String exportRoutes(List<ASTViewDef> views) {
    StringBuilder sb = new StringBuilder();
    sb.append("import React from 'react';\n");
    sb.append("import { BrowserRouter, Routes, Route } from 'react-router-dom';\n\n");

    for (ASTViewDef view : views) {
      sb.append("import ").append(view.getName()).append(" from './").append(view.getName()).append("';\n");
    }
    sb.append("import NotFound from './NotFound';\n\n");

    sb.append("export default function PageRouter() {\n");
    sb.append("  return (\n    <BrowserRouter>\n      <Routes>\n");

    for (ASTViewDef view : views) {
      if (view.isPresentViewAttrs()) {
        Optional<String> routePath = view.getViewAttrs().getViewAttrList().stream()
          .filter(attr -> attr.getName().equals("route"))
          .map(attr -> attr.getSTRING().replaceAll("\"", ""))
          .findFirst();

        if (routePath.isPresent()) {
          sb.append("        <Route path=\"")
            .append(routePath.get())
            .append("\" element={<")
            .append(view.getName());

          // Simulate static props for JSX injection if available
          if (view.isPresentPropDef()) {
            sb.append(" ");
            for (ASTPropVar p : view.getPropDef().getPropVarList()) {
              String sample = switch (p.getTypeName()) {
                case "String" -> "\"Sample\"";
                case "Number" -> "42";
                case "Boolean" -> "true";
                default -> "undefined";
              };
              sb.append(p.getName()).append("=").append(sample).append(" ");
            }
          }
          sb.append("/>" + ")" + ";\n");
        }
      }
    }

    sb.append("        <Route path=\"*\" element={<NotFound />} />\n");
    sb.append("      </Routes>\n    </BrowserRouter>\n  );\n}\n");
    return sb.toString();
  }
}
