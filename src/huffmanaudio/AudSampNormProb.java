package huffmanaudio;

import huffmanstats.ProbDist;
import org.apache.commons.math3.distribution.NormalDistribution;

import java.util.function.Consumer;

public class AudSampNormProb implements ProbDist<Double>, Consumer<Double> {

    private double sums = 0;
    private double sqrsums = 0;
    private long length;
    private int sampleSize;

    private double mean;
    private double std;

    private NormalDistribution nd;

    public AudSampNormProb(long audioLength, int sampleSize) {
        this.length = audioLength;
        this.sampleSize = sampleSize;
    }

    public AudSampNormProb(double mean, double std) {
        this.nd = new NormalDistribution(this.mean = mean, this.std = std);
    }

    void process() {
        double mean = this.sums / this.length;
        double std = Math.sqrt((this.sqrsums - this.sums * mean) / this.length + mean * mean);

        this.nd = new NormalDistribution(this.mean = Math.round(mean), this.std = Math.round(std));
    }

    @Override
    public double getMinProb() {
        return this.nd.cumulativeProbability(-1 << (8 * this.sampleSize - 1));
    }

    @Override
    public double getMaxProb() {
        return this.nd.cumulativeProbability(~(-1 << (8 * this.sampleSize - 1)));
    }

    @Override
    public Double inverseCum(double prob) {
        return this.nd.inverseCumulativeProbability(prob);
    }

    @Override
    public double cum(Double value) {
        return this.nd.cumulativeProbability(value);
    }

    @Override
    public void accept(Double sample) {
        this.sqrsums += sample * sample;
        this.sums += sample;
    }

    public double getMean() {
        return this.mean;
    }

    public double getStd() {
        return this.std;
    }

    public String toString() {
        return "Mean: " + nd.getMean() + "\nStandard Deviation: " + nd.getStandardDeviation();
    }
}
