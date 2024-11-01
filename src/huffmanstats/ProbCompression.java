package huffmanstats;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Iterator;

@Deprecated
public class ProbCompression<T extends Number> {
    private final ProbSearch<T> search;
    private final ProbDist<T>[] distributions;

    public ProbCompression(ProbSearch<T> search, ProbDist<T>[] distributions) {
        this.search = search;
        this.distributions = distributions;
    }

    public ProbDist<T>[] getDistributions() {
        return this.distributions;
    }

    public ProbSearch<T> getSearch() {
        return this.search;
    }

    public void compress(Iterator<T> values, DataOutputStream output) throws IOException {
        ProbDist<T>[] distributions = this.getDistributions();
        ProbSearch<T> search = this.getSearch();

        while(values.hasNext()) for (ProbDist<T> dist : distributions) {
            search.searchValue(values.next(), dist, output);
        }
    }
}
