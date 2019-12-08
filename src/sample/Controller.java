package sample;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import sample.domain.FileTreeItem;

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
    private TreeTableView<FileTreeItem> myTreeTableView;

    TreeTableColumn<FileTreeItem, String> fileNameColumn;
    TreeTableColumn<FileTreeItem, String> fileSizeColumn;
    private Window stage;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO (don't really need to do anything here).
        System.out.println("初始化文件树窗口");
        fileNameColumn = new TreeTableColumn<>("文件名");
        fileNameColumn.setPrefWidth(530);
        fileNameColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<FileTreeItem, String> param)
                -> new ReadOnlyStringWrapper(param.getValue().getValue().getName()));
        fileSizeColumn = new TreeTableColumn<>("文件大小");
        fileSizeColumn.setPrefWidth(100);
        fileSizeColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<FileTreeItem, String> param)
                -> new ReadOnlyStringWrapper(param.getValue().getValue().getSize()));
        myTreeTableView.getColumns().setAll(fileNameColumn, fileSizeColumn);
    }

    /**
     * 生成目录文件树；包含文件、目录大小
     *
     * @param path 路径
     * @param size 过滤小于size字节的文件
     * @return TreeItem<String>
     */
    private TreeItem<FileTreeItem> initTreeView(String path, long size) {
        long size0;
        File file = new File(path);
        size0 = getDirSize(file);
        TreeItem<FileTreeItem> item = new TreeItem<>(new FileTreeItem(file.getName(), sizeFormat(size0)));
        item.setExpanded(false);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files != null && files.length != 0) {
                // 对文件和文件夹按名称进行排序
                sortFiles(files);
                for (File file2 : files) {
                    if (file2.isDirectory()) {
                        size0 = getDirSize(file2);
                        if (size == 0 || size0 > size) {
                            item.getChildren().add(initTreeView(file2.getAbsolutePath(), size));
                        }
                    } else {
                        size0 = file2.length();
                        if (size == 0 || size0 > size) {
                            TreeItem<FileTreeItem> i2 = new TreeItem<>(new FileTreeItem(file2.getName(), sizeFormat(size0)));
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

    /**
     * "生成文件树"按钮
     *
     * @param event
     */
    public void getDir(ActionEvent event) {
        new Thread(() -> {
            String path = myText.getText();
            Platform.runLater(() -> myButton.setText("生成中..."));
            System.out.println(path);
            long size = Long.parseLong(ignoreFileSize.getText());
            TreeItem<FileTreeItem> item = initTreeView(path, size * 1024);
            item.setExpanded(true);

            // 等到Application Thread空闲的时候，Platform.runLater就会自动执行队列中修改界面的工作了
            Platform.runLater(() -> {
                myTreeTableView.setRoot(item);
                myTreeTableView.getColumns().setAll(fileNameColumn, fileSizeColumn);
                myButton.setText("生成文件树");
            });
        }).start();
    }

    /**
     * "选择目录"按钮：获得想要制作文件树的路径
     *
     * @param event
     */
    public void openDir(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File file = directoryChooser.showDialog(null);
        if (file != null) {
            myText.setText(file.getPath());
        }
    }


    /**
     * "保存文件树"按钮：将层级目录保存到本地
     *
     * @param event
     */
    public void saveDir(ActionEvent event) {
        new Thread(() -> {
            Platform.runLater(() -> saveDir.setText("生成中..."));
            String path = myText.getText();
            long size = Long.parseLong(ignoreFileSize.getText());
            File file = new File(path);
            saveFileTree(path, file.getName(), size);
            Platform.runLater(() -> saveDir.setText("保存文件树"));
        }).start();

    }

}
