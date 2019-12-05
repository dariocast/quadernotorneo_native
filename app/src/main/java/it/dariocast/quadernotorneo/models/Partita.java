package it.dariocast.quadernotorneo.models;

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

    public JSONObject toJSONObject() throws JSONException{
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("_id", getId());
        jsonObject.put("squadraUno", getSquadraUno());
        jsonObject.put("squadraDue", getSquadraDue());
        jsonObject.put("golSquadraUno", getGolSquadraUno());
        jsonObject.put("golSquadraDue", getGolSquadraDue());
        jsonObject.put("marcatori", getMarcatori());
        jsonObject.put("ammoniti", getAmmoniti());
        jsonObject.put("espulsi", getEspulsi());

        return jsonObject;
    }
}
