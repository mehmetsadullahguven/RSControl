package com.msg.rscontrol.fragment;

import static com.msg.rscontrol.fragment.NotificationFragment.addToLog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.msg.rscontrol.MainActivity;
import com.msg.rscontrol.R;
import com.msg.rscontrol.enums.RegisterAddress;
import com.msg.rscontrol.service.modbus.ReadAllStatesTask;
import com.msg.rscontrol.service.modbus.WriteAnalogValueTask;

public class HomeFragment extends Fragment{

    public Button btnMotorOn, btnMotorOff, btnValveOpen, btnValveClose;
    public ImageView ivMotorStatus, ivValveStatus;

    public SeekBar level1ProgressBar, level2ProgressBar;
    public TextView tvLevel1Label, tvLevel2Label;
    MainActivity mainActivity;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,@Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        btnMotorOn = view.findViewById(R.id.btn_motor_on);
        btnMotorOff = view.findViewById(R.id.btn_motor_off);
        btnValveOpen = view.findViewById(R.id.btn_valve_open);
        btnValveClose = view.findViewById(R.id.btn_valve_close);
        ivMotorStatus = view.findViewById(R.id.iv_motor_status);
        ivValveStatus = view.findViewById(R.id.iv_valve_status);
        level1ProgressBar = view.findViewById(R.id.pb_level1);
        level2ProgressBar = view.findViewById(R.id.pb_level2);
        tvLevel1Label = view.findViewById(R.id.tv_level1_label);
        tvLevel2Label = view.findViewById(R.id.tv_level2_label);

        mainActivity = (MainActivity) getActivity();
        if (mainActivity.isDemo) {
            demo();
        }else {
            new ReadAllStatesTask(mainActivity, this).execute();

            buttonControls();
        }
        return view;
    }


    public void demo() {
        tvLevel1Label.setText("Su Seviyesi: " + 52);
        level1ProgressBar.setProgress(52);

        tvLevel2Label.setText("Gaz Basıncı: " + 7);
        level2ProgressBar.setProgress(7);

        if (!mainActivity.motorBtnOn) {
            btnMotorOn.setEnabled(true);
            ivMotorStatus.setImageResource(R.drawable.ic_motor_kapali);
        }else {
            btnMotorOff.setEnabled(true);
            ivMotorStatus.setImageResource(R.drawable.ic_motor_acik);
        }
        if (!mainActivity.vanaBtnOn) {
            btnValveOpen.setEnabled(true);
            ivValveStatus.setImageResource(R.drawable.ic_valve_kapali);
        }else {
            btnValveClose.setEnabled(true);
            ivValveStatus.setImageResource(R.drawable.ic_valve_acik);
        }

        btnMotorOn.setOnClickListener(v -> {
            addToLog("motor açıldı");
            ivMotorStatus.setImageResource(R.drawable.ic_motor_acik);
            btnMotorOff.setEnabled(true);
            btnMotorOn.setEnabled(false);
            mainActivity.motorBtnOn = true;
        });
        btnMotorOff.setOnClickListener(v -> {
            addToLog("motor kapandı");
            ivMotorStatus.setImageResource(R.drawable.ic_motor_kapali);
            btnMotorOn.setEnabled(true);
            btnMotorOff.setEnabled(false);
            mainActivity.motorBtnOn = false;
        });
        btnValveOpen.setOnClickListener(v -> {
            addToLog("vana açıldı");
            btnValveClose.setEnabled(true);
            btnValveOpen.setEnabled(false);
            ivValveStatus.setImageResource(R.drawable.ic_valve_acik);
            mainActivity.vanaBtnOn = true;
        });
        btnValveClose.setOnClickListener(v -> {
            addToLog("vana kapandı");
            btnValveOpen.setEnabled(true);
            btnValveClose.setEnabled(false);
            ivValveStatus.setImageResource(R.drawable.ic_valve_kapali);
            mainActivity.vanaBtnOn = false;
        });
        level1ProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvLevel1Label.setText("Su Seviyesi: " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                addToLog("Su Seviyesi: " + seekBar.getProgress() + " ayarlandı");
            }
        });

        level2ProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvLevel2Label.setText("Gaz Basıncı: " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                addToLog("Gaz Basıncı: " + seekBar.getProgress() + " ayarlandı");
            }
        });
    }

    public void  buttonControls() {

        // Butonlara tıklama dinleyicileri atama
        btnMotorOn.setOnClickListener(v -> {
            new WriteAnalogValueTask(RegisterAddress.ANALOG_VALUE_REGISTER_ADDRESS_0.getValue(), 1, mainActivity, this).execute();
        });

        // Motor Kapat butonu: Hem kontrol hem de durum bobinine 'false' yazdır
        btnMotorOff.setOnClickListener(v -> {
            new WriteAnalogValueTask(RegisterAddress.ANALOG_VALUE_REGISTER_ADDRESS_0.getValue(), 0, mainActivity, this).execute();
        });

        // Valf Aç butonu: Hem kontrol hem de durum bobinine 'true' yazdır
        btnValveOpen.setOnClickListener(v -> {
            new WriteAnalogValueTask(RegisterAddress.ANALOG_VALUE_REGISTER_ADDRESS_1.getValue(), 1, mainActivity, this).execute();
        });

        // Valf Kapat butonu: Hem kontrol hem de durum bobinine 'false' yazdır
        btnValveClose.setOnClickListener(v -> {
            new WriteAnalogValueTask(RegisterAddress.ANALOG_VALUE_REGISTER_ADDRESS_1.getValue(), 0, mainActivity, this).execute();
        });

        level1ProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvLevel1Label.setText("Su Seviyesi: " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                MainActivity mainActivity = (MainActivity) getActivity();
                new WriteAnalogValueTask(RegisterAddress.ANALOG_VALUE_REGISTER_ADDRESS_2.getValue(), seekBar.getProgress(), mainActivity, HomeFragment.this).execute();
            }
        });

        level2ProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvLevel2Label.setText("Gaz Basıncı: " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                MainActivity mainActivity = (MainActivity) getActivity();
                new WriteAnalogValueTask(RegisterAddress.ANALOG_VALUE_REGISTER_ADDRESS_3.getValue(), seekBar.getProgress(), mainActivity, HomeFragment.this).execute();
            }
        });
    }
}
