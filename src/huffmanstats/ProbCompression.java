package huffmanstats;

import java.io.DataOutputStream;
import java.util.Iterator;

public abstract class ProbCompression<T extends Number> {
    private final ProbSearch<T> search;
    private final ProbDist<T>[] distributions;

    protected ProbCompression(ProbSearch<T> search, ProbDist<T>[] distributions) {
        this.search = search;
        this.distributions = distributions;
    }

    public ProbDist<T>[] getDistributions() {
        return this.distributions;
    }

    public ProbSearch<T> getSearch() {
        return this.search;
    }

    void compress(Iterator<T> values, DataOutputStream output) {
        ProbDist<T>[] distributions = this.getDistributions();
        ProbSearch<T> search = this.getSearch();

        int channels = distributions.length;
        int channelIndex = 0;

        while(values.hasNext()) {
            search.searchValue(values.next(), distributions[channelIndex++], output);

            if(channelIndex == channels)
                channelIndex = 0;
        }
    }
}
