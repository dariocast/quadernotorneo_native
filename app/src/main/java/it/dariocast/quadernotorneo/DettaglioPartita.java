package it.dariocast.quadernotorneo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class DettaglioPartita extends AppCompatActivity {
    TextView idTv;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dettaglio_partita);

        Button btnCancella = findViewById(R.id.btn_cancella);
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

        Intent incoming = getIntent();
        if (incoming.hasExtra("idPartita")) {
            idTv = findViewById(R.id.id_tv);
            id = incoming.getIntExtra("idPartita",0);
            idTv.setText(String.format(Locale.ITALIAN,"%d", id));
        }
    }
}
