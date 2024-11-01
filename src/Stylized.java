import stylized.HuffmanAudio;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

public class Stylized {
    public static void main(String[] args) throws UnsupportedAudioFileException, IOException {
        File sourceAudio = new File(args[0]);
        File compressAudio = new File(args.length > 2 ? args[1] : args[0]);
        File output = new File(args.length > 2 ? args[2] : args[1]);

        long time = System.currentTimeMillis();
        HuffmanAudio huffmanAudio = new HuffmanAudio(AudioSystem.getAudioInputStream(sourceAudio));
        System.out.println(System.currentTimeMillis() - time);
        System.out.println("HI");
        huffmanAudio.compressAudio(compressAudio, output);
    }
}
