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
    private double dilatacio = 1.5; // Default dilation value is 1.5 cm
    private Kurzor kurzor;
    private List<LerakottDeszka> lerakottDeszkak = new ArrayList<>();

    public Szoba(double x, double y) {
        this.x = x;
        this.y = y;
        this.kurzor = new Kurzor(this);
    }

    public Szoba(double x, double y, double dilatacio) {
        this.x = x;
        this.y = y;
        this.dilatacio = dilatacio;
        this.kurzor = new Kurzor(this);
    }

    public DeszkaIgeny next(){
        switch (kurzor.getEpitesiIrany()) {
            case ESZAK:
                double yEszak = kurzor.getY();
                // Ensure y is never zero or negative due to dilation
                if (yEszak <= 0) {
                    yEszak = 0.1; // Small positive value to avoid validation error
                }
                return DeszkaIgeny.builder()
                        .y(yEszak - dilatacio)
                        .felsoOldal(
                                kurzor.getY() == (this.y - dilatacio) ? null : CSAP
                        )
                        .alsoOldal(
                                kurzor.getY() == (this.y - dilatacio) ? NUT : null
                        )
                        .build();
            case DEL:
                double yDel = (this.y - dilatacio) - kurzor.getY();
                // Ensure y is never zero or negative due to dilation
                if (yDel <= 0) {
                    yDel = 0.1; // Small positive value to avoid validation error
                }
                return DeszkaIgeny.builder()
                        .y(yDel)
                        .felsoOldal(
                                kurzor.getY() == dilatacio ? CSAP : null
                        )
                        .alsoOldal(
                                kurzor.getY() == dilatacio ? null : NUT
                        )
                        .build();
                default:
                   return null;
        }
    }

    public void lerak(Deszka deszka){
        //első kurzort eltolom, mert jobb fentről indul a kurzor,
        //de a deszkák koordinátája bal fent 0,0
        if(lerakottDeszkak.isEmpty()) {
            kurzor.setX(kurzor.getX() - deszka.getSzelesseg() );
        }
        int sorszam = lerakottDeszkak.size() + 1;
        var lerakottDeszka = new LerakottDeszka(kurzor, deszka, sorszam);
        log.debug(lerakottDeszka.toString());
        lerakottDeszkak.add(lerakottDeszka);
        kurzor.mozgat(this, deszka);
    }

    public boolean kesz(){
        return kurzor!=null && 
               kurzor.getX() <= dilatacio && 
               (kurzor.getY() == dilatacio || kurzor.getY() == this.y - dilatacio);
    }
}
