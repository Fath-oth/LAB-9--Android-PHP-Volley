package com.example.projetws;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.projetws.beans.Etudiant;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListEtudiantFragment extends Fragment {
    private ListView listView;
    private RequestQueue requestQueue;
    private List<Etudiant> etudiantList = new ArrayList<>();
    private BaseAdapter adapter;

    private static final String loadUrl = "http://10.0.2.2/lab9/ws/loadEtudiant.php";
    private static final String deleteUrl = "http://10.0.2.2/lab9/ws/deleteEtudiant.php";
    private static final String updateUrl = "http://10.0.2.2/lab9/ws/updateEtudiant.php";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_list_etudiant, container, false);
        listView = view.findViewById(R.id.list_view);
        requestQueue = Volley.newRequestQueue(getContext());

        adapter = new BaseAdapter() {
            @Override
            public int getCount() { return etudiantList.size(); }
            @Override
            public Object getItem(int i) { return etudiantList.get(i); }
            @Override
            public long getItemId(int i) { return etudiantList.get(i).getId(); }
            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                if (view == null) view = LayoutInflater.from(getContext()).inflate(R.layout.item_etudiant, viewGroup, false);
                TextView name = view.findViewById(R.id.item_name);
                TextView details = view.findViewById(R.id.item_details);
                Etudiant e = etudiantList.get(i);
                name.setText(e.getNom() + " " + e.getPrenom());
                details.setText(e.getVille() + " (" + e.getSexe() + ")");
                return view;
            }
        };
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((adapterView, v, i, l) -> showOptionsDialog(etudiantList.get(i)));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadEtudiants();
    }

    private void loadEtudiants() {
        StringRequest request = new StringRequest(Request.Method.POST, loadUrl,
                response -> {
                    Log.d("LOAD_RESPONSE", response);
                    try {
                        Type type = new TypeToken<List<Etudiant>>(){}.getType();
                        List<Etudiant> loaded = new Gson().fromJson(response, type);
                        etudiantList.clear();
                        if (loaded != null) {
                            etudiantList.addAll(loaded);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (Exception ex) {
                        Log.e("JSON_ERROR", "Erreur parsing: " + response);
                    }
                },
                error -> Log.e("VOLLEY", "Load Error: " + error.toString()));
        request.setShouldCache(false);
        requestQueue.add(request);
    }

    private void showOptionsDialog(Etudiant e) {
        String[] options = {"Modifier", "Supprimer"};
        new AlertDialog.Builder(getContext())
                .setTitle("Options pour " + e.getNom() + " (ID: " + e.getId() + ")")
                .setItems(options, (dialogInterface, i) -> {
                    if (i == 0) showUpdateDialog(e);
                    else showDeleteConfirmDialog(e);
                }).show();
    }

    private void showDeleteConfirmDialog(Etudiant e) {
        new AlertDialog.Builder(getContext())
                .setTitle("Confirmation")
                .setMessage("Voulez-vous supprimer " + e.getNom() + " ?")
                .setPositiveButton("Oui", (dialogInterface, i) -> deleteEtudiant(e))
                .setNegativeButton("Non", null).show();
    }

    private void deleteEtudiant(Etudiant e) {
        StringRequest request = new StringRequest(Request.Method.POST, deleteUrl,
                response -> {
                    Log.d("DELETE_RESPONSE", response);
                    Toast.makeText(getContext(), "Réponse: " + response, Toast.LENGTH_SHORT).show();
                    loadEtudiants();
                },
                error -> Log.e("VOLLEY", "Delete Error: " + error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(e.getId()));
                return params;
            }
        };
        request.setShouldCache(false);
        requestQueue.add(request);
    }

    private void showUpdateDialog(Etudiant e) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.activity_add_etudiant, null);
        EditText nom = view.findViewById(R.id.nom);
        EditText prenom = view.findViewById(R.id.prenom);
        Spinner ville = view.findViewById(R.id.ville);
        RadioButton m = view.findViewById(R.id.m);
        RadioButton f = view.findViewById(R.id.f);
        Button addBtn = view.findViewById(R.id.add);
        addBtn.setVisibility(View.GONE);

        nom.setText(e.getNom());
        prenom.setText(e.getPrenom());
        
        // Initialiser le Spinner
        if (ville.getAdapter() instanceof ArrayAdapter) {
            ArrayAdapter adapterVille = (ArrayAdapter) ville.getAdapter();
            int pos = adapterVille.getPosition(e.getVille());
            ville.setSelection(pos);
        }

        if ("femme".equalsIgnoreCase(e.getSexe())) f.setChecked(true); else m.setChecked(true);

        new AlertDialog.Builder(getContext())
                .setTitle("Modifier Étudiant (ID: " + e.getId() + ")")
                .setView(view)
                .setPositiveButton("Enregistrer", (dialogInterface, i) -> {
                    e.setNom(nom.getText().toString());
                    e.setPrenom(prenom.getText().toString());
                    e.setVille(ville.getSelectedItem().toString());
                    e.setSexe(m.isChecked() ? "homme" : "femme");
                    updateEtudiant(e);
                })
                .setNegativeButton("Annuler", null).show();
    }

    private void updateEtudiant(Etudiant e) {
        StringRequest request = new StringRequest(Request.Method.POST, updateUrl,
                response -> {
                    Log.d("UPDATE_RESPONSE", response);
                    Toast.makeText(getContext(), "Réponse: " + response, Toast.LENGTH_SHORT).show();
                    loadEtudiants();
                },
                error -> Log.e("VOLLEY", "Update Error: " + error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(e.getId()));
                params.put("nom", e.getNom());
                params.put("prenom", e.getPrenom());
                params.put("ville", e.getVille());
                params.put("sexe", e.getSexe());
                return params;
            }
        };
        request.setShouldCache(false);
        requestQueue.add(request);
    }
}
