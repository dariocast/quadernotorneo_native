package it.dariocast.quadernotorneo.models;

public class Evento {
    private String nome;
    private String squadra;
    private TipoEvento tipo;

    public Evento(String nome, String squadra, TipoEvento tipo) {
        this.nome = nome;
        this.squadra = squadra;
        this.tipo = tipo;
    }

    public String getNome() {
        return nome;
    }

    public String getSquadra() {
        return squadra;
    }

    public TipoEvento getTipo() {
        return tipo;
    }

    public enum TipoEvento {
        GOL,
        AMMONIZIONE,
        ESPULSIONE
    }
}