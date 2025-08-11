package hu.pmamico.deszkamatek.controller;

import hu.pmamico.deszkamatek.Epito;
import hu.pmamico.deszkamatek.model.Deszka;
import hu.pmamico.deszkamatek.model.LerakottDeszka;
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

    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    @GetMapping("/raktar")
    public String raktar(Model model) {
        return "raktar";
    }

    @GetMapping("/api/epito")
    @ResponseBody
    public Map<String, Object> getEpito(
            @RequestParam(defaultValue = "460") double x,
            @RequestParam(defaultValue = "397") double y) {
        // Create a copy of the warehouse for building
        Raktar raktarCopy = raktarService.createRaktarCopy();

        var epito = Epito.builder()
                .szoba(new Szoba(x, y))
                .raktar(raktarCopy)
                .build();

        log.info(String.valueOf(epito.getRaktar()));
        epito.epit();

        // Store the remaining warehouse after building
        raktarService.setMaradekRaktar(raktarCopy);

        val szoba = epito.getSzoba();

        // Return the room data as JSON
        return Map.of(
            "szoba", Map.of(
                "x", szoba.getX(),
                "y", szoba.getY()
            ),
            "lerakottDeszkak", szoba.getLerakottDeszkak()
        );
    }
}
