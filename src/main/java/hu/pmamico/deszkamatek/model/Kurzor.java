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
        // Apply dilation to the initial position - start from the effective room size
        this.x = szoba.getX() - szoba.getDilatacio();
        this.y = szoba.getY() - szoba.getDilatacio();
        this.epitesiIrany = LerakasIrany.ESZAK;
    }

    public void mozgat(Szoba szoba, Deszka deszka){
        switch (epitesiIrany) {
            case ESZAK:
                this.y = this.y - deszka.getHosszusag();
                // Check if cursor reached the bottom edge with dilation
                if (this.y <= szoba.getDilatacio()){
                    this.y = szoba.getDilatacio(); // Ensure cursor doesn't go below dilation
                    oszlopKesz(deszka);
                }
                break;
            case DEL:
                this.y = this.y + deszka.getHosszusag();
                // Check if cursor reached the top edge with dilation
                if (this.y >= szoba.getY() - szoba.getDilatacio()){
                    this.y = szoba.getY() - szoba.getDilatacio(); // Ensure cursor doesn't go above effective top edge
                    oszlopKesz(deszka);
                }
        }
    }
}
