package telas;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
//import java.util.EventListener;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
/* import java.text.DateFormat;
import java.text.SimpleDateFormat; */
import java.sql.Time;

import javax.swing.*;
import javax.swing.border.Border;

import DAO.ConnectionDao;
import modelos.Exib;
import modelos.Filmes;

public class Exibicoes  {
    /*Data receiving */
    public static int buffersize = 1024;
    public static byte[] buffer = new byte[buffersize];
    public static int serverPort = 998;
    public static int clientPort = 999;    
    
    static Connection con = null;
    static ConnectionDao cdao = null;
    static PreparedStatement preparedStatement = null;
    static Statement state = null;
    static ResultSet rs = null;
    
    List<Filmes> filmeList = new ArrayList<>();
    static List<Exib> allExibs = new ArrayList<>();
    static JPanel painelBuscaReserva;
    static JLabel labelReserva = new JLabel("Acessar Reserva");
    static JTextField tf = new JTextField("Digite o numero da reserva", 20);
    static JFrame frame = new JFrame();

    

    public Exibicoes(DatagramSocket ds) throws InterruptedException {
        
        ExecutorService ex = Executors.newFixedThreadPool(1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setBounds(5, 5, 800, 700);

        filmeList = getAllFilmesFromDatabase();        
        renderPainelExibicoes(filmeList);
        
        frame.setVisible(true);

        Runnable getMessageFromAdmin = () ->
        {
            try{
                DatagramPacket packetReceived = new DatagramPacket(buffer, buffer.length);
                ds.receive(packetReceived);
                System.out.println("Data received");
                JLayeredPane jp = Exibicoes.frame.getLayeredPane();
                    for(Component c : jp.getComponents()){
                        JPanel panel = (JPanel) c;
                        panel.removeAll();
                        panel.revalidate();
                        panel.repaint();
                        ArrayList<Filmes> listaFilmes = getAllFilmesFromDatabase();
                        renderPainelExibicoes(listaFilmes);
                        panel.revalidate();
                        panel.repaint();
                    }
            }catch(Exception exception){
                exception.printStackTrace();
            }
        };

        ex.execute(getMessageFromAdmin);

    }

    public static void setStatusExibicao(Filmes filme, List<Exib> listExib) {

    }

    public static ArrayList<Filmes> getAllFilmesFromDatabase() {
        ArrayList<Filmes> listaFilmes = new ArrayList<>();
        Filmes filmes;
        try {
            cdao = new ConnectionDao();
            con = cdao.getConnection();
            state = con.createStatement();
            rs = state.executeQuery("SELECT * FROM filme");

            while (rs.next()) {
                filmes = new Filmes();
                filmes.setId(rs.getInt("idFilme"));
                filmes.setGenero(rs.getString("genero"));
                filmes.setDescricao(rs.getString("descricao"));
                filmes.setNomeFilme(rs.getString("nomeFILME"));
                filmes.setFilename(rs.getString("filename"));
                listaFilmes.add(filmes);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }

        return listaFilmes;
    }
    public static ImageIcon transformImage(Filmes filme){
        ImageIcon img1;
        String i1 = "../com.project.tickets/src/images/" + filme.getFilename() + ".jpg";
        img1 = new ImageIcon(i1);
        Image img = img1.getImage(); // transform it
        Image newimg = img.getScaledInstance(230, 300, java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
        img1 = new ImageIcon(newimg); // transform it back
        return img1;

    }

    public static void renderPainelExibicoes(List<Filmes> listaFilmes){
        
        JPanel painelParaCadaExibicao;
        JPanel painelParaTodasExibicoes;
        painelParaTodasExibicoes = new JPanel();
        painelParaTodasExibicoes.setLayout(new GridLayout(2, 2, 5, 5));
        painelBuscaReserva = new JPanel();
        painelBuscaReserva.setBackground(new Color(171, 156, 129)); 
        painelBuscaReserva.add(labelReserva);
        painelBuscaReserva.add(tf);
        frame.add(painelBuscaReserva, BorderLayout.NORTH);
         
        JLabel labelImagemFilme;
        JButton btnComprar;
        JButton btnReservar;
        allExibs = getAllExibicoes();
        for (Filmes filme : listaFilmes) {
            labelImagemFilme = new JLabel(transformImage(filme));
            labelImagemFilme.setPreferredSize(new Dimension(200, 300));
            Border border = BorderFactory.createLineBorder(Color.ORANGE);
            labelImagemFilme.setBorder(border);

            btnComprar = new JButton("COMPRAR");
            btnReservar = new JButton("RESERVAR");
            painelParaCadaExibicao = new JPanel();
            painelParaCadaExibicao.setBackground(new Color(41, 34, 26));
            GridBagLayout gbag = new GridBagLayout();
            GridBagConstraints gbc = new GridBagConstraints();
            painelParaCadaExibicao.setLayout(gbag);
            // PRIMEIRA LINHA

            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(5, 0, 0, 0);
            gbag.setConstraints(labelImagemFilme, gbc);

            JPanel painelBotoesCompraReservar = new JPanel();
            painelBotoesCompraReservar.setBackground(new Color(41, 34, 26));

            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.insets = new Insets(0, 0, 0, 0);
            gbag.setConstraints(painelBotoesCompraReservar, gbc);

            btnComprar.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae){
                    List<Exib> exibicaoPorFilme = getExibicaoPorFilme(filme, allExibs);
                    
                    new CompraReserva(filme, exibicaoPorFilme);
                }
            });
            painelBotoesCompraReservar.add(btnComprar);
            painelBotoesCompraReservar.add(btnReservar);

            painelParaCadaExibicao.add(labelImagemFilme);
            painelParaCadaExibicao.add(painelBotoesCompraReservar);
            
            painelParaTodasExibicoes.add(painelParaCadaExibicao);
        }
        
        painelParaTodasExibicoes.setBackground(new Color(41, 34, 26));
        JScrollPane scrollBar = new JScrollPane(painelParaTodasExibicoes);
        scrollBar.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        Exibicoes.frame.add(scrollBar, BorderLayout.CENTER);
    }

