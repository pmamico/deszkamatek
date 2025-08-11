package hu.pmamico.deszkamatek.model;

import lombok.Data;

@Data
public class Kurzor {

    private double x;
    private double y;
    private LerakasIrany epitesiIrany;

    public void oszlopKesz(Deszka deszka){
        if(epitesiIrany == LerakasIrany.DEL) {
            epitesiIrany= LerakasIrany.ESZAK;
        } else {
            epitesiIrany= LerakasIrany.DEL;
        }
        this.x = this.x - deszka.getSzelesseg();
    }

    public Kurzor(Szoba szoba) {
        this.x = szoba.getX();
        this.y = szoba.getY();
        this.epitesiIrany = LerakasIrany.ESZAK;
    }

    public void mozgat(Szoba szoba, Deszka deszka){
        switch (epitesiIrany) {
            case ESZAK:
                this.y = this.y - deszka.getHosszusag();
                if (this.y == 0){
                    oszlopKesz(deszka);
                }
                break;
            case DEL:
                this.y = this.y + deszka.getHosszusag();
                if (this.y == szoba.getY()){
                    oszlopKesz(deszka);
                }
        }
    }
}
