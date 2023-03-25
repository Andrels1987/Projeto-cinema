package telas;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import modelos.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;

public class CompraReserva extends JFrame {
    /**********************************/
    static Cliente cliente = null;
    static String dataEHorarioDaExibicao;
    static ArrayList<Acentos> cadeira_e_fila = new ArrayList<>();
    static double valorTotalDoTicket;
    static ArrayList<Ticket> pedidos = null;
    static Exib exibicao = new Exib();
    static int statusExibicao;

    /*************************************/

    static ArrayList<Integer> cadeirasEscolhidas = new ArrayList<>();
    ArrayList<Exib> dataHora;
    JTextField jtcpf, jtnome, jtFilme, jtemail, jttelefone;
    static JLabel labelValor = new JLabel("0.0");
    JLabel labelcpf, labelnome, labelemail, labelnomeFilme, labelAcento, labelDataHora, labeltelefone;
    JComboBox<String> jcDataHoraExibicao;
    JButton jbfinalizar, jbreservar, jbacentos, jbcadastrar;
    JPanel jpupper, jpbottom;

    public CompraReserva(Filmes filme, List<Exib> listDeExibicoes) {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(2, 1, 2, 2));
        setSize(new Dimension(600, 500));

        // UPPER panel COMPONENTS
        labelemail = new JLabel("EMAIL");
        jtemail = new JTextField("", 20);
        labelnome = new JLabel("Nome: ");
        labeltelefone = new JLabel("TELEFONE");
        jttelefone = new JTextField("", 20);
        jtnome = new JTextField("", 20);
        labelcpf = new JLabel("CPF");
        jtcpf = new JTextField("", 20);
        jbcadastrar = new JButton("Cadastrar");
        jtcpf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String cpf = jtcpf.getText();
                Cliente c = new Cliente("", "", "(21) ", "");
                try {
                    Exibicoes.con = Exibicoes.cdao.getConnection();
                    Exibicoes.preparedStatement = Exibicoes.con
                            .prepareStatement("SELECT * FROM cliente where cpf = ?;");
                    Exibicoes.preparedStatement.setString(1, cpf);
                    Exibicoes.rs = Exibicoes.preparedStatement.executeQuery();
                    if (Exibicoes.rs.next()) {
                        c.setId(Exibicoes.rs.getInt("idCLIENTE"));
                        c.setNome(Exibicoes.rs.getString("nomeCliente"));
                        c.setTelefone(Exibicoes.rs.getString("telefone"));
                        c.setCpf(Exibicoes.rs.getString("cpf"));
                        c.setEmail(Exibicoes.rs.getString("email"));
                    } else {
                        int resposta = JOptionPane.showConfirmDialog(null,
                                "Nenhum Cliente encontrado : Deseja Cadastrar?");

                        if (resposta == 0) {
                            jtemail.setEditable(true);
                            jtnome.setEditable(true);
                            jttelefone.setEditable(true);
                            jbcadastrar.setEnabled(true);
                        } else if (resposta == 1) {
                            jtnome.setEditable(true);
                            c.setEmail("");
                            c.setTelefone("");
                            c.setCpf(null);
                        }
                    }
                    CompraReserva.cliente = c;
                    jtnome.setText(CompraReserva.cliente.getNome());
                    jtemail.setText(CompraReserva.cliente.getEmail());
                    jttelefone.setText(CompraReserva.cliente.getTelefone());
                } catch (SQLException sqle) {

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
                        Exibicoes.con.close();
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }

