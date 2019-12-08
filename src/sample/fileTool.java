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
    private static void saveFileTree(String path, String name, long size, int level) {
        File file = new File(path);
        BufferedWriter bufw = null;
        try {
            FileWriter fw = new FileWriter(name + ".txt", true);
            bufw = new BufferedWriter(fw);
        } catch (IOException e) {
            e.printStackTrace();
        }
        level++;
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files != null && bufw != null) {
                if (files.length != 0) {
                    //对文件和文件夹进行排序
                    sortFiles(files);
                    long size0;
                    for (File file2 : files) {

                        if (file2.isDirectory()) {
                            size0 = getDirSize(file2);
                            if (size == 0 || size0 > size) {
                                try {
                                    bufw.write(levelSign(level) + "[" + sizeFormat(size0) + "]\t【" + file2.getName() + "】");
                                    bufw.newLine();
                                    bufw.flush();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                saveFileTree(file2.getAbsolutePath(), name, size, level);
                            }
                        } else {
                            size0 = file2.length();
                            if (size == 0 || size0 > size) {
                                try {
                                    bufw.write(levelSign(level) + "[" + sizeFormat(size0) + "]\t" + file2.getName());
                                    bufw.newLine();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    //对每个文件夹进行一下分隔
                    try {
                        bufw.write(levelSign(level) + separateDir(level));
                        bufw.newLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //System.out.println(levelSign(level) + separateDir(level));

                }
            }
        } else {
            System.out.println("文件夹不存在!");
        }
        if (bufw != null) {
            try {
                bufw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    bufw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static void sortFiles(File[] files) {
        Arrays.sort(files, (o1, o2) -> {
            // 将文件夹与文件分开排序
            if (o1.isDirectory() && o2.isFile()) {
                return -1;
            } else if (o2.isDirectory() && o1.isFile()) {
                return 1;
            }
            // 对字符串大写处理，使返回的拼音为小写、英文为大写，从而将英文和中文分开排序。
            return GetPinYin.getPinYin(o1.getName().toUpperCase()).compareTo(GetPinYin.getPinYin(o2.getName().toUpperCase()));
        });
    }

    //保存目录文件和文件夹名称和大小
    static void saveFileTree(String path, String name) {
        saveFileTree(path, name, 0, 0);
    }

    //保存目录文件和文件夹名称和大小,并过滤文件大小小于size的
    static void saveFileTree(String path, String name, long size) {
        saveFileTree(path, name, size, 0);
    }

    /**
     * 获取文件夹目录大小
     *
     * @param file
     * @return
     */
    static long getDirSize(File file) {
        long size = 0;
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files != null) {
                if (files.length == 0) {
                    return 0;
                } else {
                    for (File file1 : files) {
                        if (file1.isDirectory()) {
                            size = size + getDirSize(file1);
                        } else {
                            size = size + file1.length();
                        }
                    }
                }
            }
        }
        return size;
    }

    /**
     * 文件大小格式化
     *
     * @param size 文件大小
     * @return
     */
    static String sizeFormat(long size) {
        String sizeString;
        if (size < 1024) {
            sizeString = size + " B";
        } else if (size < 1024 * 1024) {
            sizeString = String.format("%.2f", size / 1024.0) + "KB";
        } else if (size < 1024 * 1024 * 1024) {
            sizeString = String.format("%.2f", size / (1024 * 1024.0)) + "MB";
        } else {
            sizeString = String.format("%.2f", size / (1024 * 1024 * 1024.0)) + "GB";
        }
        return sizeString;
    }

}
