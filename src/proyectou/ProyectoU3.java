package proyectou;

import javax.swing.JFrame;

public class ProyectoU3 extends JFrame {
    public int personajeSeleccionado = 0; // 0 = Azul, 1 = Naranja, 2 = Magenta
    
    
    public ProyectoU3() {
        
        // Configuraciones básicas de la ventana
        setTitle("Stroke Race - Menú Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false); // Evitamos que el usuario deforme la ventana y arruine el tamaño de 800x600
        
        // Al arrancar el programa, mostramos el menú primero
        mostrarMenu();
    }

    // Método para cargar y mostrar la pantalla del menú
    public void mostrarMenu() {
        PanelMenu menu = new PanelMenu(this);
        setContentPane(menu); // Quitamos lo que haya y ponemos el menú
        pack();               // Ajustamos la ventana al tamaño del panel
        setLocationRelativeTo(null); // Centramos en pantalla
    }
    
    public void mostrarSeleccionPersonaje() {
        PanelSeleccionPersonaje panelSel = new PanelSeleccionPersonaje(this);
        setContentPane(panelSel);
        pack();
        setLocationRelativeTo(null);
    }
    
    // Método para arrancar el juego (Llamado desde el botón "Jugar" de tu PanelMenu)
    public void cambiarAJuego() {
        PanelJuego panelJuego = new PanelJuego(this, personajeSeleccionado);
        
        setTitle("Stroke Race - Físicas y Gravedad"); // Cambiamos el título de la ventana
        setContentPane(panelJuego); // Quitamos el menú y ponemos el juego
        pack(); // Reajustamos por si acaso
        
        // ¡MUY IMPORTANTE! 
        // Al hacer clic en "JUGAR", el enfoque (focus) se queda atascado en el clic del ratón[cite: 282]. 
        // Si no pedimos el "focus" de vuelta hacia el PanelJuego, el juego no escuchará tus teclas para saltar[cite: 283].
        panelJuego.requestFocusInWindow(); 
        
        // Arrancamos el Hilo (Game Loop) del panel de juego
        panelJuego.iniciarJuego(); 
    }

    public static void main(String[] args) {
        // Solo instanciamos la ventana principal, el constructor se encarga de mostrar el menú
        ProyectoU3 ventana = new ProyectoU3();
        ventana.setVisible(true);
    }
}