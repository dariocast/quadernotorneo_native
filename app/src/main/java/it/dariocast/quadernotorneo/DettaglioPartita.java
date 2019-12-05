package it.dariocast.quadernotorneo;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import it.dariocast.quadernotorneo.models.Evento;
import it.dariocast.quadernotorneo.models.Giocatore;
import it.dariocast.quadernotorneo.models.Partita;

public class DettaglioPartita extends AppCompatActivity {
    private static final String TAG = "DettaglioPartita";
    int id;
    Partita partita;
    List<String> giocatoriSquadraUno;
    List<String> giocatoriSquadraDue;
    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    List<Evento> listaEventi;
    private EventoAdapter eventoAdapter;
    private TextView risultato;

    @Override
    public void onBackPressed() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(DettaglioPartita.this);
        dialogBuilder.setTitle("Uscire?")
                .setMessage("Tutte le modifiche non salvate andranno perse")
                .setCancelable(true)
                .setPositiveButton("Esci", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DettaglioPartita.super.onBackPressed();
                    }
                })
                .setNegativeButton("Cancella", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        dialogBuilder.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dettaglio_partita);

        giocatoriSquadraUno = new ArrayList<>();
        giocatoriSquadraDue = new ArrayList<>();
        listaEventi = new ArrayList<>();

        risultato = findViewById(R.id.risultato_tv);
        Typeface face= Typeface.createFromAsset(getAssets(), "font.ttf");
        risultato.setTypeface(face);

        MaterialButton btnCancella = findViewById(R.id.btn_cancella);
        btnCancella.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(DettaglioPartita.this);
                dialogBuilder.setTitle("Elimina")
                        .setMessage("Vuoi eliminare questa partita?")
                        .setCancelable(true)
                        .setPositiveButton("Elimina", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Instantiate the RequestQueue.
                                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                                String url ="https://dariocast.altervista.org/fantazama/api/partita/delete.php?id="+id;
                                // Request a string response from the provided URL.
                                JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.DELETE, url, null,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    boolean success = response.getBoolean("deleted");
                                                    if(success) {
                                                        Toast.makeText(DettaglioPartita.this, "Partita eliminata con successo", Toast.LENGTH_SHORT).show();
                                                        setResult(RESULT_OK);
                                                        finish();
                                                    }
                                                } catch (JSONException e) {
                                                    Toast.makeText(DettaglioPartita.this, "Impossibile ottenere l'id, errore: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    e.printStackTrace();
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(DettaglioPartita.this, "Impossibile eliminare la partita, errore: "+error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                                // Add the request to the RequestQueue.
                                queue.add(stringRequest);
                            }
                        })
                        .setNegativeButton("Cancella", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                dialogBuilder.show();
            }
        });

        MaterialButton btnSalva = findViewById(R.id.btn_salva);
        btnSalva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                salva();
            }
        });

        MaterialButton btnPDF = findViewById(R.id.btn_crea_pdf);
        btnPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exportToPDF();
            }
        });

        Intent incoming = getIntent();
        if (incoming.hasExtra("idPartita")) {
            id = incoming.getIntExtra("idPartita",0);
            getById(id);
        }

        recyclerView = findViewById(R.id.lista_eventi);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        eventoAdapter = new EventoAdapter(listaEventi);
        recyclerView.setAdapter(eventoAdapter);

    }

    private View.OnClickListener getGolListener(final int nSquadra) {
        List<String> listaDaUsare = null;
        String squadra = "";
        switch (nSquadra) {
            case 1:
                listaDaUsare = giocatoriSquadraUno;
                squadra = partita.getSquadraUno();
                break;
            case 2:
                listaDaUsare = giocatoriSquadraDue;
                squadra = partita.getSquadraDue();
                break;
        }
        final List<String> finalListaDaUsare = listaDaUsare;
        final String finalSquadra = squadra;
        return (new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(DettaglioPartita.this)
                        .setTitle("Assegna gol")
                        .setMessage("Scegli un giocatore")
                        .setCancelable(false);
                LayoutInflater inflater = DettaglioPartita.this.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.scegli_giocatore_dialog, null);
                dialogBuilder.setView(dialogView);
                final Spinner spinner = dialogView.findViewById(R.id.spinner_giocatore);
                final ArrayAdapter adapter = new ArrayAdapter(DettaglioPartita.this, android.R.layout.simple_spinner_item, finalListaDaUsare);
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Apply the adapter to the spinner
                spinner.setAdapter(adapter);
                dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            JSONArray marcatori = partita.getMarcatori();
                            JSONObject marcatore = new JSONObject();
                            marcatore.put("giocatore", spinner.getSelectedItem().toString());
                            marcatore.put("squadra", finalSquadra);
                            marcatori.put(marcatore);
                            partita.setMarcatori(marcatori);
                            if (nSquadra == 1) {
                                partita.setGolSquadraUno(partita.getGolSquadraUno() + 1);
                            } else {
                                partita.setGolSquadraDue(partita.getGolSquadraDue() + 1);
                            }
                            updateRisultato();
                            listaEventi.add(new Evento(spinner.getSelectedItem().toString(), finalSquadra, Evento.TipoEvento.GOL));
                            eventoAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
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
    }
    private View.OnClickListener getAmmonizioneListener(final int nSquadra) {
        List<String> listaDaUsare = null;
        String squadra = "";
        switch (nSquadra) {
            case 1:
                listaDaUsare = giocatoriSquadraUno;
                squadra = partita.getSquadraUno();
                break;
            case 2:
                listaDaUsare = giocatoriSquadraDue;
                squadra = partita.getSquadraDue();
                break;
        }
        final List<String> finalListaDaUsare = listaDaUsare;
        final String finalSquadra = squadra;
        return (new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(DettaglioPartita.this)
                        .setTitle("Ammonisci")
                        .setMessage("Scegli il giocatore")
                        .setCancelable(false);
                LayoutInflater inflater = DettaglioPartita.this.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.scegli_giocatore_dialog, null);
                dialogBuilder.setView(dialogView);
                final Spinner spinner = dialogView.findViewById(R.id.spinner_giocatore);
                final ArrayAdapter adapter = new ArrayAdapter(DettaglioPartita.this, android.R.layout.simple_spinner_item, finalListaDaUsare);
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Apply the adapter to the spinner
                spinner.setAdapter(adapter);
                dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            JSONArray ammoniti = partita.getAmmoniti();
                            JSONObject ammonito = new JSONObject();
                            ammonito.put("giocatore", spinner.getSelectedItem().toString());
                            ammonito.put("squadra", finalSquadra);
                            ammoniti.put(ammonito);
                            partita.setAmmoniti(ammoniti);
                            listaEventi.add(new Evento(spinner.getSelectedItem().toString(), finalSquadra, Evento.TipoEvento.AMMONIZIONE));
                            eventoAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
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
    }
    private View.OnClickListener getEspulsioneListener(final int nSquadra) {
        List<String> listaDaUsare = null;
        String squadra = "";
        switch (nSquadra) {
            case 1:
                listaDaUsare = giocatoriSquadraUno;
                squadra = partita.getSquadraUno();
                break;
            case 2:
                listaDaUsare = giocatoriSquadraDue;
                squadra = partita.getSquadraDue();
                break;
        }
        final List<String> finalListaDaUsare = listaDaUsare;
        final String finalSquadra = squadra;
        return (new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(DettaglioPartita.this)
                        .setTitle("Espelli")
                        .setMessage("Scegli il giocatore")
                        .setCancelable(false);
                LayoutInflater inflater = DettaglioPartita.this.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.scegli_giocatore_dialog, null);
                dialogBuilder.setView(dialogView);
                final Spinner spinner = dialogView.findViewById(R.id.spinner_giocatore);
                final ArrayAdapter adapter = new ArrayAdapter(DettaglioPartita.this, android.R.layout.simple_spinner_item, finalListaDaUsare);
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Apply the adapter to the spinner
                spinner.setAdapter(adapter);
                dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            JSONArray espulsi = partita.getEspulsi();
                            JSONObject espulso = new JSONObject();
                            espulso.put("giocatore", spinner.getSelectedItem().toString());
                            espulso.put("squadra", finalSquadra);
                            espulsi.put(espulso);
                            partita.setEspulsi(espulsi);
                            listaEventi.add(new Evento(spinner.getSelectedItem().toString(), finalSquadra, Evento.TipoEvento.ESPULSIONE));
                            eventoAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
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
    }

    private void updateRisultato() {
        String ris = partita.getGolSquadraUno()+":"+partita.getGolSquadraDue();
        risultato.setText(ris);
    }

    private void salva() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url ="https://dariocast.altervista.org/fantazama/api/partita/update.php";

        // Request a string response from the provided URL.
        JsonObjectRequest stringRequest = null;
        try {
            stringRequest = new JsonObjectRequest(Request.Method.POST, url, partita.toJSONObject(),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if(response.getBoolean("updated"))
                                    Toast.makeText(getApplicationContext(), "Partita salvata con successo", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(getApplicationContext(), "Partita NON salvata", Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                Toast.makeText(getApplicationContext(), "Impossibile salvare la partita, errore: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "Impossibile salvare la partita, errore: "+error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            queue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Impossibile salvare la partita, errore: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void getById(int id) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url ="https://dariocast.altervista.org/fantazama/api/partita/get.php?id="+id;


        // Request a string response from the provided URL.
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            partita = new Partita(response);
                            MaterialButton gol = findViewById(R.id.btn_gol);
                            gol.setOnClickListener(getGolListener(1));

                            MaterialButton gol2 = findViewById(R.id.btn_gol_2);
                            gol2.setOnClickListener(getGolListener(2));

                            MaterialButton ammonisci = findViewById(R.id.btn_ammonisci);
                            ammonisci.setOnClickListener(getAmmonizioneListener(1));

                            MaterialButton ammonisci2 = findViewById(R.id.btn_ammonisci_2);
                            ammonisci2.setOnClickListener(getAmmonizioneListener(2));

                            MaterialButton espelli = findViewById(R.id.btn_espelli);
                            espelli.setOnClickListener(getEspulsioneListener(1));

                            MaterialButton espelli2 = findViewById(R.id.btn_espelli_2);
                            espelli2.setOnClickListener(getEspulsioneListener(2));

                            updateRisultato();
                            updateListaEventi();
                            getGiocatori();
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), "Impossibile caricare la partita, errore: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Impossibile caricare la partita, errore: "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void updateListaEventi() throws JSONException{
        listaEventi.clear();
        JSONArray marcatori = partita.getMarcatori();
        for (int i = 0; i<marcatori.length();i++) {
            listaEventi.add(new Evento(marcatori.getJSONObject(i).getString("giocatore"),marcatori.getJSONObject(i).getString("squadra"), Evento.TipoEvento.GOL));
        }
        JSONArray ammoniti = partita.getAmmoniti();
        for (int i = 0; i<ammoniti.length();i++) {
            listaEventi.add(new Evento(ammoniti.getJSONObject(i).getString("giocatore"),ammoniti.getJSONObject(i).getString("squadra"), Evento.TipoEvento.AMMONIZIONE));
        }
        JSONArray espulsi = partita.getEspulsi();
        for (int i = 0; i<espulsi.length();i++) {
            listaEventi.add(new Evento(espulsi.getJSONObject(i).getString("giocatore"),espulsi.getJSONObject(i).getString("squadra"), Evento.TipoEvento.ESPULSIONE));
        }
        eventoAdapter.notifyDataSetChanged();
    }


    private  void exportToPDF() {
        if(isStoragePermissionGranted()) {
            String extstoragedir = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extstoragedir, "QuadernoTorneo");
            if(!folder.exists()) {
                boolean bool = folder.mkdir();
            }
            try {
                String filename = "partita"+partita.getId()+".pdf";
                File file = new File(folder, filename);
                if (!file.exists()) {
                    file.createNewFile();
                }
                String separator = "-------------------------------";
                Document document = new Document();
                PdfWriter.getInstance(document,
                        new FileOutputStream(file.getAbsoluteFile()));
                document.open();
                document.add(new Paragraph(partita.getSquadraUno()));
                for (int i=0;i<giocatoriSquadraUno.size();i++) {
                    document.add(new Paragraph(giocatoriSquadraUno.get(i)));
                }
                document.add(new Paragraph(separator));
                document.add(new Paragraph(partita.getSquadraDue()));
                for (int i=0;i<giocatoriSquadraDue.size();i++) {
                    document.add(new Paragraph(giocatoriSquadraDue.get(i)));
                }
                document.add(new Paragraph(separator));
                document.add(new Paragraph("Gol"));
                for (int i=0;i<partita.getMarcatori().length();i++) {
                    document.add(new Paragraph(partita.getMarcatori().getJSONObject(i).getString("giocatore")));
                }
                document.add(new Paragraph(separator));
                document.add(new Paragraph("Ammoniti"));
                for (int i=0;i<partita.getAmmoniti().length();i++) {
                    document.add(new Paragraph(partita.getAmmoniti().getJSONObject(i).getString("giocatore")));
                }
                document.add(new Paragraph(separator));
                document.add(new Paragraph("Espulsi"));
                for (int i=0;i<partita.getEspulsi().length();i++) {
                    document.add(new Paragraph(partita.getEspulsi().getJSONObject(i).getString("giocatore")));
                }

                document.close();
                Toast.makeText(this, "File salvato come PDF", Toast.LENGTH_SHORT).show();
                ((DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE))
                        .addCompletedDownload(
                                filename,
                                filename,
                                true,
                                "application/pdf",
                                file.getPath(),
                                file.length(),
                                true
                );

            }catch (IOException e){
                Log.i("error",e.getLocalizedMessage());
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {
                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    public void getGiocatori() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url ="https://dariocast.altervista.org/fantazama/api/giocatore/getGiocatoriPerGruppo.php?gruppo="+partita.getSquadraUno();

        // Request a string response from the provided URL.
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            giocatoriSquadraUno.clear();
                            for (int i = 0; i < response.length(); i++) {
                                Giocatore toInsert = new Giocatore(response.getString(i), partita.getSquadraUno());
                                giocatoriSquadraUno.add(response.getString(i));
                            }
                        } catch (JSONException e) {
                            Toast.makeText(DettaglioPartita.this, "Impossibile caricare i giocatori", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DettaglioPartita.this, "Impossibile caricare i giocatori, errore: "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Add the request to the RequestQueue.
        queue.add(jsonArrayRequest);

        url ="https://dariocast.altervista.org/fantazama/api/giocatore/getGiocatoriPerGruppo.php?gruppo="+partita.getSquadraDue();

        // Request a string response from the provided URL.
        jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            giocatoriSquadraDue.clear();
                            for (int i = 0; i < response.length(); i++) {
                                Giocatore toInsert = new Giocatore(response.getString(i), partita.getSquadraDue());
                                giocatoriSquadraDue.add(response.getString(i));
                            }
                        } catch (JSONException e) {
                            Toast.makeText(DettaglioPartita.this, "Impossibile caricare i giocatori", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DettaglioPartita.this, "Impossibile caricare i giocatori, errore: "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(jsonArrayRequest);
    }

}
