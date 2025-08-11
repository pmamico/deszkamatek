package hu.pmamico.deszkamatek.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static hu.pmamico.deszkamatek.model.OldalAllapot.CSAP;
import static hu.pmamico.deszkamatek.model.OldalAllapot.NUT;

@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Szoba {
    private double x;
    private double y;
    private Kurzor kurzor;
    private List<LerakottDeszka> lerakottDeszkak = new ArrayList<>();

    public Szoba(double x, double y) {
        this.x = x;
        this.y = y;
        this.kurzor = new Kurzor(this);
    }

    public DeszkaIgeny next(){
        switch (kurzor.getEpitesiIrany()) {
            case ESZAK:
                return DeszkaIgeny.builder()
                        .y(kurzor.getY())
                        .felsoOldal(
                                kurzor.getY() == this.y ? null : CSAP
                        )
                        .alsoOldal(
                                kurzor.getY() == this.y ? NUT : null
                        )
                        .build();
            case DEL:
                return DeszkaIgeny.builder()
                        .y(this.y - kurzor.getY())
                        .felsoOldal(
                                kurzor.getY() == 0 ? CSAP : null
                        )
                        .alsoOldal(
                                kurzor.getY() == 0 ? null : NUT
                        )
                        .build();
                default:
                   return null;
        }
    }

    public void lerak(Deszka deszka){
        var lerakottDeszka = new LerakottDeszka(kurzor, deszka);
        log.debug(lerakottDeszka.toString());
        lerakottDeszkak.add(lerakottDeszka);
        kurzor.mozgat(this, deszka);
    }

    public boolean kesz(){
        return kurzor!=null && kurzor.getX() <= 0 && (kurzor.getY() == 0 || kurzor.getY() == this.y);
    }
}
