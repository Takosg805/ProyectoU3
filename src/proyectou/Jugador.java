package proyectou;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Jugador {

    public int x = 50; // Posición X en el "mundo"
    public int y = 50;  // Posición Y en el "mundo"
 
    //Tamaño del jugador
    public int ancho = 60; 
    public int alto = 60;

    // Físicas
    public double vy = 0;
    public double gravedad = 0.5;
    public double fuerzaSalto = -14;
    public int velocidadAvance = 4; // ¡Esta es la velocidad a la que avanza solo!

    public boolean saltando = false;
    public boolean enSuelo = false;

    public boolean haGanado = false;

    // 1. Las 4 Hitboxes (Calculadas en coordenadas del mundo)
    public Rectangle getHitArriba() {
        return new Rectangle(x + 5, y, ancho - 10, 5);
    }

    public Rectangle getHitAbajo() {
        return new Rectangle(x + 5, y + alto - 5, ancho - 10, 5);
    }

    public Rectangle getHitIzquierda() {
        return new Rectangle(x, y + 5, 5, alto - 10);
    }

    public Rectangle getHitDerecha() {
        return new Rectangle(x + ancho - 5, y + 5, 5, alto - 10);
    }

    // 2. Pasamos el Mapa completo para checar colisiones
    public void actualizar(Mapa mapa) {

        // --- EJE Y: SALTO Y GRAVEDAD ---
        if (saltando && enSuelo) {
            vy = fuerzaSalto;
            enSuelo = false;
        }

        vy += gravedad;
        y += (int) vy;
        enSuelo = false;

        // Comprobamos colisiones VERTICALES iterando sobre la matriz
        for (int fila = 0; fila < mapa.nivel.length; fila++) {
            for (int col = 0; col < mapa.nivel[fila].length; col++) {
                
                int tipoBloque = mapa.nivel[fila][col];
                
                // ¡AQUÍ ESTÁ LA SOLUCIÓN! 
                // Solo nos comportamos como sólidos con la tierra (1) y lava (2). 
                // La meta (3) será ignorada por la gravedad.
                if (tipoBloque == 1 || tipoBloque == 2) { 
                    Rectangle bloque = new Rectangle(col * mapa.TAMAÑO_TILE, fila * mapa.TAMAÑO_TILE, mapa.TAMAÑO_TILE, mapa.TAMAÑO_TILE);

                    // Choca con los pies (Cae sobre el bloque)
                    if (vy > 0 && getHitAbajo().intersects(bloque)) {
                        y = bloque.y - alto; 
                        vy = 0;
                        enSuelo = true;
                    }
                    // Choca con la cabeza (Salta debajo de un bloque)
                    else if (vy < 0 && getHitArriba().intersects(bloque)) {
                        y = bloque.y + bloque.height; 
                        vy = 0;
                    }
                }
            }
        }

        // --- EJE X: AVANCE AUTOMÁTICO ---
        x += velocidadAvance;

        // Comprobamos colisiones HORIZONTALES
        for (int fila = 0; fila < mapa.nivel.length; fila++) {
            for (int col = 0; col < mapa.nivel[fila].length; col++) {
                if (mapa.nivel[fila][col] != 0) {

                    // Hitbox normal para las paredes de tierra o lava
                    Rectangle bloqueNormal = new Rectangle(col * mapa.TAMAÑO_TILE, fila * mapa.TAMAÑO_TILE, mapa.TAMAÑO_TILE, mapa.TAMAÑO_TILE);

                    if (mapa.nivel[fila][col] == 3) {
                        // ¡AQUÍ ESTÁ EL CAMBIO!
                        // Creamos una hitbox de solo 5 píxeles de ancho y la empujamos al borde derecho del cuadrado
                        Rectangle hitboxMeta = new Rectangle(
                                (col * mapa.TAMAÑO_TILE) + mapa.TAMAÑO_TILE - 5, // X desplazada a la derecha
                                fila * mapa.TAMAÑO_TILE, // Y normal
                                5, // Ancho de 5 píxeles
                                mapa.TAMAÑO_TILE // Alto normal
                        );

                        // Verificamos si choca contra esta nueva línea delgada
                        if (getHitDerecha().intersects(hitboxMeta) || getHitAbajo().intersects(hitboxMeta) || getHitArriba().intersects(hitboxMeta)) {
                            haGanado = true;
                        }
                    } // Si es un bloque normal (1 o 2) y choca por la derecha
                    else if (velocidadAvance > 0 && getHitDerecha().intersects(bloqueNormal)) {
                        x = bloqueNormal.x - ancho; // Se atora contra la pared
                    }
                }
            }
        }
    }

    public void dibujar(Graphics2D g2d, int camaraX) {
        int xPantalla = x - camaraX; // Convertimos la coordenada del mundo a la pantalla

        // Personaje
        g2d.setColor(new Color(100, 150, 255));
        g2d.fillRect(xPantalla, y, ancho, alto);

        // Hitboxes (verdes para debug)
        g2d.setColor(Color.GREEN);
        g2d.fillRect(xPantalla + 5, y, ancho - 10, 5); // Arriba
        g2d.fillRect(xPantalla + 5, y + alto - 5, ancho - 10, 5); // Abajo
        g2d.fillRect(xPantalla, y + 5, 5, alto - 10); // Izq
        g2d.fillRect(xPantalla + ancho - 5, y + 5, 5, alto - 10); // Der
    }
}
