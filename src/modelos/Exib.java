package modelos;

import java.util.Date;

public class Exib {
    private int idExibicao;
    private int idData;
    private Date data;
    private String horario;    
    private int sala;
    private int idFilme;
    private String status;

    
    public Exib(Date data, int sala, int idData, int idFilme, String horario, String status) {
        this.data = data;
        this.sala = sala;
        this.idData = idData;
        this.idFilme = idFilme; 
        this.horario = horario;
        this.status = status;
    } 
    public Exib(int idExibicao) {
        this.idExibicao = idExibicao;
    }
    public Exib() {
        
    }
    

    public Date getData() {
        return data;
    }
    public void setData(Date data) {
        this.data = data;
    }
    public int getSala() {
        return sala;
    }
    public void setSala(int sala) {
        this.sala = sala;
    }

    public int getIdData() {
        return idData;
    }

    public void setIdData(int idData) {
        this.idData = idData;
    }

    public int getIdFilme() {
        return idFilme;
    }

    public void setIdFilme(int idFilme) {
        this.idFilme = idFilme;
    }
    public String getHorario() {
        return horario;
    }
    public void setHorario(String horario) {
        this.horario = horario;
    }
    public int getIdExibicao() {
        return idExibicao;
    }
    public void setIdExibicao(int idExibicao) {
        this.idExibicao = idExibicao;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    @Override
    public String toString() {
        return "Exib [idExibicao=" + idExibicao + ", idData=" + idData + ", data=" + data + ", horario=" + horario
                + ", sala=" + sala + ", idFilme=" + idFilme + ", status=" + status + "]";
    }
    
    

    
}
