package modelos;

import java.sql.Date;
import java.sql.Time;

public class DataExibicao {
    int id;
    Date data;
    Time hora;
    public DataExibicao() {
    }
    public DataExibicao(int id, Date data, Time hora) {
        this.id = id;
        this.data = data;
        this.hora = hora;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public Date getData() {
        return data;
    }
    public void setData(Date data) {
        this.data = data;
    }
    public Time getHora() {
        return hora;
    }
    public void setHora(Time hora) {
        this.hora = hora;
    }
    @Override
    public String toString() {
        return "DataExibicao [id=" + id + ", data=" + data + ", hora=" + hora + "]";
    }

    
    
}
