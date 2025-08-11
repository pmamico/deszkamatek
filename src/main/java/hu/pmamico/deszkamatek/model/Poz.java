package hu.pmamico.deszkamatek.model;

import lombok.Data;

@Data
public class Poz {
    private double x;
    private double y;

    public Poz(Kurzor kurzor, Deszka deszka) {
        if (kurzor.getEpitesiIrany().equals(LerakasIrany.ESZAK)) {
            this.x = kurzor.getX();
            this.y = kurzor.getY();
        } else {
            this.x = kurzor.getX();
            this.y = kurzor.getY() + deszka.getHosszusag();
        }
    }
}
