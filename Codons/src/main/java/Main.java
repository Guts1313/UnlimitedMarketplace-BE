import java.util.*;

public class Main {

    public static void main(String[] args) {
//        Set<String> sequences = new HashSet<>(Set.of("A","T","C"));
        Codon codon1 = new Codon("ATG");
        Codon codon2 = new Codon("ATG");
        Codon codon3 = new Codon("CCC");
        Codon codon4 = new Codon("GGG");

        Set<Codon> possibleCodons = new HashSet<>();
        Set<Aminoacid> possibleAminos = new HashSet<>();
        List<Codon> sequence = List.of(new Codon("AAA"), new Codon("CCC"), new Codon("GGG"), new Codon("AAA"));

        possibleCodons.add(codon1);
        possibleCodons.add(codon3);
        possibleCodons.add(codon4);
        Aminoacid aminoacidOne = new Aminoacid(Set.of(new Codon("AAA"), new Codon("CCC")));
        Aminoacid aminoacidTwo = new Aminoacid(Set.of(new Codon("GGG"), new Codon("TTT")));
        Set<Aminoacid> aminoSet = Set.of(aminoacidOne,aminoacidTwo);
        AminoacidCounter aminoacidCounter = new AminoacidCounter();

        HashMap<Aminoacid, Integer> map = new HashMap<>();
        for (Aminoacid a : aminoSet) {
            map.put(a ,0);
        }
        var result = aminoacidCounter.countAminoacids(sequence,aminoSet);
        for (Aminoacid ac : result.keySet()){
            System.out.println("Result is " + ac + "" + result.get(ac));
        }

    }
}
