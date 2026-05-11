package proyectou;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class NPC {
    public int x, y;
    public int ancho = 60, alto = 60;
    
    // Físicas
    public double vy = 0;
    public double gravedad = 0.5;
    public double fuerzaSalto = -14;
    public double velocidadAvance;
    public boolean enSuelo = false;

    private double probError; 
    private int distanciaVision; // Qué tan lejos mira hacia adelante
    private Color color;

    public NPC(int startX, int startY, double velocidadAvance, double probError, Color color) {
        this.x = startX;
        this.y = startY;
        this.velocidadAvance = velocidadAvance;
        this.probError = probError;
        this.color = color;
        
        // Asignamos su "visión" dependiendo de si es torpe o preciso
        if (probError > 0) {
            this.distanciaVision = 10; // NPC Torpe: Mira muy cerca de su cara
        } else {
            this.distanciaVision = 50; // NPC Preciso: Mira un bloque entero hacia adelante
        }
    }

    public void actualizar(Mapa mapa) {
        // Avance automático
        x += velocidadAvance;

        // Gravedad
        vy += gravedad;
        y += (int) vy;
        enSuelo = false;

        // Colisiones verticales
        for (int fila = 0; fila < mapa.nivel.length; fila++) {
            for (int col = 0; col < mapa.nivel[fila].length; col++) {
                int tipoBloque = mapa.nivel[fila][col];
                if (tipoBloque == 1 || tipoBloque == 2) {
                    Rectangle bloque = new Rectangle(col * mapa.TAMAÑO_TILE, fila * mapa.TAMAÑO_TILE, mapa.TAMAÑO_TILE, mapa.TAMAÑO_TILE);
                    
                    if (vy > 0 && getHitAbajo().intersects(bloque)) {
                        y = bloque.y - alto;
                        vy = 0;
                        enSuelo = true;
                    }
                    // NUEVO: Si el NPC salta y choca con la cabeza en un techo
                    else if (vy < 0 && getHitArriba().intersects(bloque)) {
                        y = bloque.y + bloque.height; // Lo empujamos justo debajo del bloque
                        vy = 0; // Cortamos el salto para que empiece a caer por la gravedad
                    }
                }
            }
        }

        // Colisiones horizontales
        for (int fila = 0; fila < mapa.nivel.length; fila++) {
            for (int col = 0; col < mapa.nivel[fila].length; col++) {
                int tipoBloque = mapa.nivel[fila][col];
                if (tipoBloque != 0) {
                    Rectangle bloque = new Rectangle(col * mapa.TAMAÑO_TILE, fila * mapa.TAMAÑO_TILE, mapa.TAMAÑO_TILE, mapa.TAMAÑO_TILE);
                    if ((tipoBloque == 1 || tipoBloque == 2 || tipoBloque == 5) && velocidadAvance > 0 && getHitDerecha().intersects(bloque)) {
                        x = bloque.x - ancho; // Se atora contra la pared
                    }
                    else if (tipoBloque == 4 && getHitAbajo().intersects(bloque)) {
                        x -= 10; // zona opcional: ralentiza
                    }
                }
            }
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

            // Si hay pared en la cara (1, 2, 5) o hay un precipicio/lava abajo (0, 2)
            if (bloqueFrente == 1 || bloqueFrente == 2 || bloqueFrente == 5 || bloqueAbajo == 0 || bloqueAbajo == 2) {
                debeSaltar = true;
            }
        }

        // Aplicamos la toma de decisiones y la probabilidad de error
        if (debeSaltar && enSuelo) {
            if (Math.random() < probError) {
                // Falla el cálculo por torpe (no salta a tiempo o retrocede)
                if(Math.random() < 0.5) x -= 5; 
            } else {
                saltar(); // Salta correctamente
            }
        } else if (!debeSaltar && enSuelo && probError > 0) {
            // Si es muy torpe, a veces salta de la nada por error
            if (Math.random() < (probError * 0.03)) {
                saltar();
            }
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
            g2d.setColor(this.color);
            g2d.fillRect(xPantalla, y, ancho, alto);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(xPantalla, y, ancho, alto); // Contorno para verlo mejor
        }
    }

    // Hitboxes
    public Rectangle getHitArriba() { return new Rectangle(x + 5, y, ancho - 10, 5); }
    public Rectangle getHitAbajo() { return new Rectangle(x + 5, y + alto - 5, ancho - 10, 5); }
    public Rectangle getHitDerecha() { return new Rectangle(x + ancho - 5, y + 5, 5, alto - 10); }
}