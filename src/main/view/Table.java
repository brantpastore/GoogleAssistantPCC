package main.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableView;

/**
 * Table class
 * Used to hold the column Strings for both User Applications and Windows Commands
 */
public class Table extends TableView {
    private SimpleStringProperty ColumnOne;
    private SimpleStringProperty ColumnTwo;

    public Table(String colOne, String colTwo) {
        this.ColumnOne = new SimpleStringProperty(colOne);
        this.ColumnTwo = new SimpleStringProperty(colTwo);
    }

    public String getColumnOne() {
        return ColumnOne.get();
    }

    public void setColumnOne(String s) { ColumnOne.set(s); }

    public String getColumnTwo() {
        return ColumnTwo.get();
    }

    public void setColumnTwo(String s) {
        ColumnTwo.set(s);
    }
}
