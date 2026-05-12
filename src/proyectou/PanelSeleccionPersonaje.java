package proyectou;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PanelSeleccionPersonaje extends JPanel {
    private ProyectoU3 ventana;

    public PanelSeleccionPersonaje(ProyectoU3 ventana) {
        this.ventana = ventana;
        setLayout(new GridLayout(1, 3, 20, 20)); // ⭐ MODIFICACIÓN: tres columnas, espacio entre personajes

        // Personaje 1
        JPanel panel1 = crearPanelPersonaje("Samurai Poison", "/proyectou/personaje1.png", 0);
        add(panel1);

        // Personaje 2
        JPanel panel2 = crearPanelPersonaje("Furia Roja", "/proyectou/personaje2.png", 1);
        add(panel2);

        // Personaje 3
        JPanel panel3 = crearPanelPersonaje("Ninja Sombra", "/proyectou/personaje3.png", 2);
        add(panel3);
    }

    // ⭐ MODIFICACIÓN: método para crear cada bloque con imagen + botón
    private JPanel crearPanelPersonaje(String nombre, String rutaImagen, int indice) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Imagen del personaje
        ImageIcon iconoOriginal = new ImageIcon(getClass().getResource(rutaImagen));
        Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH); // ⭐ MODIFICACIÓN: tamaño uniforme
        JLabel etiquetaImagen = new JLabel(new ImageIcon(imagenEscalada), JLabel.CENTER);
        panel.add(etiquetaImagen, BorderLayout.CENTER);

        // Botón de selección
        JButton botonSeleccion = new JButton(nombre);
        botonSeleccion.setFont(new Font("Arial", Font.BOLD, 14)); // ⭐ MODIFICACIÓN: tipografía más bonita
        botonSeleccion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ventana.cambiarAJuego(indice); // ⭐ MODIFICACIÓN: llama al método con el personaje elegido
            }
        });
        panel.add(botonSeleccion, BorderLayout.SOUTH);

        return panel;
    }
}