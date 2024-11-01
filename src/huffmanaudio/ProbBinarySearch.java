package huffmanaudio;

import huffmanstats.ProbDist;
import huffmanstats.ProbSearch;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ProbBinarySearch implements ProbSearch<Double> {

    byte buffer = 0;
    byte pos = 0;

    void writeBool(boolean value, OutputStream out) throws IOException {
        buffer |= (value ? 1 : 0) << pos++;

        if(pos == 8) {
            this.writeRest(out);
        }
    }

    void writeRest(OutputStream out) throws IOException {
        pos = 0;
        out.write(buffer);
        buffer = 0;
    }

    boolean readBool(InputStream input) throws IOException {
        if(pos == 8) {
            pos = 0;

            buffer = (byte) input.read();
        }
        return ((buffer >> pos++) & 1) == 1;
    }

    @Override
    public void searchValue(Double value, ProbDist<Double> dist, OutputStream output) throws IOException {
        double
                minProb = dist.getMinProb(),
                maxProb = dist.getMaxProb();

        double pos;

        do {
            pos = dist.inverseCum((minProb + maxProb)/2.0);

            double compare;
            if(pos % 1 > 0.5) {
                pos = Math.ceil(pos);
                compare = maxProb;
            } else {
                pos = Math.floor(pos);
                compare = minProb;
            }

            double newProb = dist.cum(pos);

            if(newProb == compare)
                break;

            boolean greater = value < pos;

            if(greater) {
                maxProb = newProb;
            } else {
                minProb = newProb;
            }

            this.writeBool(greater, output);
        } while(true);
    }

    @Override
    public Double unsearchValue(ProbDist<Double> dist, InputStream input) throws IOException {
        double
                minProb = dist.getMinProb(),
                maxProb = dist.getMaxProb();

        double pos;

        do {
            pos = dist.inverseCum((minProb + maxProb)/2.0);

            double compare;
            if(pos % 1 > 0.5) {
                pos = Math.ceil(pos);
                compare = maxProb;
            } else {
                pos = Math.floor(pos);
                compare = minProb;
            }

            double newProb = dist.cum(pos);

            if(newProb == compare)
                return pos;

            if(readBool(input)) {
                maxProb = newProb;
            } else {
                minProb = newProb;
            }
        } while(true);
    }
}
