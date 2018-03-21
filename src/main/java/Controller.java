import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Vector;

public class Controller implements Initializable {

    @FXML private TableView<FlagData> tableView;
    @FXML private TextField nameFlag;
    @FXML private TextField adress;
    @FXML private TextField value;

    @FXML private TextField ipAdress;
    @FXML private TextField port;

    public void addAllData(ActionEvent event){
        ObservableList<FlagData> data = tableView.getItems();
        data.clear();
        Vector<FlagData> dataFromModbus = new Vector<FlagData>();
        ModbusTCP modbusTCP = new ModbusTCP(ipAdress.getText(),Integer.parseInt(port.getText()));
        dataFromModbus = modbusTCP.getModbusData();

        for(int i = 0; i < dataFromModbus.size(); i++){
            data.add(dataFromModbus.elementAt(i));
        }


    }

    public void pressButton(ActionEvent event){
        System.out.println("Hello world");

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
