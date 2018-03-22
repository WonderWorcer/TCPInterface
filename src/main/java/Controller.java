import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Vector;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
