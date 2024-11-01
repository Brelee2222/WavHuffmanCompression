import huffmanaudio.HuffAudComp;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws UnsupportedAudioFileException, IOException {
        File sourceAudio = new File(args[0]);
        File compressAudio = new File(args[1]);
        File outputFile = new File(args[2]);
        long time = System.currentTimeMillis();

        HuffAudComp huffAudComp = new HuffAudComp(AudioSystem.getAudioInputStream(sourceAudio));
        System.out.println("Stats time in sec: " + (System.currentTimeMillis() - time) / 1000);
        huffAudComp.dispInfo();

        FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
        DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);

        time = System.currentTimeMillis();
        huffAudComp.compressAudio(AudioSystem.getAudioInputStream(compressAudio), dataOutputStream);
        System.out.println("CompressionTime in secs: " + (System.currentTimeMillis() - time) / 1000);

        fileOutputStream.close();
        dataOutputStream.close();
    }
}
