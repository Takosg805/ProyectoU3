package proyectou;
import javax.swing.*;
import java.awt.*;
import java.net.URL;
public class PanelSeleccionPersonaje extends JPanel {
    private ProyectoU3 ventana;

    public PanelSeleccionPersonaje(ProyectoU3 ventana) {
        this.ventana = ventana;
        setPreferredSize(new Dimension(800, 600));
        setLayout(null);

        JButton btnAzul = new JButton("Jugador Azul");
        btnAzul.setBounds(100, 200, 200, 50);
        btnAzul.addActionListener(e -> {
            ventana.personajeSeleccionado = 0;
            ventana.cambiarAJuego();
        });

        JButton btnNaranja = new JButton("Jugador Naranja");
        btnNaranja.setBounds(300, 200, 200, 50);
        btnNaranja.addActionListener(e -> {
            ventana.personajeSeleccionado = 1;
            ventana.cambiarAJuego();
        });

        JButton btnMagenta = new JButton("Jugador Magenta");
        btnMagenta.setBounds(500, 200, 200, 50);
        btnMagenta.addActionListener(e -> {
            ventana.personajeSeleccionado = 2;
            ventana.cambiarAJuego();
        });

        add(btnAzul);
        add(btnNaranja);
        add(btnMagenta);
    }
}