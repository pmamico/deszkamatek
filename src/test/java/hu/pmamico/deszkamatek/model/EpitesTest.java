package hu.pmamico.deszkamatek.model;

import hu.pmamico.deszkamatek.Epito;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
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

    @Test
    public void testBothTopAndBottomCutBoardsAreExcluded() {
        // Create a warehouse with one normal board and one board with both top and bottom sides cut
        Raktar raktar = new Raktar();

        // Normal board
        Deszka normalDeszka = Deszka.builder()
                .szelesseg(10)
                .hosszusag(10)
                .build();

        // Board with both top and bottom sides cut
        Deszka bothCutDeszka = Deszka.builder()
                .szelesseg(10)
                .hosszusag(10)
                .felsoOldal(OldalAllapot.VAGOTT)
                .alsoOldal(OldalAllapot.VAGOTT)
                .build();

        // Add both boards to the warehouse
        raktar.hozzaad(normalDeszka);
        raktar.hozzaad(bothCutDeszka);

        // Verify that the warehouse has 2 boards initially
        assertEquals(2, raktar.getRaktarozott().size(), "Warehouse should have 2 boards initially");

        // Create a requirement that both boards would match dimensionally
        DeszkaIgeny igeny = DeszkaIgeny.builder()
                .x(10.0)
                .y(10.0)
                .build();

        // Search for a board that matches the requirement
        Deszka foundDeszka = raktar.keres(igeny, LerakasIrany.ESZAK);

        // Verify that the normal board was found and used
        assertEquals(10.0, foundDeszka.getSzelesseg(), "The found board should have width 10.0");
        assertEquals(10.0, foundDeszka.getHosszusag(), "The found board should have length 10.0");
        assertNotEquals(OldalAllapot.VAGOTT, foundDeszka.getFelsoOldal(), "The found board should not have top side cut");
        assertNotEquals(OldalAllapot.VAGOTT, foundDeszka.getAlsoOldal(), "The found board should not have bottom side cut");

        // Verify that only the board with both top and bottom sides cut remains in the warehouse
        assertEquals(1, raktar.getRaktarozott().size(), "One board should remain in the warehouse");

        // Verify that the remaining board is the one with both top and bottom sides cut
        Deszka remainingDeszka = raktar.getRaktarozott().get(0);
        assertEquals(OldalAllapot.VAGOTT, remainingDeszka.getFelsoOldal(), "The remaining board should have top side cut");
        assertEquals(OldalAllapot.VAGOTT, remainingDeszka.getAlsoOldal(), "The remaining board should have bottom side cut");

        log.info("Test passed: Board with both top and bottom sides cut was excluded from search");
    }

    @Test
    public void testSorszamAssignment() {
        // Create a room
        Szoba szoba = new Szoba(100, 100);

        // Create three boards
        Deszka deszka1 = Deszka.builder()
                .szelesseg(10)
                .hosszusag(10)
                .build();

        Deszka deszka2 = Deszka.builder()
                .szelesseg(10)
                .hosszusag(20)
                .build();

        Deszka deszka3 = Deszka.builder()
                .szelesseg(10)
                .hosszusag(30)
                .build();

        // Place the boards
        szoba.lerak(deszka1);
        szoba.lerak(deszka2);
        szoba.lerak(deszka3);

        // Get the placed boards
        List<LerakottDeszka> lerakottDeszkak = szoba.getLerakottDeszkak();

        // Verify that three boards were placed
        assertEquals(3, lerakottDeszkak.size(), "Three boards should have been placed");

        // Verify that the sequence numbers are assigned correctly
        assertEquals(1, lerakottDeszkak.get(0).getSorszam(), "First board should have sequence number 1");
        assertEquals(2, lerakottDeszkak.get(1).getSorszam(), "Second board should have sequence number 2");
        assertEquals(3, lerakottDeszkak.get(2).getSorszam(), "Third board should have sequence number 3");

        log.info("Test passed: Sequence numbers are assigned correctly");
    }
}
