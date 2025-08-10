package hu.pmamico.deszkamatek.model;

import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.ArrayList;

@Data
@Slf4j
public class Deszka {

    private double magassag;
    private double szelesseg;
    private double vastagsag;

    private OldalAllapot balOldal = OldalAllapot.CSAP;
    private OldalAllapot felsoOldal = OldalAllapot.CSAP;
    private OldalAllapot jobbOldal = OldalAllapot.NUT;
    private OldalAllapot alsoOldal = OldalAllapot.NUT;
    public void setNutBalOldal(boolean nut) {
        this.balOldal = nut ? OldalAllapot.NUT : OldalAllapot.CSAP;
    }

    public void setCsapBalOldal(boolean csap) {
        this.balOldal = csap ? OldalAllapot.CSAP : OldalAllapot.NUT;
    }

    public void setNutFelsoOldal(boolean nut) {
        this.felsoOldal = nut ? OldalAllapot.NUT : OldalAllapot.CSAP;
    }

    public void setCsapFelsoOldal(boolean csap) {
        this.felsoOldal = csap ? OldalAllapot.CSAP : OldalAllapot.NUT;
    }

    public void setNutJobbOldal(boolean nut) {
        this.jobbOldal = nut ? OldalAllapot.NUT : OldalAllapot.CSAP;
    }

    public void setCsapJobbOldal(boolean csap) {
        this.jobbOldal = csap ? OldalAllapot.CSAP : OldalAllapot.NUT;
    }

    public void setNutAlsoOldal(boolean nut) {
        this.alsoOldal = nut ? OldalAllapot.NUT : OldalAllapot.CSAP;
    }

    public void setCsapAlsoOldal(boolean csap) {
        this.alsoOldal = csap ? OldalAllapot.CSAP : OldalAllapot.NUT;
    }

    public Deszka(double magassag, double szelesseg, double vastagsag) {
        this.magassag = magassag;
        this.szelesseg = szelesseg;
        this.vastagsag = vastagsag;
    }

    public List<Deszka> vagas(double hossz) {
        log.info("Deszka vágása: hossz={}, eredeti méret: {}x{}x{}", hossz, magassag, szelesseg, vastagsag);
        if (hossz <= 0 || hossz >= magassag) {
            log.error("Érvénytelen vágási hossz: {}", hossz);
            throw new IllegalArgumentException("A vágási hossz érvénytelen");
        }

        List<Deszka> eredmeny = new ArrayList<>();

        Deszka elsoResz = new Deszka(hossz, szelesseg, vastagsag);
        elsoResz.setBalOldal(this.balOldal);
        elsoResz.setJobbOldal(this.jobbOldal);
        elsoResz.setFelsoOldal(OldalAllapot.VAGOTT);
        elsoResz.setAlsoOldal(this.alsoOldal);

        Deszka masodikResz = new Deszka(magassag - hossz, szelesseg, vastagsag);
        masodikResz.setBalOldal(this.balOldal);
        masodikResz.setJobbOldal(this.jobbOldal);
        masodikResz.setFelsoOldal(this.felsoOldal);
        masodikResz.setAlsoOldal(OldalAllapot.VAGOTT);

        eredmeny.add(elsoResz);
        eredmeny.add(masodikResz);

        log.info("Vágás eredménye: első rész: {}x{}x{}, második rész: {}x{}x{}", 
                elsoResz.getMagassag(), elsoResz.getSzelesseg(), elsoResz.getVastagsag(),
                masodikResz.getMagassag(), masodikResz.getSzelesseg(), masodikResz.getVastagsag());
        return eredmeny;
    }

    public Deszka hosszantiVagas(double ujSzelesseg) {
        log.info("Hosszanti vágás: új szélesség={}, eredeti méret: {}x{}x{}", ujSzelesseg, magassag, szelesseg, vastagsag);
        if (ujSzelesseg <= 0 || ujSzelesseg >= szelesseg) {
            log.error("Érvénytelen új szélesség: {}", ujSzelesseg);
            throw new IllegalArgumentException("Az új szélesség érvénytelen");
        }

        Deszka ujDeszka = new Deszka(magassag, ujSzelesseg, vastagsag);
        ujDeszka.setBalOldal(this.balOldal);
        ujDeszka.setJobbOldal(OldalAllapot.VAGOTT);
        ujDeszka.setFelsoOldal(this.felsoOldal);
        ujDeszka.setAlsoOldal(this.alsoOldal);

        this.szelesseg = this.szelesseg - ujSzelesseg;
        this.balOldal = OldalAllapot.VAGOTT;

        log.info("Hosszanti vágás eredménye: új deszka: {}x{}x{}, maradék: {}x{}x{}", 
                ujDeszka.getMagassag(), ujDeszka.getSzelesseg(), ujDeszka.getVastagsag(),
                this.magassag, this.szelesseg, this.vastagsag);
        return ujDeszka;
    }
}
