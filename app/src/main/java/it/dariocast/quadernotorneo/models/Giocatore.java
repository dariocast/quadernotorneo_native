package it.dariocast.quadernotorneo.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Giocatore {
    private String nome;
    private String gruppo;

    public Giocatore(String nome, String gruppo) {
        this.nome = nome;
        this.gruppo = gruppo;
    }

    public Giocatore(JSONObject jsonObject) throws JSONException {
        this.nome = jsonObject.getString("nome");
        this.gruppo = jsonObject.getString("gruppo");
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

}
