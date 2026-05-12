package proyectou;

import javax.swing.JPanel;
import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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
    private NPC npc1, npc2;
    private Mapa mapa;
    private int camaraX = 0;
    
    private Image imagenFondo;
    
    private int personajeSeleccionado;
    
    private boolean enPausa = false;
    private ProyectoU3 ventanaPrincipal; // referencia a la ventana
    
    

    public PanelJuego(ProyectoU3 ventanaPrincipal,int personajeSeleccionado) {
        setPreferredSize(new Dimension(800, 600)); 
        setFocusable(true);
        this.ventanaPrincipal = ventanaPrincipal;
        this.personajeSeleccionado = personajeSeleccionado;
        inicializarEntidades();
        
    
       
        mapa = new Mapa();
        
        
        try {
            URL urlFondo = getClass().getResource("fondo.png");
            if (urlFondo != null) {
                imagenFondo = new ImageIcon(urlFondo).getImage();
            }
        } catch (Exception e) {}

        addKeyListener(new KeyAdapter() {
            
            @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    ventanaPrincipal.musicaFondo.detener();
                    enPausa = !enPausa; // alterna pausa
                    if (enPausa) {
                    ventanaPrincipal.musicaFondo.detener(); // detener música al pausar
                    } else {
                    ventanaPrincipal.musicaFondo.reproducirLoop(); // reanudar música
                    synchronized (PanelJuego.this) {
                    PanelJuego.this.notify(); // despierta el hilo
                        }
                    }
                    repaint();
                }
                if (enPausa && e.getKeyCode() == KeyEvent.VK_M) {
                ventanaPrincipal.musicaFondo.detener();
                ventanaPrincipal.mostrarMenu();
                }
                if (!enPausa) {
                if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_SPACE) {
                    jugador.saltando = true;
                }
                // Si perdiste todas las vidas (Game Over) o ganaste, la tecla R reinicia el nivel
                if ((jugador.estaMuerto || jugador.haGanado) && e.getKeyCode() == KeyEvent.VK_R) {
                    reiniciarNivelCompleto();
                }
                
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
    
    private void inicializarEntidades(){
    if (personajeSeleccionado == 0) {
        jugador = new Jugador("/proyectou/personaje1.png");
        npc1 = new NPC(50, 50, 5.000000000000111, 0.3, ("/proyectou/personaje2.png"));
        npc2 = new NPC(50, 50, 4.999999999999999, 0, ("/proyectou/personaje3.png"));
    } else if (personajeSeleccionado == 1) {
        jugador = new Jugador("/proyectou/personaje3.png");
        npc1 = new NPC(50, 50, 5.000000000000111, 0.3, ("/proyectou/personaje1.png"));
        npc2 = new NPC(50, 50, 4.999999999999999, 0, ("/proyectou/personaje2.png"));
    } else if (personajeSeleccionado == 2){
        jugador = new Jugador("/proyectou/personaje2.png");
        npc1 = new NPC(50, 50, 5.000000000000111, 0.3, ("/proyectou/personaje1.png"));
        npc2 = new NPC(50, 50, 4.999999999999999, 0, ("/proyectou/personaje3.png"));
    }
    
    
    }

    // Método para cuando mueres definitivamente o ganas (Resetea todo)
    private void reiniciarNivelCompleto() {
        
        mapa = new Mapa();
        inicializarEntidades(); 
        camaraX = 0;
        iniciarJuego(); 
    }

    public void iniciarJuego() {
        corriendo = true;
        hiloJuego = new Thread(this);
        hiloJuego.start();
    }

    @Override
    public void run() {
        long tiempoEspera = 1000 / 60; 

            while (corriendo) {
                // Solo detenemos el hilo si el jugador pierde todas sus vidas o llega a la meta
                synchronized (this) {
                while (enPausa) {
                    try {
                        wait(); // el hilo se detiene aquí
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (jugador.haGanado || jugador.estaMuerto) {
                corriendo = false; 
            } else {
                // Actualizamos a todos. Los NPCs NO se resetean si el jugador solo pierde una vida.
                jugador.actualizar(mapa);
                npc1.actualizar(mapa);
                npc2.actualizar(mapa);
                
                // La cámara sigue al jugador. Si el Magenta va a 4 y tú a 5, 
                // se quedará un poco atrás pero seguirá dentro de los 800px de la pantalla.
                camaraX = jugador.x - 100;
                if (camaraX < 0) camaraX = 0;
                
            }

            repaint();
            try { Thread.sleep(tiempoEspera); } catch (Exception e) {}
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Dibujar Fondo
        if (imagenFondo != null) {
            g2d.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
        } else {
            g2d.setColor(Color.CYAN); 
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        mapa.dibujar(g2d, camaraX);
        jugador.dibujar(g2d,camaraX);
        npc1.dibujar(g2d, camaraX);
        npc2.dibujar(g2d, camaraX);
        
        if (enPausa) {
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 40));
        g2d.drawString("PAUSA", 330, 200);

        g2d.setFont(new Font("Arial", Font.PLAIN, 24));
        g2d.drawString("Presiona ESC para reanudar", 250, 300);
        g2d.drawString("Presiona M para volver al menú", 240, 350);
        }
    
        // --- DIBUJAR INTERFAZ DE PUNTOS ---
        g2d.setColor(Color.GRAY);
        g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 20));
        g2d.drawString("Jugador: " + jugador.puntuacion, 20, 70);
        g2d.drawString("NPC1: " + npc1.puntuacion, 20, 100);
        g2d.drawString("NPC2: " + npc2.puntuacion, 20, 130);

        // --- DIBUJAR INTERFAZ DE VIDAS ---
        g2d.setColor(Color.RED);
        g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24)); 
        String textoVidas = "Vidas: ";
        for(int i = 0; i < jugador.vidas; i++) {
            textoVidas += "❤ ";
        }
        g2d.drawString(textoVidas, 20, 40);

        // Panel de Victoria
        if (jugador.haGanado) {
            g2d.setColor(new Color(0, 0, 0, 150)); 
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.setColor(Color.YELLOW);
            g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 40)); 
            g2d.drawString("¡NIVEL COMPLETADO!", 180, 300);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 20)); 
            g2d.drawString("Presiona 'R' para jugar de nuevo", 240, 350);
            ventanaPrincipal.musicaFondo.detener();
            
            // PUNTUACIÓN
            g2d.setColor(Color.WHITE);
            g2d.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 20));
            g2d.drawString("Puntuación final Jugador: " + jugador.puntuacion, 240, 380);
            g2d.drawString("Puntuación final NPC1: " + npc1.puntuacion, 240, 410);
            g2d.drawString("Puntuación final NPC2: " + npc2.puntuacion, 240, 440);

            // Determinar ganador
            int maxPuntos = Math.max(jugador.puntuacion, Math.max(npc1.puntuacion, npc2.puntuacion));
            String ganador = "";
            if (jugador.puntuacion == maxPuntos) ganador = "Jugador";
            else if (npc1.puntuacion == maxPuntos) ganador = "NPC1";
            else ganador = "NPC2";

            g2d.setColor(Color.YELLOW);
            g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));
            g2d.drawString("Ganador: " + ganador, 240, 480);
        }

        // Panel de Game Over (Cuando vidas llega a 0)
        if (jugador.estaMuerto) {
            g2d.setColor(new Color(255, 0, 0, 150)); 
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.setColor(Color.WHITE);
            g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 60)); 
            g2d.drawString("¡GAME OVER!", 200, 280);
            g2d.setColor(Color.YELLOW);
            g2d.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 20)); 
            g2d.drawString("Presiona 'R' para volver a intentarlo", 240, 330);
             ventanaPrincipal.musicaFondo.detener();
            
            // PUNTUACIÓN
            g2d.setColor(Color.WHITE);
            g2d.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 20));
            g2d.drawString("Puntuación final Jugador: " + jugador.puntuacion, 240, 380);
            g2d.drawString("Puntuación final NPC1: " + npc1.puntuacion, 240, 410);
            g2d.drawString("Puntuación final NPC2: " + npc2.puntuacion, 240, 440);

            // Determinar ganador
            int maxPuntos = Math.max(jugador.puntuacion, Math.max(npc1.puntuacion, npc2.puntuacion));
            String ganador = "";
            if (jugador.puntuacion == maxPuntos) ganador = "Jugador";
            else if (npc1.puntuacion == maxPuntos) ganador = "NPC1";
            else ganador = "NPC2";

            g2d.setColor(Color.YELLOW);
            g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));
            g2d.drawString("Ganador: " + ganador, 240, 480);
            
        }
    }
}