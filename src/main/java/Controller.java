import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

public class Controller implements Initializable {
    @FXML
    private TableView<FlagData> tableView;

    @FXML
    private TextField ipAddress;
    @FXML
    private TextField port;

    public void addAllData(ActionEvent event){
        ObservableList<FlagData> data = tableView.getItems();
        data.clear();
        ModbusTCP modbusTCP = new ModbusTCP(ipAddress.getText(), Integer.parseInt(port.getText()));
        Vector<FlagData> dataFromModbus = modbusTCP.getModbusData();
        for(int i = 0; i < dataFromModbus.size(); i++){
            data.add(dataFromModbus.elementAt(i));
        }
    }




    public void getSelectedIndex(MouseEvent event){
        FlagData flagData = tableView.getSelectionModel().getSelectedItem();
        System.out.println(flagData.getValue());
        int value = 0;
        TextInputDialog dialog = new TextInputDialog(flagData.getValue());
        dialog.setTitle("Edit Cell");
        dialog.setHeaderText("Look, a Text Input Dialog");
        dialog.setContentText("Please enter new value:");
                        // Traditional way to get the response value.
                       Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            value = Integer.parseInt(result.get());
            }
                        ModbusTCP modbusTCP = new ModbusTCP(ipAddress.getText(), Integer.parseInt(port.getText()));

        modbusTCP.changeValue(Integer.parseInt(flagData.getAddress()),value);
    }

    public void showSetWindow(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("fxmlCommonRegistersSetWindow.fxml"));
            loader.setController(new CommonRegistersSetWindowController(ipAddress.getText(), port.getText()));
            Stage stage = new Stage();
            stage.setTitle("Настройка");
            stage.setScene(new Scene(loader.load(), 500, 300));
            stage.setResizable(false);
            stage.sizeToScene();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
