import javafx.beans.property.SimpleStringProperty;

public class FlagData {
    private final SimpleStringProperty address = new SimpleStringProperty("");
    private final SimpleStringProperty value = new SimpleStringProperty("");

    public FlagData(String address, String value) {
        setAddress(address);
        setValue(value);
    }

    public String getAddress() {
        return address.get();
    }

    public void setAddress(String fName) {
        address.set(fName);
    }

    public String getValue() {
        return value.get();
    }

    public void setValue(String fName) {
        value.set(fName);
    }


}
