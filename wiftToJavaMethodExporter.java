package de.yourdomain.codegen;

import de.monticore.swiftstyledsl._ast.*;

public class SwiftToJavaMethodExporter {

    public static String toJavaMethod(ASTSwiftMethod method) {
        StringBuilder sb = new StringBuilder();

        // Return type
        String returnType = method.isPresentReturnType()
                ? method.getReturnType().getTypeName()
                : "void";

        sb.append("public static ").append(returnType).append(" ")
          .append(method.getMethodName()).append("(");

        // Parameters
        if (method.isPresentParameters()) {
            var params = method.getParameters().getParameterList();
            for (int i = 0; i < params.size(); i++) {
                var p = params.get(i);
                sb.append(p.getTypeName()).append(" ").append(p.getIdentifier());
                if (i < params.size() - 1) sb.append(", ");
            }
        }
        sb.append(") {\n");

        // Body
        for (ASTStatement stmt : method.getBody().getStatementList()) {
            sb.append("  ").append(toJavaStatement(stmt)).append("\n");
        }

        sb.append("}\n");
        return sb.toString();
    }

    private static String toJavaStatement(ASTStatement stmt) {
        if (stmt instanceof ASTReturnStmt r) {
            return "return " + toExpr(r.getExpression()) + ";";
        } else if (stmt instanceof ASTCallStmt c) {
            StringBuilder call = new StringBuilder();
            call.append(c.getIdentifier()).append("(");
            var args = c.getExpressionList();
            for (int i = 0; i < args.size(); i++) {
                call.append(toExpr(args.get(i)));
                if (i < args.size() - 1) call.append(", ");
            }
            call.append(");");
            return call.toString();
        } else if (stmt instanceof ASTDeclarationStmt d) {
            return d.getTypeName() + " " + d.getIdentifier() + " = " + toExpr(d.getExpression()) + ";";
        }
        return "// unknown statement";
    }

    private static String toExpr(ASTExpression expr) {
        if (expr instanceof ASTLiteral l) return l.getValue();
        if (expr instanceof ASTIdentifier id) return id.getName();
        if (expr instanceof ASTBoolExpr b) {
            return b.getIdentifier() + " " + b.getOp() + " " + b.getLiteral().getValue();
        }
        return "/* unknown expr */";
    }
}
