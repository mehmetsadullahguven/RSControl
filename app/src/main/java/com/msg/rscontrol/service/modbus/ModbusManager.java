package com.msg.rscontrol.service.modbus;

import com.serotonin.modbus4j.ModbusMaster;

public class ModbusManager {
    private static ModbusMaster modbusMaster;
    private static String plcIpAddress;
    private static int plcPort;

    public static ModbusMaster getModbusMaster() {
        return modbusMaster;
    }

    public static void setModbusMaster(ModbusMaster master) {
        modbusMaster = master;
    }

    public static String getPlcIpAddress() {
        return plcIpAddress;
    }

    public static void setPlcIpAddress(String ipAddress) {
        plcIpAddress = ipAddress;
    }

    public static int getPlcPort() {
        return plcPort;
    }

    public static void setPlcPort(int port) {
        plcPort = port;
    }
}