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
                "Binary     : Expr left, Token operator, Expr right",
                "Grouping   : Expr expression",
                "Literal    : Object value",
                "Unary      : Token operator, Expr right"
        ));
    }

    private static void defineAst(String outputDir, String baseName, List<String> types) throws IOException {
        var path = outputDir + "/" + baseName + ".java";
        var writer = new PrintWriter(path, StandardCharsets.UTF_8);

        writer.println("package com.greenkode.lox;");
        writer.println();
        writer.println("abstract class " + baseName + " {");

        // The AST classes.
        types.forEach(type -> {
            var className = type.split(":")[0].trim();
            var fields = type.split(":")[1].trim();
            defineType(writer, baseName, className, fields);
        });

        writer.println("}");
        writer.close();
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

        // Fields.
        writer.println();
        Arrays.stream(fields).map(field -> "        final " + field + ";").forEach(writer::println);
        writer.println("    }");
    }
}
