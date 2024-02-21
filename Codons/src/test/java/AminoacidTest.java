import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class AminoacidTest {
    @Test
    void isPresent_shouldReturnTrue_whenSequenceDoesNotContainAminoacid() {
        Set<Codon> possibleCodons = new HashSet<>();
        possibleCodons.add(new Codon("ATG"));
        Aminoacid aminoacid = new Aminoacid(possibleCodons);
        List<Codon> sequence = List.of(new Codon("CCC"), new Codon("ATG"));
        boolean actual = aminoacid.isPresent(sequence);
        assertTrue(actual);
    }

    @Test
    void countAminoacids_shouldReturnCountZero_whenAminoacidNotFound() {
        AminoacidCounter aminoacidCounter = new AminoacidCounter();
        List<Codon> sequence = List.of(new Codon("CCC"), new Codon("GGG"));
        Aminoacid aminoacidOne = new Aminoacid(Set.of(new Codon("TTT")));
        Map<Aminoacid, Integer> actual = aminoacidCounter.countAminoacids(sequence, Set.of(aminoacidOne));
        Map<Aminoacid, Integer> expected = Map.of(aminoacidOne, 0);
        assertEquals(expected, actual);
    }

    @Test
    void countAminoacids_shouldReturnExpectedCount_whenAminoacidFound() {
        AminoacidCounter aminoacidCounter = new AminoacidCounter();
        List<Codon> sequence = List.of(new Codon("AAA"), new Codon("CCC"), new Codon("GGG"), new Codon("AAA"));
        Aminoacid aminoacidOne = new Aminoacid(Set.of(new Codon("AAA"), new Codon("CCC")));
        Aminoacid aminoacidTwo = new Aminoacid(Set.of(new Codon("GGG"), new Codon("TTT")));
        Map<Aminoacid, Integer> actual = aminoacidCounter.countAminoacids(sequence, Set.of(aminoacidOne, aminoacidTwo));
        Map<Aminoacid, Integer> expected = Map.of(aminoacidOne, 3, aminoacidTwo, 1);
        assertEquals(expected, actual);
    }
}