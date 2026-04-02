package es.iescarrillo.devsync.fragments;

import android.content.Intent;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import es.iescarrillo.devsync.R;
import es.iescarrillo.devsync.activities.LoginActivity;
import es.iescarrillo.devsync.models.User;
import es.iescarrillo.devsync.services.UserService;

/**
 * Fragmento encargado de mostrar el perfil del usuario.
 */
public class ProfileFragment extends Fragment {

    private TextView tvName, tvEmail, tvSkills;
    private Button btnLogout;

    private UserService userService;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initializeComponents(view);
        initializeServices();
        loadUserData();
        initializeListeners();

        return view;
    }

    private void initializeComponents(View view) {
        tvName = view.findViewById(R.id.tvProfileName);
        tvEmail = view.findViewById(R.id.tvProfileEmail);
        tvSkills = view.findViewById(R.id.tvProfileSkills);
        btnLogout = view.findViewById(R.id.btnLogout);
    }

    private void initializeServices() {
        userService = new UserService();
    }

    private void initializeListeners() {
        btnLogout.setOnClickListener(v -> logout());
    }

    /**
     * Carga los datos del usuario actual desde Firebase.
     */
    private void loadUserData() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        userService.getUserById(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                if (user != null) {
                    tvName.setText(user.getName());
                    tvEmail.setText(user.getEmail());

                    if (user.getSkills() != null && !user.getSkills().isEmpty()) {
                        tvSkills.setText(TextUtils.join(", ", user.getSkills()));
                    } else {
                        tvSkills.setText("Sin habilidades");
                    }

                } else {
                    Toast.makeText(getContext(), "Usuario no encontrado", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error al cargar usuario", Toast.LENGTH_LONG).show();
            }
        });
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