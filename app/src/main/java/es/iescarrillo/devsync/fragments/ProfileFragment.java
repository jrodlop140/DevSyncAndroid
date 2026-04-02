package es.iescarrillo.devsync.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.iescarrillo.devsync.R;
import es.iescarrillo.devsync.activities.LoginActivity;
import es.iescarrillo.devsync.models.User;
import es.iescarrillo.devsync.services.StorageService;
import es.iescarrillo.devsync.services.UserService;

/**
 * Fragmento encargado de mostrar y editar el perfil del usuario.
 * Ahora incluye guardado automático de cambios (skills y foto).
 */
public class ProfileFragment extends Fragment {

    // Componentes de la interfaz
    private TextView tvName, tvEmail;
    private EditText etSkills;
    private ImageView ivProfilePhoto;
    private Button btnLogout, btnSelectPhoto;

    // Servicios
    private UserService userService;
    private StorageService storageService;

    // Datos
    private User currentUser;
    private Uri selectedImageUri;

    // Bandera para controlar si el usuario está seleccionando una imagen.
    // Se usa para evitar guardar skills automáticamente al abrir la galería.
    private boolean isPickingImage = false;

    // Bandera para controlar si se está subiendo una foto.
    // Se usa para bloquear la interacción mientras dura la subida.
    private boolean isUploadingPhoto = false;

    // Selector de imágenes
    private ActivityResultLauncher<String> imagePickerLauncher;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initializeComponents(view);
        initializeServices();
        initializeImagePicker();
        loadUserData();
        initializeListeners();

