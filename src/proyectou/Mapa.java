package proyectou;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import javax.swing.ImageIcon;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class Mapa {
    public final int TAMAÑO_TILE = 50;
    
    private Image texturaSuelo;
    private Image texturaLava;
    private Image texturaMeta;
    private Image texturaOpcional;
    private Image texturaSinSalida;
    public static final int VACIO = 0;
    public static final int TIERRA = 1;
    public static final int LAVA = 2;
    public static final int META = 3;
    public static final int OPCIONAL = 4;
    public static final int SIN_SALIDA = 5;
    
    
    // Ya no escribimos los números a mano, solo declaramos la matriz vacía
    public int[][] nivel;

    // 1. VARIABLE PARA TU TEXTURA
    private Image texturaTierra;

    public Mapa() {
        nivel = generarNivelLargo(650); 
        texturaSuelo = new ImageIcon(getClass().getResource("/proyectou/suelo.png")).getImage();
        texturaLava = new ImageIcon(getClass().getResource("/proyectou/lava.png")).getImage();
        texturaMeta = new ImageIcon(getClass().getResource("/proyectou/obs2.png")).getImage();
        texturaOpcional = new ImageIcon(getClass().getResource("/proyectou/plataforma1.png")).getImage();
        texturaSinSalida = new ImageIcon(getClass().getResource("/proyectou/obs1.png")).getImage();
//        // 2. CARGAMOS LA TEXTURA DE LAS ROCAS
//        try {
//            URL urlTextura = getClass().getResource("plataforma.png");
//            if (urlTextura != null) {
//                texturaTierra = new ImageIcon(urlTextura).getImage();
//            } else {
//                System.out.println("No se encontró textura_tierra.png en la carpeta proyectou.");
//    }
//        } catch (Exception e) {
//            System.out.println("Error al cargar la textura: " + e.getMessage());
//        }
    }

    // MÉTODO GENERADOR DE NIVELES
    // MÉTODO GENERADOR DE NIVELES (Ajustado para 12 filas)
    private int[][] generarNivelLargo(int totalColumnas) {
        
        // ¡AQUÍ ESTABA EL ERROR! Cambiamos a 12 filas para que llene la ventana de 600px
        int[][] nuevoMapa = new int[12][totalColumnas];

        // Recorremos columna por columna armando el nivel
        for (int col = 0; col < totalColumnas; col++) {
            
            // 1. ZONA INICIAL SEGURA
            if (col < 15) {
                nuevoMapa[11][col] = 1; // El piso ahora está en la fila 11
            } 
            
            // 2. ZONA DE META
            else if (col >= totalColumnas - 10) {
                nuevoMapa[11][col] = 1; // Piso para la meta
                
                // Ponemos la pared de la meta
                if (col == totalColumnas - 2) {
                    nuevoMapa[8][col] = 3;
                    nuevoMapa[9][col] = 3;
                    nuevoMapa[10][col] = 3;
                    nuevoMapa[11][col] = 3; 
                }
            } 
            
            // 3. GENERACIÓN DEL RECORRIDO
            else {
                // Por defecto, ponemos suelo en la fila 11
                nuevoMapa[11][col] = 1;

                // Obstáculos
                if (col % 12 == 0) {
                    nuevoMapa[11][col] = 2; // Lava en el piso
                } 
                else if (col % 19 == 0) {
                    nuevoMapa[11][col] = 0;   // Aire (hueco 1)
                    nuevoMapa[11][col+1] = 0; // Aire (hueco 2)
                    col++; // Saltamos un ciclo para no sobrescribir el hueco
                }
                else if (col % 15 == 0) {
                    // Plataformas flotantes (las bajamos a la fila 8 para que las alcances)
                    nuevoMapa[8][col] = 1;
                    nuevoMapa[8][col+1] = 1;
                    nuevoMapa[8][col+2] = 1;
                }
            }
        }
        
        return nuevoMapa;
    }

    public void dibujar(Graphics2D g2d, int camaraX) {
        for (int fila = 0; fila < nivel.length; fila++) {
            for (int col = 0; col < nivel[fila].length; col++) {
                
                int tipoBloque = nivel[fila][col];
                
                if (tipoBloque != 0) {
                    int xPantalla = (col * TAMAÑO_TILE) - camaraX;
                    int yPantalla = fila * TAMAÑO_TILE;

                    if (xPantalla + TAMAÑO_TILE > 0 && xPantalla < 800) {
                        
                        if (tipoBloque == TIERRA) {
    g2d.drawImage(texturaSuelo, xPantalla, yPantalla, TAMAÑO_TILE, TAMAÑO_TILE, null);
} else if (tipoBloque == LAVA) {
    g2d.drawImage(texturaLava, xPantalla, yPantalla, TAMAÑO_TILE, TAMAÑO_TILE, null);
} else if (tipoBloque == META) {
    g2d.drawImage(texturaMeta, xPantalla, yPantalla, TAMAÑO_TILE, TAMAÑO_TILE, null);
} else if (tipoBloque == OPCIONAL) {
    g2d.drawImage(texturaOpcional, xPantalla, yPantalla, TAMAÑO_TILE, TAMAÑO_TILE, null);
} else if (tipoBloque == SIN_SALIDA) {
    g2d.drawImage(texturaSinSalida, xPantalla, yPantalla, TAMAÑO_TILE, TAMAÑO_TILE, null);
}
                    }
                }
            }
        }
    }
}



