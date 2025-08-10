package hu.pmamico.deszkamatek.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeszkaIgeny {
    private Double magassag;
    private Double szelesseg;
    private Double vastagsag;

    private OldalAllapot balOldal;
    private OldalAllapot felsoOldal;
    private OldalAllapot jobbOldal;
    private OldalAllapot alsoOldal;
}
