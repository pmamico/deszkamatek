package hu.pmamico.deszkamatek.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.thymeleaf.util.NumberUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Data
@Slf4j
public class Raktar {

    private List<Deszka> raktarozott = new ArrayList<>();

    /**
     * Creates a deep copy of this warehouse
     * @return a new Raktar instance with copies of all planks
     */
    public Raktar copy() {
        Raktar copy = new Raktar();
        for (Deszka deszka : this.raktarozott) {
            Deszka deszkaCopy = Deszka.builder()
                    .szelesseg(deszka.getSzelesseg())
                    .hosszusag(deszka.getHosszusag())
                    .balOldal(deszka.getBalOldal())
                    .felsoOldal(deszka.getFelsoOldal())
                    .jobbOldal(deszka.getJobbOldal())
                    .alsoOldal(deszka.getAlsoOldal())
                    .build();
            copy.hozzaad(deszkaCopy);
        }
        return copy;
    }


    public static Raktar build(int db, Deszka deszka) {
        Raktar raktar = new Raktar();
        raktar.hozzaad(db, deszka);
        return raktar;
    }

    public void hozzaad(Deszka deszka) {
        raktarozott.add(deszka);
    }

    public void hozzaad(int db, Deszka deszka) {
        for (int i = 0; i < db; i++) {
            raktarozott.add(deszka);
        }
    }


    public Deszka keres(DeszkaIgeny igeny, LerakasIrany lerakasIrany) {
        log.info("Deszka keresése igény alapján: {}",
                igeny);

        Optional<Deszka> exactMatch = findAndRemoveExactMatch(igeny);
        if (exactMatch.isPresent()) {
            log.info("Pontos deszka találva: {}",  exactMatch.get().getHosszusag());
            return exactMatch.get();
        }


        Optional<Deszka> cuttableMatch = findAndRemoveCuttableMatch(igeny);
        if (cuttableMatch.isPresent()) {
            Deszka deszka = cuttableMatch.get();
            log.info("Vágható deszka találva: {}", deszka.getHosszusag());

            var vagottak = deszka.vagas(igeny.getY(), lerakasIrany);
            raktarozott.add(vagottak.getLast());

            if(igeny.getX() != null && vagottak.getFirst().getSzelesseg() != igeny.getX()){
                var hosszabanVagottak = vagottak.getFirst().hosszantiVagas(igeny.getX(), true);
                raktarozott.add(hosszabanVagottak.getLast());
                return hosszabanVagottak.getFirst();
            }
            return vagottak.getFirst();
        }

        Optional<Deszka> xCuttableMatch = findAndRemoveXCuttableMatch(igeny);
        if (xCuttableMatch.isPresent()) {
            Deszka deszka = xCuttableMatch.get();
            log.info("Hosszában vágható deszka találva: {}", deszka.getHosszusag());

            var vagottak = deszka.hosszantiVagas(igeny.getX(), true);
            raktarozott.add(vagottak.getLast());
            return vagottak.getFirst();
        }

        Optional<Deszka> softMatch = findAndRemoveSoftMatch(igeny);
        if (softMatch.isPresent()) {
            var deszka = softMatch.get();
            log.info("Kisebb deszka találva: {}", deszka.getHosszusag());
            log.info(String.valueOf(getRaktarozott().size()));
            return deszka;
        }


        // If we reach here, no suitable board was found
        log.error("Nem található megfelelő deszka az igényhez: {}", igeny);

        // Create a minimal board to satisfy the requirement
        // This is a fallback solution to prevent the application from crashing
        Deszka minimalDeszka = Deszka.builder()
                .szelesseg(igeny.getX() != null ? igeny.getX() : 15.5) // Default width if not specified
                .hosszusag(igeny.getY() != null ? igeny.getY() : 0.5) // Minimal length to avoid validation errors
                .balOldal(igeny.getBalOldal() != null ? igeny.getBalOldal() : OldalAllapot.CSAP)
                .felsoOldal(igeny.getFelsoOldal() != null ? igeny.getFelsoOldal() : OldalAllapot.CSAP)
                .jobbOldal(igeny.getJobbOldal() != null ? igeny.getJobbOldal() : OldalAllapot.NUT)
                .alsoOldal(igeny.getAlsoOldal() != null ? igeny.getAlsoOldal() : OldalAllapot.NUT)
                .build();

        log.info("Létrehozva minimális deszka: {}", minimalDeszka);
        return minimalDeszka;
    }


    private Optional<Deszka> findAndRemoveSoftMatch(DeszkaIgeny igeny) {
        Iterator<Deszka> iterator = raktarozott.iterator();
        while (iterator.hasNext()) {
            Deszka deszka = iterator.next();
            if (hasBothTopAndBottomCut(deszka)) {
                continue; // Skip boards with both top and bottom sides cut
            }
            if (matchesDimensionsSoftly(deszka, igeny) &&
                    matchesSides(deszka, igeny)) {
                iterator.remove();
                return Optional.of(deszka);
            }
        }
        return Optional.empty();
    }

