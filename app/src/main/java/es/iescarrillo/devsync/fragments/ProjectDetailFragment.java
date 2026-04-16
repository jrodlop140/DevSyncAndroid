package es.iescarrillo.devsync.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
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
import es.iescarrillo.devsync.adapters.TaskAdapter;
import es.iescarrillo.devsync.enums.RequestState;
import es.iescarrillo.devsync.models.Project;
import es.iescarrillo.devsync.models.Request;
import es.iescarrillo.devsync.models.Task;
import es.iescarrillo.devsync.models.User;
import es.iescarrillo.devsync.services.ProjectService;
import es.iescarrillo.devsync.services.RequestService;
import es.iescarrillo.devsync.services.TaskService;
import es.iescarrillo.devsync.services.UserService;

/**
 * Fragmento encargado de mostrar los detalles de un proyecto.
 */
public class ProjectDetailFragment extends Fragment {

    // Componentes de la interfaz
    private TextView tvProjectName, tvProjectDescription, tvProjectStatus, tvProjectCreator,
            tvProjectTechnologies, tvProjectParticipants, tvProjectParticipantsNames,
            tvProjectTasksAccessMessage;
    private Button btnJoinProject;
    private RecyclerView rvProjectTasks;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;

    // Servicios
    private ProjectService projectService;
    private RequestService requestService;
    private UserService userService;
    private TaskService taskService;

    // Datos
    private String projectId;

