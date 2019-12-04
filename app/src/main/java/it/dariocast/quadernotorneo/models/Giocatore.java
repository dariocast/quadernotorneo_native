package it.dariocast.quadernotorneo.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Giocatore {
    //object properties
    private int id;
    private String nome;
    private String gruppo;
    private String ruolo;
    private int costo;
    private String genere;
    private String animatore;
    private String attivo;

    public Giocatore(int id, String nome, String gruppo, String ruolo, int costo, String genere, String animatore, String attivo) {
        this.id = id;
        this.nome = nome;
        this.gruppo = gruppo;
        this.ruolo = ruolo;
        this.costo = costo;
        this.genere = genere;
        this.animatore = animatore;
        this.attivo = attivo;
    }

    public Giocatore(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getInt("_id");
        this.nome = jsonObject.getString("nome");
        this.gruppo = jsonObject.getString("gruppo");
        this.ruolo = jsonObject.getString("ruolo");
        this.costo = jsonObject.getInt("costo");
        this.genere = jsonObject.getString("genere");
        this.animatore = jsonObject.getString("animatore");
        this.attivo = jsonObject.getString("attivo");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getGruppo() {
        return gruppo;
    }

    public void setGruppo(String gruppo) {
        this.gruppo = gruppo;
    }

    public String getRuolo() {
        return ruolo;
    }

    public void setRuolo(String ruolo) {
        this.ruolo = ruolo;
    }

    public int getCosto() {
        return costo;
    }

    public void setCosto(int costo) {
        this.costo = costo;
    }

    public String getGenere() {
        return genere;
    }

    public void setGenere(String genere) {
        this.genere = genere;
    }

    public String getAnimatore() {
        return animatore;
    }

    public void setAnimatore(String animatore) {
        this.animatore = animatore;
    }

    public String getAttivo() {
        return attivo;
    }

    public void setAttivo(String attivo) {
        this.attivo = attivo;
    }
}
