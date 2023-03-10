package yanghgri;

import com.sun.jna.platform.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static final FileUtils fileUtils = FileUtils.getInstance();
    public static final List<String> commandList = new ArrayList<>();

    static {
        commandList.add("C:\\Program Files\\7-Zip\\7z.exe");
        commandList.add("a");
        commandList.add("-tzip");
        commandList.add("");
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("请输入开始路径");
        System.out.println("默认：D:\\MediaLib\\エロ视界");
        Scanner scanner = new Scanner(System.in);
        File startPath = new File(scanner.next());
        if (startPath.isDirectory()) {
            walkFileTree(startPath);
        } else {
            System.out.println("请输入目录而非文件");
        }
        scanner.nextLine();
        System.out.println("摁下回车结束程序");
        scanner.nextLine();
    }

    private static void walkFileTree(File file) throws IOException, InterruptedException {
        File[] fs = file.listFiles();
        assert fs != null;
        int imageCounter = 0;
        for (File f : fs) {
            if (!f.getName().startsWith("'") || !f.getName().endsWith("'")) {
                if (f.isDirectory()) {
                    walkFileTree(f);
                } else if (f.isFile() && (f.getName().endsWith(".jpg") || f.getName().endsWith(".jpeg") || f.getName().endsWith(".png") || f.getName().endsWith(".webp"))) {
                    imageCounter++;
                }
            }
        }
        if (fs.length == imageCounter && imageCounter != 0) {
            String chapterPath = file.getAbsolutePath();
            // 在字号为 12 磅的 windows terminal 客户端中正好占满一行的 "-"
            System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------");
            System.out.println("章节目录：" + chapterPath);
            startArchive(file);
            System.out.println("\n放入回收站：" + chapterPath);
            fileUtils.moveToTrash(file);
        }
    }

    public static void startArchive(File workPath) throws IOException, InterruptedException {
        commandList.set(3, workPath.getAbsolutePath() + ".cbz");
        ProcessBuilder processBuilder = new ProcessBuilder(commandList);
        processBuilder.directory(workPath);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        outputMessage(process.getInputStream());
        process.waitFor();
    }

    public static void outputMessage(InputStream input) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(input));
        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
    }
}