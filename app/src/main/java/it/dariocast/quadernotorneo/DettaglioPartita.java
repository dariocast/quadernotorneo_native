package it.dariocast.quadernotorneo;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import it.dariocast.quadernotorneo.models.Giocatore;
import it.dariocast.quadernotorneo.models.Partita;

public class DettaglioPartita extends AppCompatActivity {
    private static final String TAG = "DettaglioPartita";
    int id;
    Partita partita;
    List<Giocatore> giocatoriSquadraUno;
    List<Giocatore> giocatoriSquadraDue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dettaglio_partita);

        giocatoriSquadraUno = new ArrayList<>();
        giocatoriSquadraDue = new ArrayList<>();

        MaterialButton btnCancella = findViewById(R.id.btn_cancella);
        btnCancella.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                String url ="https://dariocast.altervista.org/fantazama/api/partita/delete.php?id="+id;
                // Request a string response from the provided URL.
                JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.DELETE, url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String message = response.getString("message");
                                    Toast.makeText(DettaglioPartita.this, message, Toast.LENGTH_SHORT).show();
                                    setResult(RESULT_OK);
                                    finish();
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
        });

        MaterialButton btnPdf = findViewById(R.id.btn_crea_pdf);
        btnPdf.setOnClickListener(new View.OnClickListener() {
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
                            partita = new Partita(response.getJSONObject("partita"));
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


    private  void exportToPDF() {
        if(isStoragePermissionGranted()) {
            String extstoragedir = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extstoragedir, "QuadernoTorneo");
            if(!folder.exists()) {
                boolean bool = folder.mkdir();
            }
            try {
                String filename = "partita"+partita.getId()+".pdf";
                final File file = new File(folder, filename);
                file.createNewFile();
                FileOutputStream fOut = new FileOutputStream(file);


                PdfDocument document = new PdfDocument();
                PdfDocument.PageInfo pageInfo = new
                        PdfDocument.PageInfo.Builder(595, 842, 1).create();
                PdfDocument.Page page = document.startPage(pageInfo);
                Canvas canvas = page.getCanvas();
                Paint paint = new Paint();

                canvas.drawText(partita.printA4(), 10, 10, paint);

                document.finishPage(page);
                document.writeTo(fOut);
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
                                Giocatore toInsert = new Giocatore(response.getJSONObject(i));
                                giocatoriSquadraUno.add(toInsert);
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

        url ="https://dariocast.altervista.org/fantazama/api/giocatore/getGruppo.php?gruppo="+partita.getSquadraDue();

        // Request a string response from the provided URL.
        jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            giocatoriSquadraDue.clear();
                            for (int i = 0; i < response.length(); i++) {
                                Giocatore toInsert = new Giocatore(response.getJSONObject(i));
                                giocatoriSquadraDue.add(toInsert);
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
