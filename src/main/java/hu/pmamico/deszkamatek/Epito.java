package hu.pmamico.deszkamatek;


import hu.pmamico.deszkamatek.model.Raktar;
import hu.pmamico.deszkamatek.model.Szoba;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Epito {

    private Raktar raktar;
    private Szoba szoba;


    public void epit(){
        while (!raktar.ures() && !szoba.kesz()){
            log.info("Kurzor: {}",szoba.getKurzor());
            var deszkaigeny = szoba.next();
            log.info("Deszkaigeny: {}",deszkaigeny);
            var deszka = raktar.keres(deszkaigeny, szoba.getKurzor().getEpitesiIrany());
            log.info("Találat: {}",deszka);
            szoba.lerak(deszka);
        }
    }

}
