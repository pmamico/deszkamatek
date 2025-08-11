package hu.pmamico.deszkamatek.model;

import lombok.Data;

@Data
public class Poz {
    private double x;
    private double y;

    public Poz(Kurzor kurzor, Deszka deszka, double dilatacio) {
        if (kurzor.getEpitesiIrany().equals(LerakasIrany.ESZAK)) {
            this.x = Math.max(kurzor.getX(), dilatacio);
            this.y = kurzor.getY();
        } else {
            this.x = Math.max(kurzor.getX(), dilatacio);
            this.y = kurzor.getY() + deszka.getHosszusag();
        }
    }
}
