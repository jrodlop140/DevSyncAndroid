package es.iescarrillo.devsync.services;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProjectService {

    private final DatabaseReference databaseReference;

    /**
     * Constructor que inicializa la referencia a la base de datos de Firebase.
     */
    public ProjectService() {
        databaseReference = FirebaseDatabase
                .getInstance()
                .getReference("projects");
    }

    /**
     * Obtiene una referencia a todos los proyectos en la base de datos.
     * @return DatabaseReference a los proyectos.
     */
    public DatabaseReference getAllProjects() {
        return databaseReference;
    }

    /**
     * Obtiene una referencia a un proyecto específico en la base de datos.
     * @param projectId ID del proyecto.
     * @return DatabaseReference al proyecto.
     */
    public DatabaseReference getProjectById(String projectId) {
        return databaseReference.child(projectId);
    }
}