package proyectou;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.ImageIcon;
import java.net.URL;

public class Mapa {
    public final int TAMAÑO_TILE = 50;
    public int[][] nivel;
    
    // 1. VARIABLE PARA TU TEXTURA
    private Image texturaTierra;

    public Mapa() {
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

    private int[][] generarNivelLargo(int totalColumnas) {
        int[][] nuevoMapa = new int[12][totalColumnas];

        for (int col = 0; col < totalColumnas; col++) {
            if (col < 15) {
                nuevoMapa[11][col] = 1; 
            } 
            else if (col >= totalColumnas - 10) {
                nuevoMapa[11][col] = 1; 
                if (col == totalColumnas - 2) {
                    nuevoMapa[8][col] = 3;
                    nuevoMapa[9][col] = 3;
                    nuevoMapa[10][col] = 3;
                    nuevoMapa[11][col] = 3; 
                }
            } 
            else {
                nuevoMapa[11][col] = 1;

                if (col % 12 == 0) {
                    nuevoMapa[11][col] = 2; 
                } 
                else if (col % 19 == 0) {
                    nuevoMapa[11][col] = 0;   
                    nuevoMapa[11][col+1] = 0; 
                    col++; 
                }
                else if (col % 15 == 0) {
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
                            // 3. DIBUJAMOS LA TEXTURA EN LUGAR DEL COLOR CAFÉ
                            if (texturaTierra != null) {
                                // drawImage estira la imagen al tamaño exacto de tu bloque (50x50)
                                g2d.drawImage(texturaTierra, xPantalla, yPantalla, TAMAÑO_TILE, TAMAÑO_TILE, null);
                            } else {
                                // Respaldo por si hay algún error con la imagen
                                g2d.setColor(new Color(139, 69, 19)); 
                                g2d.fillRect(xPantalla, yPantalla, TAMAÑO_TILE, TAMAÑO_TILE);
                            }
                            
                            // Mantenemos el borde verde temporalmente (puedes borrarlo después)
                            g2d.setColor(Color.GREEN); 
                            g2d.drawRect(xPantalla, yPantalla, TAMAÑO_TILE, TAMAÑO_TILE);
                            
                        } else if (tipoBloque == 2) {
                            g2d.setColor(Color.RED); 
                            g2d.fillRect(xPantalla, yPantalla, TAMAÑO_TILE, TAMAÑO_TILE);
                            g2d.setColor(Color.GREEN); 
                            g2d.drawRect(xPantalla, yPantalla, TAMAÑO_TILE, TAMAÑO_TILE);
                            
                        } else if (tipoBloque == 3) {
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