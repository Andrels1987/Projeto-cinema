import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;



import DAO.ConnectionDao;
import modelos.DataExibicao;
import modelos.Filmes;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;

public class TelaAdministrador extends JFrame {
    private PropertyChangeSupport support = new PropertyChangeSupport(this);
    static ArrayList<Filmes> listaDeFilmes;

    static ArrayList<DataExibicao> listaDeIdsDatas;
    static ArrayList<Integer> listaDeIdsSalas;
    static JTextField jtfilme;
    static JTextField jtgenero;
    static JTextField jtdescricao;;
    static JTextField jtfilename;
    static JTextField jtdata;
    static JTextField jtstatus;
    static JButton enviar;
    static JLabel nomeFilme;
    static JLabel genero = new JLabel("Genero");
    static JLabel descricao = new JLabel("Descrição");
    static JLabel filename = new JLabel("Filename");
    static JLabel sala = new JLabel("Sala");
    static JLabel data = new JLabel("Data");
    static JLabel status = new JLabel("Status");
    static JLabel horario = new JLabel("Hora");
    static JComboBox<String> jcfilme;
    static JComboBox<String> generos;
    static JComboBox<String> jcsala;
    static JComboBox<String> jcdata;
    static JComboBox<String> jchorario;
    ////////////////////////////////////////////
    static Connection con = null;
    static ConnectionDao cdao = new ConnectionDao();
    static ResultSet res = null;
    static PreparedStatement preparedStatement = null;
    ////////////////////////
    //static boolean change = false;
    ////
    JTabbedPane jtp = new JTabbedPane();
    //Panel addFilmes, addExibicoes, clientes;
    public static DatagramSocket ds;
    public static int buffersize = 1024;
    public static byte[] buffer = new byte[buffersize];
    public static int serverPort = 998;
    public static int clientPort = 999;
    static ObjectOutputStream objoutput;
    static Filmes filme;
    

    TelaAdministrador(DatagramSocket ds) {
        
        listaDeIdsDatas = getAllAvailableDates();
        listaDeFilmes = getAllAvailableFilmes();
        listaDeIdsSalas = getAllAvailableRooms();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(340, 350));
        jtp.add("Filmes", new ADMFilmeS(ds));
        jtp.add("Exibiçoes", new ADMExibicoes(ds));
        //jtp.add("Clientes", new ADMClientes());
        add(jtp);
        setVisible(true);

    }
    TelaAdministrador(){

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    ds = new DatagramSocket(serverPort);
                    new TelaAdministrador(ds);
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    public static ArrayList<DataExibicao> getAllAvailableDates(){
        ArrayList<DataExibicao> dateList = new ArrayList<>();
        Date d;
        Time t;
        int id;
        try {
            TelaAdministrador.con = TelaAdministrador.cdao.getConnection();
            TelaAdministrador.preparedStatement = TelaAdministrador.con.prepareStatement("SELECT * FROM data_hora");
            
             TelaAdministrador.res = TelaAdministrador.preparedStatement.executeQuery();
            
            while(TelaAdministrador.res.next()){
                d = TelaAdministrador.res.getDate("data");
                t = TelaAdministrador.res.getTime("horario");
                id = TelaAdministrador.res.getInt("idData");
                dateList.add(new DataExibicao(id,d,t));
            }
            
        } catch ( IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | SQLException e1) {
            System.out.println(e1.getMessage());
        }
        return dateList;
    }
    public static ArrayList<Filmes> getAllAvailableFilmes(){
        ArrayList<Filmes> lf = new ArrayList<>();
        Filmes f;
        try {
            TelaAdministrador.con = TelaAdministrador.cdao.getConnection();
            TelaAdministrador.preparedStatement = TelaAdministrador.con.prepareStatement("SELECT * FROM filme");
            
            TelaAdministrador.res = TelaAdministrador.preparedStatement.executeQuery();
            
            while(TelaAdministrador.res.next()){
               f = new Filmes();
               f.setId(TelaAdministrador.res.getInt("idFilme"));
               f.setGenero(TelaAdministrador.res.getString("genero"));
               f.setDescricao(TelaAdministrador.res.getString("descricao"));
               f.setNomeFilme(TelaAdministrador.res.getString("nomeFILME"));
               f.setFilename(TelaAdministrador.res.getString("filename"));
               lf.add(f);
            }

        } catch (IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | SQLException e1) {
            e1.printStackTrace();
        }
        return lf;
    }

    public static ArrayList<Integer> getAllAvailableRooms(){
        ArrayList<Integer> lf = new ArrayList<>();
        try {
            TelaAdministrador.con = TelaAdministrador.cdao.getConnection();
            TelaAdministrador.preparedStatement = TelaAdministrador.con.prepareStatement("SELECT idSALA FROM sala");            
            TelaAdministrador.res = TelaAdministrador.preparedStatement.executeQuery();
            
            while(TelaAdministrador.res.next()){
               int s = TelaAdministrador.res.getInt("idSALA");               
               lf.add(s);
            }

        } catch (IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | SQLException e1) {
            e1.printStackTrace();
        }
        return lf;
    }
   
    public void setValue(ArrayList<Filmes> newListfilme){
        support.firePropertyChange("listaDeFilmes", listaDeFilmes, newListfilme);
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl){
        support.addPropertyChangeListener(pcl);
    }
    public void removePropertyChangeListener(PropertyChangeListener pcl){
        support.removePropertyChangeListener(pcl);
    }
}

