import javafx.beans.property.SimpleStringProperty;
public class FlagData {


    private final SimpleStringProperty flagName = new SimpleStringProperty("");
    private final SimpleStringProperty adress = new SimpleStringProperty("");
    private final SimpleStringProperty value = new SimpleStringProperty("");

    public FlagData() {
        this("", "", "");
    }
    public FlagData(String flagName, String adress, String value) {
        setFlagName(flagName);
        setAdress(adress);
        setValue(value);
    }

    public String getFlagName() {
        return flagName.get();
    }

    public void setFlagName(String fName) {
        flagName.set(fName);
    }

    public String getAdress() {
        return adress.get();
    }

    public void setAdress(String fName) {
        adress.set(fName);
    }

    public String getValue() {
        return value.get();
    }

    public void setValue(String fName) {
        value.set(fName);
    }


}
