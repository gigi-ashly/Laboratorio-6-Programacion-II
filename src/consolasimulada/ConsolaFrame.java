package consolasimulada;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.BorderFactory;

public class ConsolaFrame extends JFrame {

    private final JTextArea areaSalida;
    private final JTextField campoEntrada;
    private final JLabel etiquetaRuta;
    private final Consola consola;

    public ConsolaFrame() {
        consola = new Consola();
        areaSalida = new JTextArea();
        campoEntrada = new JTextField();
        etiquetaRuta = new JLabel("", SwingConstants.LEFT);

        setTitle("Consola de comandos");
        setSize(850, 550);
        setMinimumSize(new java.awt.Dimension(700, 450));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        Color fondo = new Color(18, 18, 18);
        Color texto = new Color(220, 230, 220);
        Font fuente = new Font("Consolas", Font.PLAIN, 14);

        areaSalida.setEditable(false);
        areaSalida.setBackground(fondo);
        areaSalida.setForeground(texto);
        areaSalida.setCaretColor(texto);
        areaSalida.setFont(fuente);
        areaSalida.setLineWrap(true);
        areaSalida.setWrapStyleWord(false);

        campoEntrada.setBackground(fondo);
        campoEntrada.setForeground(texto);
        campoEntrada.setCaretColor(texto);
        campoEntrada.setFont(fuente);
        campoEntrada.setBorder(BorderFactory.createEmptyBorder(7, 8, 7, 8));

        etiquetaRuta.setForeground(new Color(120, 220, 120));
        etiquetaRuta.setBackground(fondo);
        etiquetaRuta.setOpaque(true);
        etiquetaRuta.setFont(fuente);
        etiquetaRuta.setBorder(BorderFactory.createEmptyBorder(7, 8, 7, 8));

        JPanel panelEntrada = new JPanel(new BorderLayout());
        panelEntrada.setBackground(fondo);
        panelEntrada.add(etiquetaRuta, BorderLayout.WEST);
        panelEntrada.add(campoEntrada, BorderLayout.CENTER);

        add(new JScrollPane(areaSalida), BorderLayout.CENTER);
        add(panelEntrada, BorderLayout.SOUTH);

        campoEntrada.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String linea = campoEntrada.getText();
                campoEntrada.setText("");
                procesarLinea(linea);
                actualizarPrompt();
                campoEntrada.requestFocusInWindow();
            }
        });

        areaSalida.append("Consola simulada iniciada.\n");
        areaSalida.append("Escriba Help para ver los comandos disponibles.\n\n");
        actualizarPrompt();
        campoEntrada.requestFocusInWindow();
    }

    private void procesarLinea(String linea) {
        areaSalida.append(etiquetaRuta.getText() + linea + "\n");
        String resultado = consola.ejecutar(linea);
        if (resultado != null && !resultado.isEmpty()) {
            areaSalida.append(resultado);
            if (!resultado.endsWith("\n")) {
                areaSalida.append("\n");
            }
        }
        if (consola.debeCerrar()) {
            dispose();
        }
        areaSalida.setCaretPosition(areaSalida.getDocument().getLength());
    }

    private void actualizarPrompt() {
        etiquetaRuta.setText(consola.getPrompt());
    }
}
