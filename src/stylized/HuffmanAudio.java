package stylized;

import org.apache.commons.math3.distribution.NormalDistribution;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;

@Deprecated
public class HuffmanAudio {
    private final WaveformStats waveFormStats;
    private NormalDistribution[] nd;

    public HuffmanAudio(AudioInputStream audioInput) throws IOException {
        this.waveFormStats = new WaveformStats(audioInput);

        double[] means = this.waveFormStats.getMean();
        double[] stds = this.waveFormStats.getStandardDeviation();

        int channels = audioInput.getFormat().getChannels();

        this.nd = new NormalDistribution[channels];

        for(int channelIndex = 0; channelIndex < channels; channelIndex++) {
            this.nd[channelIndex] = new NormalDistribution(means[channelIndex], stds[channelIndex]);
        }
    }

    public WaveformStats getWaveFormStats() {
        return this.waveFormStats;
    }
    public NormalDistribution[] getNormalDistributions() {
        return this.nd;
    }

    public void compressAudio(File compress, File output) throws UnsupportedAudioFileException, IOException {
        AudioInputStream audio = AudioSystem.getAudioInputStream(compress);

        AudioFormat format = audio.getFormat();

        int frameSize = format.getFrameSize();
        int channels = format.getChannels();
        int sampleSize = frameSize / channels;

        NormalDistribution[] normalDistributions = this.getNormalDistributions();

        double[]
                initalProbMin = new double[channels],
                initalProbMax = new double[channels];

        for(int channelIndex = 0; channelIndex < channels; channelIndex++) {
            initalProbMin[channelIndex] = normalDistributions[channelIndex].cumulativeProbability(-1 << (8 * sampleSize - 1));
            initalProbMax[channelIndex] = normalDistributions[channelIndex].cumulativeProbability(~(-1 << (8 * sampleSize - 1)));
        }

        FileOutputStream fileOutputStream = new FileOutputStream(output);
        DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);

        dataOutputStream.writeDouble(waveFormStats.getMean()[0]);
        dataOutputStream.writeDouble(waveFormStats.getStandardDeviation()[0]);

        long digitSum = 0;

        while(audio.available() > 0) {
            byte[] frame = audio.readNBytes(frameSize);
            for(int channel = 0; channel < channels; channel++) {
                NormalDistribution nd = normalDistributions[channel];
                int sample = 0;

                for(int samplePos = 0; samplePos < sampleSize; samplePos++) {
                    sample |= (frame[channel * sampleSize + samplePos] & 0xff) << 8 * samplePos;
                }

                if(sample >> (8 * sampleSize - 1) == 1)
                    sample |= -1 << 8 * sampleSize;

                double target = Math.round(sample);

                double probMin = initalProbMin[channel];
                double probMax = initalProbMax[channel];

                double pos;

                do {
                    pos = ((probMin + probMax)/2.0);

                    double compare;
                    if(pos % 1 > 0.5) {
                        pos = Math.ceil(pos);
                        compare = probMax;
                    } else {
                        pos = Math.floor(pos);
                        compare = probMin;
                    }

                    double newProb = nd.cumulativeProbability(pos);

                    if(newProb == compare)
                        break;

                    boolean up = target < pos;

                    if(up) {
                        probMax = newProb;
                    } else {
                        probMin = newProb;
                    }

                    digitSum++;
                } while(true);
            }
        }

        fileOutputStream.close();
        dataOutputStream.close();
        audio.close();

        System.out.println("DONE");

        System.out.println("Pre-Compression Value Size: " + sampleSize);
        System.out.println("Post-Compression Value Size: " + digitSum / channels / audio.getFrameLength());
    }
}
