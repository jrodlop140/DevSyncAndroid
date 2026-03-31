package es.iescarrillo.devsync.services;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import es.iescarrillo.devsync.models.Request;

/**
 * Servicio encargado de gestionar las solicitudes de unión a proyectos
 * en Firebase Realtime Database.
 */
public class RequestService {

    private final DatabaseReference databaseReference;

    /**
     * Constructor que inicializa la referencia a la base de datos.
     */
    public RequestService() {
        databaseReference = FirebaseDatabase
                .getInstance()
                .getReference("requests");
    }

    /**
     * Guarda una solicitud en la base de datos usando una clave compuesta
     * por el id del desarrollador y el id del proyecto.
     *
     * @param request Solicitud a guardar.
     */
    public void saveRequest(Request request) {
        String id = request.getDeveloperId() + "_" + request.getProjectId();
        request.setId(id);
        databaseReference.child(id).setValue(request);
    }

    /**
     * Obtiene una referencia a todas las solicitudes.
     *
     * @return Referencia a las solicitudes.
     */
    public DatabaseReference getAllRequests() {
        return databaseReference;
    }

    /**
     * Obtiene una referencia a una solicitud concreta a partir del desarrollador
     * y el proyecto.
     *
     * @param developerId Id del desarrollador.
     * @param projectId   Id del proyecto.
     * @return Referencia a la solicitud concreta.
     */
    public DatabaseReference getRequestByDeveloperAndProject(String developerId, String projectId) {
        String requestId = developerId + "_" + projectId;
        return databaseReference.child(requestId);
    }
}