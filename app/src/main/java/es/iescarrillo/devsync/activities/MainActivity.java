package es.iescarrillo.devsync.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import es.iescarrillo.devsync.R;
import es.iescarrillo.devsync.fragments.HomeFragment;
import es.iescarrillo.devsync.fragments.ProfileFragment;
import es.iescarrillo.devsync.fragments.ProjectsFragment;
import es.iescarrillo.devsync.fragments.RequestsFragment;

/**
 * Actividad principal de la aplicación.
 * <p>
 * Se encarga de gestionar la navegación mediante BottomNavigationView
 * y cargar los distintos fragments (Home, Projects, Requests, Profile).
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Componente de navegación inferior
     */
    private BottomNavigationView bottomNavigation;

    /**
     * Fragment seleccionado actualmente
     */
    private Fragment selectedFragment;

    /**
     * Método que se ejecuta al crear la actividad.
     * Inicializa la interfaz, configura la navegación y carga el fragment inicial.
     *
     * @param savedInstanceState Estado previo de la actividad (si existe).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Ajuste de padding para evitar solapamiento con barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeComponents();
        initializeNavigation();

        // Cargar fragment inicial solo si es la primera vez
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }
    }

    /**
     * Inicializa los componentes de la interfaz.
     */
    private void initializeComponents() {
        bottomNavigation = findViewById(R.id.bottomNavigation);
    }

    /**
     * Configura la navegación del BottomNavigationView.
     * Permite cambiar entre los distintos fragments de la aplicación.
     */
    private void initializeNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {

            selectedFragment = null;

            if (item.getItemId() == R.id.nav_home) {
                selectedFragment = new HomeFragment();

            } else if (item.getItemId() == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();

            } else if (item.getItemId() == R.id.nav_projects) {
                selectedFragment = new ProjectsFragment();

            } else if (item.getItemId() == R.id.nav_requests) {
                selectedFragment = new RequestsFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }

            return false;
        });
    }

    /**
     * Carga un fragment en el contenedor principal de la actividad.
     *
     * @param fragment Fragment que se desea mostrar.
     */
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}