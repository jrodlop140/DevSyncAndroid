package es.iescarrillo.devsync.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import es.iescarrillo.devsync.R;
import es.iescarrillo.devsync.models.Project;
import es.iescarrillo.devsync.services.ProjectService;

/**
 * Fragmento encargado de mostrar los detalles de un proyecto.
 */

public class ProjectDetailFragment extends Fragment {

    // Componentes de la interfaz
    private TextView tvProjectName, tvProjectDescription, tvProjectStatus, tvProjectCreator, tvProjectTechnologies;
    private ProjectService projectService;
    private String projectId;

    public ProjectDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_project_detail, container, false);

        initializeComponents(view);
        initializeServices();
        getArgumentsData();
        loadProject();

        return view;
    }

    /**
     * Inicializa los componentes de la interfaz.
     *
     * @param view Vista principal del fragment.
     */
    private void initializeComponents(View view) {
        tvProjectName = view.findViewById(R.id.tvDetailProjectName);
        tvProjectDescription = view.findViewById(R.id.tvDetailProjectDescription);
        tvProjectStatus = view.findViewById(R.id.tvDetailProjectStatus);
        tvProjectCreator = view.findViewById(R.id.tvDetailProjectCreator);
        tvProjectTechnologies = view.findViewById(R.id.tvDetailProjectTechnologies);
    }

    /**
     * Inicializa los servicios y estructuras de datos necesarias.
     */
    private void initializeServices() {
        projectService = new ProjectService();
    }

    /**
     * Obtiene los datos pasados por argumentos.
     */
    private void getArgumentsData() {
        if (getArguments() != null) {
            projectId = getArguments().getString("projectId");
        }
    }

    /**
     * Carga los detalles del proyecto desde Firebase Realtime Database.
     */
    private void loadProject() {
        if (TextUtils.isEmpty(projectId)) {
            Toast.makeText(getContext(), "Proyecto no válido", Toast.LENGTH_LONG).show();
            return;
        }

        projectService.getProjectById(projectId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Project project = snapshot.getValue(Project.class);

                if (project != null) {
                    project.setId(snapshot.getKey());

                    tvProjectName.setText(project.getName());
                    tvProjectDescription.setText(project.getDescription());
                    tvProjectCreator.setText(project.getCreator());

                    if (project.getStatus() != null) {
                        tvProjectStatus.setText(project.getStatus().name());
                    } else {
                        tvProjectStatus.setText("");
                    }

                    if (project.getTechnologies() != null && !project.getTechnologies().isEmpty()) {
                        tvProjectTechnologies.setText(TextUtils.join(", ", project.getTechnologies()));
                    }

                } else {
                    Toast.makeText(getContext(), "Proyecto no encontrado", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error al cargar el proyecto", Toast.LENGTH_LONG).show();
            }
        });
    }
}