package hu.pmamico.deszkamatek.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Data
@Slf4j
public class Raktar {

    private List<Deszka> raktarozott = new ArrayList<>();


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
            return vagottak.getFirst();
        }

        var deszka =  findAndRemoveSoftMatch(igeny).orElseThrow();
        log.info("Kisebb deszka találva: {}", deszka.getHosszusag());
        log.info(String.valueOf(getRaktarozott().size()));
        return deszka;
    }


    private Optional<Deszka> findAndRemoveSoftMatch(DeszkaIgeny igeny) {
        Iterator<Deszka> iterator = raktarozott.iterator();
        while (iterator.hasNext()) {
            Deszka deszka = iterator.next();
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
            if (!canBeCutToMatch(d, igeny)) continue;

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

    private boolean canBeCutToMatch(Deszka deszka, DeszkaIgeny igeny) {
        if (igeny.getY() != null && deszka.getHosszusag() > igeny.getY()) {
            return true;
        }

        if (igeny.getX() != null && deszka.getSzelesseg() > igeny.getX()) {
            return true;
        }

        return false;
    }

    public boolean ures(){
        return raktarozott.isEmpty();
    }
}
