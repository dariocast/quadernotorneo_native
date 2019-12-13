package it.dariocast.quadernotorneo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import it.dariocast.quadernotorneo.models.Partita;

import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    List<Partita> partite;
    List<String> gruppi;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK) {
            loadPartite();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        partite = new ArrayList<>();
        gruppi = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new PartiteAdapter(partite);
        recyclerView.setAdapter(mAdapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Nuova partita")
                        .setMessage("Scegli le due squadre")
                        .setCancelable(false);
                LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.scegli_squadre_dialog, null);
                dialogBuilder.setView(dialogView);
                final DatePicker datePicker = dialogView.findViewById(R.id.date_picker);
                final Spinner spinner = dialogView.findViewById(R.id.spinner_squadra_uno);
                final Spinner spinner2 = dialogView.findViewById(R.id.spinner_squadra_due);
                final ArrayAdapter adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_spinner_item, gruppi);
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Apply the adapter to the spinner
                spinner.setAdapter(adapter);
                spinner2.setAdapter(adapter);
                dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            Calendar cal = Calendar.getInstance();
                            cal.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
                            cal.set(Calendar.MONTH, datePicker.getMonth());
                            cal.set(Calendar.YEAR, datePicker.getYear());
                            creaPartita(spinner.getSelectedItem().toString(), spinner2.getSelectedItem().toString(), cal.getTimeInMillis()/1000);
                        } catch (JSONException e) {
                            Toast.makeText(MainActivity.this, "Errore durante la creazione. Riprova", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                });
                dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

                dialogBuilder.show();
            }
        });

        final SwipeRefreshLayout swipeLayout = findViewById(R.id.swipe_view);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadPartite();
                loadGruppi();
                swipeLayout.setRefreshing(false);
            }
        });
    }

    private void creaPartita(String squadra1, String squadra2, long date) throws JSONException {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url ="https://dariocast.altervista.org/fantazama/api/partita/create.php";

        JSONObject jsonRepr = new JSONObject();
        jsonRepr.put("squadraUno",squadra1);
        jsonRepr.put("squadraDue",squadra2);
        jsonRepr.put("data",date);
        // Request a string response from the provided URL.
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, url, jsonRepr,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            partite.add(new Partita(response.getJSONObject("partita")));
                            mAdapter.notifyDataSetChanged();
                            Toast.makeText(getApplicationContext(), "Partita creata con successo", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), "Impossibile ottenere l'id, errore: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Impossibile creare la partita, errore: "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void loadGruppi() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url ="https://dariocast.altervista.org/fantazama/api/giocatore/getNomiGruppi.php";

        // Request a string response from the provided URL.
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            gruppi.clear();
                            for (int i = 0; i < response.length(); i++) {
                                String nomeGruppo = response.getString(i);
                                gruppi.add(nomeGruppo);
                            }
                        } catch (JSONException e) {
                            Toast.makeText(MainActivity.this, "Impossibile caricare i nomi dei gruppi", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Impossibile caricare i nomi dei gruppi, errore: "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Add the request to the RequestQueue.
        queue.add(jsonArrayRequest);
    }

    private void loadPartite() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url ="https://dariocast.altervista.org/fantazama/api/partita/getAll.php";

        // Request a string response from the provided URL.
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            partite.clear();
                            for (int i = 0; i < response.length(); i++) {
                                Partita toInsert = new Partita(response.getJSONObject(i));
                                partite.add(toInsert);
                            }
                            mAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            Toast.makeText(MainActivity.this, "Impossibile caricare le partite", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Impossibile caricare le partite, errore: "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Add the request to the RequestQueue.
        queue.add(jsonArrayRequest);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            loadPartite();
            loadGruppi();
        }
        if (id == R.id.calcola_classifica) {
            calcolaClassifica();
        }
        if (id == R.id.reset_classifica) {
            resetClassifica();
        }

        return super.onOptionsItemSelected(item);
    }

    private void resetClassifica() {
    }

    private void calcolaClassifica() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPartite();
        loadGruppi();
    }
}
