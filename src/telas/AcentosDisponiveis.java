package telas;

import java.awt.*;
import java.awt.event.*;
import java.util.*;import java.util.concurrent.Phaser;

import javax.swing.*;
import javax.swing.plaf.DimensionUIResource;

import modelos.Acentos;
import modelos.Exib;
import modelos.Ticket;

public class AcentosDisponiveis extends JFrame{
    int quantidadeDeAcentosSelecionados = 0;
    ArrayList<Integer> seats = new ArrayList<>();
    Phaser phase = new Phaser(1);
    JButton finalizar;
    JPanel painelbtn;
    double v = new Ticket().getValor();
    double valor = 0.0;
    

    public AcentosDisponiveis(ArrayList<Acentos> acentos, ArrayList<Ticket> tickets, Exib exibicao) {
        CompraReserva.statusExibicao = 0;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(new DimensionUIResource(600, 600));
        setLayout(new BorderLayout());
        finalizar = new JButton("Finalizar e Fechar");
        painelbtn = new JPanel();
        painelbtn.setLayout(new GridLayout(5, 5, 2, 2));
        painelbtn.setBounds(0, 0, getWidth(), getHeight() - 200);
        for (Acentos a : acentos) {
            JToggleButton b = new JToggleButton("Cadeira: " + a.getId());
            b.setContentAreaFilled(false);
            b.setOpaque(true);
            b.setActionCommand("Acento " + a.getId());
            b.setBackground(new Color(121,245,235));
            b.setFont(new Font("Arial", Font.BOLD,15));
            b.addItemListener(new ItemListener() {                
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (b.isSelected()) {
                        b.setBackground(new Color(84,241,84));
                        setSeats(a.getId());
                        CompraReserva.cadeira_e_fila.add(new Acentos(a.getId(), a.getFila()));
                        valor+=v;
                        quantidadeDeAcentosSelecionados++;
                    }else{
                        b.setBackground(new Color(121,245,235));
                        removeSeats(a.getId());
                        valor-=v;
                        quantidadeDeAcentosSelecionados--;
                    }
                    
                    CompraReserva.valorTotalDoTicket = valor;
                }
            }); 
            for (Ticket t : tickets) {
                if (a.getId() == t.getIdCadeira()) {
                    b.setBackground(new Color(84,241,84));
                    b.setEnabled(false);
                }

            }

            painelbtn.add(b);
        }

        finalizar.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                CompraReserva.labelValor.setText(valor+"");
                CompraReserva.cadeirasEscolhidas = seats;
                if((tickets.size() + quantidadeDeAcentosSelecionados) == acentos.size()){
                    CompraReserva.statusExibicao = 1;
                    for(Exib ex : Exibicoes.exib){
                        if(ex.getIdExibicao() == exibicao.getIdExibicao()){                            
                            ex.setStatus("esgotado");
                        }
                    }
                }                
                setVisible(false);
                dispose();
            }
            
        });

        add(painelbtn, BorderLayout.CENTER);
        add(finalizar, BorderLayout.SOUTH);
        setVisible(true);
    }

    public void setSeats(int a){
        seats.add(a);
    }
    public void removeSeats(int a){
        seats.remove(Integer.valueOf(a));
    }
    

    
    
   
}