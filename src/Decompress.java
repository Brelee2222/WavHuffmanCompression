import huffmanaudio.HuffAudDecomp;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Decompress {
    public static void main(String[] args) throws IOException {
        AudioInputStream decompressedAudio = HuffAudDecomp.Decompress(new FileInputStream(args[0]));

        AudioSystem.write(decompressedAudio, AudioFileFormat.Type.WAVE, new File(args[1]));
    }
}
