package stylized;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.*;

@Deprecated
public class WaveformStats {
    private final double[] means;
    private final double[] standardDeviations;

    public WaveformStats(AudioInputStream audioInput) throws IOException {
        AudioFormat format = audioInput.getFormat();

        int
                frameSize = format.getFrameSize(),
                channels = format.getChannels(),
                sampleSize = format.getSampleSizeInBits();

        BufferedInputStream dataInput = new BufferedInputStream(audioInput, frameSize);

        double[]
                sqrSums = new double[channels],
                sums = new double[channels];

        while(audioInput.available() > 0) {
            for(int channel = 0; channel < channels; channel++) {
                int sample = 0;
                for(int samplePos = 0; samplePos < sampleSize; samplePos += 8)
                    sample |= dataInput.read() << samplePos;

                if (sample >> sampleSize - 1 == 1)
                    sample |= -1 << sampleSize;

                sums[channel] += sample;
                sqrSums[channel] += (double) sample * sample;
            }
        }

        audioInput.close();

        long frameLength = audioInput.getFrameLength();

        double[]
                means = new double[channels],
                stds = new double[channels];

        for(int channel = 0; channel < channels; channel++) {
            means[channel] = sums[channel] / frameLength;

            stds[channel] = Math.sqrt((sqrSums[channel] - sums[channel] * means[channel]) / frameLength + means[channel] * means[channel]);

            System.out.println(stds[channel]);
        }

        this.means = means;
        this.standardDeviations = stds;
    }

    public double[] getMean() {
        return this.means;
    }

    public double[] getStandardDeviation() {
        return this.standardDeviations;
    }
}