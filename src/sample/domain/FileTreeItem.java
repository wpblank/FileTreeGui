package sample.domain;

import javafx.beans.property.SimpleStringProperty;

/**
 * 文件数节点
 *
 * @author izumi
 */
public class FileTreeItem {
    private SimpleStringProperty name;
    private SimpleStringProperty size;

    public FileTreeItem(String name, String size) {
        this.name = new SimpleStringProperty(name);
        this.size = new SimpleStringProperty(size);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getSize() {
        return size.get();
    }

    public void setSize(String size) {
        this.size.set(size);
    }
}
