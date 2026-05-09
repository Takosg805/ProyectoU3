package proyectou;

import javax.swing.JPanel;
import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URL;

public class PanelJuego extends JPanel implements Runnable {

    private Thread hiloJuego;
    private boolean corriendo = false;

    private Jugador jugador;
    private Mapa mapa;
    private int camaraX = 0;
    
    // Variable para guardar el fondo
    private Image imagenFondo; 

    public PanelJuego() {
        // 1. Forzamos el tamaño del panel para que empate con la matriz
        setPreferredSize(new Dimension(800, 600)); 
        setFocusable(true);
        
        jugador = new Jugador();
        mapa = new Mapa();

        // 2. Cargamos la imagen de fondo de forma segura
        try {
            URL urlFondo = getClass().getResource("fondo.png");
            if (urlFondo != null) {
                imagenFondo = new ImageIcon(urlFondo).getImage();
            } else {
                System.out.println("No se encontró fondo.png en la carpeta proyectou.");
            }
        } catch (Exception e) {
            System.out.println("Error al cargar la imagen: " + e.getMessage());
        }

        // 3. Controles del jugador (Como corre solo, solo necesita saltar)
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_SPACE) {
                    jugador.saltando = true;
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_SPACE) {
                    jugador.saltando = false;
                }
            }
        });
    }

    // Método para arrancar el Game Loop
    public void iniciarJuego() {
        corriendo = true;
        hiloJuego = new Thread(this);
        hiloJuego.start();
    }

    // El Game Loop principal
    @Override
    public void run() {
        long tiempoEspera = 1000 / 60; // 60 FPS

        while (corriendo) {
            // Si el jugador toca la meta, detenemos el mundo
            if (jugador.haGanado) {
                corriendo = false;
            } else {
                // Actualizamos físicas y colisiones
                jugador.actualizar(mapa);
                
                // La cámara sigue al jugador manteniéndolo a 100px del borde izquierdo
                camaraX = jugador.x - 100;
                
                // Evitamos que la cámara muestre áreas negativas al inicio
                if (camaraX < 0) {
                    camaraX = 0;
                }
            }

            // Mandamos a redibujar la pantalla
            repaint();

            // Pausa para mantener los FPS estables
            try { 
                Thread.sleep(tiempoEspera); 
            } catch (Exception e) { 
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // 1. DIBUJAMOS EL FONDO PRIMERO (Capa más lejana)
        if (imagenFondo != null) {
            g2d.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
        } else {
            // Si no hay imagen, pintamos un cielo celeste de respaldo
            g2d.setColor(Color.CYAN); 
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        // 2. DIBUJAMOS EL MAPA (Se mueve restándole la cámara)
        mapa.dibujar(g2d, camaraX);

        // 3. DIBUJAMOS AL JUGADOR (Se mueve restándole la cámara)
        jugador.dibujar(g2d, camaraX);

        // 4. DIBUJAMOS EL PANEL DE VICTORIA (Capa más cercana, encima de todo)
        if (jugador.haGanado) {
            // Filtro oscuro semi-transparente
            g2d.setColor(new Color(0, 0, 0, 150)); 
            g2d.fillRect(0, 0, getWidth(), getHeight());

            // Texto de victoria centrado
            g2d.setColor(Color.YELLOW);
            g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 40)); 
            g2d.drawString("¡NIVEL COMPLETADO!", 180, 300);
            
            // Subtítulo
            g2d.setColor(Color.WHITE);
            g2d.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 20)); 
            g2d.drawString("¡Buen trabajo en Stroke Race!", 260, 350);
        }
    }
}