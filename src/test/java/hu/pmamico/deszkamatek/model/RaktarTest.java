package hu.pmamico.deszkamatek.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class RaktarTest {

    private Raktar raktar;

    @BeforeEach
    void setUp() {
        raktar = new Raktar();
    }

    @Test
    void testHozzaad() {
        Deszka deszka = new Deszka(100, 20, 2);
        raktar.hozzaad(deszka);
        
        assertThat(raktar.getRaktarozott()).hasSize(1);
        assertThat(raktar.getRaktarozott().get(0)).isEqualTo(deszka);
    }

    @Test
    void testKeresExactMatch() {
        // Add a board to the warehouse
        Deszka deszka = new Deszka(100, 20, 2);
        raktar.hozzaad(deszka);
        
        // Create a requirement that matches exactly
        DeszkaIgeny igeny = DeszkaIgeny.builder()
                .magassag(100.0)
                .szelesseg(20.0)
                .build();
        
        // Search for a board that matches the requirement
        Deszka talalt = raktar.keres(igeny);
        
        // Verify that the found board matches the requirement
        assertThat(talalt).isNotNull();
        assertThat(talalt.getMagassag()).isEqualTo(100);
        assertThat(talalt.getSzelesseg()).isEqualTo(20);
        
        // Verify that the board was removed from the warehouse
        assertThat(raktar.getRaktarozott()).isEmpty();
    }

    @Test
    void testKeresNullRequirements() {
        // Add a board to the warehouse
        Deszka deszka = new Deszka(100, 20, 2);
        raktar.hozzaad(deszka);
        
        // Create a requirement with null values (any board will match)
        DeszkaIgeny igeny = DeszkaIgeny.builder().build();
        
        // Search for a board that matches the requirement
        Deszka talalt = raktar.keres(igeny);
        
        // Verify that the found board is the one we added
        assertThat(talalt).isEqualTo(deszka);
        
        // Verify that the board was removed from the warehouse
        assertThat(raktar.getRaktarozott()).isEmpty();
    }

    @Test
    void testKeresNoMatch() {
        // Add a board to the warehouse
        Deszka deszka = new Deszka(100, 20, 2);
        raktar.hozzaad(deszka);
        
        // Create a requirement that doesn't match
        DeszkaIgeny igeny = DeszkaIgeny.builder()
                .magassag(150.0)
                .szelesseg(30.0)
                .build();
        
        // Search for a board that matches the requirement
        Deszka talalt = raktar.keres(igeny);
        
        // Verify that no board was found
        assertThat(talalt).isNull();
        
        // Verify that the warehouse is unchanged
        assertThat(raktar.getRaktarozott()).hasSize(1);
        assertThat(raktar.getRaktarozott().get(0)).isEqualTo(deszka);
    }

    @Test
    void testKeresCutHeight() {
        // Add a board to the warehouse
        Deszka deszka = new Deszka(200, 20, 2);
        raktar.hozzaad(deszka);
        
        // Create a requirement for a shorter board
        DeszkaIgeny igeny = DeszkaIgeny.builder()
                .magassag(110.0)
                .build();
        
        // Search for a board that matches the requirement
        Deszka talalt = raktar.keres(igeny);
        
        // Verify that a board was found and cut to the right size
        assertThat(talalt).isNotNull();
        assertThat(talalt.getMagassag()).isEqualTo(110);
        assertThat(talalt.getSzelesseg()).isEqualTo(20);
        
        // Verify that the remaining piece was put back in the warehouse
        assertThat(raktar.getRaktarozott()).hasSize(1);
        assertThat(raktar.getRaktarozott().get(0).getMagassag()).isEqualTo(90);
        assertThat(raktar.getRaktarozott().get(0).getSzelesseg()).isEqualTo(20);
    }

    @Test
    void testKeresCutWidth() {
        // Add a board to the warehouse
        Deszka deszka = new Deszka(100, 20, 2);
        raktar.hozzaad(deszka);
        
        // Create a requirement for a narrower board
        DeszkaIgeny igeny = DeszkaIgeny.builder()
                .szelesseg(8.0)
                .build();
        
        // Search for a board that matches the requirement
        Deszka talalt = raktar.keres(igeny);
        
        // Verify that a board was found and cut to the right size
        assertThat(talalt).isNotNull();
        assertThat(talalt.getMagassag()).isEqualTo(100);
        assertThat(talalt.getSzelesseg()).isEqualTo(8);
        
        // Verify that the remaining piece was put back in the warehouse
        assertThat(raktar.getRaktarozott()).hasSize(1);
        assertThat(raktar.getRaktarozott().get(0).getMagassag()).isEqualTo(100);
        assertThat(raktar.getRaktarozott().get(0).getSzelesseg()).isEqualTo(12);
    }

    @Test
    void testExampleScenario() {
        // Add two boards to the warehouse: 200 cm and 100 cm
        Deszka deszka1 = new Deszka(200, 20, 2);
        Deszka deszka2 = new Deszka(100, 20, 2);
        raktar.hozzaad(deszka1);
        raktar.hozzaad(deszka2);
        
        // Case 1: Requirement for 100 cm - should return the 100 cm board
        DeszkaIgeny igeny1 = DeszkaIgeny.builder()
                .magassag(100.0)
                .build();
        
        Deszka talalt1 = raktar.keres(igeny1);
        
        assertThat(talalt1).isNotNull();
        assertThat(talalt1.getMagassag()).isEqualTo(100);
        assertThat(raktar.getRaktarozott()).hasSize(1);
        assertThat(raktar.getRaktarozott().get(0).getMagassag()).isEqualTo(200);
        
        // Case 2: Requirement for 110 cm - should cut the 200 cm board
        DeszkaIgeny igeny2 = DeszkaIgeny.builder()
                .magassag(110.0)
                .build();
        
        Deszka talalt2 = raktar.keres(igeny2);
        
        assertThat(talalt2).isNotNull();
        assertThat(talalt2.getMagassag()).isEqualTo(110);
        assertThat(raktar.getRaktarozott()).hasSize(1);
        assertThat(raktar.getRaktarozott().get(0).getMagassag()).isEqualTo(90);
    }
}