package es.iescarrillo.devsync.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import es.iescarrillo.devsync.R;
import es.iescarrillo.devsync.enums.TaskStatus;
import es.iescarrillo.devsync.models.Project;
import es.iescarrillo.devsync.models.Request;
import es.iescarrillo.devsync.models.Task;
import es.iescarrillo.devsync.models.User;
import es.iescarrillo.devsync.services.ProjectService;
import es.iescarrillo.devsync.services.RequestService;
import es.iescarrillo.devsync.services.TaskService;
import es.iescarrillo.devsync.services.UserService;

/**
 * Fragmento encargado de mostrar un resumen general del usuario.
 */
public class HomeFragment extends Fragment {

    private TextView tvHomeWelcome, tvHomeProjects, tvHomeRequests,
            tvHomeTasksAssigned, tvHomeTasksPending, tvHomeTasksInProgress, tvHomeTasksDone;

    private UserService userService;
    private ProjectService projectService;
    private RequestService requestService;
    private TaskService taskService;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initializeComponents(view);
        initializeServices();
        loadHomeData();

        return view;
    }

    /**
     * Inicializa los componentes de la interfaz.
     *
     * @param view Vista principal del fragment.
     */
    private void initializeComponents(View view) {
        tvHomeWelcome = view.findViewById(R.id.tvHomeWelcome);
        tvHomeProjects = view.findViewById(R.id.tvHomeProjects);
        tvHomeRequests = view.findViewById(R.id.tvHomeRequests);
        tvHomeTasksAssigned = view.findViewById(R.id.tvHomeTasksAssigned);
        tvHomeTasksPending = view.findViewById(R.id.tvHomeTasksPending);
        tvHomeTasksInProgress = view.findViewById(R.id.tvHomeTasksInProgress);
        tvHomeTasksDone = view.findViewById(R.id.tvHomeTasksDone);
    }

    /**
     * Inicializa los servicios necesarios.
     */
    private void initializeServices() {
        userService = new UserService();
        projectService = new ProjectService();
        requestService = new RequestService();
        taskService = new TaskService();
    }

    /**
     * Carga todos los datos del home.
     */
    private void loadHomeData() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(getContext(), "Invalid session", Toast.LENGTH_LONG).show();
            return;
        }

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        loadWelcome(currentUserId);
        loadProjectsCount(currentUserId);
        loadRequestsCount(currentUserId);
        loadTasksSummary(currentUserId);
    }

    /**
     * Carga el saludo con el nombre del usuario.
     *
     * @param currentUserId Id del usuario actual.
     */
    private void loadWelcome(String currentUserId) {
        userService.getUserById(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                if (user != null && user.getName() != null) {
                    tvHomeWelcome.setText("Welcome, " + user.getName());
                } else {
                    tvHomeWelcome.setText("Welcome");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null && isAdded()) {
                    Toast.makeText(getContext(), "Error loading user", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Carga el número de proyectos en los que participa el usuario.
     *
     * @param currentUserId Id del usuario actual.
     */
    private void loadProjectsCount(String currentUserId) {
        projectService.getAllProjects().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalProjects = 0;

                for (DataSnapshot data : snapshot.getChildren()) {
                    Project project = data.getValue(Project.class);

                    if (project != null && project.getParticipants() != null
                            && project.getParticipants().containsKey(currentUserId)) {
                        totalProjects++;
                    }
                }

                tvHomeProjects.setText("Projects: " + totalProjects);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null && isAdded()) {
                    Toast.makeText(getContext(), "Error loading projects", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Carga el número de solicitudes enviadas por el usuario.
     *
     * @param currentUserId Id del usuario actual.
     */
    private void loadRequestsCount(String currentUserId) {
        requestService.getAllRequests()
                .orderByChild("developerId")
                .equalTo(currentUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int totalRequests = (int) snapshot.getChildrenCount();
                        tvHomeRequests.setText("Requests: " + totalRequests);
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
     * Carga el resumen de tareas asignadas al usuario.
     *
     * @param currentUserId Id del usuario actual.
     */
    private void loadTasksSummary(String currentUserId) {
        taskService.getAllTasks().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalAssignedActive = 0;
                int totalPending = 0;
                int totalInProgress = 0;
                int totalDone = 0;

                for (DataSnapshot data : snapshot.getChildren()) {
                    Task task = data.getValue(Task.class);

                    if (task != null && currentUserId.equals(task.getAssignedDeveloper())) {

                        if (task.getStatus() == TaskStatus.PENDING) {
                            totalAssignedActive++;
                            totalPending++;
                        } else if (task.getStatus() == TaskStatus.IN_PROGRESS) {
                            totalAssignedActive++;
                            totalInProgress++;
                        } else if (task.getStatus() == TaskStatus.DONE) {
                            totalDone++;
                        }
                    }
                }

                tvHomeTasksAssigned.setText(String.valueOf(totalAssignedActive));
                tvHomeTasksPending.setText(String.valueOf(totalPending));
                tvHomeTasksInProgress.setText(String.valueOf(totalInProgress));
                tvHomeTasksDone.setText(String.valueOf(totalDone));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null && isAdded()) {
                    Toast.makeText(getContext(), "Error loading tasks", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}