                    try {
                        Exibicoes.rs.close();
                    } catch (SQLException e2) {
                        e2.printStackTrace();
                    }
                    try {
                        Exibicoes.preparedStatement.close();
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }
            }

        });
        jpupper = new JPanel();
        jpupper.setLayout(null);
        jpupper.setBackground(new Color(0, 0, 234));

        jpupper.add(labelcpf);
        labelcpf.setBounds(20, 10, 100, 30);
        labelcpf.setFont(new Font("Arial", Font.BOLD, 15));
        jpupper.add(jtcpf);
        jtcpf.setBounds(100, 10, 200, 20);

        jpupper.add(labelnome);
        labelnome.setBounds(20, 40, 100, 30);
        labelnome.setFont(new Font("Arial", Font.BOLD, 15));
        jpupper.add(jtnome);
        jtnome.setBounds(100, 40, 200, 20);
        jtnome.setEditable(false);

        jpupper.add(labelemail);
        labelemail.setBounds(20, 70, 100, 30);
        labelemail.setFont(new Font("Arial", Font.BOLD, 15));
        jpupper.add(jtemail);
        jtemail.setBounds(100, 70, 200, 20);
        jtemail.setEditable(false);

        jpupper.add(labeltelefone);
        labeltelefone.setBounds(20, 100, 100, 30);
        labeltelefone.setFont(new Font("Arial", Font.BOLD, 15));
        jpupper.add(jttelefone);
        jttelefone.setBounds(130, 100, 200, 20);
        jttelefone.setEditable(false);

        jpupper.add(jbcadastrar);
        jbcadastrar.setBounds(180, 150, 100, 20);
        jbcadastrar.setEnabled(false);
        jbcadastrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                ExecutorService service = Executors.newSingleThreadExecutor();
                Runnable insertCliente = () -> {
                    CompraReserva.cliente.setCpf(jtcpf.getText());
                    CompraReserva.cliente.setEmail(jtemail.getText());
                    CompraReserva.cliente.setNome(jtnome.getText());
                    CompraReserva.cliente.setTelefone(jttelefone.getText());
                    try {
                        String query = "INSERT INTO cliente VALUES(null, ?,?,?,?)";
                        Exibicoes.con = Exibicoes.cdao.getConnection();
                        Exibicoes.preparedStatement = Exibicoes.con.prepareStatement(query,
                                Statement.RETURN_GENERATED_KEYS);
                        Exibicoes.preparedStatement.setString(1, CompraReserva.cliente.getNome());
                        Exibicoes.preparedStatement.setString(2, CompraReserva.cliente.getTelefone());
                        Exibicoes.preparedStatement.setString(3, CompraReserva.cliente.getCpf());
                        Exibicoes.preparedStatement.setString(4, CompraReserva.cliente.getEmail());

                        int res = Exibicoes.preparedStatement.executeUpdate();
                        if (res == 1) {
                            ResultSet rs2 = Exibicoes.preparedStatement.getGeneratedKeys();
                            rs2.next();
                            int idCliente = rs2.getInt(1);
                            JOptionPane.showMessageDialog(null, "Cliente cadastrado com sucesso");
                            CompraReserva.cliente.setId(idCliente);
                        }
                    } catch (IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                            | SecurityException | SQLException e) {
                        e.printStackTrace();
                    }
                };

                service.submit(insertCliente);
            }
        });

        // BOTTON PANEL
        // BOTTOM COMPONENTS
        labelnomeFilme = new JLabel(filme.getNomeFilme());
        labelDataHora = new JLabel("Data da exibição");
        labelAcento = new JLabel("Acentos");
        jbacentos = new JButton("Acentos");
        jbacentos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Phaser psr = new Phaser(1);
                ExecutorService ex = Executors.newSingleThreadExecutor();
                BuscaPedidosEAcentos tb = new BuscaPedidosEAcentos(psr, CompraReserva.exibicao);
                ex.execute(tb);

                psr.arriveAndAwaitAdvance();
                psr.arriveAndAwaitAdvance();
                psr.arriveAndAwaitAdvance();
                psr.arriveAndDeregister();
                System.out.println("TERMINADO: " + psr.isTerminated());
                if (psr.isTerminated()) {
                    ex.shutdown();
                }

            }

        });

        labelDataHora = new JLabel("Data da exibição");

        //// MODIFICAR PARA STREAMS / FUNCIONAL PROGRAMMING
        jcDataHoraExibicao = new JComboBox<>();
        dataHora = new ArrayList<>();
        for (Exib e : listDeExibicoes) {
            Date data = e.getData();
            String d = data.toString();
            int sala = e.getSala();
            String hora = e.getHorario();
            d = "(Sala: " + sala + ") - " + d + " às " + hora;
            jcDataHoraExibicao.addItem(d);
            dataHora.add(e);
        }

        try {
            CompraReserva.exibicao = dataHora.get(jcDataHoraExibicao.getSelectedIndex());
            CompraReserva.dataEHorarioDaExibicao = CompraReserva.exibicao.getData().toString() + " - "
                    + CompraReserva.exibicao.getHorario();
        } catch (IndexOutOfBoundsException e) {
            return;
        }

        jcDataHoraExibicao.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                CompraReserva.exibicao = dataHora.get(jcDataHoraExibicao.getSelectedIndex());
                CompraReserva.dataEHorarioDaExibicao = exibicao.getData().toString() + " - "
                        + CompraReserva.exibicao.getHorario();
            }
        });

        jpbottom = new JPanel();
        jpbottom.setLayout(null);
        jpbottom.setBackground(new Color(0, 222, 0));

        jpbottom.add(labelnomeFilme);
        labelnomeFilme.setFont(new Font("Arial", Font.BOLD, 25));
        labelnomeFilme.setBounds(20, 10, 220, 30);

        jpbottom.add(labelDataHora);
        labelDataHora.setFont(new Font("Arial", Font.BOLD, 25));
        labelDataHora.setBounds(20, 50, 200, 30);
        jpbottom.add(jcDataHoraExibicao);
        jcDataHoraExibicao.setBounds(230, 50, 310, 25);
        jpbottom.add(labelAcento);
        labelAcento.setBounds(20, 90, 100, 30);
        labelAcento.setFont(new Font("Arial", Font.BOLD, 25));
        jpbottom.add(jbacentos);
        jbacentos.setBounds(150, 90, 100, 30);

        jpbottom.add(labelValor);
        labelValor.setBounds(20, 135, 120, 30);
        labelValor.setFont(new Font("Arial", Font.BOLD, 25));
        labelValor.setEnabled(false);

        jbfinalizar = new JButton("Finalizar");
        jbreservar = new JButton("Reservar");
        jpbottom.add(jbreservar);
        jbreservar.setBounds(230, 165, 100, 30);
        jpbottom.add(jbfinalizar);
        jbfinalizar.setBounds(350, 165, 100, 30);

        // FINALIZAR COMPRA
        jbfinalizar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                criarPedidos("finalizado");
                enviarPedido(filme);
            }

        });
        /// testando botao reservar
        jbreservar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                criarPedidos("reservado");
                enviarPedido(filme);
            }

        });

        add(jpupper);
        add(jpbottom);
        setVisible(true);

    }

    public List<String> buscarCodigoPedido() {
        List<String> codigos = new ArrayList<>();
        String result = "";
        try {
            Exibicoes.con = Exibicoes.cdao.getConnection();
            Exibicoes.state = Exibicoes.con.createStatement();
            Exibicoes.rs = Exibicoes.state.executeQuery("SELECT codigoPedido FROM pedido");

            while (Exibicoes.rs.next()) {
                result = Exibicoes.rs.getString("codigoPedido");
                codigos.add(result);
            }
        } catch (Exception e) {
        }

        return codigos;

    }

    public void enviarPedido(Filmes filme) {
        // ADDING TO DATABASE
        // INSERT INTO pedido VALUES(idPedido,valor, idCLIENTE,idExibicao,idCadeira)

        if (CompraReserva.cliente != null) {
            String query = "INSERT INTO pedido VALUES ";
            for (int i = 0; i < CompraReserva.pedidos.size(); i++) {
                query = query + "(" + null + "," +
                        CompraReserva.pedidos.get(i).getValor() + "," + CompraReserva.pedidos.get(i).getCpfCliente() +
                        "," + CompraReserva.pedidos.get(i).getIdExibicao() + ","
                        + CompraReserva.pedidos.get(i).getIdCadeira() +
                        "," + CompraReserva.pedidos.get(i).getCodigoPedido() + "," + "'"
                        + CompraReserva.pedidos.get(i).getStatusPedido() + "'" + ")";
                if (i < (CompraReserva.pedidos.size() - 1)) {
                    query += ",";
                }
            }
            System.out.println("QUERY: " + query);
            try {
                Exibicoes.con = Exibicoes.cdao.getConnection();
                Exibicoes.state = Exibicoes.con.createStatement();
                Exibicoes.state.execute(query);

                if (CompraReserva.statusExibicao == 1) {
                    Exibicoes.state = Exibicoes.con.createStatement();
                    Exibicoes.state.execute("UPDATE exibicao SET statusExibicao = 'esgotado' WHERE idExibicao = "
                            + CompraReserva.exibicao.getIdExibicao());
                    Exibicoes.setStatusExibicao(filme, dataHora);
                    Exibicoes.updateTelaExibicao();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
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
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }

                dispose();
            }
        }

        CompraReserva.cliente = null;
        CompraReserva.labelValor = new JLabel("");

    }

    public void criarPedidos(String statuspedido) {
        CompraReserva.pedidos = new ArrayList<>();
        Random ran = new Random();
        List<String> codPedidos = buscarCodigoPedido();
        String codigo = "";
        if (CompraReserva.cliente != null) {
            for (int cadeira : CompraReserva.cadeirasEscolhidas) {
                String codigoAnterior = codigo;
                Ticket t = new Ticket();
                t.setCpfCliente(CompraReserva.cliente.getCpf());
                t.setIdExibicao(CompraReserva.exibicao.getIdExibicao());
                t.setIdCadeira(cadeira);
                Integer cod = ran.nextInt(1000);
                codigo = String.format("%03d", cod);
                for (int i = 0; i < codPedidos.size(); i++) {
                    if (codigo.equals(codPedidos.get(i)) || codigo.equals(codigoAnterior)) {
                        cod = ran.nextInt(1000);
                        codigo = cod.toString();
                        i = 0;
                    }
                }

                t.setCodigoPedido(codigo);
                t.setStatusPedido(statuspedido);
                CompraReserva.pedidos.add(t);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Nenhum cliente inserido");
        }
    }
}
