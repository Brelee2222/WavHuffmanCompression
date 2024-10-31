package huffmanstats;

import java.util.function.Consumer;

public interface ProbDist<T extends Number> extends Consumer<T> {
    double getMinProb();
    double getMaxProb();

    T inverseCum(double prob);
}