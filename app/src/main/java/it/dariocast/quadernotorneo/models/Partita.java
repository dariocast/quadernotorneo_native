package it.dariocast.quadernotorneo.models;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import it.dariocast.quadernotorneo.utils.Utils;

public class Partita {
    public Partita(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getInt("id");
        this.squadraUno = jsonObject.getString("squadra_uno");
        this.squadraDue = jsonObject.getString("squadra_due");
        this.golSquadraUno = jsonObject.getInt("gol_squadra_uno");
        this.golSquadraDue = jsonObject.getInt("gol_squadra_due");
        this.marcatori = Utils.stringToIntArray(jsonObject.getString("marcatori"));
        this.ammoniti = Utils.stringToIntArray(jsonObject.getString("ammoniti"));
        this.espulsi = Utils.stringToIntArray(jsonObject.getString("espulsi"));
    }

    public void persistOnDb(final Context ctx) throws JSONException {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(ctx);
        String url ="https://dariocast.altervista.org/fantazama/api/partita/create.php";

        JSONObject jsonRepr = this.toJson();

        // Request a string response from the provided URL.
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, url, jsonRepr,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(ctx, "Squadra creata con successo", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ctx, "Impossibile caricare le partite, errore: "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private JSONObject toJson() throws JSONException {
        JSONObject toReturn = new JSONObject();
        toReturn.put("id",getId());
        toReturn.put("squadra_uno",getSquadraUno());
        toReturn.put("squadra_due",getSquadraDue());
        toReturn.put("gol_squadra_uno",getGolSquadraUno());
        toReturn.put("gol_squadra_due",getGolSquadraDue());
        toReturn.put("marcatori",getMarcatori());
        toReturn.put("ammoniti",getAmmoniti());
        toReturn.put("espulsi",getEspulsi());
        return toReturn;
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

    public int[] getMarcatori() {
        return marcatori;
    }

    public void setMarcatori(int[] marcatori) {
        this.marcatori = marcatori;
    }

    public int[] getAmmoniti() {
        return ammoniti;
    }

    public void setAmmoniti(int[] ammoniti) {
        this.ammoniti = ammoniti;
    }

    public int[] getEspulsi() {
        return espulsi;
    }

    public void setEspulsi(int[] espulsi) {
        this.espulsi = espulsi;
    }

    //object properties
    private int id;
    private String squadraUno;
    private String squadraDue;
    private int golSquadraUno;
    private int golSquadraDue;
    private int[] marcatori;
    private int[] ammoniti;
    private int[] espulsi;

    private static int idCounter = 1;

    public Partita(String squadraUno, String squadraDue, int golSquadraUno, int golSquadraDue, int[] marcatori, int[] ammoniti, int[] espulsi) {
        this.id = Partita.idCounter;
        idCounter++;
        this.squadraUno = squadraUno;
        this.squadraDue = squadraDue;
        this.golSquadraUno = golSquadraUno;
        this.golSquadraDue = golSquadraDue;
        this.marcatori = marcatori;
        this.ammoniti = ammoniti;
        this.espulsi = espulsi;
    }

    public Partita(String squadraUno, String squadraDue) {
        this.id = Partita.idCounter;
        idCounter++;
        this.squadraUno = squadraUno;
        this.squadraDue = squadraDue;
    }
}
