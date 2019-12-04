package it.dariocast.quadernotorneo.models;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Partita {
    public Partita(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getInt("_id");
        this.squadraUno = jsonObject.getString("squadraUno");
        this.squadraDue = jsonObject.getString("squadraDue");
        this.golSquadraUno = jsonObject.getInt("golSquadraUno");
        this.golSquadraDue = jsonObject.getInt("golSquadraDue");
        this.marcatori = jsonObject.getJSONArray("marcatori");
        this.ammoniti = jsonObject.getJSONArray("ammoniti");
        this.espulsi = jsonObject.getJSONArray("espulsi");
    }

    public void persistOnDb(final Context ctx) throws JSONException {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(ctx);
        String url ="https://dariocast.altervista.org/fantazama/api/partita/create.php";

        JSONObject jsonRepr = new JSONObject();
        jsonRepr.put("squadraUno",getSquadraUno());
        jsonRepr.put("squadraDue",getSquadraDue());
        // Request a string response from the provided URL.
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, url, jsonRepr,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            id = response.getJSONObject("partita").getInt("_id");
                        } catch (JSONException e) {
                            Toast.makeText(ctx, "Impossibile ottenere l'id, errore: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                        Toast.makeText(ctx, "Partita creata con successo", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ctx, "Impossibile creare la partita, errore: "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSquadraUno() {
        return squadraUno;
    }

    public void setSquadraUno(String squadraUno) {
        this.squadraUno = squadraUno;
    }

    public String getSquadraDue() {
        return squadraDue;
    }

    public void setSquadraDue(String squadraDue) {
        this.squadraDue = squadraDue;
    }

    public int getGolSquadraUno() {
        return golSquadraUno;
    }

    public void setGolSquadraUno(int golSquadraUno) {
        this.golSquadraUno = golSquadraUno;
    }

    public int getGolSquadraDue() {
        return golSquadraDue;
    }

    public void setGolSquadraDue(int golSquadraDue) {
        this.golSquadraDue = golSquadraDue;
    }

    public JSONArray getMarcatori() {
        return marcatori;
    }

    public void setMarcatori(JSONArray marcatori) {
        this.marcatori = marcatori;
    }

    public JSONArray getAmmoniti() {
        return ammoniti;
    }

    public void setAmmoniti(JSONArray ammoniti) {
        this.ammoniti = ammoniti;
    }

    public JSONArray getEspulsi() {
        return espulsi;
    }

    public void setEspulsi(JSONArray espulsi) {
        this.espulsi = espulsi;
    }

    //object properties
    private int id;
    private String squadraUno;
    private String squadraDue;
    private int golSquadraUno;
    private int golSquadraDue;
    private JSONArray marcatori;
    private JSONArray ammoniti;
    private JSONArray espulsi;

    public Partita(String squadraUno, String squadraDue, int golSquadraUno, int golSquadraDue, JSONArray marcatori, JSONArray ammoniti, JSONArray espulsi) {
        this.squadraUno = squadraUno;
        this.squadraDue = squadraDue;
        this.golSquadraUno = golSquadraUno;
        this.golSquadraDue = golSquadraDue;
        this.marcatori = marcatori;
        this.ammoniti = ammoniti;
        this.espulsi = espulsi;
    }

    public Partita(String squadraUno, String squadraDue) {
        this.squadraUno = squadraUno;
        this.squadraDue = squadraDue;
    }


    public String printA4() {
        return squadraUno + "\t" +
                golSquadraUno +
                ":" +
                golSquadraDue +
                "\t" +
                squadraDue +
                "\r\n" +
                marcatori.toString() +
                "- - - - - - - - - - - - - -\r\n" +
                ammoniti.toString() +
                "- - - - - - - - - - - - - -\r\n" +
                espulsi.toString();
    }
}