class ADMFilmeS extends JPanel implements ActionListener, PropertyChangeListener {    
    TelaAdministrador ta;

    ADMFilmeS(DatagramSocket ds) {
        ta = new TelaAdministrador();
        ta.addPropertyChangeListener(this);
        JButton enviar = new JButton("Enviar");
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        setLayout(gbl);

        // primeira linhas
        TelaAdministrador.nomeFilme = new JLabel("Filme");
        gbc.insets = new Insets(5, 5, 0, 0);
        gbl.setConstraints(TelaAdministrador.nomeFilme, gbc);
        TelaAdministrador.jtfilme = new JTextField(20);
        gbc.gridx = 1;        
        gbl.setConstraints(TelaAdministrador.jtfilme, gbc);
        // segunda linha
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbl.setConstraints(TelaAdministrador.genero, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        TelaAdministrador.generos = new JComboBox<>();
        TelaAdministrador.generos.addItem("ação");
        TelaAdministrador.generos.addItem("comedia");
        TelaAdministrador.generos.addItem("Romance");
        TelaAdministrador.generos.addItem("Aventura");
        gbl.setConstraints(TelaAdministrador.generos, gbc);
        // terceira linha
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbl.setConstraints(TelaAdministrador.descricao, gbc);
        gbc.gridx = 1;
        TelaAdministrador.jtdescricao = new JTextField(20);
        gbl.setConstraints(TelaAdministrador.jtdescricao, gbc);
        // quarta linha
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbl.setConstraints(TelaAdministrador.filename, gbc);
        TelaAdministrador.jtfilename = new JTextField("",20);
        gbc.gridx = 1;
        gbl.setConstraints(TelaAdministrador.jtfilename, gbc);
        // quinta linha
        gbc.gridx = 1;
        gbc.gridy = 4;

        gbl.setConstraints(enviar, gbc);

        enviar.addActionListener(this);

        /* TelaAdministrador.jtfilename.getDocument().addDocumentListener( new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                if(TelaAdministrador.jtfilename.getText().length() == 2 || TelaAdministrador.jtfilename.getText().length() == 5){
                    Runnable insertDash = () -> {
                        String fieldText = TelaAdministrador.jtfilename.getText();
                        fieldText+="/";
                        TelaAdministrador.jtfilename.setText(fieldText);
                        
                    };
                    SwingUtilities.invokeLater(insertDash);
                }
                              
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                
            }

        }); */

        add(TelaAdministrador.nomeFilme);
        add(TelaAdministrador.jtfilme);
        add(TelaAdministrador.genero);
        add(TelaAdministrador.generos);
        add(TelaAdministrador.descricao);
        add(TelaAdministrador.jtdescricao);
        add(TelaAdministrador.filename);
        add(TelaAdministrador.jtfilename);
        add(enviar);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        String nf = TelaAdministrador.jtfilme.getText();
        String desc = TelaAdministrador.jtdescricao.getText();
        String fn = TelaAdministrador.jtfilename.getText();
        int genIndex = TelaAdministrador.generos.getSelectedIndex();
        String gen = TelaAdministrador.generos.getItemAt(genIndex);
        
         try {
            TelaAdministrador.con = TelaAdministrador.cdao.getConnection();
            TelaAdministrador.preparedStatement = TelaAdministrador.con.prepareStatement("INSERT INTO filme VALUES(NULL,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            TelaAdministrador.preparedStatement.setString(1, gen);
            TelaAdministrador.preparedStatement.setString(2, desc);
            TelaAdministrador.preparedStatement.setString(3, nf);
            TelaAdministrador.preparedStatement.setString(4, fn);
            int res = TelaAdministrador.preparedStatement.executeUpdate();
            
            if(res > 0){
                ResultSet rs2 = TelaAdministrador.preparedStatement.getGeneratedKeys();
                rs2.next();
                int key = rs2.getInt(1);
                TelaAdministrador.filme = new Filmes(key, gen, desc, nf, fn);
                Filmes f = new Filmes(key, gen, desc, nf, fn);
                TelaAdministrador.listaDeFilmes.add(f);
                ta.setValue(TelaAdministrador.listaDeFilmes);
                ArrayList<Filmes> newList = new ArrayList<>(TelaAdministrador.listaDeFilmes);
                TelaAdministrador.jcfilme.removeAllItems();
                for (Filmes e2 : newList) {
                    TelaAdministrador.jcfilme.addItem(e2.getNomeFilme());
                }
                

                TelaAdministrador.objoutput = new ObjectOutputStream(out);
                TelaAdministrador.objoutput.writeObject(TelaAdministrador.filme);
                TelaAdministrador.buffer = out.toByteArray();
                TelaAdministrador.ds.send(new DatagramPacket(TelaAdministrador.buffer, TelaAdministrador.buffer.length, InetAddress.getLocalHost(), TelaAdministrador.clientPort));
                System.out.println("Message sent from client");
                Thread.sleep(2000); 
           }

        } catch (IOException | InterruptedException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | SQLException e1) {
            e1.printStackTrace();
        }  
    
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        
    }

}

 class ADMExibicoes extends JPanel implements ActionListener {;
    

    ADMExibicoes(DatagramSocket ds) {
        JButton enviar = new JButton("Enviar");
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        setLayout(gbl);

        // primeira linhas
        TelaAdministrador.nomeFilme = new JLabel("Filme");
        gbc.insets = new Insets(5, 3, 0, 0);
        gbl.setConstraints(TelaAdministrador.nomeFilme, gbc);
        TelaAdministrador.jcfilme = new JComboBox<>();
        for (Filmes e : TelaAdministrador.listaDeFilmes) {
            TelaAdministrador.jcfilme.addItem(e.getNomeFilme());
        }
        gbc.gridx = 1;
        gbl.setConstraints(TelaAdministrador.jcfilme, gbc);
        // segunda linha
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbl.setConstraints(TelaAdministrador.sala, gbc);
        TelaAdministrador.jcsala = new JComboBox<>();
        for(Integer i : TelaAdministrador.listaDeIdsSalas){
            TelaAdministrador.jcsala.addItem("Sala: " + i);
        }
        gbc.gridx = 1;
        gbl.setConstraints(TelaAdministrador.jcsala, gbc);
        // terceira linha
        gbc.gridx = 0;
        gbc.gridy = 2;        
        gbl.setConstraints(TelaAdministrador.data, gbc);
        TelaAdministrador.jcdata = new JComboBox<>();
        for(DataExibicao i : TelaAdministrador.listaDeIdsDatas){
            TelaAdministrador.jcdata.addItem(i.getData() + " - " + i.getHora());
        }
        gbc.gridx = 1;
        gbl.setConstraints(TelaAdministrador.jcdata, gbc);
        /* TelaAdministrador.jchorario = new JComboBox<>();
        for(DataExibicao i : TelaAdministrador.listaDeIdsDatas){
            TelaAdministrador.jchorario.addItem("" + i.getHora());
        } */
        //gbc.gridx = 2;
        //gbl.setConstraints(TelaAdministrador.jchorario, gbc);
        // quarta linha
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbl.setConstraints(TelaAdministrador.status, gbc);
        TelaAdministrador.jtstatus = new JTextField(20);
        gbc.gridx = 1;
        gbl.setConstraints(TelaAdministrador.jtstatus, gbc);
        //Quinta linha
        gbc.gridx = 1;
        gbc.gridy = 4;

        gbl.setConstraints(enviar, gbc);

        enviar.addActionListener(this);
        add(TelaAdministrador.nomeFilme);
        add(TelaAdministrador.jcfilme);
        add(TelaAdministrador.sala);
        add(TelaAdministrador.jcsala);
        add(TelaAdministrador.data);
        add(TelaAdministrador.jcdata);
        //add(TelaAdministrador.jchorario);
        add(TelaAdministrador.status);
        add(TelaAdministrador.jtstatus);
        add(enviar);
    }

     @Override
    public void actionPerformed(ActionEvent e) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        //Exib nf = TelaAdministrador.listaDeFilmes.get(TelaAdministrador.jcfilme.getSelectedIndex());
        
        Filmes newFilme = new Filmes();
        String nomefilme = (String) TelaAdministrador.jcfilme.getSelectedItem();
        for(Filmes f : TelaAdministrador.listaDeFilmes){
            if(f.getNomeFilme().equals(nomefilme)){
                newFilme = f;
            }
        }
        
        int sala = TelaAdministrador.listaDeIdsSalas.get(TelaAdministrador.jcsala.getSelectedIndex());
        DataExibicao data = TelaAdministrador.listaDeIdsDatas.get(TelaAdministrador.jcdata.getSelectedIndex());
        String status = TelaAdministrador.jtstatus.getText();

        try {
            TelaAdministrador.con = TelaAdministrador.cdao.getConnection();
            TelaAdministrador.preparedStatement = TelaAdministrador.con.prepareStatement("INSERT INTO exibicao VALUES(NULL,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            TelaAdministrador.preparedStatement.setInt(1, data.getId());
            TelaAdministrador.preparedStatement.setInt(2, newFilme.getId());
            TelaAdministrador.preparedStatement.setInt(3, sala);
            TelaAdministrador.preparedStatement.setString(4, status);
            int res = TelaAdministrador.preparedStatement.executeUpdate();
            
            if(res == 1){
                ResultSet rs2 = TelaAdministrador.preparedStatement.getGeneratedKeys();
                rs2.next();
                //int key = rs2.getInt(1);
                TelaAdministrador.filme = new Filmes(newFilme.getId(), newFilme.getGenero(), newFilme.getDescricao(), newFilme.getNomeFilme(), newFilme.getFilename());
                TelaAdministrador.objoutput = new ObjectOutputStream(out);
                TelaAdministrador.objoutput.writeObject(TelaAdministrador.filme);
                TelaAdministrador.buffer = out.toByteArray();
                TelaAdministrador.ds.send(new DatagramPacket(TelaAdministrador.buffer, TelaAdministrador.buffer.length, InetAddress.getLocalHost(), TelaAdministrador.clientPort));
                Thread.sleep(2000);
                System.out.println("message sent");
            }

        } catch (IOException | InterruptedException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | SQLException e1) {
            e1.printStackTrace();
        } 
    }

}

/*class ADMClientes extends JPanel {
    JLabel nome = new JLabel("Nome");
    JLabel cpf = new JLabel("CPF");
    JLabel email = new JLabel("Email");

    ADMClientes() {

        add(nome);
        add(cpf);
        add(email);
    }

} */
