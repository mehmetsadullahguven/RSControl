package com.msg.rscontrol;

import static com.msg.rscontrol.fragment.NotificationFragment.addToLog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.msg.rscontrol.fragment.HomeFragment;
import com.msg.rscontrol.fragment.NotificationFragment;
import com.msg.rscontrol.fragment.SettingsFragment;
import com.msg.rscontrol.service.modbus.ModbusManager;
import com.serotonin.modbus4j.ModbusMaster;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "PLCModbusControl";

    BottomNavigationView bottomNav;
    private FragmentManager fragmentManager;

    protected TextView tvConnectionStatus;

    // Modbus Değişkenleri
    public ModbusMaster modbusMaster;
    public boolean isConnected = false;

    public boolean isDemo = false;

    public boolean motorBtnOn, vanaBtnOn;
    public final int slaveId = 1;
    public String plcIpAddress;
    public Integer plcPort;

    // PLC Modbus Adresleri
    public final int ANALOG_VALUE_REGISTER_ADDRESS = 0;
    public final int ANALOG_VALUE_REGISTER_ADDRESS_1 = 1;
    public final int ANALOG_VALUE_REGISTER_ADDRESS_2 = 2;
    public final int ANALOG_VALUE_REGISTER_ADDRESS_3 = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ModbusManager'dan hazır bağlantı nesnesini al
        modbusMaster = ModbusManager.getModbusMaster();
        plcIpAddress = ModbusManager.getPlcIpAddress();
        plcPort = ModbusManager.getPlcPort();

        // UI Bileşenlerini eşleştirme
        tvConnectionStatus = findViewById(R.id.tv_connection_status);

        Intent intent = getIntent();
        boolean demo = intent.getBooleanExtra("demo", false);

        bottomNav = findViewById(R.id.bottom_navigation);
        fragmentManager = getSupportFragmentManager();

        HomeFragment homeFragment = new HomeFragment();
        loadFragment(homeFragment);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                loadFragment(new HomeFragment());
                return true;
            } else if (itemId == R.id.navigation_notifications) {
                loadFragment(new NotificationFragment());
                return true;
            } else if (itemId == R.id.navigation_settings) {
                loadFragment(new SettingsFragment());
                return true;
            } else if (itemId == R.id.navigation_exit) {
                disconnectAndGoToLogin();
                return true;
            }
            return false;
        });

        if (demo) {
            tvConnectionStatus.setText("Bağlantı Durumu: Demo");
            isDemo = true;
        }else {
            // Bağlantı durumunu kontrol et ve UI'ı güncelle
            if (modbusMaster != null && modbusMaster.isInitialized()) {
                isConnected = true;
                topUpdateUI(true);
            }
        }

    }

    // Bağlantıyı kesen ve giriş sayfasına yönlendiren metot
    public void disconnectAndGoToLogin() {

        FirebaseAuth.getInstance().signOut();

        if (modbusMaster != null) {
            modbusMaster.destroy();
            modbusMaster = null;
            ModbusManager.setModbusMaster(null);
            addToLog("exit");
            Toast.makeText(this, "Bağlantı kesildi.", Toast.LENGTH_SHORT).show();
        }
        isConnected = false;

        // Giriş sayfasına dön
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        // Bu flag'ler, kullanıcı geri tuşuna bastığında tekrar bu sayfaya dönmesini engeller
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void topUpdateUI(boolean connected) {
        isConnected = connected;
        if (connected) {
            tvConnectionStatus.setText("Bağlantı Durumu: Bağlı (" + plcIpAddress + ":" + plcPort + ")");
            tvConnectionStatus.setTextColor(Color.GREEN);
        } else {
            tvConnectionStatus.setText("Bağlantı Durumu: Bağlı Değil");
            tvConnectionStatus.setTextColor(Color.RED);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Uygulama kapanırken Modbus bağlantısını düzgün bir şekilde kes
        if (modbusMaster != null) {
            modbusMaster.destroy();
            modbusMaster = null;
            ModbusManager.setModbusMaster(null);
            addToLog("Modbus bağlantısı kapatıldı.");
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}