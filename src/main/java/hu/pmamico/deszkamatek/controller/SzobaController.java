package hu.pmamico.deszkamatek.controller;

import hu.pmamico.deszkamatek.Epito;
import hu.pmamico.deszkamatek.model.Deszka;
import hu.pmamico.deszkamatek.model.LerakottDeszka;
import hu.pmamico.deszkamatek.model.Raktar;
import hu.pmamico.deszkamatek.model.Szoba;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class SzobaController {

    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    @GetMapping("/api/szoba")
    @ResponseBody
    public Map<String, Object> getSzoba() {
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
