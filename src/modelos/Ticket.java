package modelos;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import DAO.ConnectionDao;

public class Ticket {
    static ArrayList<String> codigos = new ArrayList<>();
    ConnectionDao cdao = new ConnectionDao();
    Connection con = null;
    Statement state = null;
    ResultSet rs = null;
    static public final double valorUnitario = 35.0;
    private Integer idPedido;
    private int idExibicao;
    private String cpfCliente;
    private int idCadeira;
    private String codigoPedido;
    private String statusPedido;

    /*
     * public Ticket(int idSala, int idfilme, int idDataExibicao, int idCliente, int
     * idCadeira) {
     * this.idSala = idSala;
     * this.idfilme = idfilme;
     * this.idDataExibicao = idDataExibicao;
     * this.idCliente = idCliente;
     * this.idCadeira = idCadeira;
     * }
     */
    public Ticket() {

    }

    public Ticket(int idPedido, String cpfCliente, int idExibicao, int idCadeira, String codigoPedido, String statusPedido) {
        this.idPedido = idPedido;
        this.cpfCliente = cpfCliente;
        this.idExibicao = idExibicao;
        this.idCadeira = idCadeira;
        this.codigoPedido = codigoPedido;
        this.statusPedido = statusPedido;
    }

    public double getValor() {
        return valorUnitario;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public int getIdExibicao() {
        return idExibicao;
    }

    public void setIdExibicao(int idExibicao) {
        this.idExibicao = idExibicao;
    }

    public String getCpfCliente() {
        return cpfCliente;
    }

    public void setCpfCliente(String cpfCliente) {
        this.cpfCliente = cpfCliente;
    }

    public int getIdCadeira() {
        return idCadeira;
    }

    public void setIdCadeira(int idCadeira) {
        this.idCadeira = idCadeira;
    }

    public String getCodigoPedido() {
        return codigoPedido;
    }

    public void setCodigoPedido(String codigoPedido) {
        this.codigoPedido = codigoPedido;
    }

    public String getStatusPedido() {
        return statusPedido;
    }

    public void setStatusPedido(String statusPedido) {
        this.statusPedido = statusPedido;
    }

    @Override
    public String toString() {
        return "Ticket [cdao=" + cdao + ", con=" + con + ", state=" + state + ", rs=" + rs + ", idPedido=" + idPedido
                + ", idExibicao=" + idExibicao + ", cpfCliente=" + cpfCliente + ", idCadeira=" + idCadeira
                + ", codigoPedido=" + codigoPedido + ", statusPedido=" + statusPedido + "]";
    }

    

}
