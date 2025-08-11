package hu.pmamico.deszkamatek.model;

import lombok.Data;

@Data
public class LerakottDeszka {

    private Poz poz;
    private Deszka deszka;
    private int sorszam;

    public LerakottDeszka(Kurzor kurzor, Deszka deszka, int sorszam) {
        this.poz = new Poz(kurzor, deszka);
        this.deszka = deszka;
        this.sorszam = sorszam;
    }

    @Override
    public String toString() {
        return "Lerakott deszka #" + sorszam + ": " +
                poz.getX()+"-"+poz.getY()+": "+deszka.getHosszusag();

    }
}
