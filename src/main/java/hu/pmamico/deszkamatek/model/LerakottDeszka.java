package hu.pmamico.deszkamatek.model;

import lombok.Data;

@Data
public class LerakottDeszka {

    private Poz poz;
    private Deszka deszka;

    public LerakottDeszka(Kurzor kurzor, Deszka deszka) {
        this.poz = new Poz(kurzor, deszka);
        this.deszka = deszka;
    }

    @Override
    public String toString() {
        return "Lerakott deszka: " +
                poz.getX()+"-"+poz.getY()+": "+deszka.getHosszusag();

    }
}
