package huffmanstats;

public interface ProbDist<T extends Number> {
    double getMinProb();
    double getMaxProb();

    T inverseCum(double prob);

    double cum(T value);
}