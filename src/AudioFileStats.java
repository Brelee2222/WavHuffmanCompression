import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

public class AudioFileStats {
    public final File audioFile;

    private double[] means;
    private double[] standardDeviations;

    public AudioFileStats(File audioFile) {
        this.audioFile = audioFile;
    }

    public void computeStats() throws UnsupportedAudioFileException, IOException {
        AudioInputStream audio = AudioSystem.getAudioInputStream(this.audioFile);

        AudioFormat format = audio.getFormat();

        int
                frameSize = format.getFrameSize(),
                channels = format.getChannels(),
                sampleSize = frameSize / channels;

        double[]
                sqrSums = new double[channels],
                sums = new double[channels];

        byte[] frame = new byte[frameSize];

        while(audio.available() > 0) {
            audio.read(frame);

            for(int channel = 0; channel < channels; channel++) {
                int sample = 0;

                for(int samplePos = 0; samplePos < sampleSize; samplePos++) {
                    sample |= frame[channel * sampleSize + samplePos] << (8 * samplePos);
                }

                sums[channel] += sample;
                sqrSums[channel] += (double) sample * sample;
            }
        }

        audio.close();

        long frameLength = audio.getFrameLength();

        double[]
                means = new double[channels],
                stds = new double[channels];

        for(int channel = 0; channel < channels; channel++) {
            means[channel] = sums[channel] / frameLength;

            stds[channel] = Math.sqrt((sqrSums[channel] - sums[channel] * means[channel]) / frameLength + means[channel] * means[channel]);
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