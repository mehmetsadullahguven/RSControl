package com.msg.rscontrol.enums;

public enum RegisterAddress {
    ANALOG_VALUE_REGISTER_ADDRESS_0(0),
    ANALOG_VALUE_REGISTER_ADDRESS_1(1),
    ANALOG_VALUE_REGISTER_ADDRESS_2(2),
    ANALOG_VALUE_REGISTER_ADDRESS_3(3);

    private final int value;

    RegisterAddress(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}