package es.iescarrillo.devsync.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import es.iescarrillo.devsync.R;

/**
 * Fragmento perfil de la aplicación.
 */
public class ProfileFragment extends Fragment {
    //Cosntructor vacío requerido
    public ProfileFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
}