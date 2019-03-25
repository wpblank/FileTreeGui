package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

import static sample.fileTool.*;

public class Controller implements Initializable {
    @FXML
    private Button myButton;

    @FXML
    private Button openDir;

    @FXML
    private Button saveDir;

    @FXML
    private TextField myText;

    @FXML
    public TextField ignoreFileSize;

    @FXML
    private TreeView<String> myTreeView;
    private Window stage;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO (don't really need to do anything here).
    }

    /**
     * 生成目录文件树；包含文件、目录大小
     *
     * @param path 路径
     * @return TreeItem<String>
     */
    private TreeItem<String> initTreeView(String path) {
        return initTreeView(path, 0);
    }

    /**
     * 生成目录文件树；包含文件、目录大小
     *
     * @param path 路径
     * @param size 过滤小于size字节的文件
     * @return TreeItem<String>
     */
    private TreeItem<String> initTreeView(String path, long size) {

        long size0;
        File file = new File(path);
        size0 = getDirSize(file);
        TreeItem<String> item = new TreeItem<>(file.getName() + "\t" + sizeFormat(size0));
        item.setExpanded(false);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files != null && files.length != 0) {
                //对文件和文件夹按名称进行排序
                Arrays.sort(files, (o1, o2) -> {
                    //将文件夹与文件分开排序
                    if (o1.isDirectory() && o2.isFile())
                        return -1;
                    else if (o2.isDirectory() && o1.isFile())
                        return 1;
                    //对字符串大写处理，使返回的拼音为小写、英文为大写，从而将英文和中文分开排序。
                    return GetPinYin.getPinYin(o1.getName().toUpperCase()).compareTo(GetPinYin.getPinYin(o2.getName().toUpperCase()));
                });

                for (File file2 : files) {
                    if (file2.isDirectory()) {
                        size0 = getDirSize(file2);
                        if (size == 0 || size0 > size) {
                            item.getChildren().add(initTreeView(file2.getAbsolutePath(), size));
                        }
                    } else {
                        size0 = file2.length();
                        if (size == 0 || size0 > size) {
                            TreeItem<String> i2 = new TreeItem<>(file2.getName() + "\t" + sizeFormat(size0));
                            item.getChildren().add(i2);
                        }
                    }
                }
            }

        } else {
            System.out.println("文件夹不存在!");
        }
        return item;
    }

    //"生成文件树"按钮
    public void getDir(ActionEvent event) {
        //多线程
        new Thread(() -> {
            String path = myText.getText();

            Platform.runLater(() -> myButton.setText("生成中..."));
            System.out.println(path);
            long size = Long.parseLong(ignoreFileSize.getText());
            TreeItem<String> item = initTreeView(path, size * 1024);
            item.setExpanded(true);
            //等到Application Thread空闲的时候，Platform.runLater就会自动执行队列中修改界面的工作了
            Platform.runLater(() -> {
                myTreeView.setRoot(item);
                myButton.setText("生成文件树");
            });
        }).start();
    }

    //"选择目录"按钮：获得想要制作文件树的路径
    public void openDir(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File file = directoryChooser.showDialog(null);
        if (file != null)
            myText.setText(file.getPath());
    }


    //"保存文件树"按钮：将层级目录保存到本地
    public void saveDir(ActionEvent event) {
        //多线程
        new Thread(() ->{
            Platform.runLater(() -> saveDir.setText("生成中..."));
            String path = myText.getText();
            long size = Long.parseLong(ignoreFileSize.getText());
            File file = new File(path);
            saveFileTree(path, file.getName(), size);
            Platform.runLater(() -> saveDir.setText("保存文件树"));
        }).start();

    }

}
