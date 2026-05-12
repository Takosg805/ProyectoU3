package proyectou;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import javax.swing.ImageIcon;

public class Jugador {

    public int x = 50; 
    public int y = 50;  
    
    public int puntuacion = 0;
    
    public int vidas = 3; 
    public boolean estaMuerto = false;
    
    // NUEVO: Tiempo de invencibilidad para no perder todas las vidas de golpe
    public int tiempoInvencible = 0; 
 
    public int ancho = 60; 
    public int alto = 60;

    public double vy = 0;
    public double gravedad = 0.5;
    public double fuerzaSalto = -14;
    public int velocidadAvance = 5; 

    public boolean saltando = false;
    public boolean enSuelo = false;
    public boolean haGanado = false;
    private Image sprite;
    public Color colorJugador;
    
    private boolean mostrarHitbox = false;
    
    // Constructor que recibe el color
    public Jugador(String rutaSprite) {
        ancho = 60;
        alto = 60;
        sprite = new ImageIcon(getClass().getResource(rutaSprite)).getImage(); 
    }

    public Rectangle getHitArriba() { return new Rectangle(x + 5, y, ancho - 10, 5); }
    public Rectangle getHitAbajo() { return new Rectangle(x + 5, y + alto - 5, ancho - 10, 5); }
    public Rectangle getHitIzquierda() { return new Rectangle(x, y + 5, 5, alto - 10); }
    public Rectangle getHitDerecha() { return new Rectangle(x + ancho - 5, y + 5, 5, alto - 10); }

    // NUEVA LÓGICA DE DAÑO: Invencibilidad y retroceso ligero
    public void recibirDano() {
        if (tiempoInvencible > 0) return; // Si está parpadeando, ignoramos el daño
        
        vidas--; 
        if (vidas <= 0) {
            estaMuerto = true; 
        } else {
            tiempoInvencible = 90; // 1.5 segundos de invencibilidad
            
            // Conserva su progreso, solo lo retrocedemos 3 bloques (150px) y lo tiramos del cielo
            x = Math.max(50, x - 150); 
            y = 0; 
            vy = 0;
        }
    }

    public void actualizar(Mapa mapa) {
        if (haGanado || estaMuerto) return;

        // Reducimos el temporizador de invencibilidad cada frame
        if (tiempoInvencible > 0) {
            tiempoInvencible--;
        }

        if (y > 600) {
            recibirDano();
            return; 
        }

        if (saltando && enSuelo) {
            vy = fuerzaSalto;
            enSuelo = false;
        }

        vy += gravedad;
        y += (int) vy;
        enSuelo = false;

        for (int fila = 0; fila < mapa.nivel.length; fila++) {
            for (int col = 0; col < mapa.nivel[fila].length; col++) {
                int tipoBloque = mapa.nivel[fila][col];
                
                if (tipoBloque != 0) {
                    Rectangle bloque = new Rectangle(col * mapa.TAMAÑO_TILE, fila * mapa.TAMAÑO_TILE, mapa.TAMAÑO_TILE, mapa.TAMAÑO_TILE);

                    if (tipoBloque == 2) {
                        if (getHitAbajo().intersects(bloque) || getHitArriba().intersects(bloque)) {
                            recibirDano();
                            return;
                        }
                    } 
                    else if (tipoBloque == 1 || tipoBloque == 6) { 
                        if (vy > 0 && getHitAbajo().intersects(bloque)) {
                            y = bloque.y - alto; 
                            vy = 0;
                            enSuelo = true;
                        }
                        else if (vy < 0 && getHitArriba().intersects(bloque)) {
                            y = bloque.y + bloque.height; 
                            vy = 0;
                        }
                    }
                }
            }
        }
        int xAnterior = x;
        x += velocidadAvance;

        for (int fila = 0; fila < mapa.nivel.length; fila++) {
            for (int col = 0; col < mapa.nivel[fila].length; col++) {
                int tipoBloque = mapa.nivel[fila][col];
                
                if (tipoBloque != 0) {
                    Rectangle bloqueNormal = new Rectangle(col * mapa.TAMAÑO_TILE, fila * mapa.TAMAÑO_TILE, mapa.TAMAÑO_TILE, mapa.TAMAÑO_TILE);

                    if (tipoBloque == 3) {
                        Rectangle hitboxMeta = new Rectangle((col * mapa.TAMAÑO_TILE) + mapa.TAMAÑO_TILE - 5, fila * mapa.TAMAÑO_TILE, 5, mapa.TAMAÑO_TILE);
                        if (getHitDerecha().intersects(hitboxMeta) || getHitAbajo().intersects(hitboxMeta) || getHitArriba().intersects(hitboxMeta)) {
                            haGanado = true;
                        }
                    } 
                    else if (tipoBloque == 2 && velocidadAvance > 0 && getHitDerecha().intersects(bloqueNormal)) {
                        recibirDano();
                        return;
                    }
                    else if (tipoBloque == 1 && velocidadAvance > 0 && getHitDerecha().intersects(bloqueNormal)) {
                        x = bloqueNormal.x - ancho; 
                    }
                    else if (tipoBloque == 4 && getHitDerecha().intersects(bloqueNormal)) {
                        x -= 10; 
                    }
                    else if (tipoBloque == 5 && getHitDerecha().intersects(bloqueNormal)) {
                        x = bloqueNormal.x - ancho; 
                    }else if (tipoBloque == 6 && velocidadAvance > 0 && getHitDerecha().intersects(bloqueNormal))
                    {
                        recibirDano();
                        return;
                    }
                }
            }
        }
        puntuacion += (x-xAnterior);
    }
    public void dibujar(Graphics2D g2d, int camaraX) {
        // EFECTO DE PARPADEO: Si es invencible, intercala dibujos para que parezca fantasma
        if (tiempoInvencible > 0 && (tiempoInvencible / 10) % 2 == 0) {
            return; 
        }

        int xPantalla = x - camaraX; 

        
       

        g2d.drawImage(sprite, x - camaraX, y, ancho, alto, null);
        
        if (mostrarHitbox) {
        g2d.fillRect(xPantalla + 5, y, ancho - 10, 5); 
        g2d.fillRect(xPantalla + 5, y + alto - 5, ancho - 10, 5); 
        g2d.fillRect(xPantalla, y + 5, 5, alto - 10); 
        g2d.fillRect(xPantalla + ancho - 5, y + 5, 5, alto - 10);
        }
    }
    }
