import org.apache.commons.math3.distribution.NormalDistribution;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

public class HuffmanAudio {
    private final File audioFile;
    private final AudioFileStats audioStats;

    public HuffmanAudio(File audioFile) {
        this.audioFile = audioFile;
        this.audioStats = new AudioFileStats(audioFile);
    }

    public void computeCompression() throws UnsupportedAudioFileException, IOException {
        this.audioStats.computeStats();

        double mean = audioStats.getMean()[0];
        double std = audioStats.getStandardDeviation()[0];
        double i = 0.1;

        NormalDistribution nd = new NormalDistribution(mean, std);

        AudioInputStream audio = AudioSystem.getAudioInputStream(this.audioFile);

        double digitSum = 0;

        AudioFormat format = audio.getFormat();

        int frameSize = format.getFrameSize();
        int channels = format.getChannels();
        int sampleSize = frameSize / channels;

        System.out.println(sampleSize);

        System.out.println("Calculating...");

        while(audio.available() > 0) {
            byte[] frame = audio.readNBytes(frameSize);
            for(int channel = 0; channel < channels - 1; channel++) {
                int sample = 0;

                for(int samplePos = 0; samplePos < sampleSize; samplePos++) {
                    sample |= frame[channel * sampleSize + samplePos] << (8 * samplePos);
                }

                double target = Math.round(Math.abs(sample) / std / i) * std * i;

                double min = 0;
                double max = Math.ceil(32767 / std / i) * std * i;

                double pos;

                digitSum += 1;
                double cdfMax;
                double cdfMin;
                double cdfPos;

                do {
                    pos = min;

                    cdfMax = nd.cumulativeProbability(max);
                    cdfMin = nd.cumulativeProbability(min);

                    do {
                        pos += std * i;
                        cdfPos = nd.cumulativeProbability(pos + std * i);
                    } while(cdfPos - cdfMin < cdfMax - cdfPos);

                    if(pos < target) {
                        min = pos;
                    } else {
                        max = pos;
                    }

                    digitSum += 1;
//                    System.out.println(Math.abs(max-min));
                } while(Math.abs(Math.abs(max-min) - std * i) > 0.0001);
//                System.out.println("hi");
            }
        }

        audio.close();

        System.out.println("Frame length: " + audio.getFrameLength());
        System.out.println("Average value length: " + digitSum / audio.getFrameLength());
        System.out.println("Original value size: " + sampleSize * 8);
        System.out.println("Percent reduction: " + (1 - digitSum / audio.getFrameLength() / sampleSize / 8));
        System.out.println("Total possible values: " + 65535 / std / i);
        System.out.println("Value space: " + std * i);
    }
}
