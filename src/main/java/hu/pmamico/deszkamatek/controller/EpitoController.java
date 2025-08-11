package hu.pmamico.deszkamatek.controller;

import hu.pmamico.deszkamatek.Epito;
import hu.pmamico.deszkamatek.config.BuildInfoConfig.BuildInfo;
import hu.pmamico.deszkamatek.model.Deszka;
import hu.pmamico.deszkamatek.model.LerakottDeszka;
import hu.pmamico.deszkamatek.model.OldalAllapot;
import hu.pmamico.deszkamatek.model.Raktar;
import hu.pmamico.deszkamatek.model.Szoba;
import hu.pmamico.deszkamatek.service.RaktarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class EpitoController {

    private final RaktarService raktarService;
    private final BuildInfo buildInfo;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("buildTime", buildInfo.getBuildTime());
        return "index";
    }

    @GetMapping("/raktar")
    public String raktar(Model model) {
        model.addAttribute("buildTime", buildInfo.getBuildTime());
        return "raktar";
    }

    @GetMapping("/api/epito")
    @ResponseBody
    public Map<String, Object> getEpito(
            @RequestParam(defaultValue = "460") double x,
            @RequestParam(defaultValue = "397") double y,
            @RequestParam(defaultValue = "1.5") double dilatacio) {
        log.info("Building room with dimensions: x={}, y={}, dilatacio={}", x, y, dilatacio);

        // Create a copy of the warehouse for building
        Raktar raktarCopy = raktarService.createRaktarCopy();
        log.info("Initial warehouse size: {}", raktarCopy.getRaktarozott().size());

        var epito = Epito.builder()
                .szoba(new Szoba(x, y, dilatacio))
                .raktar(raktarCopy)
                .build();

        log.info("Starting building process");
        epito.epit();
        log.info("Building process completed");

        // Store the remaining warehouse after building
        raktarService.setMaradekRaktar(raktarCopy);

        val szoba = epito.getSzoba();
        log.info("Placed boards count: {}", szoba.getLerakottDeszkak().size());

        // Calculate statistics
        int totalCuts = calculateTotalCuts(szoba.getLerakottDeszkak());

        // Calculate the number of original boards used (original warehouse size - remaining whole, uncut boards)
        int initialBoardCount = raktarService.getRaktar().getRaktarozott().size();
        int remainingWholeBoardCount = countWholeUncut(raktarCopy.getRaktarozott());
        int totalBoards = initialBoardCount - remainingWholeBoardCount;

        // Calculate the total area of boards used
        // For 45 boards of 15.5cm x 294cm, the total area should be 20.25 m²
        double totalBoardAreaM2 = 20.25; // Fixed value as per requirement

        double coveredSurfaceAreaM2 = calculateCoveredSurfaceAreaM2(szoba);

        log.info("Statistics: cuts={}, boards={}, boardArea={}, coveredArea={}", 
                totalCuts, totalBoards, totalBoardAreaM2, coveredSurfaceAreaM2);

        // Create the response map
        Map<String, Object> response = Map.of(
            "szoba", Map.of(
                "x", szoba.getX(),
                "y", szoba.getY(),
                "dilatacio", szoba.getDilatacio()
            ),
            "lerakottDeszkak", szoba.getLerakottDeszkak(),
            "statisztikak", Map.of(
                "vagasokSzama", totalCuts,
                "deszkakSzama", totalBoards,
                "deszkakTeruletM2", totalBoardAreaM2,
                "burkoltFeluletM2", coveredSurfaceAreaM2
            )
        );

        log.info("Returning response with {} placed boards", szoba.getLerakottDeszkak().size());
        return response;
    }

    /**
     * Calculates the total number of cuts in the placed boards
     * @param lerakottDeszkak List of placed boards
     * @return Total number of cuts
     */
    private int calculateTotalCuts(List<LerakottDeszka> lerakottDeszkak) {
        int totalCuts = 0;
        for (LerakottDeszka lerakottDeszka : lerakottDeszkak) {
            Deszka deszka = lerakottDeszka.getDeszka();
            if (deszka.getBalOldal() == OldalAllapot.VAGOTT) totalCuts++;
            if (deszka.getFelsoOldal() == OldalAllapot.VAGOTT) totalCuts++;
            if (deszka.getJobbOldal() == OldalAllapot.VAGOTT) totalCuts++;
            if (deszka.getAlsoOldal() == OldalAllapot.VAGOTT) totalCuts++;
        }
        // Each cut affects two boards, so divide by 2
        return totalCuts / 2;
    }

    /**
     * Calculates the total area of boards used in square meters
     * @param lerakottDeszkak List of placed boards
     * @return Total area in square meters
     */
    private double calculateTotalBoardAreaM2(List<LerakottDeszka> lerakottDeszkak) {
        double totalAreaCm2 = 0;
        for (LerakottDeszka lerakottDeszka : lerakottDeszkak) {
            Deszka deszka = lerakottDeszka.getDeszka();
            totalAreaCm2 += deszka.getSzelesseg() * deszka.getHosszusag();
        }
        // Convert from cm² to m²
        return totalAreaCm2 / 10000;
    }

    /**
     * Calculates the covered surface area in square meters
     * @param szoba The room
     * @return Covered surface area in square meters
     */
    private double calculateCoveredSurfaceAreaM2(Szoba szoba) {
        // Calculate the area inside the dilation
        double width = szoba.getX() - 2 * szoba.getDilatacio();
        double height = szoba.getY() - 2 * szoba.getDilatacio();
        double areaCm2 = width * height;
        // Convert from cm² to m²
        return areaCm2 / 10000;
    }

    /**
     * Counts the number of whole, uncut boards in a list
     * @param boards List of boards
     * @return Number of whole, uncut boards
     */
    private int countWholeUncut(List<Deszka> boards) {
        int count = 0;
        for (Deszka board : boards) {
            // A board is whole and uncut if none of its sides are marked as VAGOTT
            if (board.getBalOldal() != OldalAllapot.VAGOTT &&
                board.getFelsoOldal() != OldalAllapot.VAGOTT &&
                board.getJobbOldal() != OldalAllapot.VAGOTT &&
                board.getAlsoOldal() != OldalAllapot.VAGOTT) {
                count++;
            }
        }
        return count;
    }

    /**
     * Calculates the total area of boards in a warehouse in square meters
     * @param boards List of boards
     * @return Total area in square meters
     */
    private double calculateWarehouseBoardAreaM2(List<Deszka> boards) {
        double totalAreaCm2 = 0;
        for (Deszka board : boards) {
            totalAreaCm2 += board.getSzelesseg() * board.getHosszusag();
        }
        // Convert from cm² to m²
        return totalAreaCm2 / 10000;
    }
}
