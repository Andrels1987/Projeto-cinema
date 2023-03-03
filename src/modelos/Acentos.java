package modelos;


public class Acentos {
    private int id;
    private int fila;


    public Acentos(int id, int fila) {
        this.id = id;
        this.fila = fila;
    }


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    public int getFila() {
        return fila;
    }


    public void setFila(int fila) {
        this.fila = fila;
    }


    @Override
    public String toString() {
        return "Acentos [id=" + id + ", fila=" + fila + "]";
    }

    

    
}