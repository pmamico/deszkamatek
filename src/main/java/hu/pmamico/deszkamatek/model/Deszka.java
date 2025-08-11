package hu.pmamico.deszkamatek.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.ArrayList;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class Deszka {

    private double szelesseg;
    private double hosszusag;

    @Builder.Default
    private OldalAllapot balOldal = OldalAllapot.CSAP;
    @Builder.Default
    private OldalAllapot felsoOldal = OldalAllapot.CSAP;
    @Builder.Default
    private OldalAllapot jobbOldal = OldalAllapot.NUT;
    @Builder.Default
    private OldalAllapot alsoOldal = OldalAllapot.NUT;

    public List<Deszka> vagas(double hossz, LerakasIrany lerakasIrany) {
        log.info("Deszka vágása: hossz={}, eredeti méret: {}x{}", hossz, szelesseg, hosszusag);
        if (hossz <= 0 || hossz >= hosszusag) {
            log.error("Érvénytelen vágási hossz: {}", hossz);
            throw new IllegalArgumentException("A vágási hossz érvénytelen");
        }

        List<Deszka> eredmeny = new ArrayList<>();

        final boolean alulVagunk = (lerakasIrany == LerakasIrany.DEL);

        final double alsoHossz = alulVagunk ? hossz : this.hosszusag - hossz;
        final double felsoHossz = alulVagunk ? this.hosszusag - hossz : hossz;

        Deszka also = buildDeszka(alsoHossz, this.alsoOldal, OldalAllapot.VAGOTT);
        Deszka felso = buildDeszka(felsoHossz, OldalAllapot.VAGOTT, this.felsoOldal);

        eredmeny.add(also);
        eredmeny.add(felso);

        if(!alulVagunk) {
            eredmeny = eredmeny.reversed();
        }


        return eredmeny;
    }

    public List<Deszka> hosszantiVagas(double szelesseg, boolean jobbOldalMarad) {
        log.info("Deszka vágása: széles={}, eredeti méret: {}x{}", szelesseg, szelesseg, hosszusag);

        if (szelesseg <= 0) {
           szelesseg = Math.abs(szelesseg);
           jobbOldalMarad = !jobbOldalMarad;
        }

        List<Deszka> eredmeny = new ArrayList<>();


        final double jobbSzeles = (jobbOldalMarad ? szelesseg : this.szelesseg - szelesseg);
        final double balSzeles = (jobbOldalMarad ? this.szelesseg - szelesseg : szelesseg);

        Deszka jobb = buildKeskenyDeszka(jobbSzeles, this.jobbOldal, OldalAllapot.VAGOTT);
        Deszka bal = buildKeskenyDeszka(balSzeles, OldalAllapot.VAGOTT, this.balOldal);

        eredmeny.add(jobb);
        eredmeny.add(bal);

        if(!jobbOldalMarad) {
            eredmeny = eredmeny.reversed();
        }

        return eredmeny;
    }

    private Deszka buildDeszka(double hossz,
                               OldalAllapot alsoOldal,
                               OldalAllapot felsoOldal) {
        return Deszka.builder()
                .szelesseg(this.szelesseg)
                .hosszusag(hossz)
                .balOldal(this.balOldal)
                .jobbOldal(this.jobbOldal)
                .alsoOldal(alsoOldal)
                .felsoOldal(felsoOldal)
                .build();
    }

    private Deszka buildKeskenyDeszka(double szelesseg,
                               OldalAllapot jobbOldal,
                               OldalAllapot balOldal) {
        return Deszka.builder()
                .szelesseg(szelesseg)
                .hosszusag(this.hosszusag)
                .balOldal(balOldal)
                .jobbOldal(jobbOldal)
                .alsoOldal(this.alsoOldal)
                .felsoOldal(this.felsoOldal)
                .build();
    }

    public boolean hosszabanVagott(){
        return (this.balOldal == OldalAllapot.VAGOTT || this.jobbOldal == OldalAllapot.VAGOTT);
    }

}
