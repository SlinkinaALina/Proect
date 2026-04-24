package com.example.obchayakopilka.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.obchayakopilka.R;
import com.example.obchayakopilka.database.AppDatabase;
import com.example.obchayakopilka.models.User;

public class RegisterActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etUsername;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private Button btnRegister;
    private AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        database = AppDatabase.getInstance(this);

        etEmail = findViewById(R.id.et_email);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnRegister = findViewById(R.id.btn_register);

        btnRegister.setOnClickListener(v -> register());
    }

    private void register() {
        String email = etEmail.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            User existingUser = database.userDao().getUserByEmail(email);
            if (existingUser != null) {
                runOnUiThread(() -> Toast.makeText(this, "Пользователь уже существует", Toast.LENGTH_SHORT).show());
                return;
            }

            User newUser = new User(email, username, password, "member", 1);
            database.userDao().insert(newUser);

            runOnUiThread(() -> {
                Toast.makeText(this, "Регистрация успешна!", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }
}
