package huffmanaudio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.*;

public class HuffAudDecomp {
    public static AudioInputStream Decompress(InputStream input) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(input);

        int channels = dataInputStream.readShort();
        float frameRate = dataInputStream.readFloat();
        float sampleRate = dataInputStream.readFloat();
        byte sampleSize = dataInputStream.readByte();
        short frameSize = (short) (sampleSize * channels);
        long frameLength = dataInputStream.readLong();

        AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, sampleRate, sampleSize, channels, frameSize, frameRate, false);

        PipedOutputStream pipedOutputStream = new PipedOutputStream();
        PipedInputStream pipedInputStream = new PipedInputStream(pipedOutputStream);

        AudioInputStream audioInputStream = new AudioInputStream(pipedInputStream, format, frameLength);

        new Thread(() -> {

            ProbBinarySearch probBinarySearch = new ProbBinarySearch();

            AudSampNormProb[] dists = new AudSampNormProb[channels];
            for (int channelIndex = 0; channelIndex < channels; channelIndex++) {
                try {
                    dists[channelIndex] = new AudSampNormProb(dataInputStream.readInt(), (double) dataInputStream.readInt());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            try {
                while (input.available() > 0) {
                    for(AudSampNormProb dist : dists) {
                        int value = (int) (double) probBinarySearch.unsearchValue(dist, input);

                        for(int samplePos = 0; samplePos < sampleSize; samplePos += 8) {
                            pipedOutputStream.write(value >> samplePos);
                        }
                    }

                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();

        return audioInputStream;
    }
}
