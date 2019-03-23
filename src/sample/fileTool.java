package sample;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

class fileTool {

    //文件层级信息
    private static String levelSign(int level) {
        StringBuilder sb = new StringBuilder();
        sb.append("▕——");
        for (int x = 0; x < level; x++) {
            sb.insert(0, "▕  ");
        }
        return sb.toString();
    }

    //目录分隔线
    private static String separateDir(int level) {
        StringBuilder sb = new StringBuilder();
        for (int x = 0; x < 20 - level; x++) {
            sb.insert(0, "——");
        }
        return sb.toString();
    }

    /**
     * @param path  path为文件夹路径
     * @param size  过滤大小小于size的文件或文件夹
     * @param level level为初始层级，一般为0
     *              保存目录文件和文件夹名称和大小
     */
    private static void saveFileTree(String path, String name, long size, int level) throws IOException {
        File file = new File(path);
        FileWriter fw = new FileWriter(name + ".txt", true);
        BufferedWriter bufw = new BufferedWriter(fw);

        level++;
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files != null && files.length != 0) {
                //对文件和文件夹进行排序
                Arrays.sort(files, (o1, o2) -> {
                    //将文件夹与文件分开排序
                    if (o1.isDirectory() && o2.isFile())
                        return -1;
                    else if (o2.isDirectory() && o1.isFile())
                        return 1;
                    //对字符串大写处理，使返回的拼音为小写、英文为大写，从而将英文和中文分开排序。
                    return GetPinYin.getPinYin(o1.getName().toUpperCase()).compareTo(GetPinYin.getPinYin(o2.getName().toUpperCase()));
                });

                long size0;
                for (File file2 : files) {
                    if (file2.isDirectory()) {
                        size0 = getDirSize(file2);
                        if (size == 0 || size0 > size) {
                            bufw.write(levelSign(level) + sizeFormat(size0) + "【" + file2.getName() + "】");
                            bufw.newLine();
                            bufw.flush();
                            saveFileTree(file2.getAbsolutePath(), name, size, level);
                        }
                    } else {
                        size0 = file2.length();
                        if (size == 0 || size0 > size) {
                            bufw.write(levelSign(level) + sizeFormat(size0) + file2.getName());
                            bufw.newLine();
                        }
                    }
                }
                //对每个文件夹进行一下分隔
                bufw.write(levelSign(level) + separateDir(level));
                bufw.newLine();
            }

        } else {
            System.out.println("文件夹不存在!");
        }
        bufw.flush();
        bufw.close();
    }

    //保存目录文件和文件夹名称和大小
    static void saveFileTree(String path, String name) throws IOException {
        saveFileTree(path, name, 0, 0);
    }

    //保存目录文件和文件夹名称和大小,并过滤文件大小小于size的
    static void saveFileTree(String path, String name, long size) throws IOException {
        saveFileTree(path, name, size, 0);
    }

    //获取文件夹目录大小
    static long getDirSize(File file) {
        long size = 0;
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files != null && files.length != 0)
                for (File file1 : files) {
                    if (file1.isDirectory()) {
                        size = size + getDirSize(file1);
                    } else {
                        size = size + file1.length();
                    }
                }
            else
                return 0;
        }
        return size;
    }

    //文件大小格式化
    static String sizeFormat(long size) {
        if (size < 1024)
            return "[" + size + "B]";
        else if (size < 1024 * 1024)
            return "[" + String.format("%.1f", size / 1024.0) + "KB]";
        else if (size < 1024 * 1024 * 1024)
            return "[" + String.format("%.1f", size / (1024 * 1024.0)) + "MB]";
        else
            return "[" + String.format("%.1f", size / (1024 * 1024 * 1024.0)) + "GB]";
    }

}
