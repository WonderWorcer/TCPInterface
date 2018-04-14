import net.wimpi.modbus.ModbusException;
import net.wimpi.modbus.io.ModbusTCPTransaction;
import net.wimpi.modbus.msg.*;
import net.wimpi.modbus.net.TCPMasterConnection;
import net.wimpi.modbus.procimg.InputRegister;
import net.wimpi.modbus.procimg.SimpleRegister;
import net.wimpi.modbus.util.BitVector;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Vector;

public class ModbusTCP {

    private static TCPMasterConnection connection = null;
    private static ModbusTCPTransaction transactionFC2 = null;
    private static ModbusTCPTransaction transactionFC4 = null;
    private static ModbusTCPTransaction transactionFC3 = null;
    private static ModbusTCPTransaction commonTransaction = null;


    //Request/response for input statuses (discrete inputs) (FC02)
    private static ReadInputDiscretesRequest requestFC2 = null;

    private static ReadInputDiscretesResponse responseFC2 = null;

    //Request/response for analog input registers (FC04)
    private static ReadInputRegistersRequest requestFC4 = null;
    private static ReadInputRegistersResponse responseFC4 = null;

    //Request/response for analog output holding registers (FC03)
    private static ReadMultipleRegistersRequest requestFC3 = null;
    private static ReadMultipleRegistersResponse responseFC3 = null;

    private static ReadMultipleRegistersRequest request = null;
    private static ReadMultipleRegistersResponse response = null;

    private static WriteSingleRegisterRequest commonRequest = null;
    private static WriteSingleRegisterResponse commonResponse = null;

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

        //Read 8 analog output holding registers starting from 40001
        int offsetFC3 = 40001;
        int countFC3 = 8;
        requestFC3 = new ReadMultipleRegistersRequest(offsetFC3, countFC3);

        //Create request transactions
        transactionFC2 = new ModbusTCPTransaction(connection);
        transactionFC4 = new ModbusTCPTransaction(connection);
        transactionFC3 = new ModbusTCPTransaction(connection);
        transactionFC2.setRequest(requestFC2);
        transactionFC4.setRequest(requestFC4);
        transactionFC3.setRequest(requestFC3);

        //Infinite read loop
        int analogAddIndex = 0;
        int analogOutputAddIndex = 0;
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

            transactionFC3.execute();
            responseFC3 = (ReadMultipleRegistersResponse)transactionFC3.getResponse();

            for (InputRegister register : responseFC3.getRegisters()) {
                dateFromModbus.add(new FlagData(String.valueOf(offsetFC3 + analogOutputAddIndex),String.valueOf(register.getValue())));
                analogOutputAddIndex++;
            }

