import net.wimpi.modbus.ModbusException;
import net.wimpi.modbus.io.ModbusTCPTransaction;
import net.wimpi.modbus.msg.ReadInputDiscretesRequest;
import net.wimpi.modbus.msg.ReadInputDiscretesResponse;
import net.wimpi.modbus.msg.ReadInputRegistersRequest;
import net.wimpi.modbus.msg.ReadInputRegistersResponse;
import net.wimpi.modbus.net.TCPMasterConnection;
import net.wimpi.modbus.procimg.InputRegister;
import net.wimpi.modbus.util.BitVector;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Vector;

public class ModbusTCP {

    private static TCPMasterConnection connection = null;
    private static ModbusTCPTransaction transactionFC2 = null;
    private static ModbusTCPTransaction transactionFC4 = null;

    //Request/response for input statuses (discrete inputs) (FC02)
    private static ReadInputDiscretesRequest requestFC2 = null;
    private static ReadInputDiscretesResponse responseFC2 = null;

    //Request/response for analog input registers (FC04)
    private static ReadInputRegistersRequest requestFC4 = null;
    private static ReadInputRegistersResponse responseFC4 = null;
    String ip;

    public ModbusTCP(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    int port;

    public Vector getModbusData(){

        Vector<FlagData> dateFromModbus = new Vector<>();
        InetAddress connectionAddress = null; //127.0.0.1
        try {
            connectionAddress = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        int connectionPort = port; //502
        try {
            connection = new TCPMasterConnection(connectionAddress);
            connection.setPort(connectionPort);
            connection.connect();
        } catch (Exception ex) {
            System.out.println(new StringBuilder("ERROR: Could not establish connection to slave device. Device address: ")
                    .append(connectionAddress.getHostAddress())
                    .append(':')
                    .append(connectionPort));
            connection.close();
            return null;
        }

        //Read 16 analog input registers starting from 30001
        int offsetFC4 = 30001;
        int countFC4 = 16;
        requestFC4 = new ReadInputRegistersRequest(offsetFC4, countFC4);

        //Read 24 discrete inputs starting from 10001
        int offsetFC2 = 10001;
        int countFC2 = 24;
        requestFC2 = new ReadInputDiscretesRequest(offsetFC2, countFC2);

        //Create request transactions
        transactionFC2 = new ModbusTCPTransaction(connection);
        transactionFC4 = new ModbusTCPTransaction(connection);
        transactionFC2.setRequest(requestFC2);
        transactionFC4.setRequest(requestFC4);

        //Infinite read loop
        int analogAddIndex = 0;
        int requestIndex = 0;
        try {
            transactionFC4.execute();
            responseFC4 = (ReadInputRegistersResponse)transactionFC4.getResponse();

            for (InputRegister register : responseFC4.getRegisters()) {
                dateFromModbus.add(new FlagData(String.valueOf(offsetFC4 + analogAddIndex),
                        new StringBuilder(String.format("0x%04X", register.getValue() & 0xFFFF))
                                .append(" (")
                                .append(register.getValue())
                                .append(")").toString()));
                analogAddIndex++;
            }

            transactionFC2.execute();
            responseFC2 = (ReadInputDiscretesResponse)transactionFC2.getResponse();
            BitVector discreteInputs = responseFC2.getDiscretes();
            for(int i = 0; i< discreteInputs.size(); i++){
                dateFromModbus.add(new FlagData(String.valueOf(offsetFC2 + i), discreteInputs.getBit(i) == true ? "1" : "0"));
            }

            requestIndex++;

        } catch (ModbusException ex) {
            connection.close();
        }

        //Close connection
        connection.close();
        return dateFromModbus;
    }

}
