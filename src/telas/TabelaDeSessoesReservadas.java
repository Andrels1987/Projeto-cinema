package telas;

import java.awt.*;
import java.awt.event.*;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.*;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import modelos.Filmes;
import modelos.Ticket;

public class TabelaDeSessoesReservadas extends JFrame {
    ArrayList<Integer> ids = new ArrayList<>();
    ArrayList<Ticket> pedidos;
    String cpf;


    TabelaDeSessoesReservadas(String cpf) {
        this.cpf = cpf;
        System.out.println("GERANDO TABELA");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 300);
        setVisible(true);
    }

    public Object[][] getReservas() {
        pedidos = new ArrayList<>();
        try {
            String query = "SELECT idPedido,nomeFilme,horario,data, codigoPedido, e.idExibicao FROM pedido p " + 
            "JOIN exibicao e ON p.idExibicao = e.idExibicao " + 
            "JOIN filme f ON e.idFilme = f.idFilme " +
            "JOIN data_hora dh ON e.idData = dh.idData " +
            "WHERE cpfCliente = ? AND statusPedido = ? ";
            Exibicoes.con = Exibicoes.cdao.getConnection();
            Exibicoes.preparedStatement = Exibicoes.con.prepareStatement(query);
            Exibicoes.preparedStatement.setString(1, this.cpf);
            Exibicoes.preparedStatement.setString(2, "reservado");
            Exibicoes.rs = Exibicoes.preparedStatement.executeQuery();
            while(Exibicoes.rs.next()){
                int id = Exibicoes.rs.getInt("idPedido");
                String nomeFilme = Exibicoes.rs.getString("nomeFilme");
                Time horario = Exibicoes.rs.getTime("horario");       
                Date data = Exibicoes.rs.getDate("data");
                String codigoPedido = Exibicoes.rs.getString("codigoPedido");
                int idExibicao = Exibicoes.rs.getInt("idExibicao");
                pedidos.add(new Ticket(id, nomeFilme, horario, data, codigoPedido, idExibicao));
            }
        } catch (Exception e) {
            e.printStackTrace();
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

            } catch (Exception ex) {
                System.out.println("Erro ao fechar conexão");
            }
        }

        Object[][] res = new Object[pedidos.size()][7];
        for (int i = 0; i < pedidos.size(); i++) {
            res[i][0] = pedidos.get(i).getIdPedido();
            res[i][1] = pedidos.get(i).getNomeFilme();
            res[i][2] = pedidos.get(i).getHorario();
            res[i][3] = pedidos.get(i).getData();
            res[i][4] = pedidos.get(i).getCodigoPedido();
            res[i][5] = false;
        }
        return res;
    }

    public void updateReservas(List<Integer> ids, String action) {

        int a = 0;
        String queryEnd = "";
        while (a < ids.size()) {
            queryEnd += ids.get(a);
            if (a < (ids.size() - 1)) {
                queryEnd += ",";
            }
            a++;
        }
        queryEnd += ")";

        try {
            Exibicoes.con = Exibicoes.cdao.getConnection();
            if (action.equals("finalizar")) {
                String queryFinalizar = "UPDATE pedido SET statusPedido = 'finalizado' where idPEDIDO IN(" + queryEnd;
                Exibicoes.preparedStatement = Exibicoes.con.prepareStatement(queryFinalizar);
                Exibicoes.preparedStatement.executeUpdate();
            } else if (action.equals("deletar")) {
                String queryDeletar = "DELETE FROM pedido where idPEDIDO IN(" + queryEnd;
                Exibicoes.preparedStatement = Exibicoes.con.prepareStatement(queryDeletar);
                // quantidade de linhas da tabela afetadas em caso de sucesso na requisição
                int success = Exibicoes.preparedStatement.executeUpdate();
                if (success > 0) {
                   
                    String update = "UPDATE exibicao SET statusExibicao='disponivel' WHERE idExibicao = ?";
                    Exibicoes.preparedStatement = Exibicoes.con.prepareStatement(update);
                    Exibicoes.preparedStatement.setInt(1, pedidos.get(0).getIdExibicao());
                    Exibicoes.preparedStatement.executeUpdate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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

            } catch (Exception ex) {
                System.out.println("Erro ao fechar conexão");
            }
        }

        ids.removeAll(ids);

        for (Component c : getComponents()) {
            JRootPane cp = (JRootPane) c;
            for (Component c2 : cp.getComponents())
                if (c2 instanceof JLayeredPane) {
                    JLayeredPane layeredPane = (JLayeredPane) c2;
                    for (Component c1 : layeredPane.getComponents()) {
                        if (c1 instanceof JPanel) {
                            JPanel p2 = (JPanel) c1;
                            p2.removeAll();
                            p2.repaint();
                            p2.revalidate();
                            System.out.println("CPF : " + pedidos.get(0).getCpfCliente());
                            gerarTabela();
                            p2.repaint();
                            p2.revalidate();
                        }
                    }
                }
        }

    }

    public void gerarTabela() {
        // Headers for JTable
        String[] columns = { "IdPedido", "nomeFilme", "Data", "Horario", "Codigo Pedido",
                "Marcar" };
        // data for JTable in a 2D table
        Object[][] data = getReservas();
        DefaultTableModel model = new DefaultTableModel(data, columns);
        JTable table = new JTable(model) {
            public Class<?> getColumnClass(int column) {
                return getValueAt(0, column).getClass();

            }
        };

        table.getModel().addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                Integer id = 0;
                Boolean marked = (Boolean) table.getValueAt(table.getSelectedRow(), table.getSelectedColumn());
                id = (Integer) table.getValueAt(table.getSelectedRow(), 0);
                if (marked) {
                    ids.add(id);
                } else {
                    int index = ids.indexOf(id);
                    System.out.println("INDEX " + index);
                    if (index != -1) {
                        ids.remove(index);
                        // ids.removeAll(ids);
                    }
                }

                System.out.println(ids);

            }
        });
        JPanel painelBtn = new JPanel();
        JButton btnFinalizar = new JButton("finalizar");
        btnFinalizar.setActionCommand("finalizar");
        btnFinalizar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                updateReservas(ids, btnFinalizar.getActionCommand());
            }
        });
        JButton btnDeletar = new JButton("Deletar pedidos");
        btnDeletar.setActionCommand("deletar");
        btnDeletar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                ExecutorService ex = Executors.newFixedThreadPool(1);
                updateReservas(ids, btnDeletar.getActionCommand());
                Runnable update = () -> {
                    for (Component c : Exibicoes.frame.getRootPane().getComponents()) {
                        if (c instanceof JLayeredPane) {
                            JLayeredPane p1 = (JLayeredPane) c;
                            for (Component c1 : p1.getComponents()) {
                                if (c1 instanceof JPanel) {
                                    JPanel p2 = (JPanel) c1;
                                    p2.removeAll();
                                    p2.repaint();
                                    p2.revalidate();
                                    ArrayList<Filmes> filmeList = Exibicoes.getAllFilmesFromDatabase();
                                    Exibicoes.renderPainelExibicoes(filmeList);
                                    p2.repaint();
                                    p2.revalidate();
                                }
                            }
                        }
                    }

                };

                ex.execute(update);
                ex.shutdown();
                if (ex.isTerminated()) {
                    System.out.println("SERVICE TERMINADO");
                    ex.shutdownNow();
                }

            }
        });
        painelBtn.add(btnFinalizar);
        painelBtn.add(btnDeletar);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane);
        JLabel labelHead = new JLabel("Lista de reservas");
        labelHead.setFont(new Font("Arial", Font.TRUETYPE_FONT, 20));
        add(labelHead, BorderLayout.PAGE_START);
        add(painelBtn, BorderLayout.PAGE_END);
    }
}
