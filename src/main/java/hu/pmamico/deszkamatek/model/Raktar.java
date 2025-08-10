package hu.pmamico.deszkamatek.model;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class Raktar {

    @Getter
    @Setter
    private List<Deszka> raktarozott = new ArrayList<>();
    public void hozzaad(Deszka deszka) {
        raktarozott.add(deszka);
    }

    public void hozzaadMind(List<Deszka> deszkak) {
        log.info("Több deszka hozzáadása a raktárhoz: {} db", deszkak.size());
        raktarozott.addAll(deszkak);
    }

    /**
     * Result of a plank search, containing the plank and whether it was cut
     */
    public static class KeresesEredmeny {
        private final Deszka deszka;
        private final boolean vagva;

        public KeresesEredmeny(Deszka deszka, boolean vagva) {
            this.deszka = deszka;
            this.vagva = vagva;
        }

        public Deszka getDeszka() {
            return deszka;
        }

        public boolean isVagva() {
            return vagva;
        }
    }

    /**
     * Search for a plank matching the requirements
     * @param igeny the requirements
     * @return the result containing the plank and whether it was cut, or null if no matching plank was found
     */
    @SneakyThrows
    public KeresesEredmeny keresDeszka(DeszkaIgeny igeny) {
        log.info("Deszka keresése igény alapján: szelesseg={}, hosszusag={}, vastagsag={}",
                igeny.getSzelesseg(), igeny.getHosszusag(), igeny.getVastagsag());

        Optional<Deszka> exactMatch = findExactMatch(igeny);
        if (exactMatch.isPresent()) {
            Deszka talalt = exactMatch.get();
            raktarozott.remove(talalt);
            return new KeresesEredmeny(talalt, false);
        }

        Optional<Deszka> cuttableMatch = findCuttableMatch(igeny);
        if (cuttableMatch.isPresent()) {
            Deszka deszka = cuttableMatch.get();
            raktarozott.remove(deszka);
            log.info("Vágható deszka találva: {}x{}x{}", deszka.getSzelesseg(), deszka.getHosszusag(), deszka.getVastagsag());
            if (igeny.getSzelesseg() != null && deszka.getSzelesseg() > igeny.getSzelesseg()) {
                throw new Exception("nincs megirva");
            } else if (igeny.getHosszusag() != null && deszka.getHosszusag() > igeny.getHosszusag()) {
                log.info("Deszka hosszanti vágása magasság szerint: {}", igeny.getHosszusag());
                val vagottak = deszka.vagas(igeny.getHosszusag());
                raktarozott.addAll(vagottak);
                return keresDeszka(igeny);
            }
        }

        // If no exact or cuttable match is found, and we have a width requirement,
        // find the longest available plank
        if (igeny.getSzelesseg() != null && !raktarozott.isEmpty()) {
            Deszka leghosszabb = findLongestPlank();
            if (leghosszabb != null) {
                raktarozott.remove(leghosszabb);
                log.info("Nincs megfelelő méretű deszka, a leghosszabb kerül felhasználásra: {}x{}x{}", 
                        leghosszabb.getSzelesseg(), leghosszabb.getHosszusag(), leghosszabb.getVastagsag());
                return new KeresesEredmeny(leghosszabb, false);
            }
        }

        return null;
    }

    /**
     * Search for a plank matching the requirements (backward compatibility)
     * @param igeny the requirements
     * @return the plank, or null if no matching plank was found
     */
    public Deszka keres(DeszkaIgeny igeny) {
        KeresesEredmeny eredmeny = keresDeszka(igeny);
        return eredmeny != null ? eredmeny.getDeszka() : null;
    }

    /**
     * Find the longest plank in the warehouse
     * @return the longest plank, or null if the warehouse is empty
     */
    private Deszka findLongestPlank() {
        if (raktarozott.isEmpty()) {
            return null;
        }

        return raktarozott.stream()
                .max((d1, d2) -> Double.compare(d1.getSzelesseg(), d2.getSzelesseg()))
                .orElse(null);
    }

    private Optional<Deszka> findExactMatch(DeszkaIgeny igeny) {
        return raktarozott.stream()
                .filter(deszka -> matchesDimensions(deszka, igeny))
                .filter(deszka -> matchesSides(deszka, igeny))
                .findFirst();
    }

    private Optional<Deszka> findCuttableMatch(DeszkaIgeny igeny) {
        return raktarozott.stream()
                .filter(deszka -> canBeCutToMatch(deszka, igeny))
                .sorted((d1, d2) -> {
                    boolean d1HasCutSide = hasVagottSide(d1);
                    boolean d2HasCutSide = hasVagottSide(d2);

                    if (d1HasCutSide && !d2HasCutSide) {
                        return -1; // d1 comes first (has cut side)
                    } else if (!d1HasCutSide && d2HasCutSide) {
                        return 1;  // d2 comes first (has cut side)
                    } else {
                        return 0;  // no preference
                    }
                })
                .findFirst();
    }

    private boolean hasVagottSide(Deszka deszka) {
        return deszka.getBalOldal() == OldalAllapot.VAGOTT ||
               deszka.getFelsoOldal() == OldalAllapot.VAGOTT ||
               deszka.getJobbOldal() == OldalAllapot.VAGOTT ||
               deszka.getAlsoOldal() == OldalAllapot.VAGOTT;
    }

    private boolean matchesDimensions(Deszka deszka, DeszkaIgeny igeny) {
        return (igeny.getSzelesseg() == null || deszka.getSzelesseg() == igeny.getSzelesseg()) &&
               (igeny.getHosszusag() == null || deszka.getHosszusag() == igeny.getHosszusag()) &&
               (igeny.getVastagsag() == null || deszka.getVastagsag() == igeny.getVastagsag());
    }

    private boolean matchesSides(Deszka deszka, DeszkaIgeny igeny) {
        return (igeny.getBalOldal() == null || deszka.getBalOldal() == igeny.getBalOldal()) &&
               (igeny.getFelsoOldal() == null || deszka.getFelsoOldal() == igeny.getFelsoOldal()) &&
               (igeny.getJobbOldal() == null || deszka.getJobbOldal() == igeny.getJobbOldal()) &&
               (igeny.getAlsoOldal() == null || deszka.getAlsoOldal() == igeny.getAlsoOldal());
    }

    private boolean canBeCutToMatch(Deszka deszka, DeszkaIgeny igeny) {
        if (igeny.getSzelesseg() != null && deszka.getSzelesseg() > igeny.getSzelesseg()) {
            return true;
        }

        if (igeny.getHosszusag() != null && deszka.getHosszusag() > igeny.getHosszusag()) {
            return true;
        }

        return false;
    }

    public int getSize(){
        return raktarozott.size();
    }
}
