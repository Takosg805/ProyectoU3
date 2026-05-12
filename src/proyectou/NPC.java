package proyectou;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import javax.swing.ImageIcon;

public class NPC {
    public int x, y;
    public int ancho = 60, alto = 60;
    
    public int puntuacion = 0;
    public boolean estaMuerto = false;
    public int vidas = 3;
    
    // Físicas
    public double vy = 0;
    public double gravedad = 0.5;
    public double fuerzaSalto = -14;
    public double velocidadAvance;
    public boolean enSuelo = false;
    
    
    private Image sprite;

    private double probError; 
    private int distanciaVision; // Qué tan lejos mira hacia adelante
    private int ultimaColMuerte = -1; //memoria de errores
    private int framesAtorado = 0;    //contador de atoros
    private int ultimoX = -1;         //para detectar si avanza
    

    public NPC(int startX, int startY, double velocidadAvance, double probError, String rutaSprite) {
        this.x = startX;
        this.y = startY;
        this.velocidadAvance = velocidadAvance;
        this.probError = probError;
        
        ancho = 60;
        alto = 60;
        sprite = new ImageIcon(getClass().getResource(rutaSprite)).getImage();
        // Asignamos su "visión" dependiendo de si es torpe o preciso
        this.distanciaVision=30;
    }

    public void actualizar(Mapa mapa) {
        
        if(estaMuerto)
            return;
        // Avance automático
        x += velocidadAvance;
        
        puntuacion= x;
        
        // Gravedad
        vy += gravedad;
        y += (int) vy;
        enSuelo = false;

        // Colisiones verticales
        for (int fila = 0; fila < mapa.nivel.length; fila++) {
            for (int col = 0; col < mapa.nivel[fila].length; col++) {
                int tipoBloque = mapa.nivel[fila][col];
                if (tipoBloque == mapa.TIERRA || tipoBloque == mapa.LAVA || tipoBloque == mapa.PIEDRA) {
                    Rectangle bloque = new Rectangle(col * mapa.TAMAÑO_TILE, fila * mapa.TAMAÑO_TILE, mapa.TAMAÑO_TILE, mapa.TAMAÑO_TILE);
                    
                    if (vy > 0 && getHitAbajo().intersects(bloque)) {
                        y = bloque.y - alto;
                        vy = 0;
                        enSuelo = true;
                        if (tipoBloque == mapa.LAVA) perderVida(mapa); // solo lava mata
                    } else if (vy < 0 && getHitArriba().intersects(bloque)) {
                        y = bloque.y + bloque.height;
                        vy = 0;
                    }
                }
            }
        }

        // Colisiones horizontales
        for (int fila = 0; fila < mapa.nivel.length; fila++) {
            for (int col = 0; col < mapa.nivel[fila].length; col++) {
                int tipoBloque = mapa.nivel[fila][col];
                if (tipoBloque != mapa.VACIO) {
                    Rectangle bloque = new Rectangle(col * mapa.TAMAÑO_TILE, fila * mapa.TAMAÑO_TILE, mapa.TAMAÑO_TILE, mapa.TAMAÑO_TILE);
                    if ((tipoBloque == mapa.TIERRA || tipoBloque == mapa.LAVA || tipoBloque == mapa.SIN_SALIDA || tipoBloque == mapa.PIEDRA) 
                        && velocidadAvance > 0 && getHitDerecha().intersects(bloque)) {
                        x = bloque.x - ancho;
                        if (tipoBloque == mapa.LAVA) perderVida(mapa);
                        if (tipoBloque == mapa.PIEDRA) x -= 3; // retrocede un poco si se pega
                        if (enSuelo) saltar(); //intenta saltar al pegarse
                    }
                }
            }
        }
        //Muerte por caída
        if(y>600){
        perderVida(mapa);
        }
        // ========================================================
        // NUEVA INTELIGENCIA ARTIFICIAL: PUNTO DE VISIÓN
        // ========================================================
        
        // Calculamos qué hay exactamente delante del NPC usando la matriz
        int puntoVisionX = x + ancho + distanciaVision;
        int colFrente = puntoVisionX / mapa.TAMAÑO_TILE;
        
        // Altura de los pies y altura debajo de los pies
        int filaPies = (y + alto - 10) / mapa.TAMAÑO_TILE; 
        int filaAbajo = (y + alto + 10) / mapa.TAMAÑO_TILE;

        boolean debeSaltar = false;

        // Verificamos que no intentemos leer fuera del mapa
        if (colFrente < mapa.nivel[0].length && filaAbajo < mapa.nivel.length && filaPies >= 0) {
            int bloqueFrente = mapa.nivel[filaPies][colFrente];
            int bloqueAbajo = mapa.nivel[filaAbajo][colFrente];

           // Diferenciamos obstáculos
            if (bloqueFrente == mapa.LAVA || bloqueAbajo == mapa.LAVA || bloqueAbajo == mapa.VACIO) {
                fuerzaSalto = -16; // ⭐ MODIFICACIÓN: salto más fuerte para huecos/lava
                debeSaltar = true; // mortal → siempre saltar
            } else if (bloqueFrente == mapa.PIEDRA || bloqueFrente == mapa.SIN_SALIDA) {
                fuerzaSalto = -14; // salto normal
                debeSaltar = true; // obstáculo → saltar, pero no mata si falla
            } else if (bloqueFrente == mapa.TIERRA) {
                fuerzaSalto = -15; // salto medio para pared
                debeSaltar = true; // pared → saltar
            }
            
            // ⭐ MODIFICACIÓN: memoria de errores
                if (colFrente == ultimaColMuerte) {
                    debeSaltar = true; // salta antes si ya murió aquí
                }
        }

               // Aplicamos la toma de decisiones y la probabilidad de error
            if (debeSaltar && enSuelo) {
                if (Math.random() < probError) {
                    // NPC torpe: a veces falla y retrocede
                    if (Math.random() < 0.5) x -= 2;
                } else {
                    saltar(); // salta correctamente
                }
            } else if (!debeSaltar && enSuelo && probError > 0) {
                // NPC torpe: a veces salta sin necesidad
                if (Math.random() < (probError * 0.02)) {
                    saltar();
                }
            }
            
            if (x == ultimoX) {
            framesAtorado++;
            if (framesAtorado > 20 && enSuelo) {
                saltar(); // salto forzado
                framesAtorado = 0;
                }
            } else {
                framesAtorado = 0;
            }
            ultimoX = x;
    
    }
    
