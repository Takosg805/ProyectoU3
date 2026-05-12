package proyectou;
import javax.swing.*;
import java.awt.*;
public class PanelSeleccionPersonaje extends JPanel {
    private ProyectoU3 ventanaPrincipal;
    private int personajeSeleccionado;
    public PanelSeleccionPersonaje(ProyectoU3 ventana) {
        this.ventanaPrincipal = ventana;
        setPreferredSize(new Dimension(800, 600));
        setLayout(null);

        JButton botonPersonaje1 = new JButton("Jugador Azul");
        botonPersonaje1.setBounds(100, 200, 200, 50);
        botonPersonaje1.addActionListener(e -> ventanaPrincipal.cambiarAJuego(0));

        JButton botonPersonaje2= new JButton("Jugador Naranja");
        botonPersonaje2.setBounds(300, 200, 200, 50);
         botonPersonaje2.addActionListener(e -> ventanaPrincipal.cambiarAJuego(1));

        JButton botonPersonaje3 = new JButton("Jugador Magenta");
        botonPersonaje3.setBounds(500, 200, 200, 50);
        botonPersonaje3.addActionListener(e -> ventanaPrincipal.cambiarAJuego(2));

        add(botonPersonaje1);
        add(botonPersonaje2);
        add(botonPersonaje3);
    }
}