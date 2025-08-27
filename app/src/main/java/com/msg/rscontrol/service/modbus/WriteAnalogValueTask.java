package com.msg.rscontrol.service.modbus;

import static com.msg.rscontrol.fragment.NotificationFragment.addToLog;

import android.os.AsyncTask;

import com.msg.rscontrol.MainActivity;
import com.msg.rscontrol.fragment.HomeFragment;
import com.serotonin.modbus4j.code.DataType;
import com.serotonin.modbus4j.locator.BaseLocator;

public class WriteAnalogValueTask  extends AsyncTask<Void, Void, Boolean> {

    private int registerAddress;
    private int valueToWrite;
    private MainActivity mainActivity;
    private HomeFragment homeFragment;

    public WriteAnalogValueTask(int registerAddress, int valueToWrite, MainActivity mainActivity, HomeFragment homeFragment) {
        this.registerAddress = registerAddress;
        this.valueToWrite = valueToWrite;
        this.mainActivity = mainActivity;
        this.homeFragment = homeFragment; // Referansı atayın
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        if (!mainActivity.isConnected || mainActivity.modbusMaster == null) {
            addToLog("Hata: PLC'ye bağlı değilsiniz.");
            return false;
        }

        try {
            BaseLocator<Number> locator = BaseLocator.holdingRegister(mainActivity.slaveId, registerAddress, DataType.TWO_BYTE_INT_SIGNED);

            mainActivity.modbusMaster.setValue(locator, valueToWrite);
            return true;
        }catch (Exception e) {
            addToLog("Analog Değer Yazma Hatası (Adres: " + registerAddress + "): " + e.getMessage());
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (success) {
            new ReadAllStatesTask(mainActivity, homeFragment).execute();

            String log = "register ya da value hatası";

            if (registerAddress == 0) {
                if (valueToWrite == 1) {
                    log = "Motor açıldı";
                }else {
                    log = "Motor Kapalı";
                }
            } else if (registerAddress == 1) {
                if (valueToWrite == 1) {
                    log = "Vana açıldı";
                }else {
                    log = "Vana Kapalı";
                }
            } else if (registerAddress == 2) {
                log = "Su seviyesi: " + valueToWrite + " ayarlandı";
            } else if (registerAddress == 3) {
                log = "Gaz basıncı: " + valueToWrite + " ayarlandı";
            }
            addToLog(log);
        } else {
            addToLog("Analog değer yazma hatası.");
        }
    }
}