        return view;
    }

    /**
     * Guarda automáticamente las skills cuando el fragment deja de estar en primer plano.
     * Esto ocurre, por ejemplo, al cambiar a otro fragment o salir de la pantalla.
     * No se ejecuta si el usuario está abriendo el selector de imágenes o si se está
     * subiendo una foto.
     */
    @Override
    public void onPause() {
        super.onPause();

        if (!isPickingImage && !isUploadingPhoto) {
            saveSkillsIfChanged();
        }
    }

    /**
     * Inicializa los componentes de la interfaz.
     *
     * @param view Vista principal del fragment.
     */
    private void initializeComponents(View view) {
        tvName = view.findViewById(R.id.tvProfileName);
        tvEmail = view.findViewById(R.id.tvProfileEmail);
        etSkills = view.findViewById(R.id.etProfileSkills);
        ivProfilePhoto = view.findViewById(R.id.ivProfilePhoto);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnSelectPhoto = view.findViewById(R.id.btnSelectPhoto);
    }

    /**
     * Inicializa los servicios necesarios.
     */
    private void initializeServices() {
        userService = new UserService();
        storageService = new StorageService();
    }

    /**
     * Inicializa el selector de imágenes para elegir una foto desde la galería.
     * Además, guarda automáticamente la nueva foto seleccionada.
     */
    private void initializeImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    // El selector ya se ha cerrado, por lo que deja de considerarse
                    // que el usuario está eligiendo una imagen.
                    isPickingImage = false;

                    if (uri != null) {
                        selectedImageUri = uri;
                        ivProfilePhoto.setImageURI(uri);

                        // Guardar automáticamente la foto al seleccionarla
                        saveProfilePhotoAutomatically();
                    }
                }
        );
    }

    /**
     * Inicializa los listeners de la interfaz.
     * Incluye guardado de skills al pulsar la acción "Done" del teclado.
     */
    private void initializeListeners() {
        btnLogout.setOnClickListener(v -> logout());

        btnSelectPhoto.setOnClickListener(v -> {
            // Activar bandera para impedir guardado de skills al abrir la galería
            isPickingImage = true;
            imagePickerLauncher.launch("image/*");
        });

        etSkills.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                saveSkillsIfChanged();
                etSkills.clearFocus();
                return true;
            }
            return false;
        });
    }

    /**
     * Carga los datos del usuario actual desde Firebase.
     */
    private void loadUserData() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            showToastSafely("Invalid session", Toast.LENGTH_LONG);
            return;
        }

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        userService.getUserById(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentUser = snapshot.getValue(User.class);

                if (currentUser != null) {
                    currentUser.setId(snapshot.getKey());

                    tvName.setText(currentUser.getName());
                    tvEmail.setText(currentUser.getEmail());

                    // Mostrar skills
                    if (currentUser.getSkills() != null && !currentUser.getSkills().isEmpty()) {
                        etSkills.setText(TextUtils.join(", ", currentUser.getSkills()));
                    } else {
                        etSkills.setText("");
                    }

                    // Mostrar foto si existe
                    if (!TextUtils.isEmpty(currentUser.getPhoto())) {
                        Picasso.get()
                                .load(currentUser.getPhoto())
                                .into(ivProfilePhoto);
                    }

                } else {
                    showToastSafely("User not found", Toast.LENGTH_LONG);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showToastSafely("Error loading user", Toast.LENGTH_LONG);
            }
        });
    }

    /**
     * Guarda las skills únicamente si han cambiado respecto a las almacenadas
     * en el usuario actual.
     */
    private void saveSkillsIfChanged() {
        if (currentUser == null || isUploadingPhoto) {
            return;
        }

        String skillsText = etSkills.getText().toString().trim();
        List<String> newSkills = parseSkills(skillsText);

        List<String> currentSkills = currentUser.getSkills();
        if (currentSkills == null) {
            currentSkills = new ArrayList<>();
        }

        if (currentSkills.equals(newSkills)) {
            return;
        }

        currentUser.setSkills(newSkills);

        userService.updateUser(currentUser, task -> {
            if (task.isSuccessful()) {
                showToastSafely("Skills updated", Toast.LENGTH_SHORT);
            } else {
                showToastSafely("Error saving skills", Toast.LENGTH_LONG);
            }
        });
    }

    /**
     * Sube automáticamente la nueva foto de perfil a Firebase Storage
     * y actualiza la URL en el usuario.
     */
    private void saveProfilePhotoAutomatically() {
        if (currentUser == null || selectedImageUri == null) {
            return;
        }

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            showToastSafely("Invalid session", Toast.LENGTH_LONG);
            return;
        }

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Bloquear la interacción mientras se sube la imagen
        setUploadingState(true);

        storageService.uploadProfilePhoto(uid, selectedImageUri,
                downloadUri -> {
                    currentUser.setPhoto(downloadUri.toString());

                    userService.updateUser(currentUser, task -> {
                        // Restaurar la interfaz al finalizar la operación
                        setUploadingState(false);

                        if (task.isSuccessful()) {
                            showToastSafely("Photo updated", Toast.LENGTH_SHORT);
                        } else {
                            showToastSafely("Error saving photo", Toast.LENGTH_LONG);
                        }
                    });
                },
                e -> {
                    // Restaurar la interfaz si ocurre un error durante la subida
                    setUploadingState(false);
                    showToastSafely("Error uploading photo", Toast.LENGTH_LONG);
                }
        );
    }

    /**
     * Activa o desactiva la interacción del usuario durante la subida de la foto.
     *
     * @param isLoading true si se está subiendo la foto, false en caso contrario.
     */
    private void setUploadingState(boolean isLoading) {
        isUploadingPhoto = isLoading;

        btnSelectPhoto.setEnabled(!isLoading);
        etSkills.setEnabled(!isLoading);

        // Opcional: bloquear también logout mientras se sube la foto
        // btnLogout.setEnabled(!isLoading);
    }

    /**
     * Convierte un String de skills separadas por comas en una lista.
     *
     * @param skillsText Texto introducido por el usuario.
     * @return Lista de skills.
     */
    private List<String> parseSkills(String skillsText) {
        if (TextUtils.isEmpty(skillsText)) {
            return new ArrayList<>();
        }

        List<String> rawList = Arrays.asList(skillsText.split(","));
        List<String> cleanList = new ArrayList<>();

        for (String skill : rawList) {
            String clean = skill.trim();
            if (!clean.isEmpty()) {
                cleanList.add(clean);
            }
        }

        return cleanList;
    }

    /**
     * Muestra un mensaje Toast solo si el fragment sigue asociado a un contexto válido.
     *
     * @param message  Mensaje a mostrar.
     * @param duration Duración del Toast.
     */
    private void showToastSafely(String message, int duration) {
        if (isAdded() && getContext() != null) {
            Toast.makeText(getContext(), message, duration).show();
        }
    }

    /**
     * Cierra la sesión del usuario.
     */
    private void logout() {
        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}