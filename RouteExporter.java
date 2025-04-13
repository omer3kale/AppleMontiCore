package de.yourdomain.codegen;

import de.monticore.viewdsl._ast.*;

import java.util.*;

public class RouteExporter {

  public static String exportRoutes(List<ASTViewDef> views) {
    StringBuilder sb = new StringBuilder();
    sb.append("import React from 'react';\n");
    sb.append("import { BrowserRouter, Routes, Route } from 'react-router-dom';\n\n");

    // import components
    for (ASTViewDef view : views) {
      sb.append("import ").append(view.getName()).append(" from './").append(view.getName()).append("';\n");
    }
    sb.append("import NotFound from './NotFound';\n\n");

    sb.append("export default function PageRouter() {\n");
    sb.append("  return (\n    <BrowserRouter>\n      <Routes>\n");

    for (ASTViewDef view : views) {
      if (view.isPresentViewAttrs()) {
        for (ASTViewAttr attr : view.getViewAttrs().getViewAttrList()) {
          if (attr.getName().equals("route")) {
            sb.append("        <Route path=\"")
              .append(attr.getSTRING().replaceAll("\"", ""))
              .append("\" element={<")
              .append(view.getName());
            if (view.isPresentPropDef()) {
              sb.append(" ");
              for (ASTPropVar p : view.getPropDef().getPropVarList()) {
                sb.append(p.getName()).append("=\"TODO\" ");
              }
            }
            sb.append("/>" + ")" + ";\n");
          }
        }
      }
    }

    sb.append("        <Route path=\"*\" element={<NotFound />} />\n");
    sb.append("      </Routes>\n    </BrowserRouter>\n  );\n}\n");
    return sb.toString();
  }
}
