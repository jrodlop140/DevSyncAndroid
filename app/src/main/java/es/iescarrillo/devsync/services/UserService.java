package es.iescarrillo.devsync.services;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import es.iescarrillo.devsync.models.User;

/**
 * Servicio encargado de gestionar las operaciones sobre los usuarios
 * en Firebase Realtime Database.
 */
public class UserService {
    private DatabaseReference databaseReference;

    /**
     * Constructor que inicializa la referencia a la base de datos.
     */
    public UserService() {
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
    }

    /**
     * Guarda un usuario en la base de datos.
     *
     * @param user     Usuario a guardar.
     * @param listener Listener para conocer el resultado de la operación.
     */
    public void saveUser(User user, OnCompleteListener<Void> listener) {
        databaseReference.child(user.getId()).setValue(user).addOnCompleteListener(listener);
    }

    /**
     * Actualiza un usuario existente.
     *
     * @param user     Usuario con los datos actualizados.
     * @param listener Listener para conocer el resultado de la operación.
     */
    public void updateUser(User user, OnCompleteListener<Void> listener) {
        databaseReference.child(user.getId()).setValue(user).addOnCompleteListener(listener);
    }

    /**
     * Elimina un usuario de la base de datos.
     *
     * @param userId   ID del usuario a eliminar.
     * @param listener Listener para conocer el resultado de la operación.
     */
    public void deleteUser(String userId, OnCompleteListener<Void> listener) {
        databaseReference.child(userId).removeValue().addOnCompleteListener(listener);
    }

    /**
     * Obtiene una referencia a todos los usuarios de la base de datos.
     *
     * @return Referencia a todos los usuarios.
     */
    public DatabaseReference getAllUsers() {
        return databaseReference;
    }
}