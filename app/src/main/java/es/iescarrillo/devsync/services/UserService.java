package es.iescarrillo.devsync.services;

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
     * @param user Usuario a guardar.
     */
    public void saveUser(User user) {
        databaseReference.child(user.getId()).setValue(user);
    }

    /**
     * Actualiza un usuario existente.
     *
     * @param user Usuario con los datos actualizados.
     */
    public void updateUser(User user) {
        databaseReference.child(user.getId()).setValue(user);
    }

    /**
     * Elimina un usuario de la base de datos.
     *
     * @param userId ID del usuario a eliminar.
     */
    public void deleteUser(String userId) {
        databaseReference.child(userId).removeValue();
    }

}
