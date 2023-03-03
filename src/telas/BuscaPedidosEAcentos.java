package telas;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.Phaser;

import modelos.*;

public class BuscaPedidosEAcentos implements Runnable {

    Phaser p;
    Exib exib;
    ArrayList<Ticket> tickets = new ArrayList<>();
    ArrayList<Acentos> acentos = new ArrayList<>();

    public BuscaPedidosEAcentos(Phaser p, Exib e) {
        this.p = p;
        this.exib = e;
        p.register();
        new Thread().start();
    }

    @Override
    public void run() {
        //System.out.println("FASE 1: buscar pedidos");
        try {
            tickets = buscarPedidosPorExibição(exib);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        p.arriveAndAwaitAdvance();
        
       // System.out.println("FASE 2: buscar acentos disponiveis");
        try {
            acentos = buscarAcentos();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        p.arriveAndAwaitAdvance();

        //System.out.println("FASE 3: abrir janela de escolha dos acentos");
        //System.out.println("EXIBICAO " + exib);
        new AcentosDisponiveis(acentos, tickets, exib);
        p.arriveAndDeregister();
    }

    public ArrayList<Ticket> buscarPedidosPorExibição(Exib exibicao) throws SQLException {
        ArrayList<Ticket> t = new ArrayList<>();
        try {
            Exibicoes.con = Exibicoes.cdao.getConnection();
            Exibicoes.preparedStatement = Exibicoes.con.prepareStatement("SELECT * FROM bdcinema.pedido " +
                    "where idExibicao = ?");
            Exibicoes.preparedStatement.setInt(1, exibicao.getIdExibicao());
            Exibicoes.rs = Exibicoes.preparedStatement.executeQuery();
            while (Exibicoes.rs.next()) {
                t.add(new Ticket(Exibicoes.rs.getInt("idPedido"), Exibicoes.rs.getString("cpfCliente"), Exibicoes.rs.getInt("idExibicao"),
                        Exibicoes.rs.getInt("idCadeira"), Exibicoes.rs.getString("codigoPedido"), Exibicoes.rs.getString("statusPedido")));
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
        } catch (InvocationTargetException e3) {
            e3.printStackTrace();
        } catch (NoSuchMethodException e4) {
            e4.printStackTrace();
        } catch (SecurityException e5) {
            e5.printStackTrace();
        } finally {
            try {
                if (Exibicoes.con != null) {
                    Exibicoes.con.close();
                }
                if (Exibicoes.rs != null) {
                    Exibicoes.rs.close();
                }
                if (Exibicoes.preparedStatement != null) {
                    Exibicoes.preparedStatement.close();
                }
            } catch (Exception e6) {
                e6.printStackTrace();
            }
        }
        
        return t;
    }

    public ArrayList<Acentos> buscarAcentos() throws SQLException {
        ArrayList<Acentos> acentosList = new ArrayList<>();
        try {
            // buscando acentos
            Exibicoes.con = Exibicoes.cdao.getConnection();
            Exibicoes.state = Exibicoes.con.createStatement();
            Exibicoes.rs = Exibicoes.state.executeQuery("SELECT * FROM bdcinema.cadeira");
            while (Exibicoes.rs.next()) {
                int idCadeira = Exibicoes.rs.getInt("idCADEIRA");
                int fila = Exibicoes.rs.getInt("fila");
                Acentos ac = new Acentos(idCadeira, fila);
                acentosList.add(ac);
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        } catch (IllegalArgumentException e1) {
            e1.printStackTrace();
        } catch (InvocationTargetException e1) {
            e1.printStackTrace();
        } catch (NoSuchMethodException e1) {
            e1.printStackTrace();
        } catch (SecurityException e1) {
            e1.printStackTrace();
        } finally {
            try {
                if (Exibicoes.con != null) {
                    Exibicoes.con.close();
                }
                if (Exibicoes.rs != null) {
                    Exibicoes.rs.close();
                }
                if (Exibicoes.state != null) {
                    Exibicoes.state.close();
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }

        return acentosList;
    }
}
