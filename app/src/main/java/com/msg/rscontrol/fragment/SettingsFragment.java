package com.msg.rscontrol.fragment;

import static com.msg.rscontrol.fragment.NotificationFragment.addToLog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.msg.rscontrol.R;

import java.util.Objects;

public class SettingsFragment extends Fragment {

    private EditText email, oldPassword, newPassword;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        email = view.findViewById(R.id.email);
        email.setText(user != null ? user.getEmail() : null);
        oldPassword = view.findViewById(R.id.oldPassword);
        newPassword = view.findViewById(R.id.newPassword);
        Button changeBtn = view.findViewById(R.id.btn_change);


        changeBtn.setOnClickListener(v -> {
            if (user != null) {
                changePasswordButton(user);
            }
        });
        return view;
    }

    private void changePasswordButton(FirebaseUser user)
    {
        AuthCredential credential = EmailAuthProvider.getCredential(email.getText().toString(), oldPassword.getText().toString());
        user.reauthenticate(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        changePassword(user,newPassword.getText().toString());
                    } else {
                        // Hata oluştu, genellikle şifre yanlış girilmiştir
                        addToLog("Mevcut şifreniz yanlış!");
                        Toast.makeText(requireContext(), "Mevcut şifreniz yanlış!", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void changePassword(FirebaseUser user, String newPassword) {
        user.updatePassword(newPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Şifre başarıyla değiştirildi
                        addToLog("Mevcut şifreniz yanlış!");
                        Toast.makeText(requireContext(), "Şifre başarıyla değiştirildi.", Toast.LENGTH_SHORT).show();

                        // İsteğe bağlı olarak kullanıcıyı bir sonraki ekrana yönlendirebilirsiniz
                        // veya oturumunu kapatıp tekrar giriş yapmasını isteyebilirsiniz.
                    } else {
                        // Şifre değiştirme hatası oluştu
                        // En sık rastlanan hata: Şifrenin çok kısa olması veya
                        // tekrar oturum açma gereksinimi (bu senaryoyu zaten ele aldık)
                        addToLog("Şifre değiştirme hatası");
                        Toast.makeText(requireContext(), "Şifre değiştirme hatası: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
