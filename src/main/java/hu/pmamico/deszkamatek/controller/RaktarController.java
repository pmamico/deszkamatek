package hu.pmamico.deszkamatek.controller;

import hu.pmamico.deszkamatek.model.Deszka;
import hu.pmamico.deszkamatek.model.OldalAllapot;
import hu.pmamico.deszkamatek.model.Raktar;
import hu.pmamico.deszkamatek.service.RaktarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/raktar")
@RequiredArgsConstructor
public class RaktarController {

    private final RaktarService raktarService;

    /**
     * Get the contents of the editable warehouse
     * @return a map of board types to counts
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getRaktarTartalom() {
        Map<Deszka, Integer> raktarTartalom = raktarService.getRaktarTartalom();
        return convertRaktarTartalomToResponse(raktarTartalom);
    }

    /**
     * Get the contents of the remaining warehouse after room building
     * @return a map of board types to counts, or an empty map if no room has been built
     */
    @GetMapping("/maradek")
    public ResponseEntity<Map<String, Object>> getMaradekRaktarTartalom() {
        Raktar maradekRaktar = raktarService.getMaradekRaktar();
        if (maradekRaktar == null) {
            return ResponseEntity.ok(Map.of());
        }

        // Convert the remaining warehouse contents to a map of board types to counts
        Map<Deszka, Integer> maradekTartalom = new HashMap<>();
        for (Deszka deszka : maradekRaktar.getRaktarozott()) {
            boolean found = false;
            for (Map.Entry<Deszka, Integer> entry : maradekTartalom.entrySet()) {
                Deszka key = entry.getKey();
                if (raktarService.isSameBoardType(key, deszka)) {
                    maradekTartalom.put(key, entry.getValue() + 1);
                    found = true;
                    break;
                }
            }
            if (!found) {
                maradekTartalom.put(deszka, 1);
            }
        }

        return convertRaktarTartalomToResponse(maradekTartalom);
    }

    /**
     * Helper method to convert warehouse contents to a JSON response
     */
    private ResponseEntity<Map<String, Object>> convertRaktarTartalomToResponse(Map<Deszka, Integer> raktarTartalom) {
        // Convert to a format suitable for JSON response
        Map<String, Object> response = new HashMap<>();
        int index = 0;

        for (Map.Entry<Deszka, Integer> entry : raktarTartalom.entrySet()) {
            Deszka deszka = entry.getKey();
            Integer darabszam = entry.getValue();

            Map<String, Object> deszkaInfo = new HashMap<>();
            deszkaInfo.put("szelesseg", deszka.getSzelesseg());
            deszkaInfo.put("hosszusag", deszka.getHosszusag());
            deszkaInfo.put("balOldal", deszka.getBalOldal());
            deszkaInfo.put("felsoOldal", deszka.getFelsoOldal());
            deszkaInfo.put("jobbOldal", deszka.getJobbOldal());
            deszkaInfo.put("alsoOldal", deszka.getAlsoOldal());
            deszkaInfo.put("darabszam", darabszam);

            response.put("deszka_" + index, deszkaInfo);
            index++;
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Add boards to the warehouse
     * @param request the request containing board details and count
     * @return a response indicating success
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> hozzaadDeszka(@RequestBody DeszkaHozzaadasRequest request) {
        log.info("Deszka hozzáadása: {}", request);

        Deszka deszka = Deszka.builder()
                .szelesseg(request.getSzelesseg())
                .hosszusag(request.getHosszusag())
                .balOldal(request.getBalOldal())
                .felsoOldal(request.getFelsoOldal())
                .jobbOldal(request.getJobbOldal())
                .alsoOldal(request.getAlsoOldal())
                .build();

        raktarService.hozzaad(deszka, request.getDarabszam());

        return ResponseEntity.ok(Map.of("message", "Deszka sikeresen hozzáadva a raktárhoz"));
    }

    /**
     * Delete boards from the warehouse
     * @param request the request containing board details and count
     * @return a response indicating success or failure
     */
    @DeleteMapping
    public ResponseEntity<Map<String, String>> torolDeszka(@RequestBody DeszkaHozzaadasRequest request) {
        log.info("Deszka törlése: {}", request);

        Deszka deszka = Deszka.builder()
                .szelesseg(request.getSzelesseg())
                .hosszusag(request.getHosszusag())
                .balOldal(request.getBalOldal())
                .felsoOldal(request.getFelsoOldal())
                .jobbOldal(request.getJobbOldal())
                .alsoOldal(request.getAlsoOldal())
                .build();

        boolean success = raktarService.torol(deszka, request.getDarabszam());

        if (success) {
            return ResponseEntity.ok(Map.of("message", "Deszka sikeresen törölve a raktárból"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "Nem sikerült törölni a deszkát a raktárból"));
        }
    }

    /**
     * Request class for adding boards to the warehouse
     */
    @lombok.Data
    public static class DeszkaHozzaadasRequest {
        private double szelesseg;
        private double hosszusag;
        private OldalAllapot balOldal;
        private OldalAllapot felsoOldal;
        private OldalAllapot jobbOldal;
        private OldalAllapot alsoOldal;
        private int darabszam;
    }
}
