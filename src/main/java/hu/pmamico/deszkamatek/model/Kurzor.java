package hu.pmamico.deszkamatek.model;

import lombok.Data;

@Data
public class Kurzor {

    private double x;
    private double y;
    private LerakasIrany epitesiIrany;

    public void oszlopKesz(Deszka deszka, Szoba szoba){
        if(epitesiIrany == LerakasIrany.DEL) {
            epitesiIrany= LerakasIrany.ESZAK;
        } else {
            epitesiIrany= LerakasIrany.DEL;
        }

        // If deszka is null, use the standard width from the room
        double szelesseg = (deszka != null) ? deszka.getSzelesseg() : szoba.getStandardSzelesseg();
        this.x = this.x - szelesseg;
    }

    public Kurzor(Szoba szoba) {
        // Apply dilation to the initial position - start from the effective room size
        this.x = szoba.getX() - szoba.getDilatacio();
        this.y = szoba.getY() - szoba.getDilatacio();
        this.epitesiIrany = LerakasIrany.ESZAK;
    }

    public void mozgat(Szoba szoba, Deszka deszka){
        // Default movement value when deszka is null
        double defaultMovement = 15.0; // A reasonable default value

        switch (epitesiIrany) {
            case ESZAK:
                // If deszka is null, use default movement
                double moveY = (deszka != null) ? deszka.getHosszusag() : defaultMovement;
                this.y = this.y - moveY;

                // Check if cursor reached the bottom edge with dilation
                if (this.y <= szoba.getDilatacio()){
                    this.y = szoba.getDilatacio(); // Ensure cursor doesn't go below dilation
                    oszlopKesz(deszka, szoba);
                }
                break;
            case DEL:
                // If deszka is null, use default movement
                double moveYDel = (deszka != null) ? deszka.getHosszusag() : defaultMovement;
                this.y = this.y + moveYDel;

                // Check if cursor reached the top edge with dilation
                if (this.y >= szoba.getY() - szoba.getDilatacio()){
                    this.y = szoba.getY() - szoba.getDilatacio(); // Ensure cursor doesn't go above effective top edge
                    oszlopKesz(deszka, szoba);
                }
        }
    }
}
