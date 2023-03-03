package telas;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.plaf.DimensionUIResource;
import java.awt.*;

public class Pagamento extends JFrame {
    JLabel pagamento = new JLabel("Pagamento");
    JLabel nCartao = new JLabel("N° Cartão");
    JLabel codCartao = new JLabel("Cod. segurança");
    JLabel validade = new JLabel("Validade");
    JLabel cartaoNaoEncontrado = new JLabel("Cartão nao cadastrado");
    JTextField jtNCartao;
    JTextField jtcodCartao;
    JTextField jtValidade;
    JButton cancelar;
    JButton comprar;

    public Pagamento() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(new DimensionUIResource(400, 400));
        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        setLayout(gridBag);

        // LABEL PAGAMENTO
        pagamento.setFont(new Font("Arial", Font.BOLD,20));
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(10, 10, 2, 0);
        gridBag.setConstraints(pagamento, constraints);
        
        // LABEL CARTÃO E CAMPO NUMERO DO CARTÃO
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.WEST;
        nCartao = new JLabel("N° do Cartão");
        constraints.gridx = 0;
        constraints.gridy = 1;
        gridBag.setConstraints(nCartao, constraints);

        
        jtNCartao = new JTextField("", 20);
        constraints.gridx = 1;
        constraints.gridy = 1;
        gridBag.setConstraints(jtNCartao, constraints);

        // LABEL CODIGO DE SEGURANÇA E CAMPO SEGURANÇA
        codCartao = new JLabel("Cod. segurança");
        constraints.gridx = 0;
        constraints.gridy = 2;
        gridBag.setConstraints(codCartao, constraints);

        jtcodCartao = new JTextField("", 4);
        constraints.gridx = 1;
        constraints.gridy = 2;
        gridBag.setConstraints(jtcodCartao, constraints);

        // LABEL VALIDADE DO CARTÃO E CAMPO VALIDADE
        validade = new JLabel("Validade do Cartão");
        constraints.gridx = 0;
        constraints.gridy = 3;
        gridBag.setConstraints(validade, constraints);

        jtValidade = new JTextField("", 20);
        constraints.gridx = 1;
        constraints.gridy = 3;        
        gridBag.setConstraints(jtValidade, constraints);


        //PAINEL DE BOTOES NO FUNDO

        JPanel bottoPanel = new JPanel();
        cancelar = new JButton("Cancelar");
        comprar = new JButton("Comprar");
        bottoPanel.add(cancelar);
        bottoPanel.add(comprar);
        
        constraints.gridx = 1;
        constraints.gridy = 5;        
        gridBag.setConstraints(bottoPanel, constraints);


        add(pagamento);
        add(nCartao);
        add(jtNCartao);
        add(codCartao);
        add(jtcodCartao);
        add(validade);
        add(jtValidade);
        add(bottoPanel);
        setVisible(true);
    }

}
