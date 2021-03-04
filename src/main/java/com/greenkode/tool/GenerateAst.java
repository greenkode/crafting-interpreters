package com.greenkode.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class GenerateAst {

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: generate_ast <output directory>");
            System.exit(64);
        }
        var outputDir = args[0];
        defineAst(outputDir, "Expr", Arrays.asList(
                "Assign     : Token name, Expr value",
                "Binary     : Expr left, Token operator, Expr right",
                "Grouping   : Expr expression",
                "Literal    : Object value",
                "Unary      : Token operator, Expr right",
                "Variable   : Token name"
        ));
        defineAst(outputDir, "Stmt", Arrays.asList(
                "Expression     : Expr expression",
                "Print          : Expr expression",
                "Let            : Token name, Expr initializer"
        ));
    }

    private static void defineAst(String outputDir, String baseName, List<String> types) throws IOException {
        var path = outputDir + "/" + baseName + ".java";
        var writer = new PrintWriter(path, StandardCharsets.UTF_8);

        writer.println("package com.greenkode.lox;");
        writer.println();
        writer.println("abstract class " + baseName + " {");

        defineVisitor(writer, baseName, types);

        // The AST classes.
        types.forEach(type -> {
            var className = type.split(":")[0].trim();
            var fields = type.split(":")[1].trim();
            defineType(writer, baseName, className, fields);
        });

        writer.println();
        writer.println("    abstract <R> R accept(Visitor<R> visitor);");

        writer.println("}");

        writer.close();
    }

    private static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
        writer.println("    interface Visitor<R> {");

        types.forEach(type -> {
            var typeName = type.split(":")[0].trim();
            writer.println("    R visit" + typeName + baseName + "(" + typeName + " " + baseName.toLowerCase() + ");");
        });

        writer.println("    }");
    }

    private static void defineType(PrintWriter writer, String baseName, String className, String fieldList) {
        writer.println("    static class " + className + " extends " + baseName + " {");

        // Constructor.
        writer.println("        " + className + "(" + fieldList + ") {");

        // Store parameters in fields
        var fields = fieldList.split(", ");
        Arrays.stream(fields).map(field -> field.split(" ")[1])
                .map(name -> "          this." + name + " = " + name + ";").forEach(writer::println);
        writer.println("        }");


        // Visitor pattern.
        writer.println();
        writer.println("    @Override");
        writer.println("    <R> R accept(Visitor<R> visitor) {");
        writer.println("        return visitor.visit" + className + baseName + "(this);");
        writer.println("        }");

        // Fields.
        writer.println();
        Arrays.stream(fields).map(field -> "        final " + field + ";").forEach(writer::println);
        writer.println("    }");
    }
}
