package proyectou;

import javax.swing.JFrame;

public class ProyectoU3 {

    public static void main(String[] args) {
        JFrame ventana = new JFrame("Stroke Race - Físicas y Gravedad");
        PanelJuego panel = new PanelJuego();

        ventana.add(panel);
        ventana.pack();
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);

        // ¡Iniciamos el hilo del juego después de que la ventana es visible!
        panel.iniciarJuego();
    }
}