    private Optional<Deszka> findAndRemoveExactMatch(DeszkaIgeny igeny) {
        Iterator<Deszka> it = raktarozott.iterator();
        while (it.hasNext()) {
            Deszka d = it.next();
            if (hasBothTopAndBottomCut(d)) {
                continue; // Skip boards with both top and bottom sides cut
            }
            if (matchesDimensions(d, igeny) && matchesSides(d, igeny)) {
                it.remove();                 // biztonságos eltávolítás iterálás közben
                return Optional.of(d);
            }
        }
        return Optional.empty();
    }

    private Optional<Deszka> findAndRemoveCuttableMatch(DeszkaIgeny igeny) {
        int firstNonCutIndex = -1;

        for (int i = 0; i < raktarozott.size(); i++) {
            Deszka d = raktarozott.get(i);
            if (hasBothTopAndBottomCut(d)) continue; // Skip boards with both top and bottom sides cut
            if (!canY_BeCutToMatch(d, igeny)) continue;

            boolean hasCut = hasVagottSide(d);
            if (hasCut) {
                // találtunk vágott oldalút – ez a preferált, azonnal kivesszük
                Deszka chosen = raktarozott.remove(i);
                return Optional.of(chosen);
            }
            // jegyezzük meg az első nem vágott jelöltet esetre, ha később sem lesz vágott
            if (firstNonCutIndex == -1) {
                firstNonCutIndex = i;
            }
        }

        if (firstNonCutIndex != -1) {
            Deszka chosen = raktarozott.remove(firstNonCutIndex);
            return Optional.of(chosen);
        }
        return Optional.empty();
    }


    private Optional<Deszka> findAndRemoveXCuttableMatch(DeszkaIgeny igeny) {
        int firstNonCutIndex = -1;

        for (int i = 0; i < raktarozott.size(); i++) {
            Deszka d = raktarozott.get(i);
            if (hasBothTopAndBottomCut(d)) continue; // Skip boards with both top and bottom sides cut
            if (!canX_BeCutToMatch(d, igeny)) continue;

            boolean hasCut = hasVagottSide(d);
            if (hasCut) {
                // találtunk vágott oldalút – ez a preferált, azonnal kivesszük
                Deszka chosen = raktarozott.remove(i);
                return Optional.of(chosen);
            }
            // jegyezzük meg az első nem vágott jelöltet esetre, ha később sem lesz vágott
            if (firstNonCutIndex == -1) {
                firstNonCutIndex = i;
            }
        }

        if (firstNonCutIndex != -1) {
            Deszka chosen = raktarozott.remove(firstNonCutIndex);
            return Optional.of(chosen);
        }
        return Optional.empty();
    }

    private boolean hasVagottSide(Deszka deszka) {
        return deszka.getBalOldal() == OldalAllapot.VAGOTT ||
               deszka.getFelsoOldal() == OldalAllapot.VAGOTT ||
               deszka.getJobbOldal() == OldalAllapot.VAGOTT ||
               deszka.getAlsoOldal() == OldalAllapot.VAGOTT;
    }

    private boolean hasBothTopAndBottomCut(Deszka deszka) {
        return deszka.getFelsoOldal() == OldalAllapot.VAGOTT && 
               deszka.getAlsoOldal() == OldalAllapot.VAGOTT;
    }

    private boolean matchesDimensions(Deszka deszka, DeszkaIgeny igeny) {
        return (igeny.getX() == null || deszka.getSzelesseg() == igeny.getX()) &&
               (igeny.getY() == null || deszka.getHosszusag() == igeny.getY());
    }

    private boolean matchesDimensionsSoftly(Deszka deszka, DeszkaIgeny igeny) {
        return (igeny.getX() == null || deszka.getSzelesseg() <= igeny.getX()) &&
                (igeny.getY() == null || deszka.getHosszusag() <= igeny.getY());
    }

    private boolean matchesSides(Deszka deszka, DeszkaIgeny igeny) {
        return (igeny.getBalOldal() == null || deszka.getBalOldal() == igeny.getBalOldal()) &&
               (igeny.getFelsoOldal() == null || deszka.getFelsoOldal() == igeny.getFelsoOldal()) &&
               (igeny.getJobbOldal() == null || deszka.getJobbOldal() == igeny.getJobbOldal()) &&
               (igeny.getAlsoOldal() == null || deszka.getAlsoOldal() == igeny.getAlsoOldal());
    }

    private boolean canY_BeCutToMatch(Deszka deszka, DeszkaIgeny igeny) {

        if (igeny.getY() != null && deszka.getHosszusag() > igeny.getY()) {
            return true;
        }



        return false;
    }

    private boolean canX_BeCutToMatch(Deszka deszka, DeszkaIgeny igeny) {

        if (igeny.getX() != null && deszka.getHosszusag() > igeny.getX()) {
            return true;
        }



        return false;
    }




    public boolean ures(){
        return raktarozott.isEmpty();
    }
}
