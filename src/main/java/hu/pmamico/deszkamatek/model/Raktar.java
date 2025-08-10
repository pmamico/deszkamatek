package hu.pmamico.deszkamatek.model;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class Raktar {

    @Getter
    private final List<Deszka> raktarozott = new ArrayList<>();
    public void hozzaad(Deszka deszka) {
        log.info("Deszka hozzáadása a raktárhoz: {}x{}x{}", deszka.getMagassag(), deszka.getSzelesseg(), deszka.getVastagsag());
        raktarozott.add(deszka);
    }

    public void hozzaadMind(List<Deszka> deszkak) {
        log.info("Több deszka hozzáadása a raktárhoz: {} db", deszkak.size());
        raktarozott.addAll(deszkak);
    }

    public Deszka keres(DeszkaIgeny igeny) {
        log.info("Deszka keresése igény alapján: magassag={}, szelesseg={}, vastagsag={}", 
                igeny.getMagassag(), igeny.getSzelesseg(), igeny.getVastagsag());

        Optional<Deszka> exactMatch = findExactMatch(igeny);
        if (exactMatch.isPresent()) {
            Deszka talalt = exactMatch.get();
            raktarozott.remove(talalt);
            log.info("Pontos egyezés találva: {}x{}x{}", talalt.getMagassag(), talalt.getSzelesseg(), talalt.getVastagsag());
            return talalt;
        }

        Optional<Deszka> cuttableMatch = findCuttableMatch(igeny);
        if (cuttableMatch.isPresent()) {
            Deszka deszka = cuttableMatch.get();
            raktarozott.remove(deszka);
            log.info("Vágható deszka találva: {}x{}x{}", deszka.getMagassag(), deszka.getSzelesseg(), deszka.getVastagsag());
            if (igeny.getMagassag() != null && deszka.getMagassag() > igeny.getMagassag()) {
                log.info("Deszka vágása magasság szerint: {}", igeny.getMagassag());
                List<Deszka> vagottDeszkak = deszka.vagas(igeny.getMagassag());
                Deszka kivett = vagottDeszkak.get(0);
                Deszka maradek = vagottDeszkak.get(1);
                raktarozott.add(maradek);
                log.info("Kivett deszka: {}x{}x{}, maradék visszahelyezve: {}x{}x{}", 
                        kivett.getMagassag(), kivett.getSzelesseg(), kivett.getVastagsag(),
                        maradek.getMagassag(), maradek.getSzelesseg(), maradek.getVastagsag());
                return kivett;
            } else if (igeny.getSzelesseg() != null && deszka.getSzelesseg() > igeny.getSzelesseg()) {
                log.info("Deszka hosszanti vágása szélesség szerint: {}", igeny.getSzelesseg());
                Deszka kivett = deszka.hosszantiVagas(igeny.getSzelesseg());
                raktarozott.add(deszka);
                log.info("Kivett deszka: {}x{}x{}, maradék visszahelyezve: {}x{}x{}", 
                        kivett.getMagassag(), kivett.getSzelesseg(), kivett.getVastagsag(),
                        deszka.getMagassag(), deszka.getSzelesseg(), deszka.getVastagsag());
                return kivett;
            }
        }

        return null;
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
                .findFirst();
    }

    private boolean matchesDimensions(Deszka deszka, DeszkaIgeny igeny) {
        return (igeny.getMagassag() == null || deszka.getMagassag() == igeny.getMagassag()) &&
               (igeny.getSzelesseg() == null || deszka.getSzelesseg() == igeny.getSzelesseg()) &&
               (igeny.getVastagsag() == null || deszka.getVastagsag() == igeny.getVastagsag());
    }

    private boolean matchesSides(Deszka deszka, DeszkaIgeny igeny) {
        return (igeny.getBalOldal() == null || deszka.getBalOldal() == igeny.getBalOldal()) &&
               (igeny.getFelsoOldal() == null || deszka.getFelsoOldal() == igeny.getFelsoOldal()) &&
               (igeny.getJobbOldal() == null || deszka.getJobbOldal() == igeny.getJobbOldal()) &&
               (igeny.getAlsoOldal() == null || deszka.getAlsoOldal() == igeny.getAlsoOldal());
    }

    private boolean canBeCutToMatch(Deszka deszka, DeszkaIgeny igeny) {
        if (igeny.getMagassag() != null && deszka.getMagassag() > igeny.getMagassag()) {
            return true;
        }

        if (igeny.getSzelesseg() != null && deszka.getSzelesseg() > igeny.getSzelesseg()) {
            return true;
        }

        return false;
    }
}
