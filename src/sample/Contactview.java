package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.util.Callback;

import java.util.ArrayList;


/**
 * @Author  Ali Albakri
 * the main idea behind this class is to open another Fxml to show a new window containing the info of the contacts
 */
public class Contactview {

    @FXML  TableView<ArrayList<String>> contactTable;

    /**
     *
     *  @Author  Ali Albakri
     * @param ContactList
     * @param tags_list
     * This method generates a table view containing any info in an excel or any database data.
     */

    public void generateView(ArrayList<ArrayList<String>> ContactList,ObservableList<String> tags_list){

        //preparing the contacts information to be shown in the table
        ObservableList <ArrayList<String>> data = FXCollections.observableArrayList();

        data.addAll(ContactList);

        //adding a columns matching each columns in the excel file
        for (int i = 0;i<tags_list.size();i++){
            //ading tags
            TableColumn columnTitle = new TableColumn(tags_list.get(i));
            final  int colomnum =i;


            columnTitle.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ArrayList<String>,String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<ArrayList<String>,String> param) {

                    //returning the columns to be written latter
                    return new SimpleStringProperty((param.getValue().get(colomnum)));
                }
            });
            columnTitle.setResizable(true);

            contactTable.getColumns().add(columnTitle);


        }
        //adding the columns to the table view
        contactTable.setItems(data);



    }
}
