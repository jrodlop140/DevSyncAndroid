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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import es.iescarrillo.devsync.R;
import es.iescarrillo.devsync.adapters.RequestAdapter;
import es.iescarrillo.devsync.models.Project;
import es.iescarrillo.devsync.models.Request;
import es.iescarrillo.devsync.models.RequestItem;
import es.iescarrillo.devsync.services.ProjectService;
import es.iescarrillo.devsync.services.RequestService;

/**
 * Fragmento encargado de mostrar las solicitudes enviadas por el usuario actual.
 */
public class RequestsFragment extends Fragment {

    private RecyclerView rvRequests;

    private RequestAdapter requestAdapter;
    private List<RequestItem> requestItemList;

    private RequestService requestService;
    private ProjectService projectService;

    public RequestsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_requests, container, false);

        initializeComponents(view);
        initializeServices();
        initializeRecyclerView();
        loadRequests();

        return view;
    }

    /**
     * Inicializa los componentes de la interfaz.
     *
     * @param view Vista principal del fragment.
     */
    private void initializeComponents(View view) {
        rvRequests = view.findViewById(R.id.rvRequests);
    }

    /**
     * Inicializa los servicios y estructuras de datos necesarias.
     */
    private void initializeServices() {
        requestService = new RequestService();
        projectService = new ProjectService();
        requestItemList = new ArrayList<>();
    }

    /**
     * Inicializa el RecyclerView y su adaptador.
     */
    private void initializeRecyclerView() {
        requestAdapter = new RequestAdapter(requestItemList);
        rvRequests.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRequests.setAdapter(requestAdapter);
    }

    /**
     * Carga las solicitudes enviadas por el usuario actual.
     */
    private void loadRequests() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(getContext(), "Invalid session", Toast.LENGTH_LONG).show();
            return;
        }

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        requestService.getAllRequests()
                .orderByChild("developerId")
                .equalTo(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        requestItemList.clear();

                        for (DataSnapshot data : snapshot.getChildren()) {
                            Request request = data.getValue(Request.class);

                            if (request != null) {
                                loadProjectNameAndAddRequest(request);
                            }
                        }

                        if (snapshot.getChildrenCount() == 0) {
                            requestAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        if (FirebaseAuth.getInstance().getCurrentUser() != null && isAdded()) {
                            Toast.makeText(getContext(), "Error loading requests", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /**
     * Carga el nombre del proyecto asociado a la solicitud y lo añade a la lista visual.
     *
     * @param request Solicitud del usuario.
     */
    private void loadProjectNameAndAddRequest(Request request) {
        projectService.getProjectById(request.getProjectId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Project project = snapshot.getValue(Project.class);

                        String projectName = "Unknown project";
                        if (project != null && project.getName() != null) {
                            projectName = project.getName();
                        }

                        RequestItem requestItem = new RequestItem(
                                projectName,
                                request.getState(),
                                request.getRequestDate()
                        );

                        requestItemList.add(requestItem);
                        requestAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        if (FirebaseAuth.getInstance().getCurrentUser() != null && isAdded()) {
                            Toast.makeText(getContext(), "Error loading project", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}