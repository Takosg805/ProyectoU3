package proyectou;

import javax.swing.JFrame;

public class ProyectoU3 extends JFrame {
    public int personajeSeleccionado = 0; 
    Musica musicaMenu;
    Musica musicaFondo;
    public ProyectoU3() {
        
        // Configuraciones básicas de la ventana
        setTitle("Stroke Race - Menú Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false); // Evitamos que el usuario deforme la ventana y arruine el tamaño de 800x600
        musicaFondo = new Musica("/proyectou/Paper_dash.wav");
        musicaMenu = new Musica("/proyectou/Paper_dash.wav");
        // Al arrancar el programa, mostramos el menú primero
        mostrarMenu();
    }

    // Método para cargar y mostrar la pantalla del menú
    public void mostrarMenu() {
        PanelMenu menu = new PanelMenu(this);
        setContentPane(menu); // Quitamos lo que haya y ponemos el menú
        pack();               // Ajustamos la ventana al tamaño del panel
        setLocationRelativeTo(null); // Centramos en pantalla
        musicaFondo.detener();   // aseguramos que la música del juego se detenga
        musicaMenu.reproducirLoop(); // reproducimos música del menú
    }
    
    public void mostrarSeleccionPersonaje() {
        PanelSeleccionPersonaje panelSel = new PanelSeleccionPersonaje(this);
        setContentPane(panelSel);
        pack();
        setLocationRelativeTo(null);
    }
    
    // Método para arrancar el juego (Llamado desde el botón "Jugar" de tu PanelMenu)
    public void cambiarAJuego(int personajeSeleccionado) {
        PanelJuego panelJuego = new PanelJuego(this, personajeSeleccionado);
        
        setTitle("Stroke Race - Físicas y Gravedad"); // Cambiamos el título de la ventana
        setContentPane(panelJuego); // Quitamos el menú y ponemos el juego
        setContentPane(panelJuego);
        revalidate();   //  fuerza a Swing a recalcular el layout
        repaint();      // redibuja la ventana
        pack();         //  ajusta el tamaño de la ventana al contenido
        setLocationRelativeTo(null); // centra la ventana en pantalla
        reproducirMusicaJuego();
        
        // ¡MUY IMPORTANTE! 
        // Al hacer clic en "JUGAR", el enfoque (focus) se queda atascado en el clic del ratón[cite: 282]. 
        // Si no pedimos el "focus" de vuelta hacia el PanelJuego, el juego no escuchará tus teclas para saltar[cite: 283].
        panelJuego.requestFocusInWindow(); 
        
        // Arrancamos el Hilo (Game Loop) del panel de juego
        panelJuego.iniciarJuego();
        
        musicaMenu.detener();    // detenemos música del menú
        musicaFondo.reproducirLoop(); // reproducimos música del juego
    }
    
        public void reproducirMusicaJuego() {
        musicaFondo.reproducirLoop();
    }

    public void detenerMusica() {
        musicaFondo.detener();
    }

    public static void main(String[] args) {
        // Solo instanciamos la ventana principal, el constructor se encarga de mostrar el menú
        ProyectoU3 ventana = new ProyectoU3();
        ventana.setVisible(true);
    }
}