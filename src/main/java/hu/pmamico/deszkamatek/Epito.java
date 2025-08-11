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
        log.info("Starting building process with warehouse size: {}", raktar.getRaktarozott().size());
        int boardsPlaced = 0;

        while (!raktar.ures() && !szoba.kesz()){
            log.info("Kurzor: {}, Room complete: {}, Warehouse empty: {}", 
                    szoba.getKurzor(), szoba.kesz(), raktar.ures());

            var deszkaigeny = szoba.next();
            log.info("Deszkaigeny: {}", deszkaigeny);

            var deszka = raktar.keres(deszkaigeny, szoba.getKurzor().getEpitesiIrany());
            log.info("Találat: {}", deszka);

            if (deszka != null) {
                // Only place the board if a suitable one was found
                szoba.lerak(deszka);
                boardsPlaced++;
                log.info("Board placed. Total boards placed: {}", boardsPlaced);
            } else {
                // If no suitable board was found, move the cursor to the next position
                // without placing a board
                log.info("Nincs megfelelő deszka, továbblépés a következő pozícióra");
                szoba.getKurzor().mozgat(szoba, null);
            }
        }

        log.info("Building completed. Total boards placed: {}, Room complete: {}, Warehouse empty: {}", 
                boardsPlaced, szoba.kesz(), raktar.ures());
        log.info("Final cursor position: {}", szoba.getKurzor());
        log.info("Placed boards: {}", szoba.getLerakottDeszkak().size());
    }

}
