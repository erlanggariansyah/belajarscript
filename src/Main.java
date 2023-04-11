import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static final ExceptionHandler exceptionHandler = new ExceptionHandler();
    private static final String OUTPUT_CLASS_NAME = "BelajarScript";
    private static final String MAIN_METHOD_NAME = "main";
    private static final String FILE_EXTENSION = ".bs";

    public static void main(String[] args) throws Exception {
        Thread.setDefaultUncaughtExceptionHandler(exceptionHandler);
        Scanner scanner = new Scanner(System.in);
        System.out.println("Masukkan file bs (*.bs):");
        String path = scanner.nextLine();
        compileAndRun(path);
    }

    private static void compileAndRun(String filePath) throws Exception {
        if (!filePath.endsWith(FILE_EXTENSION)) {
            throw new Exception("File bs tidak valid.");
        }

        String code = readCodeFromFile(filePath);
        Map<String, String> dictionary = createDictionary();

        for (Map.Entry<String, String> entry : dictionary.entrySet()) {
            code = code.replaceAll("(?i)\\b" + entry.getKey() + "\\b", entry.getValue());
        }

        code = formatCode(code);
        compileAndExecuteCode(code);
    }

    private static String readCodeFromFile(String filePath) throws IOException {
        StringBuilder code = new StringBuilder();
        File file = new File(filePath);
        Scanner scanner = new Scanner(file);

        while (scanner.hasNext()) {
            code.append(scanner.nextLine());
        }

        scanner.close();
        return code.toString();
    }

    private static Map<String, String> createDictionary() {
        Map<String, String> dictionary = new LinkedHashMap<>();

        dictionary.put("Bit", "byte");
        dictionary.put("Nomor", "short");
        dictionary.put("NomorSedang", "int");
        dictionary.put("NomorBesar", "long");
        dictionary.put("NomorFloat", "float");
        dictionary.put("NomorDouble", "double");
        dictionary.put("Karakter", "char");
        dictionary.put("Kalimat", "String");

        dictionary.put("jika", "if");
        dictionary.put("lantas", "else");
        dictionary.put("jika lagi", "else if");

        dictionary.put("cetak", "System.out.println");

        dictionary.put("privat", "private");
        dictionary.put("proteksi", "protected");
        dictionary.put("publik", "public");

        dictionary.put("ketika", "while");
        dictionary.put("ulangi", "for");

        dictionary.put("coba", "try");
        dictionary.put("tangkap", "catch");
        dictionary.put("akhir", "finally");
        dictionary.put("konstan", "final");
        dictionary.put("lepas", "throw");
        dictionary.put("lepaskan", "throws");

        return dictionary;
    }

    private static String formatCode(String code) {
        code = code.trim().replaceAll(" +", " ");
        code = code.replaceAll("\\R", " ");
        code = code.replace(";", ";\n");

        return code;
    }

    private static void compileAndExecuteCode(String code) throws Exception {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        File outputFile = new File("src/output/" + OUTPUT_CLASS_NAME + ".java");

        try (PrintWriter printWriter = new PrintWriter(outputFile)) {
            printWriter.println("package output; public class " + OUTPUT_CLASS_NAME + " { public static void " + MAIN_METHOD_NAME + "(String[] args) { " + code + " } }");
        }

        Iterable<? extends JavaFileObject> fileObjects = fileManager.getJavaFileObjects(outputFile);
        if (!compiler.getTask(null, fileManager, null, null, null, fileObjects).call()) {
            throw new Exception("Kesalahan saat kompilasi.");
        }

        URL[] urls = new URL[]{new File("src/output").toURI().toURL()};
        URLClassLoader urlClassLoader = new URLClassLoader(urls);
        Object belajarScript = urlClassLoader.loadClass("output." + OUTPUT_CLASS_NAME).getDeclaredConstructor().newInstance();
        belajarScript.getClass().getMethod(MAIN_METHOD_NAME, String[].class).invoke(belajarScript, new Object[] { null });
    }
}
