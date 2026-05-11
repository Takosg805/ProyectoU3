package proyectou;
import javax.sound.sampled.*;

public class Musica {
    private Clip clip;

    public Musica(String ruta) {
        try {
            AudioInputStream audio = AudioSystem.getAudioInputStream(getClass().getResource("/proyectou/Paper_dash.wav"));
            clip = AudioSystem.getClip();
            clip.open(audio);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reproducirLoop() {
        if (clip != null) {
            clip.loop(Clip.LOOP_CONTINUOUSLY); // se repite sin fin
            clip.start();
        }
    }

    public void detener() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }
    public void setVolumen(float valorDb) {
        if (clip != null) {
            FloatControl volumen = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            volumen.setValue(valorDb); 
            // Ejemplo: -10.0f baja volumen, 0.0f volumen normal, +6.0f más fuerte
        }
    }
}