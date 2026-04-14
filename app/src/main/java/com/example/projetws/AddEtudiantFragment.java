package com.example.projetws;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.HashMap;
import java.util.Map;

public class AddEtudiantFragment extends Fragment {
    private EditText nom, prenom;
    private Spinner ville;
    private RadioButton m, f;
    private Button add;
    private RequestQueue requestQueue;
    private static final String insertUrl = "http://10.0.2.2/lab9/ws/createEtudiant.php";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_add_etudiant, container, false);
        nom = view.findViewById(R.id.nom);
        prenom = view.findViewById(R.id.prenom);
        ville = view.findViewById(R.id.ville);
        m = view.findViewById(R.id.m);
        f = view.findViewById(R.id.f);
        add = view.findViewById(R.id.add);
        requestQueue = Volley.newRequestQueue(getContext());
        add.setOnClickListener(v -> envoyerEtudiant());
        return view;
    }

    private void envoyerEtudiant() {
        StringRequest request = new StringRequest(Request.Method.POST, insertUrl,
                response -> Toast.makeText(getContext(), "Ajouté avec succès", Toast.LENGTH_SHORT).show(),
                error -> Log.e("VOLLEY", error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("nom", nom.getText().toString());
                params.put("prenom", prenom.getText().toString());
                params.put("ville", ville.getSelectedItem().toString());
                params.put("sexe", m.isChecked() ? "homme" : "femme");
                return params;
            }
        };
        requestQueue.add(request);
    }
}