            requestIndex++;

        } catch (ModbusException ex) {
            connection.close();
        }

        //Close connection
        connection.close();
        return dateFromModbus;
    }


    /**
     * SETPREPTUTIME_REQUEST
     * @param time - Время в мс
     */
    public void setPrepTuTimeRequest(int time){
        SimpleRegister reg = new SimpleRegister(1);
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
            return ;
        }

        int offsetFC4 = 40008;
        reg.setValue(time);
        commonRequest = new WriteSingleRegisterRequest(offsetFC4,reg);
        commonTransaction = new ModbusTCPTransaction(connection);
        commonTransaction.setRequest(commonRequest);

        try {
            commonTransaction.execute();
            commonResponse =  (WriteSingleRegisterResponse) commonTransaction.getResponse();
        } catch (ModbusException e) {
            e.printStackTrace();
        }
    }


    /**
     * SETPREPTCTIME_REQUEST
     * @param time - Время в мс
     */
    public void setPrepTcTimeRequest(int time){
        SimpleRegister reg = new SimpleRegister(1);
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
            return ;
        }

        int offsetFC4 = 40007;
        reg.setValue(time);
        commonRequest = new WriteSingleRegisterRequest(offsetFC4,reg);
        commonTransaction = new ModbusTCPTransaction(connection);
        commonTransaction.setRequest(commonRequest);

        try {
            commonTransaction.execute();
            commonResponse =  (WriteSingleRegisterResponse) commonTransaction.getResponse();
        } catch (ModbusException e) {
            e.printStackTrace();
        }
    }


    /**
     * SETPREPAUTOTIME_REQUEST
     * @param time - Время в мс
     */
    public void setPrepAutoTimeRequest(int time){
        SimpleRegister reg = new SimpleRegister(1);
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
            return ;
        }

        int offsetFC4 = 40006;
        reg.setValue(time);
        commonRequest = new WriteSingleRegisterRequest(offsetFC4,reg);
        commonTransaction = new ModbusTCPTransaction(connection);
        commonTransaction.setRequest(commonRequest);

        try {
            commonTransaction.execute();
            commonResponse =  (WriteSingleRegisterResponse) commonTransaction.getResponse();
        } catch (ModbusException e) {
            e.printStackTrace();
        }
    }


    /**
     * SETRTSTIME_REQUEST
     * @param time - Время в мс
     */
    public void setRstTimeRequest(int time){
        SimpleRegister reg = new SimpleRegister(1);
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
            return ;
        }

        int offsetFC4 = 40005;
        reg.setValue(time);
        commonRequest = new WriteSingleRegisterRequest(offsetFC4,reg);
        commonTransaction = new ModbusTCPTransaction(connection);
        commonTransaction.setRequest(commonRequest);

        try {
            commonTransaction.execute();
            commonResponse =  (WriteSingleRegisterResponse) commonTransaction.getResponse();
        } catch (ModbusException e) {
            e.printStackTrace();
        }
    }


    /**
     * SETANSWERTIME_REQUEST
     * @param time - Время в мс
     */
    public void setAnswerTimeRequest(int time){
        SimpleRegister reg = new SimpleRegister(1);
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
            return ;
        }

        int offsetFC4 = 40004;
        reg.setValue(time);
        commonRequest = new WriteSingleRegisterRequest(offsetFC4,reg);
        commonTransaction = new ModbusTCPTransaction(connection);
        commonTransaction.setRequest(commonRequest);

        try {
            commonTransaction.execute();
            commonResponse =  (WriteSingleRegisterResponse) commonTransaction.getResponse();
        } catch (ModbusException e) {
            e.printStackTrace();
        }
    }

    /**
     * SETLOG_REQUEST
     * @param serialNumber - Серийный номер устройства
     * @param logicalNumber - Логический номер устройства
     */
    public void setLogRequest(long serialNumber, int logicalNumber){
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
            return;
        }

        int offset = 40001;
        int countReg = 2;

        request = new ReadMultipleRegistersRequest(offset, countReg);
        commonTransaction = new ModbusTCPTransaction(connection);
        commonTransaction.setRequest(request);

        StringBuilder fullSerialNumber = new StringBuilder();
        try {
            commonTransaction.execute();
            response = (ReadMultipleRegistersResponse)commonTransaction.getResponse();

            for (InputRegister register : response.getRegisters()) {
                fullSerialNumber.append(register.getValue());
            }
            long resultSerialNumber = Long.valueOf(fullSerialNumber.toString());
            if(resultSerialNumber == serialNumber)
            {
                SimpleRegister reg = new SimpleRegister(1);
                int offsetFC4 = 40003;
                reg.setValue(logicalNumber);
                commonRequest = new WriteSingleRegisterRequest(offsetFC4, reg);
                commonTransaction = new ModbusTCPTransaction(connection);
                commonTransaction.setRequest(commonRequest);
                try {
                    commonTransaction.execute();
                    commonResponse = (WriteSingleRegisterResponse) commonTransaction.getResponse();
                } catch (ModbusException e) {
                    e.printStackTrace();
                }
            }

        } catch (ModbusException ex) {
            connection.close();
        }

        //Close connection
        connection.close();


    }

    public void changeValue(int offset, int value){
        SimpleRegister reg = new SimpleRegister(1);
        reg.setValue(value);

        InetAddress connectionAddress = null; //127.0.0.1
        try {
            connectionAddress = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        int connectionPort = 503; //502
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
            return ;
        }
        //TS - signals
        if(offset < 30000 ){
            WriteSingleRegisterRequest changeRequest = new WriteSingleRegisterRequest(offset,reg);
            commonTransaction = new ModbusTCPTransaction(connection);
            commonTransaction.setRequest(changeRequest);

            try {
                commonTransaction.execute();
                //           commonResponse =  (WriteSingleRegisterResponse) commonTransaction.getResponse();
            } catch (ModbusException e) {
                e.printStackTrace();
            }

        }
        //TI - signals
        else if(offset > 30000 && offset < 40000)
        {
            WriteSingleRegisterRequest changeRequest = new WriteSingleRegisterRequest(offset,reg);
            commonTransaction = new ModbusTCPTransaction(connection);
            commonTransaction.setRequest(changeRequest);

            try {
                commonTransaction.execute();
     //           commonResponse =  (WriteSingleRegisterResponse) commonTransaction.getResponse();
            } catch (ModbusException e) {
                e.printStackTrace();
            }

        }
        //Common data - still not work
        else
        {
            WriteSingleRegisterRequest changeRequest = new WriteSingleRegisterRequest(offset,reg);
            commonTransaction = new ModbusTCPTransaction(connection);
            commonTransaction.setRequest(changeRequest);

            try {
                commonTransaction.execute();
                //           commonResponse =  (WriteSingleRegisterResponse) commonTransaction.getResponse();
            } catch (ModbusException e) {
                e.printStackTrace();
            }

        }


    }

}
