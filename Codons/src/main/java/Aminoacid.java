import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class Aminoacid {
    private Set<Codon> possibleCodons;

    public Aminoacid(Set<Codon> possibleCodons) {
        this.possibleCodons = possibleCodons;
    }

    public boolean isPresent(Collection<Codon> codonSequence) {
        if (codonSequence == null || codonSequence.isEmpty()) {
            return false;
        }
        for (Codon c : codonSequence) {
            if (possibleCodons.contains(c)) {
                return true;
            }
        }
        return false;
    }

    public Set<Codon> getPossibleCodons() {
        if (this.possibleCodons == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(this.possibleCodons);
    }
    @Override
    public String toString(){
        return "" + possibleCodons;
    }
}