    private void perderVida(Mapa mapa) {
    vidas--;
    if (vidas <= 0) {
        estaMuerto = true;
    } else {
        // Buscar un punto seguro cerca de la posición actual
        int colActual = x / mapa.TAMAÑO_TILE;
        int filaSegura = -1;

        // Recorremos hacia atrás unas columnas para encontrar suelo
        for (int col = colActual; col >= Math.max(0, colActual - 5); col--) {
            for (int fila = mapa.nivel.length - 1; fila >= 0; fila--) {
                if (mapa.nivel[fila][col] == mapa.TIERRA || mapa.nivel[fila][col] == mapa.PIEDRA) {
                    filaSegura = fila;
                    x = col * mapa.TAMAÑO_TILE;
                    y = (filaSegura * mapa.TAMAÑO_TILE) - alto;
                    vy = 0;
                    return;
                }
            }
        }

        // Si no encuentra nada, reaparece un poco más atrás en la misma altura
        x = Math.max(50, x - 100);
        vy = 0;
    }
}

    public void saltar() {
        if (enSuelo) {
            vy = fuerzaSalto;
            enSuelo = false;
        }
    }

    public void dibujar(Graphics2D g2d, int camaraX) {
        int xPantalla = x - camaraX;
        // Solo lo dibujamos si está dentro de la pantalla para optimizar
        if (xPantalla + ancho > -100 && xPantalla < 900) {
            g2d.drawImage(sprite, x - camaraX, y, ancho, alto, null);
            
        }
    }
    
    // Hitboxes
    public Rectangle getHitArriba() { return new Rectangle(x + 5, y, ancho - 10, 5); }
    public Rectangle getHitAbajo() { return new Rectangle(x + 5, y + alto - 5, ancho - 10, 5); }
    public Rectangle getHitDerecha() { return new Rectangle(x + ancho - 5, y + 5, 5, alto - 10); }
}