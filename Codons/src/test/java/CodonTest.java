import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CodonTest {
    @Test
    void toString_shouldReturnCodonNucleotideSequenceAsString() {
        Codon codon = new Codon(Nucleotide.T, Nucleotide.C, Nucleotide.A);
        String actual = codon.toString();
        assertEquals("TCA", actual);
    }

    @Test
    void charsConstructor_shouldSetNucleotideFields() {
        Codon codon = new Codon('C', 'G', 'T');
        assertEquals(Nucleotide.C, codon.getFirst());
        assertEquals(Nucleotide.G, codon.getSecond());
        assertEquals(Nucleotide.T, codon.getThird());
    }

    @Test
    void stringConstructor_shouldSetNucleotideFields() {
        Codon codon = new Codon("GTA");
        assertEquals(Nucleotide.G, codon.getFirst());
        assertEquals(Nucleotide.T, codon.getSecond());
        assertEquals(Nucleotide.A, codon.getThird());
    }
}
