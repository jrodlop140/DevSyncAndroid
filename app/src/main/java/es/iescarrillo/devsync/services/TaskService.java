package es.iescarrillo.devsync.services;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Servicio encargado de gestionar las tareas en Firebase Realtime Database.
 */
public class TaskService {

    private final DatabaseReference databaseReference;

    /**
     * Constructor que inicializa la referencia a las tareas.
     */
    public TaskService() {
        databaseReference = FirebaseDatabase
                .getInstance()
                .getReference("tasks");
    }

    /**
     * Obtiene una referencia a todas las tareas.
     *
     * @return Referencia a las tareas.
     */
    public DatabaseReference getAllTasks() {
        return databaseReference;
    }
}