    public ProjectDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_project_detail, container, false);

        initializeComponents(view);
        initializeServices();
        initializeTasksRecyclerView();
        getArgumentsData();
        initializeListeners();
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
        btnJoinProject = view.findViewById(R.id.btnJoinProject);
        tvProjectParticipants = view.findViewById(R.id.tvDetailProjectParticipants);
        tvProjectParticipantsNames = view.findViewById(R.id.tvDetailProjectParticipantsNames);
        rvProjectTasks = view.findViewById(R.id.rvProjectTasks);
        tvProjectTasksAccessMessage = view.findViewById(R.id.tvProjectTasksAccessMessage);
    }

    /**
     * Inicializa los servicios necesarios.
     */
    private void initializeServices() {
        projectService = new ProjectService();
        requestService = new RequestService();
        userService = new UserService();
        taskService = new TaskService();
        taskList = new ArrayList<>();
    }

    /**
     * Inicializa el RecyclerView de tareas del proyecto.
     */
    private void initializeTasksRecyclerView() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        taskAdapter = new TaskAdapter(taskList, currentUserId);
        rvProjectTasks.setLayoutManager(new LinearLayoutManager(getContext()));
        rvProjectTasks.setAdapter(taskAdapter);
    }

    /**
     * Inicializa los listeners de la interfaz.
     */
    private void initializeListeners() {
        btnJoinProject.setOnClickListener(v -> checkIfRequestExistsAndCreate());
    }

    /**
     * Obtiene los datos recibidos por argumentos.
     */
    private void getArgumentsData() {
        if (getArguments() != null) {
            projectId = getArguments().getString("projectId");
        }
    }

    /**
     * Comprueba si ya existe una solicitud para el usuario actual y el proyecto.
     * Si no existe, crea una nueva.
     * Si existe pero está REJECTED, permite volver a solicitar.
     */
    private void checkIfRequestExistsAndCreate() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        requestService.getRequestByDeveloperAndProject(currentUserId, projectId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (!snapshot.exists()) {
                            createJoinRequest(currentUserId);
                            return;
                        }

                        Request request = snapshot.getValue(Request.class);

                        if (request == null || request.getState() == null) {
                            createJoinRequest(currentUserId);
                            return;
                        }

                        if (request.getState() == RequestState.REJECTED) {
                            request.setState(RequestState.PENDING);
                            request.setRequestDate(String.valueOf(System.currentTimeMillis()));
                            request.setCreatedAt(System.currentTimeMillis());

                            requestService.saveRequest(request);

                            Toast.makeText(getContext(), "Request sent", Toast.LENGTH_LONG).show();
                            btnJoinProject.setEnabled(false);
                            btnJoinProject.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(getContext(),
                                    "You have already sent a request for this project",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(),
                                "Error checking the request",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Crea una solicitud de unión al proyecto.
     *
     * @param currentUserId Id del usuario actual.
     */
    private void createJoinRequest(String currentUserId) {
        Request request = new Request();
        request.setDeveloperId(currentUserId);
        request.setProjectId(projectId);
        request.setState(RequestState.PENDING);
        request.setRequestDate(String.valueOf(System.currentTimeMillis()));
        request.setCreatedAt(System.currentTimeMillis());

        requestService.saveRequest(request);

        Toast.makeText(getContext(), "Request sent", Toast.LENGTH_LONG).show();
        btnJoinProject.setEnabled(false);
        btnJoinProject.setVisibility(View.GONE);
    }

    /**
     * Carga las tareas asociadas al proyecto actual.
     */
    private void loadProjectTasks() {
        taskService.getAllTasks().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                taskList.clear();

                for (DataSnapshot data : snapshot.getChildren()) {
                    Task task = data.getValue(Task.class);

                    if (task != null && projectId.equals(task.getProjectId())) {
                        task.setId(data.getKey());
                        taskList.add(task);
                    }
                }

                taskAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error loading tasks", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Carga los detalles del proyecto desde Firebase Realtime Database.
     */
    private void loadProject() {
        if (TextUtils.isEmpty(projectId)) {
            Toast.makeText(getContext(), "Invalid project", Toast.LENGTH_LONG).show();
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
                    } else {
                        tvProjectTechnologies.setText("");
                    }

                    String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    // Comprobamos si el usuario actual es el creador o un participante
                    boolean isCreator = currentUserId.equals(project.getCreator());
                    boolean isParticipant = project.getParticipants() != null
                            && project.getParticipants().containsKey(currentUserId);

                    // Si el usuario es el creador o un participante, ocultamos el botón
                    if (isCreator || isParticipant) {
                        btnJoinProject.setVisibility(View.GONE);
                    } else {
                        btnJoinProject.setVisibility(View.GONE);
                        checkIfRequestAlreadyExists(currentUserId);
                    }

                    int totalParticipants = 0;
                    if (project.getParticipants() != null) {
                        totalParticipants = project.getParticipants().size();
                    }

                    tvProjectParticipants.setText("Participants: " + totalParticipants);

                    // Cargamos los nombres de los participantes
                    loadParticipantNames(project);

                    // Solo el creador o un participante pueden ver las tareas del proyecto
                    if (isCreator || isParticipant) {
                        rvProjectTasks.setVisibility(View.VISIBLE);
                        tvProjectTasksAccessMessage.setVisibility(View.GONE);
                        loadProjectTasks();
                    } else {
                        taskList.clear();
                        taskAdapter.notifyDataSetChanged();
                        rvProjectTasks.setVisibility(View.GONE);
                        tvProjectTasksAccessMessage.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(getContext(), "Project not found", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error loading project", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Carga los nombres de los participantes del proyecto.
     *
     * @param project Proyecto cuyos participantes se desean mostrar.
     */
    private void loadParticipantNames(Project project) {
        if (project.getParticipants() == null || project.getParticipants().isEmpty()) {
            tvProjectParticipantsNames.setText("No participants");
            return;
        }

        userService.getAllUsers().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                StringBuilder participantsNames = new StringBuilder();

                for (DataSnapshot data : snapshot.getChildren()) {
                    String userId = data.getKey();

                    if (project.getParticipants().containsKey(userId)) {
                        User user = data.getValue(User.class);

                        if (user != null && user.getName() != null) {
                            participantsNames.append("• ")
                                    .append(user.getName())
                                    .append("\n");
                        }
                    }
                }

                if (participantsNames.length() > 0) {
                    tvProjectParticipantsNames.setText(participantsNames.toString().trim());
                } else {
                    tvProjectParticipantsNames.setText("No participants");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error loading participants", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Comprueba si el usuario actual ya tiene una solicitud enviada para este proyecto.
     * Si está REJECTED, vuelve a mostrar el botón.
     *
     * @param currentUserId Id del usuario actual.
     */
    private void checkIfRequestAlreadyExists(String currentUserId) {
        requestService.getRequestByDeveloperAndProject(currentUserId, projectId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (!snapshot.exists()) {
                            btnJoinProject.setVisibility(View.VISIBLE);
                            btnJoinProject.setEnabled(true);
                            return;
                        }

                        Request request = snapshot.getValue(Request.class);

                        if (request == null || request.getState() == null) {
                            btnJoinProject.setVisibility(View.VISIBLE);
                            btnJoinProject.setEnabled(true);
                            return;
                        }

                        if (request.getState() == RequestState.REJECTED) {
                            btnJoinProject.setVisibility(View.VISIBLE);
                            btnJoinProject.setEnabled(true);
                        } else {
                            btnJoinProject.setVisibility(View.GONE);
                            btnJoinProject.setEnabled(false);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(),
                                "Error checking existing request",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}