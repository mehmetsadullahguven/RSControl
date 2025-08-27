package com.msg.rscontrol.service.modbus;

import static com.msg.rscontrol.fragment.NotificationFragment.addToLog;

import android.os.AsyncTask;
import android.util.Log;

import com.msg.rscontrol.R;
import com.msg.rscontrol.MainActivity;
import com.msg.rscontrol.fragment.HomeFragment;
import com.serotonin.modbus4j.code.DataType;
import com.serotonin.modbus4j.locator.BaseLocator;

public class ReadAllStatesTask extends AsyncTask<Void, Void, Integer[]> {
    private MainActivity mainActivity;

    private HomeFragment homeFragment;

    public ReadAllStatesTask(MainActivity activity, HomeFragment fragment) {
        this.mainActivity = activity;
        this.homeFragment = fragment;
    }

    @Override
    protected Integer[] doInBackground(Void... voids) {
        if (!mainActivity.isConnected || mainActivity.modbusMaster == null) {
            addToLog("Hata: Modbus Master bağlı değil veya null.");
            return null;
        }
        Integer[] states = new Integer[4];
        try {
            BaseLocator<Number> analogLocator = BaseLocator.holdingRegister(
                    mainActivity.slaveId,
                    mainActivity.ANALOG_VALUE_REGISTER_ADDRESS,
                    DataType.TWO_BYTE_INT_SIGNED
            );

            BaseLocator<Number> analogLocator1 = BaseLocator.holdingRegister(
                    mainActivity.slaveId,
                    mainActivity.ANALOG_VALUE_REGISTER_ADDRESS_1,
                    DataType.TWO_BYTE_INT_SIGNED
            );

            BaseLocator<Number> analogLocator2 = BaseLocator.holdingRegister(
                    mainActivity.slaveId,
                    mainActivity.ANALOG_VALUE_REGISTER_ADDRESS_2,
                    DataType.TWO_BYTE_INT_SIGNED
            );

            BaseLocator<Number> analogLocator3 = BaseLocator.holdingRegister(
                    mainActivity.slaveId,
                    mainActivity.ANALOG_VALUE_REGISTER_ADDRESS_3,
                    DataType.TWO_BYTE_INT_SIGNED
            );


            states[0] = (Integer) mainActivity.modbusMaster.getValue(analogLocator).intValue();
            states[1] = (Integer) mainActivity.modbusMaster.getValue(analogLocator1).intValue();
            states[2] = (Integer) mainActivity.modbusMaster.getValue(analogLocator2).intValue();
            states[3] = (Integer) mainActivity.modbusMaster.getValue(analogLocator3).intValue();

            if (
                (states[0] == null) &&
                (states[1] == null) &&
                (states[2] == null) &&
                (states[3] == null)
            ) {
                addToLog("Uyarı: PLC okunamadı. Simülatör/PLC ayarlarını kontrol edin.");
                return null;
            }

            return states;
        } catch (Exception e) {
            Log.e(mainActivity.TAG, "Durum Okuma Hatası: " + e.getMessage(), e);
            e.printStackTrace();
            addToLog("Durum Okuma Hatası Detayı: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            return null;
        }
    }

    @Override
    protected void onPostExecute(Integer[] states) {
        if (states != null) {
            // Motor Durumunu Görselle Güncelle
            if (states[0] != null) {
                if (states[0] >= 1) {
                    // Motor açık, yeşil renkli resmi göster
                    homeFragment.ivMotorStatus.setImageResource(R.drawable.ic_motor_acik);
                    homeFragment.btnMotorOn.setEnabled(false);
                    homeFragment.btnMotorOff.setEnabled(true);
                } else {
                    // Motor kapalı, kırmızı renkli resmi göster
                    homeFragment.ivMotorStatus.setImageResource(R.drawable.ic_motor_kapali);
                    homeFragment.btnMotorOn.setEnabled(true);
                    homeFragment.btnMotorOff.setEnabled(false);
                }
            } else {
                homeFragment.ivMotorStatus.setImageResource(R.drawable.ic_motor_kapali);
                addToLog("Motor Durumu Okunamadı.");
            }

            // Valf Durumunu Görselle Güncelle
            if (states[1] != null) {
                if (states[1] >= 1) {
                    // Valf açık, yeşil renkli resmi göster
                    homeFragment.ivValveStatus.setImageResource(R.drawable.ic_valve_acik);
                    homeFragment.btnValveOpen.setEnabled(false);
                    homeFragment.btnValveClose.setEnabled(true);
                } else {
                    // Valf kapalı, kırmızı renkli resmi göster
                    homeFragment.ivValveStatus.setImageResource(R.drawable.ic_valve_kapali);
                    homeFragment.btnValveOpen.setEnabled(true);
                    homeFragment.btnValveClose.setEnabled(false);
                }
            } else {
                homeFragment.ivValveStatus.setImageResource(R.drawable.ic_valve_kapali);
                addToLog("Valf Durumu Okunamadı.");
            }

            if (states[2] != null) {
                homeFragment.tvLevel1Label.setText("Su Seviyesi: " + states[2]);

                homeFragment.level1ProgressBar.setProgress(states[2]);
            } else {
                addToLog("Su seviyesi Okunamadı.");
            }

            if (states[3] != null) {
                homeFragment.tvLevel2Label.setText("Gaz basıncı: " + states[3]);
                homeFragment.level2ProgressBar.setProgress(states[3]);
            } else {
                addToLog("Gaz basıncı Okunamadı.");
            }
        } else {
            // Okuma görevi başarısız olursa görselleri varsayılan hale getir
            homeFragment.ivMotorStatus.setImageResource(R.drawable.ic_motor_kapali);
            homeFragment.ivValveStatus.setImageResource(R.drawable.ic_valve_kapali);
            addToLog("Durum okuma görevi başarısız oldu.");
        }
    }
}