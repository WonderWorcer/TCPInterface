import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Vector;

public class CommonRegistersSetWindowController implements Initializable {

    private String ip;
    private String port;

    public CommonRegistersSetWindowController(String ip, String port) {
        this.ip = ip;
        this.port = port;
    }

    @FXML
    private javafx.scene.control.Button closeButton;

    @FXML
    private TextField answerTimeTextField;

    @FXML
    private TextField RTSTimeTextField;

    @FXML
    private TextField autoTimeTextField;

    @FXML
    private TextField TSTimeTextField;

    @FXML
    private TextField TUTimeTextField;

    @FXML
    private void closeButtonAction(){
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void refreshButtonAction() {
        ModbusTCP modbusTCP = new ModbusTCP(ip, Integer.parseInt(port));
        Vector<FlagData> modbusData = modbusTCP.getModbusData();
        for(FlagData fd : modbusData){
            switch (fd.getAddress()){
                case "40004":
                    answerTimeTextField.setText(fd.getValue());
                    break;
                case "40005":
                    RTSTimeTextField.setText((fd.getValue()));
                    break;
                case "40006":
                    autoTimeTextField.setText(fd.getValue());
                    break;
                case "40007":
                    TSTimeTextField.setText(fd.getValue());
                    break;
                case "40008":
                    TUTimeTextField.setText(fd.getValue());
                    break;
                default:
                    break;
            }
        }
    }

    private boolean isTextFieldsValid() {
        if(!answerTimeTextField.getText().isEmpty() && !RTSTimeTextField.getText().isEmpty() &&
                !autoTimeTextField.getText().isEmpty() && !TSTimeTextField.getText().isEmpty() && !TUTimeTextField.getText().isEmpty()){
            try {
                Integer.parseInt(answerTimeTextField.getText());
                Integer.parseInt(RTSTimeTextField.getText());
                Integer.parseInt(autoTimeTextField.getText());
                Integer.parseInt(TSTimeTextField.getText());
                Integer.parseInt(TUTimeTextField.getText());
                return true;
            } catch (NumberFormatException ex) {
                return false;
            }
        } else {
            return false;
        }
    }

    @FXML
    private void setValuesButtonAction(){
        if(isTextFieldsValid()){
            ModbusTCP modbusTCP = new ModbusTCP(ip, Integer.parseInt(port));
            modbusTCP.setAnswerTimeRequest(Integer.parseInt(answerTimeTextField.getText()));
            modbusTCP.setRstTimeRequest(Integer.parseInt(RTSTimeTextField.getText()));
            modbusTCP.setPrepAutoTimeRequest(Integer.parseInt(autoTimeTextField.getText()));
            modbusTCP.setPrepTcTimeRequest(Integer.parseInt(TSTimeTextField.getText()));
            modbusTCP.setPrepTuTimeRequest(Integer.parseInt(TUTimeTextField.getText()));
        } else {
            //TODO: show validation message
        }
    }

    @FXML
    public void setLogicalButtonAction(){

        final long[] serNumber = {0};
        final int[] logNumber = {0};

        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Задание логического номера КП");
        dialog.setHeaderText("Задание логического номера КП");
        ButtonType okButtonType = new ButtonType("Задать номер", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        TextField serialNumber = new TextField();
        TextField logicalNumber = new TextField();
        grid.add(new Label("Серийный номер КП"), 0, 0);
        grid.add(serialNumber, 1, 0);
        grid.add(new Label("Логический номер КП"), 0, 1);
        grid.add(logicalNumber, 1, 1);
        Node loginButton = dialog.getDialogPane().lookupButton(okButtonType);
        loginButton.setDisable(true);
        serialNumber.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });
        dialog.getDialogPane().setContent(grid);
        Platform.runLater(() -> serialNumber.requestFocus());
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return new Pair<>(serialNumber.getText(), logicalNumber.getText());
            }
            return null;
        });
        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(serialLogical -> {
            serNumber[0] = Long.parseLong(serialLogical.getKey());
            logNumber[0] = Integer.parseInt(serialLogical.getValue());

            ModbusTCP modbusTCP = new ModbusTCP(ip, Integer.parseInt(port));
            modbusTCP.setLogRequest(serNumber[0], logNumber[0]);
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        refreshButtonAction();
    }
}
