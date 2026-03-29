package es.iescarrillo.devsync.services;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Servicio encargado de gestionar la autenticación de usuarios con Firebase.
 * Permite registrar, iniciar sesión, cerrar sesión y obtener el usuario actual.
 */
public class AuthService {
    private FirebaseAuth auth;

    /**
     * Constructor que inicializa FirebaseAuth.
     */
    public AuthService() {
        auth = FirebaseAuth.getInstance();
    }

    /**
     * Registra un nuevo usuario en Firebase Authentication.
     *
     * @param email    Correo electrónico del usuario.
     * @param password Contraseña del usuario.
     * @param listener Listener para gestionar el resultado de la operación.
     */
    public void register(String email, String password, OnCompleteListener<AuthResult> listener) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(listener);
    }

    /**
     * Inicia sesión con email y contraseña.
     *
     * @param email    Correo electrónico.
     * @param password Contraseña.
     * @param listener Listener para gestionar el resultado.
     */
    public void login(String email, String password, OnCompleteListener<AuthResult> listener) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(listener);
    }

    /**
     * Cierra la sesión del usuario actual.
     */
    public void logout() {
        auth.signOut();
    }

    /**
     * Devuelve el usuario actualmente autenticado.
     *
     * @return FirebaseUser actual o null si no hay sesión.
     */
    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }
}

