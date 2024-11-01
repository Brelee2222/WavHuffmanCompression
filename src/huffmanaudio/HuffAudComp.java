package huffmanaudio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.*;

public class HuffAudComp {
    private final AudioFormat format;
    private final ProbBinarySearch probSearch = new ProbBinarySearch();
    private final AudSampNormProb[] probDists;

    public HuffAudComp(AudioInputStream audioInput) throws IOException {
        this.format = audioInput.getFormat();

        int
                channels = this.format.getChannels(),
                sampleSize = this.format.getSampleSizeInBits();

        long framesLength = audioInput.getFrameLength();

        AudSampNormProb[] sampDists = new AudSampNormProb[channels];

        BufferedInputStream dataInput = new BufferedInputStream(audioInput, this.format.getFrameSize());

        for(int channelIndex = 0; channelIndex < channels; channelIndex++)
            sampDists[channelIndex] = new AudSampNormProb(framesLength, sampleSize);

        for(long framesLeft = 0; framesLeft < framesLength; framesLeft++) {
            for(int channelIndex = 0; channelIndex < channels; channelIndex++) {
                int sample = 0;
                for (int samplePos = 0; samplePos < sampleSize; samplePos += 8)
                    sample |= dataInput.read() << samplePos;

                if (sample >> sampleSize - 1 == 1)
                    sample |= -1 << sampleSize;

                sampDists[channelIndex].accept((double) sample);
            }
        }

        audioInput.close();
        dataInput.close();

        for(AudSampNormProb sampDist : sampDists)
            sampDist.process();

        this.probDists = sampDists;
    }

    public AudioFormat getFormat() {
        return this.format;
    }

    private ProbBinarySearch getProbSearch() {
        return this.probSearch;
    }
    private AudSampNormProb[] getProbDists() {
        return this.probDists;
    }

    public void compressAudio(AudioInputStream audioInput, OutputStream output) throws IOException {
        AudSampNormProb[] dists = this.getProbDists();
        ProbBinarySearch search = this.getProbSearch();

        AudioFormat format = this.getFormat();

        int sampleSize = format.getSampleSizeInBits();

        DataOutputStream dataOutputStream = new DataOutputStream(output);

        dataOutputStream.writeShort(format.getChannels());
        dataOutputStream.writeFloat(format.getFrameRate());
        dataOutputStream.writeFloat(format.getSampleRate());
        dataOutputStream.writeByte(format.getSampleSizeInBits());
        dataOutputStream.writeLong(audioInput.getFrameLength());

        for(AudSampNormProb dist : dists) {
            dataOutputStream.writeInt((int) dist.getMean());
            dataOutputStream.writeInt((int) dist.getStd());
        }

        dataOutputStream.flush();

        BufferedInputStream dataInput = new BufferedInputStream(audioInput, format.getFrameSize());

        for(long frames = 0, framesLength = audioInput.getFrameLength(); frames < framesLength; frames++) {
//        while(audioInput.available() > 0) {
            for(AudSampNormProb dist : dists) {
                int sample = 0;
                for (int samplePos = 0; samplePos < sampleSize; samplePos += 8)
                    sample |= dataInput.read() << samplePos;

                if (sample >> sampleSize - 1 == 1)
                    sample |= -1 << sampleSize;

                search.searchValue((double) sample, dist, output);
            }

            output.flush();
        }

        search.writeRest(output);

        audioInput.close();
    }

    public void dispInfo() {
        System.out.println("Compression Info:");

        int channels = this.getFormat().getChannels();

        for(int channelIndex = 0; channelIndex < channels; channelIndex++) {
            System.out.println("Channel " + channelIndex + ":");
            System.out.println(this.probDists[channelIndex] + "\n");
        }
    }
}
