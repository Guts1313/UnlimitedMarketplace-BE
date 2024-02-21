import java.util.Objects;

public class Codon {
    private final Nucleotide first;
    private final Nucleotide second;
    private final Nucleotide third;

    public Codon(Nucleotide first, Nucleotide second, Nucleotide third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public Codon(char first, char second, char third) {
        this(Nucleotide.valueOf(String.valueOf(first)),
                Nucleotide.valueOf(String.valueOf(second)),
                Nucleotide.valueOf(String.valueOf(third)));
    }

    public Codon(String nucleotides) {
        this(nucleotides.charAt(0),
                nucleotides.charAt(1),
                nucleotides.charAt(2));
    }

    public Nucleotide getFirst() {
        return first;
    }

    public Nucleotide getSecond() {
        return second;
    }

    public Nucleotide getThird() {
        return third;
    }

    @Override
    public String toString() {
        return "" + first + second + third;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        return this.first == ((Codon) o).first && this.second == ((Codon) o).second && this.third == ((Codon) o).third;
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second, third);
    }


}