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
    private double vastagsag;

    private OldalAllapot balOldal = OldalAllapot.CSAP;
    private OldalAllapot felsoOldal = OldalAllapot.CSAP;
    private OldalAllapot jobbOldal = OldalAllapot.NUT;
    private OldalAllapot alsoOldal = OldalAllapot.NUT;

    public List<Deszka> vagas(double hossz) {
        log.info("Deszka vágása: hossz={}, eredeti méret: {}x{}x{}", hossz, szelesseg, hosszusag, vastagsag);
        if (hossz <= 0 || hossz >= hosszusag) {
            log.error("Érvénytelen vágási hossz: {}", hossz);
            throw new IllegalArgumentException("A vágási hossz érvénytelen");
        }

        List<Deszka> eredmeny = new ArrayList<>();

        Deszka elsoResz = Deszka.builder()
                .szelesseg(this.szelesseg)
                .hosszusag(hossz)
                .vastagsag(this.vastagsag)
                .balOldal(this.balOldal)
                .jobbOldal(this.jobbOldal)
                .alsoOldal(this.alsoOldal)
                .felsoOldal(OldalAllapot.VAGOTT)
                .build();

        Deszka masodikResz = Deszka.builder()
                .szelesseg(this.szelesseg)
                .hosszusag(this.hosszusag - hossz)
                .vastagsag(this.vastagsag)
                .balOldal(this.balOldal)
                .jobbOldal(this.jobbOldal)
                .alsoOldal(OldalAllapot.VAGOTT)
                .felsoOldal(this.felsoOldal)
                .build();

        eredmeny.add(elsoResz);
        eredmeny.add(masodikResz);

        return eredmeny;
    }
}
