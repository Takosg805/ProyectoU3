package proyectou;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.ImageIcon;
import java.net.URL;


public class Mapa {
    public final int TAMAÑO_TILE = 50;
    
    // Ya no escribimos los números a mano, solo declaramos la matriz vacía
    public int[][] nivel;

    // 1. VARIABLE PARA TU TEXTURA
    private Image texturaTierra;

    public Mapa() {
        // Al iniciar el juego, mandamos a construir un mapa de 650 columnas (~3 minutos)
        nivel = generarNivelLargo(650); 
        
        // 2. CARGAMOS LA TEXTURA DE LAS ROCAS
        try {
            URL urlTextura = getClass().getResource("plataforma.png");
            if (urlTextura != null) {
                texturaTierra = new ImageIcon(urlTextura).getImage();
            } else {
                System.out.println("No se encontró textura_tierra.png en la carpeta proyectou.");
    }
        } catch (Exception e) {
            System.out.println("Error al cargar la textura: " + e.getMessage());
        }
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
                        
                        if (tipoBloque == 1) {
                            g2d.setColor(new Color(139, 69, 19)); // Tierra
                            g2d.fillRect(xPantalla, yPantalla, TAMAÑO_TILE, TAMAÑO_TILE);
                            }
                            
                            // Mantenemos el borde verde temporalmente (puedes borrarlo después)
                            g2d.setColor(Color.GREEN); 
                            g2d.drawRect(xPantalla, yPantalla, TAMAÑO_TILE, TAMAÑO_TILE);
                            
                        } else if (tipoBloque == 2) {
                            g2d.setColor(Color.RED); // Lava
                            g2d.fillRect(xPantalla, yPantalla, TAMAÑO_TILE, TAMAÑO_TILE);
                            g2d.setColor(Color.GREEN); 
                            g2d.drawRect(xPantalla, yPantalla, TAMAÑO_TILE, TAMAÑO_TILE);
                            
                        } else if (tipoBloque == 3) {
                            // Hitbox de la Meta
                            g2d.setColor(new Color(255, 255, 0, 50)); 
                            g2d.fillRect(xPantalla, yPantalla, TAMAÑO_TILE, TAMAÑO_TILE);
                            g2d.setColor(Color.YELLOW); 
                            g2d.fillRect(xPantalla + TAMAÑO_TILE - 5, yPantalla, 5, TAMAÑO_TILE);
                        }
                        else if (tipoBloque == 4) {
                            g2d.setColor(Color.BLUE); // zona opcional
                            g2d.fillRect(xPantalla, yPantalla, TAMAÑO_TILE, TAMAÑO_TILE);
                        } else if (tipoBloque == 5) {
                            g2d.setColor(Color.BLACK); // sin salida
                            g2d.fillRect(xPantalla, yPantalla, TAMAÑO_TILE, TAMAÑO_TILE);
                        }
                    }
                }
            }
        }
    }


