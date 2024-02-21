import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AminoacidCounter {

    public Map<Aminoacid, Integer> countAminoacids(List<Codon> sequence, Set<Aminoacid> aminoacids) {
        Map<Aminoacid, Integer> countPerAminoacid = new HashMap<>();
        // TODO: implement counting logic return countPerAminoacid;
        for (Aminoacid aminoacid : aminoacids) {
            countPerAminoacid.put(aminoacid, 0);
        }
        for (Codon c : sequence) {
            if (c != null) {
                for (Aminoacid a : aminoacids) {
                    if (a.isPresent(List.of(c))) {
                        countPerAminoacid.put(a, countPerAminoacid.get(a) + 1);
                    }

                }
            }
        }
        return countPerAminoacid;
    }
}