    public static ArrayList<Exib> getExibicaoPorFilme(Filmes filme,List<Exib> exibicoes){
        ArrayList<Exib> exibicaoFilme = new ArrayList<>();
        for(Exib e : exibicoes){
            if(filme.getId() == e.getIdFilme()){
                exibicaoFilme.add(e);
            }
        }
        return exibicaoFilme;
    }

    public static ArrayList<Exib> getAllExibicoes(){
        ArrayList<Exib> exibicaoFilme = new ArrayList<>();
        try {
            cdao = new ConnectionDao();
            con = cdao.getConnection();
            preparedStatement = con.prepareStatement(
                    "SELECT idExibicao, f.idFilme, e.idData, idSALA, horario, statusFilme, dh.data " +
                            "from exibicao e join data_hora dh " +
                            "on e.idData = dh.idData join filme f on f.idFilme = e.idFilme");
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                int idFilme = rs.getInt("idFilme");
                int idData = rs.getInt("idData");
                int idSala = rs.getInt("idSALA");
                String statusFilme = rs.getString("statusFilme");
                // DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date dataExibicao = rs.getDate("data");
                dataExibicao = new Date(dataExibicao.getTime());
                int idExibicao = rs.getInt("idExibicao");
                Time horario = rs.getTime("horario");
                Exib item = new Exib(dataExibicao, idSala, idData, idFilme, horario.toString(),
                        statusFilme);
                item.setIdExibicao(idExibicao);
                exibicaoFilme.add(item);
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        } catch (IllegalArgumentException e1) {
            e1.printStackTrace();
        } catch (NoSuchMethodException e1) {
            e1.printStackTrace();
        } catch (SecurityException e1) {
            e1.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return exibicaoFilme;
    }

    
}
