package huffmanstats;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface ProbSearch<T extends Number> {
    void searchValue(T value, ProbDist<T> dist, OutputStream output) throws IOException;

    T unsearchValue(ProbDist<T> dist, InputStream input) throws IOException;
}