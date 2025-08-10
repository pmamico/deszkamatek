package hu.pmamico.deszkamatek.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SzobaTest {

    private static final Logger logger = LoggerFactory.getLogger(SzobaTest.class);
    private Raktar raktar;

    @BeforeEach
    void setUp() {
        raktar = new Raktar();
        for( int i = 0; i < 100; i++ ) {
            raktar.hozzaad(
                    Deszka.builder()
                            .szelesseg(20)
                            .hosszusag(100)
                            .vastagsag(2)
                            .balOldal(OldalAllapot.CSAP)
                            .felsoOldal(OldalAllapot.CSAP)
                            .jobbOldal(OldalAllapot.NUT)
                            .alsoOldal(OldalAllapot.NUT)
                            .build()
            );
        }
    }

    @Test
    void kisSzobaPontosMeret() {
        Szoba szoba =
                Szoba.builder()
                        .hossz(100)
                        .szelesseg(100)
                        .build();

        szoba.betoltes(raktar);
        assertThat(szoba.isBetoltve()).isTrue();
        assertThat(raktar.getSize()).isEqualTo(95);
    }

    @Test
    void nagySzobaPontosMeret() {
        Szoba szoba =
                Szoba.builder()
                        .hossz(200)
                        .szelesseg(1000)
                        .build();

        szoba.betoltes(raktar);
        assertThat(szoba.isBetoltve()).isTrue();
        assertThat(raktar.getSize()).isEqualTo(0);
    }


    @Test
    void nincselegdeszka() {
        Szoba szoba =
                Szoba.builder()
                        .hossz(200)
                        .szelesseg(1200)
                        .build();

        szoba.betoltes(raktar);
        assertThat(szoba.isBetoltve()).isFalse();
        assertThat(raktar.getSize()).isEqualTo(0);
    }


    @Test
    void felbevagasokkal() {
        Szoba szoba =
                Szoba.builder()
                        .hossz(50)
                        .szelesseg(120)
                        .build();

        szoba.betoltes(raktar);
        assertThat(szoba.isBetoltve()).isTrue();
        assertThat(raktar.getSize()).isEqualTo(97);
    }




}
