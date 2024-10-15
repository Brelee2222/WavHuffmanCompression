import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws UnsupportedAudioFileException, IOException {
        File audioFile = new File(args[0]);
        HuffmanAudio huffmanAudio = new HuffmanAudio(audioFile);

        huffmanAudio.computeCompression();
    }
}
