package hu.pmamico.deszkamatek.service;

import hu.pmamico.deszkamatek.model.Deszka;
import hu.pmamico.deszkamatek.model.OldalAllapot;
import hu.pmamico.deszkamatek.model.Raktar;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class RaktarService {

    private final Raktar raktar = new Raktar();
    private Raktar maradekRaktar = null;

    @PostConstruct
    public void init() {
        log.info("Initializing warehouse with default boards");

        // Add some default boards to the warehouse
        Deszka defaultDeszka = Deszka.builder()
                .szelesseg(15.5)
                .hosszusag(294)
                .build();

        raktar.hozzaad(45, defaultDeszka);

        log.info("Warehouse initialized with {} boards", raktar.getRaktarozott().size());
    }

    /**
     * Returns the editable warehouse instance
     */
    public Raktar getRaktar() {
        return raktar;
    }

    /**
     * Returns the remaining warehouse instance after room building
     * If no room has been built yet, returns null
     */
    public Raktar getMaradekRaktar() {
        return maradekRaktar;
    }

    /**
     * Sets the remaining warehouse
     */
    public void setMaradekRaktar(Raktar maradekRaktar) {
        this.maradekRaktar = maradekRaktar;
    }

    /**
     * Creates a copy of the editable warehouse for building
     */
    public Raktar createRaktarCopy() {
        return raktar.copy();
    }

    /**
     * Adds a board to the warehouse
     * @param deszka the board to add
     * @param darabszam the number of boards to add
     */
    public void hozzaad(Deszka deszka, int darabszam) {
        raktar.hozzaad(darabszam, deszka);
    }

    /**
     * Removes a specific board type from the warehouse
     * @param deszka the board type to remove
     * @param darabszam the number of boards to remove
     * @return true if the boards were removed successfully, false otherwise
     */
    public boolean torol(Deszka deszka, int darabszam) {
        // Get the current count of this board type
        int currentCount = 0;
        for (Map.Entry<Deszka, Integer> entry : getRaktarTartalom().entrySet()) {
            if (isSameBoardType(entry.getKey(), deszka)) {
                currentCount = entry.getValue();
                break;
            }
        }

        // Check if we have enough boards to remove
        if (currentCount < darabszam) {
            return false;
        }

        // Remove the boards
        int removed = 0;
        for (int i = raktar.getRaktarozott().size() - 1; i >= 0 && removed < darabszam; i--) {
            Deszka d = raktar.getRaktarozott().get(i);
            if (isSameBoardType(d, deszka)) {
                raktar.getRaktarozott().remove(i);
                removed++;
            }
        }

        return removed == darabszam;
    }

    /**
     * Returns the contents of the warehouse grouped by board type with counts
     * @return a map of board types to counts
     */
    public Map<Deszka, Integer> getRaktarTartalom() {
        Map<Deszka, Integer> result = new HashMap<>();

        for (Deszka deszka : raktar.getRaktarozott()) {
            boolean found = false;

            // Check if we already have this board type in our result
            for (Map.Entry<Deszka, Integer> entry : result.entrySet()) {
                Deszka key = entry.getKey();
                if (isSameBoardType(key, deszka)) {
                    // Increment count for this board type
                    result.put(key, entry.getValue() + 1);
                    found = true;
                    break;
                }
            }

            // If this is a new board type, add it to the result
            if (!found) {
                result.put(deszka, 1);
            }
        }

        return result;
    }

    /**
     * Checks if two boards are of the same type (same dimensions and sides)
     */
    public boolean isSameBoardType(Deszka a, Deszka b) {
        return Objects.equals(a.getSzelesseg(), b.getSzelesseg()) &&
               Objects.equals(a.getHosszusag(), b.getHosszusag()) &&
               Objects.equals(a.getBalOldal(), b.getBalOldal()) &&
               Objects.equals(a.getFelsoOldal(), b.getFelsoOldal()) &&
               Objects.equals(a.getJobbOldal(), b.getJobbOldal()) &&
               Objects.equals(a.getAlsoOldal(), b.getAlsoOldal());
    }
}
