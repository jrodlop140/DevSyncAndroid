package es.iescarrillo.devsync.activities;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

import es.iescarrillo.devsync.R;
import es.iescarrillo.devsync.models.User;
import es.iescarrillo.devsync.services.AuthService;
import es.iescarrillo.devsync.services.UserService;

public class RegisterActivity extends AppCompatActivity {
    // Componentes de la vista //
    private EditText etName, etEmail, etPassword, etConfirmPassword;
    private Button btnRegister, btnGoLogin;

    // Servicios //
    private AuthService authService;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeComponents();
        initializeServices();
        initializeListeners();
    }

    // Inicializa los componentes de la vista //
    private void initializeComponents() {
        etName = findViewById(R.id.etNameRegister);
        etEmail = findViewById(R.id.etEmailRegister);
        etPassword = findViewById(R.id.etPasswordRegister);
        etConfirmPassword = findViewById(R.id.etConfirmPasswordRegister);

        btnRegister = findViewById(R.id.btnRegister);
        btnGoLogin = findViewById(R.id.btnGoLogin);
    }

    // Inicializa los servicios //
    private void initializeServices() {
        authService = new AuthService();
        userService = new UserService();
    }

    // Inicializa los listeners //
    private void initializeListeners() {
        btnRegister.setOnClickListener(v -> registerUser());

        btnGoLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Método encargado de registrar un nuevo usuario.
     * Realiza la validación de datos, crea el usuario en Firebase Auth
     * y guarda sus datos en la base de datos.
     */
    private void registerUser() {

        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (!validateFields(name, email, password, confirmPassword)) {
            return;
        }

        authService.register(email, password, task -> {
            if (task.isSuccessful()) {

                String uid = authService.getCurrentUser().getUid();

                User user = new User();
                user.setId(uid);
                user.setName(name);
                user.setEmail(email);
                user.setPhoto("");
                user.setSkills(new ArrayList<>());
                user.setProjects(new HashMap<>());
                user.setCreatedAt(System.currentTimeMillis());

                userService.saveUser(user, saveTask -> {
                    if (saveTask.isSuccessful()) {

                        Toast.makeText(RegisterActivity.this,
                                "Usuario registrado correctamente",
                                Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();

                    } else {

                        String error = saveTask.getException() != null
                                ? saveTask.getException().getMessage()
                                : "Error al guardar en base de datos";

                        Toast.makeText(RegisterActivity.this,
                                error,
                                Toast.LENGTH_LONG).show();
                    }
                });

            } else {

                String error = task.getException() != null
                        ? task.getException().getMessage()
                        : "Error en el registro";

                Toast.makeText(RegisterActivity.this,
                        error,
                        Toast.LENGTH_LONG).show();
            }
        });
    }


    // Valida los campos del formulario //
    private boolean validateFields(String name, String email, String password, String confirmPassword) {

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_LONG).show();
            return false;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
}
