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
        double i = 0.01;

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

                double target = Math.round(sample / std / i) * std * i;

                double probMin = 0;
                double probMax = 1;
                double pos;

//                System.out.println(nd.cumulativeProbability(target));
                if(target >= 8051) {
                    System.out.println(target);
                    System.out.println(nd.cumulativeProbability(target));
                }
                if(nd.cumulativeProbability(target) >= 0.999999999999999) {
                    continue;
                }

                do {
                    pos = Math.floor(nd.inverseCumulativeProbability((probMin + probMax)/2.0) / std / i) * std * i;

                    double newProb = nd.cumulativeProbability(pos);

                    if(newProb == probMin) {
//                        System.out.println("pp");
                        break;
                    }

                    if(target < pos) {
                        probMax = newProb;
                    } else {
                        probMin = newProb;
                    }
                    digitSum += 1;
                } while(true);
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
