package es.iescarrillo.devsync.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import es.iescarrillo.devsync.R;
import es.iescarrillo.devsync.adapters.ProjectAdapter;
import es.iescarrillo.devsync.models.Project;
import es.iescarrillo.devsync.services.ProjectService;

/**
 * Fragmento encargado de mostrar la lista de proyectos.
 */
public class ProjectsFragment extends Fragment {

    private RecyclerView rvProjects;
    private List<Project> projectList;
    private ProjectAdapter projectAdapter;
    private ProjectService projectService;

    // Constructor vacío requerido
    public ProjectsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_projects, container, false);

        initializeComponents(view);
        initializeServices();
        initializeRecyclerView();
        loadProjects();

        return view;
    }

    /**
     * Inicializa los componentes de la interfaz.
     *
     * @param view Vista principal del fragment.
     */
    private void initializeComponents(View view) {
        rvProjects = view.findViewById(R.id.rvProjects);
    }

    /**
     * Inicializa los servicios y estructuras de datos necesarias.
     */
    private void initializeServices() {
        projectService = new ProjectService();
        projectList = new ArrayList<>();
    }

    /**
     * Configura el RecyclerView y su adaptador.
     */
    private void initializeRecyclerView() {
        projectAdapter = new ProjectAdapter(projectList, this::openProjectDetail);
        rvProjects.setLayoutManager(new LinearLayoutManager(getContext()));
        rvProjects.setAdapter(projectAdapter);
    }

    /**
     * Carga los proyectos desde Firebase Realtime Database.
     */
    private void loadProjects() {
        projectService.getAllProjects().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                projectList.clear();

                for (DataSnapshot data : snapshot.getChildren()) {
                    Project project = data.getValue(Project.class);

                    if (project != null) {
                        project.setId(data.getKey());
                        projectList.add(project);
                    }
                }

                projectAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error al cargar proyectos", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Abre el fragmento de detalles del proyecto.
     *
     * @param project Proyecto seleccionado.
     */
    private void openProjectDetail(Project project) {
        ProjectDetailFragment detailFragment = new ProjectDetailFragment();

        Bundle bundle = new Bundle();
        bundle.putString("projectId", project.getId());
        detailFragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, detailFragment)
                .addToBackStack(null)
                .commit();
    }
}