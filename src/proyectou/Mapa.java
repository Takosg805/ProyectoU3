package proyectou;

import java.awt.Color;
import java.awt.Graphics2D;

public class Mapa {
    public final int TAMAÑO_TILE = 50;
    
    // Ya no escribimos los números a mano, solo declaramos la matriz vacía
    public int[][] nivel;

    public Mapa() {
        // Al iniciar el juego, mandamos a construir un mapa de 650 columnas (~3 minutos)
        nivel = generarNivelLargo(650); 
    }

    // MÉTODO GENERADOR DE NIVELES
    private int[][] generarNivelLargo(int totalColumnas) {
        // Creamos una matriz de 10 filas por "totalColumnas"
        int[][] nuevoMapa = new int[10][totalColumnas];

        // Recorremos columna por columna armando el nivel
        for (int col = 0; col < totalColumnas; col++) {
            
            // 1. ZONA INICIAL SEGURA (Los primeros 15 bloques para reaccionar)
            if (col < 15) {
                nuevoMapa[9][col] = 1; // Puro suelo seguro
            } 
            
            // 2. ZONA DE META (Los últimos 10 bloques)
            else if (col >= totalColumnas - 10) {
                nuevoMapa[9][col] = 1; // Suelo para la meta
                
                // Ponemos la pared de la meta exactamente en la penúltima columna
                if (col == totalColumnas - 2) {
                    nuevoMapa[6][col] = 3;
                    nuevoMapa[7][col] = 3;
                    nuevoMapa[8][col] = 3;
                    nuevoMapa[9][col] = 3; 
                }
            } 
            
            // 3. GENERACIÓN DEL RECORRIDO (Obstáculos y plataformas)
            else {
                // Por defecto, ponemos suelo en la fila 9
                nuevoMapa[9][col] = 1;

                // Para evitar que sea imposible, usamos matemáticas (módulo %) 
                // para espaciar los obstáculos y que no salgan todos juntos.

                // Cada 12 bloques, un bloque de lava/pico
                if (col % 12 == 0) {
                    nuevoMapa[9][col] = 2; 
                } 
                // Cada 19 bloques, un hueco en el suelo de 2 bloques de ancho
                else if (col % 19 == 0) {
                    nuevoMapa[9][col] = 0;   // Aire
                    nuevoMapa[9][col+1] = 0; // Aire en el siguiente también
                    col++; // Saltamos un ciclo para no sobrescribir el hueco
                }
                // Cada 15 bloques, una plataforma flotante para saltar
                else if (col % 15 == 0) {
                    nuevoMapa[6][col] = 1;
                    nuevoMapa[6][col+1] = 1;
                    nuevoMapa[6][col+2] = 1;
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
                    }
                }
            }
        }
    }
}