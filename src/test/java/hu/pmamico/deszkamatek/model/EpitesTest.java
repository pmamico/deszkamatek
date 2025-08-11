package hu.pmamico.deszkamatek.model;

import hu.pmamico.deszkamatek.Epito;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
@Slf4j
public class EpitesTest {

    @Test
    public void test() {
        var epito = Epito.builder()
                        .szoba(new Szoba(100, 100))
                        .raktar(Raktar.build(10,
                                Deszka.builder()
                                  .szelesseg(10)
                                  .hosszusag(10)
                        .build()))
                .build();

        log.info(String.valueOf(epito.getRaktar()));
        epito.epit();

        log.info(String.valueOf(epito.getRaktar().getRaktarozott().size()));
    }

    @Test
    public void real() {
        var epito = Epito.builder()
                .szoba(new Szoba(460, 397))
                .raktar(Raktar.build(45,
                        Deszka.builder()
                                .szelesseg(15.5)
                                .hosszusag(294)
                                .build()))
                .build();

        log.info(String.valueOf(epito.getRaktar()));
        epito.epit();

        log.info("marad: "+epito.getRaktar().getRaktarozott().size());
        log.info("marad: "+epito.getRaktar());
        log.info("kesz? " + epito.getSzoba().kesz());
    }
}
