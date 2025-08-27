package com.msg.rscontrol;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.msg.rscontrol.service.modbus.ModbusManager;
import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.code.DataType;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.ip.IpParameters;
import com.serotonin.modbus4j.locator.BaseLocator;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText email, password, plcIP, plcPort;

    private Button loginBtn, registerBtn, demoBtn;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.oldPassword);
        plcIP = findViewById(R.id.plc_ip);
        plcPort = findViewById(R.id.plc_port);
        registerBtn = findViewById(R.id.btn_register);
        loginBtn = findViewById(R.id.btn_login);
        demoBtn = findViewById(R.id.btn_demo);

        mAuth = FirebaseAuth.getInstance();

        registerBtn.setOnClickListener(v -> {
            if (email.getText().toString().isEmpty()) {
                Toast.makeText(this, "Lütfen geçerli bir email giriniz", Toast.LENGTH_SHORT).show();
            }else if (password.getText().toString().isEmpty()) {
                Toast.makeText(this, "Lütfen geçerli bir şifre giriniz", Toast.LENGTH_SHORT).show();
            }else {
                registerUser(email.getText().toString(), password.getText().toString());
            }
        });

        loginBtn.setOnClickListener(v -> {
            if (email.getText().toString().isEmpty()) {
                Toast.makeText(this, "Lütfen geçerli bir email giriniz", Toast.LENGTH_SHORT).show();
            }else if (password.getText().toString().isEmpty()) {
                Toast.makeText(this, "Lütfen geçerli bir şifre giriniz", Toast.LENGTH_SHORT).show();
            }else {
                loginUser(email.getText().toString(), password.getText().toString());
            }
        });
        demoBtn.setOnClickListener(v -> showDemoScreen());

    }

    @Override
    public void onStart()
    {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Toast.makeText(this, "Giriş başarılı. Bağlantı kuruluyor...", Toast.LENGTH_SHORT).show();
            new ConnectTask().execute(plcIP.getText().toString(), plcPort.getText().toString());
        }
    }

    private void registerUser(String email, String password)
    {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Kayıt başarılı", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Kayıt başarısız", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginUser(String email, String password)
    {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
           if (task.isSuccessful()) {
               Toast.makeText(this, "Giriş başarılı. Bağlantı kuruluyor...", Toast.LENGTH_SHORT).show();
               new ConnectTask().execute(plcIP.getText().toString(), plcPort.getText().toString());
           }else {
               Toast.makeText(this, "Hatalı kullanıcı adı veya şifre", Toast.LENGTH_SHORT).show();
           }
        });

    }

    private void showDemoScreen() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class).putExtra("demo", true));
        finish();
    }


    private class ConnectTask extends AsyncTask<String, Void, ModbusMaster>
    {
        @Override
        protected void onPreExecute() {
            loginBtn.setEnabled(false);
        }

        @Override
        protected ModbusMaster doInBackground(String... params) {
            String plcIpAddress = params[0];
            int plcPort = Integer.parseInt(params[1]);

            try {
                ModbusFactory factory = new ModbusFactory();
                IpParameters ipParameters = new IpParameters();
                ipParameters.setHost(plcIpAddress);
                ipParameters.setPort(plcPort);

                ModbusMaster master = factory.createTcpMaster(ipParameters, false);
                master.init();

                // Gerçek bağlantıyı test etmek için bir okuma işlemi yapalım
                if (master.isInitialized()) {
                    BaseLocator<Number> testLocator = BaseLocator.holdingRegister(
                            1,
                            0,
                            DataType.TWO_BYTE_INT_SIGNED
                    ); // Varsayılan bir holding okuması

                    Number masterValue = master.getValue(testLocator);// Bu işlem başarılı olursa bağlantı gerçekten kurulmuştur.
                    Log.d(TAG, "PLC'ye başarıyla bağlanıldı ve test okuması başarılı.");
                    // Bağlantı bilgilerini statik sınıfa kaydet
                    ModbusManager.setModbusMaster(master);
                    ModbusManager.setPlcIpAddress(plcIpAddress);
                    ModbusManager.setPlcPort(plcPort);
                    return master;
                }
            } catch (ModbusTransportException e) {
                Log.e(TAG, "Modbus Transport Hatası: Bağlantı kurulamadı. " + e.getMessage(), e);
            } catch (Exception e) {
                Log.e(TAG, "Genel Bağlantı Hatası: " + e.getMessage(), e);
            }
            return null; // Bağlantı başarısız olursa null döner
        }

        @Override
        protected void onPostExecute(ModbusMaster master) {
            loginBtn.setEnabled(true);
            if (master != null) {
                Toast.makeText(LoginActivity.this, "PLC'ye bağlandı! Kontrol ekranına yönlendiriliyorsunuz.", Toast.LENGTH_LONG).show();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "Bağlantı hatası! IP ve portu kontrol edin.", Toast.LENGTH_LONG).show();
            }
        }
    }

}
