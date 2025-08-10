package hu.pmamico.deszkamatek.model;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DeszkaTest {

    @Test
    void testDeszkaCreation() {
        Deszka deszka = new Deszka(200, 20, 2);

        assertThat(deszka.getMagassag()).isEqualTo(200);
        assertThat(deszka.getSzelesseg()).isEqualTo(20);
        assertThat(deszka.getVastagsag()).isEqualTo(2);

        // Default values should be: left=CSAP, top=CSAP, right=NUT, bottom=NUT
        assertThat(deszka.getBalOldal()).isEqualTo(OldalAllapot.CSAP);
        assertThat(deszka.getJobbOldal()).isEqualTo(OldalAllapot.NUT);
        assertThat(deszka.getFelsoOldal()).isEqualTo(OldalAllapot.CSAP);
        assertThat(deszka.getAlsoOldal()).isEqualTo(OldalAllapot.NUT);
    }

    @Test
    void testNutEsCsapBeallitas() {
        Deszka deszka = new Deszka(200, 20, 2);

        deszka.setNutBalOldal(true);
        deszka.setCsapJobbOldal(true);
        deszka.setNutFelsoOldal(true);
        deszka.setCsapAlsoOldal(true);

        assertThat(deszka.getBalOldal()).isEqualTo(OldalAllapot.NUT);
        assertThat(deszka.getJobbOldal()).isEqualTo(OldalAllapot.CSAP);
        assertThat(deszka.getFelsoOldal()).isEqualTo(OldalAllapot.NUT);
        assertThat(deszka.getAlsoOldal()).isEqualTo(OldalAllapot.CSAP);
    }

    @Test
    void testVagas() {
        Deszka deszka = new Deszka(200, 20, 2);
        deszka.setNutBalOldal(true);
        deszka.setCsapJobbOldal(true);
        deszka.setNutFelsoOldal(true);
        deszka.setCsapAlsoOldal(true);

        List<Deszka> vagottDeszkak = deszka.vagas(80);

        assertThat(vagottDeszkak).hasSize(2);

        Deszka elsoResz = vagottDeszkak.get(0);
        assertThat(elsoResz.getMagassag()).isEqualTo(80);
        assertThat(elsoResz.getSzelesseg()).isEqualTo(20);
        assertThat(elsoResz.getVastagsag()).isEqualTo(2);

        assertThat(elsoResz.getBalOldal()).isEqualTo(OldalAllapot.NUT);
        assertThat(elsoResz.getJobbOldal()).isEqualTo(OldalAllapot.CSAP);
        assertThat(elsoResz.getFelsoOldal()).isEqualTo(OldalAllapot.VAGOTT);
        assertThat(elsoResz.getAlsoOldal()).isEqualTo(OldalAllapot.CSAP);

        Deszka masodikResz = vagottDeszkak.get(1);
        assertThat(masodikResz.getMagassag()).isEqualTo(120);
        assertThat(masodikResz.getSzelesseg()).isEqualTo(20);
        assertThat(masodikResz.getVastagsag()).isEqualTo(2);

        assertThat(masodikResz.getBalOldal()).isEqualTo(OldalAllapot.NUT);
        assertThat(masodikResz.getJobbOldal()).isEqualTo(OldalAllapot.CSAP);
        assertThat(masodikResz.getFelsoOldal()).isEqualTo(OldalAllapot.NUT);
        assertThat(masodikResz.getAlsoOldal()).isEqualTo(OldalAllapot.VAGOTT);
    }

    @Test
    void testHosszantiVagas() {
        Deszka deszka = new Deszka(200, 20, 2);
        deszka.setNutBalOldal(true);
        deszka.setCsapJobbOldal(true);
        deszka.setNutFelsoOldal(true);
        deszka.setCsapAlsoOldal(true);

        Deszka levagottResz = deszka.hosszantiVagas(8);

        assertThat(levagottResz.getSzelesseg()).isEqualTo(8);
        assertThat(levagottResz.getMagassag()).isEqualTo(200);
        assertThat(levagottResz.getVastagsag()).isEqualTo(2);

        assertThat(levagottResz.getBalOldal()).isEqualTo(OldalAllapot.NUT);
        assertThat(levagottResz.getJobbOldal()).isEqualTo(OldalAllapot.VAGOTT);
        assertThat(levagottResz.getFelsoOldal()).isEqualTo(OldalAllapot.NUT);
        assertThat(levagottResz.getAlsoOldal()).isEqualTo(OldalAllapot.CSAP);

        assertThat(deszka.getSzelesseg()).isEqualTo(12);
        assertThat(deszka.getBalOldal()).isEqualTo(OldalAllapot.VAGOTT);
        assertThat(deszka.getJobbOldal()).isEqualTo(OldalAllapot.CSAP);
    }

    @Test
    void testErvenytelenVagas() {
        Deszka deszka = new Deszka(200, 20, 2);

        assertThatThrownBy(() -> deszka.vagas(0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("vágási hossz érvénytelen");

        assertThatThrownBy(() -> deszka.vagas(-10))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("vágási hossz érvénytelen");

        assertThatThrownBy(() -> deszka.vagas(200))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("vágási hossz érvénytelen");

        assertThatThrownBy(() -> deszka.vagas(250))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("vágási hossz érvénytelen");
    }

    @Test
    void testErvenytelenHosszantiVagas() {
        Deszka deszka = new Deszka(200, 20, 2);

        assertThatThrownBy(() -> deszka.hosszantiVagas(0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("új szélesség érvénytelen");

        assertThatThrownBy(() -> deszka.hosszantiVagas(-5))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("új szélesség érvénytelen");

        assertThatThrownBy(() -> deszka.hosszantiVagas(20))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("új szélesség érvénytelen");

        assertThatThrownBy(() -> deszka.hosszantiVagas(30))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("új szélesség érvénytelen");
    }
}
