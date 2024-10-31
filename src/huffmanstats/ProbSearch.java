package huffmanstats;

import java.io.DataOutputStream;

public interface ProbSearch<T extends Number> {
    void searchValue(T value, ProbDist<T> dist, DataOutputStream output);
}