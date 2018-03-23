import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

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

    public void setPrepTuTimeRequest(ActionEvent event){
        int time = 0;

        TextInputDialog dialog = new TextInputDialog("500");
        dialog.setTitle("SETPREPTUTIME_REQUEST");
        dialog.setHeaderText("Look, a Text Input Dialog");
        dialog.setContentText("Please enter new value:");

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            time = Integer.parseInt(result.get());
        }

        ModbusTCP modbusTCP = new ModbusTCP(ipAddress.getText(), Integer.parseInt(port.getText()));
        modbusTCP.setPrepTuTimeRequest(time);
    }

    public void setPrepTcTimeRequest(ActionEvent event){
        int time = 0;

        TextInputDialog dialog = new TextInputDialog("500");
        dialog.setTitle("SETPREPTCTIME_REQUEST");
        dialog.setHeaderText("Look, a Text Input Dialog");
        dialog.setContentText("Please enter new value:");

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            time = Integer.parseInt(result.get());
        }

        ModbusTCP modbusTCP = new ModbusTCP(ipAddress.getText(), Integer.parseInt(port.getText()));
        modbusTCP.setPrepTcTimeRequest(time);
    }


    public void setPrepAutoTimeRequest(ActionEvent event){
        int time = 0;

        TextInputDialog dialog = new TextInputDialog("500");
        dialog.setTitle("SETPREPAUTOTIME_REQUEST");
        dialog.setHeaderText("Look, a Text Input Dialog");
        dialog.setContentText("Please enter new value:");

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            time = Integer.parseInt(result.get());
        }

        ModbusTCP modbusTCP = new ModbusTCP(ipAddress.getText(), Integer.parseInt(port.getText()));
        modbusTCP.setPrepAutoTimeRequest(time);
    }

    public void setRstTimeRequest(ActionEvent event){
        int time = 0;

        TextInputDialog dialog = new TextInputDialog("500");
        dialog.setTitle("SETRTSTIME_REQUEST");
        dialog.setHeaderText("Look, a Text Input Dialog");
        dialog.setContentText("Please enter new value:");

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            time = Integer.parseInt(result.get());
        }

        ModbusTCP modbusTCP = new ModbusTCP(ipAddress.getText(), Integer.parseInt(port.getText()));
        modbusTCP.setRstTimeRequest(time);
    }


    public void setAnswerTimeRequest(ActionEvent event){
        int time = 0;

        TextInputDialog dialog = new TextInputDialog("500");
        dialog.setTitle("SETANSWERTIME_REQUEST");
        dialog.setHeaderText("Look, a Text Input Dialog");
        dialog.setContentText("Please enter new value:");

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            time = Integer.parseInt(result.get());
        }

        ModbusTCP modbusTCP = new ModbusTCP(ipAddress.getText(), Integer.parseInt(port.getText()));
        modbusTCP.setAnswerTimeRequest(time);
    }


    public void setLogRequest(ActionEvent event){

        final int[] serNumber = {0};
        final int[] logNumber = {0};

        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("SETLOG_REQUEST");
        dialog.setHeaderText("Look, a Dialog");
        ButtonType loginButtonType = new ButtonType("Change number", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        TextField serialNumber = new TextField();
        serialNumber.setPromptText("SerialNumber");
        TextField logicalNumber = new TextField();
        logicalNumber.setPromptText("LogicalNumber");
        grid.add(new Label("SerialNumber:"), 0, 0);
        grid.add(serialNumber, 1, 0);
        grid.add(new Label("LogicalNumber:"), 0, 1);
        grid.add(logicalNumber, 1, 1);
        Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);
        serialNumber.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });
        dialog.getDialogPane().setContent(grid);
        Platform.runLater(() -> serialNumber.requestFocus());
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(serialNumber.getText(), logicalNumber.getText());
            }
            return null;
        });
        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(serialLogical -> {

            serNumber[0] = Integer.parseInt(serialLogical.getKey());
            logNumber[0] = Integer.parseInt(serialLogical.getValue());

        });


        ModbusTCP modbusTCP = new ModbusTCP(ipAddress.getText(), Integer.parseInt(port.getText()));
        modbusTCP.setLogRequest(serNumber[0], logNumber[0]);
    }




    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
