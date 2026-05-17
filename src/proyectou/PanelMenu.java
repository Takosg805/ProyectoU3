package proyectou;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

public class PanelMenu extends JPanel {

    private Image imagenFondo;
    private ProyectoU3 ventanaPrincipal; // Guardamos la referencia a la ventana principal

    public PanelMenu(ProyectoU3 ventana) {
        this.ventanaPrincipal = ventana;
        
        // El menú tendrá el mismo tamaño que el juego
        setPreferredSize(new Dimension(800, 600)); 
        
        // setLayout(null) nos permite poner los botones exactamente en las coordenadas que queramos
        setLayout(null); 

        // 1. CARGAMOS EL MISMO FONDO DEL JUEGO
        try {
            URL urlFondo = getClass().getResource("fondo.png");
            if (urlFondo != null) {
                imagenFondo = new ImageIcon(urlFondo).getImage();
            }
        } catch (Exception e) {
            System.out.println("Error al cargar fondo del menú");
        }

        // 2. CREAMOS LOS BOTONES
        JButton btnJugar = new JButton("JUGAR");
        btnJugar.setBounds(300, 250, 200, 50); // x, y, ancho, alto
        btnJugar.setFont(new Font("Arial", Font.BOLD, 24));
        // Al presionar jugar, le decimos a la ventana principal que cambie la pantalla
        btnJugar.addActionListener(e -> ventanaPrincipal.mostrarSeleccionPersonaje());

        JButton btnOpciones = new JButton("CREDITOS");
        btnOpciones.setBounds(300, 330, 200, 50);
        btnOpciones.setFont(new Font("Arial", Font.BOLD, 24));
        btnOpciones.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(ventanaPrincipal,String.format("""
                                                                             Integrantes del equipo:
                                                                             
                                                                             MARCOS GARCIA LOPEZ
                                                                             PEDRO TOMAS GUTIÉRREZ GONZÁLEZ
                                                                             RICARDO OTTMAR GUTIÉRREZ GUZMÁN
                                                                             DIEGO PALACIO FLORES
                                                                             FERNANDO RIVERA PÉREZ
                                                                             MARIA GUADALUPE ZUÑIGA ALCANTAR
                                                                             """, null),"COLABORADORES", 1, null);
            }
        });
        
        JButton btnSalir = new JButton("SALIR");
        btnSalir.setBounds(300, 410, 200, 50);
        btnSalir.setFont(new Font("Arial", Font.BOLD, 24));
        btnSalir.addActionListener(e -> System.exit(0)); // Cierra el programa por completo

        // 3. AGREGAMOS LOS BOTONES AL PANEL
        add(btnJugar);
        add(btnOpciones);
        add(btnSalir);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Dibujamos el fondo
        if (imagenFondo != null) {
            g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        // Dibujamos el título del juego en la parte superior
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 70));
        
        // Un pequeño truco para hacerle sombra al texto
        g.setColor(Color.BLACK);
        g.drawString("STROKE RACE", 153, 153); // Sombra
        g.setColor(Color.WHITE);
        g.drawString("STROKE RACE", 150, 150); // Texto principal
    }
}