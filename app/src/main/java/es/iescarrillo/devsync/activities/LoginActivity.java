package es.iescarrillo.devsync.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import es.iescarrillo.devsync.R;
import es.iescarrillo.devsync.services.AuthService;

/**
 * Actividad de inicio de sesión.
 * Permite comprobar si el usuario se autentica correctamente en Firebase.
 */
public class LoginActivity extends AppCompatActivity {

    // Componentes de la vista //
    private EditText etEmailLogin, etPasswordLogin;
    private Button btnLogin;

    private TextView tvGoToSignup;

    // Servicios //
    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeComponents();
        initializeServices();
        initializeListeners();
    }

    /**
     * Inicializa los componentes de la interfaz.
     */
    private void initializeComponents() {
        etEmailLogin = findViewById(R.id.etEmailLogin);
        etPasswordLogin = findViewById(R.id.etPasswordLogin);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToSignup = findViewById(R.id.tvGoToSignup);
    }

    /**
     * Inicializa los servicios.
     */
    private void initializeServices() {
        authService = new AuthService();
    }

    /**
     * Inicializa los listeners de los botones.
     */
    private void initializeListeners() {
        btnLogin.setOnClickListener(v -> loginUser());

        tvGoToSignup.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Método para iniciar sesión con email y contraseña.
     */
    private void loginUser() {
        String email = etEmailLogin.getText().toString().trim();
        String password = etPasswordLogin.getText().toString().trim();

        if (!validateFields(email, password)) {
            return;
        }

        authService.login(email, password, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            } else {
                String error = task.getException() != null
                        ? task.getException().getMessage()
                        : "Incorrect email or password";

                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Valida los campos del formulario de login.
     *
     * @param email    Correo electrónico.
     * @param password Contraseña.
     * @return true si los datos son válidos, false en caso contrario.
     */
    private boolean validateFields(String email, String password) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
}