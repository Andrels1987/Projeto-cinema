package modelos;

import java.io.Serializable;

public class Filmes implements Serializable{
    private int id;
    private String genero;
    private String descricao;
    private String nomeFilme;
    private String filename;
    
    public Filmes(int id, String genero, String descricao, String nomeFilme, String filename) {
        this.id = id;
        this.genero = genero;
        this.descricao = descricao;
        this.nomeFilme = nomeFilme;
        this.filename = filename;
    }

    public Filmes() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getNomeFilme() {
        return nomeFilme;
    }

    public void setNomeFilme(String nomeFilme) {
        this.nomeFilme = nomeFilme;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    public String toString() {
        return "Filmes [id=" + id + ", genero=" + genero + ", descricao=" + descricao + ", nomeFilme=" + nomeFilme
                + ", filename=" + filename + "]";
    }

    
}
