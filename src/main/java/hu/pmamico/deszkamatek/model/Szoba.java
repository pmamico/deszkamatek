package hu.pmamico.deszkamatek.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class Szoba {

    public enum Orientacio {
        SZELTEBEN,  // widthwise
        HOSSZABAN   // lengthwise
    }

    private double szelesseg;  // width in mm
    private double hossz;      // length in mm
    private Orientacio orientacio;
    private static double DILATACIO = 0;
    @Builder.Default
    private List<ElhelyezettDeszka> elhelyezettDeszkak = new ArrayList<>();
    @Builder.Default
    private int vagasokSzama = 0;

    public Szoba(double szelesseg, double hossz, Orientacio orientacio) {
        this.szelesseg = szelesseg;
        this.hossz = hossz;
        this.orientacio = orientacio;
        this.elhelyezettDeszkak = new ArrayList<>();
        this.vagasokSzama = 0;
    }
    @Data
    public static class ElhelyezettDeszka {
        private final Deszka deszka;
        private final double x;  // x position in mm
        private final double y;  // y position in mm

        public ElhelyezettDeszka(Deszka deszka, double x, double y) {
            this.deszka = deszka;
            this.x = x;
            this.y = y;
        }
    }

    /**
     * Fill the room with planks from the warehouse
     * @param raktar the warehouse to get planks from
     * @return the number of cuts made
     */
    public int betoltes(Raktar raktar) {
        log.info("Szoba betöltése: {}x{} mm, orientáció: {}", szelesseg, hossz, orientacio);

        // Calculate effective dimensions (accounting for dilatation margin)
        double effektivSzelesseg = szelesseg - 2 * DILATACIO;
        double effektivHossz = hossz - 2 * DILATACIO;

        if (orientacio == Orientacio.SZELTEBEN) {
            // Fill widthwise (planks laid perpendicular to length)
            betoltesSzelteben(raktar, effektivSzelesseg, effektivHossz);
        } else {
            // Fill lengthwise (planks laid parallel to length)
            betoltesHosszaban(raktar, effektivSzelesseg, effektivHossz);
        }

        log.info("Szoba betöltése kész. Elhelyezett deszkák: {}, vágások száma: {}", 
                elhelyezettDeszkak.size(), vagasokSzama);

        return vagasokSzama;
    }

    /**
     * Fill the room with planks from the warehouse and check if it was successful
     * @param raktar the warehouse to get planks from
     * @return true if the room was successfully filled, false otherwise
     */
    public boolean betoltesEredmeny(Raktar raktar) {
        betoltes(raktar);
        return isBetoltve();
    }

    /**
     * Check if the room has been successfully filled with planks
     * @return true if the room is completely filled, false otherwise
     */
    public boolean isBetoltve() {
        if (elhelyezettDeszkak.isEmpty()) {
            return false;
        }

        double effektivSzelesseg = szelesseg - 2 * DILATACIO;
        double effektivHossz = hossz - 2 * DILATACIO;
        double effektivTerulet = effektivSzelesseg * effektivHossz;

        double fedettTerulet = 0.0;

        if (orientacio == Orientacio.SZELTEBEN) {
            double maxX = DILATACIO + effektivSzelesseg;
            double maxY = DILATACIO + effektivHossz;

            for (double y = DILATACIO; y < maxY;) {
                boolean sorFedve = false;
                for (ElhelyezettDeszka elhelyezett : elhelyezettDeszkak) {
                    if (Math.abs(elhelyezett.getY() - y) < 0.001) {
                        sorFedve = true;
                        break;
                    }
                }

                if (!sorFedve) {
                    return false;
                }

                double sorMagassag = 0.0;
                for (ElhelyezettDeszka elhelyezett : elhelyezettDeszkak) {
                    if (Math.abs(elhelyezett.getY() - y) < 0.001) {
                        sorMagassag = elhelyezett.getDeszka().getHosszusag();
                        break;
                    }
                }

                double sorX = DILATACIO;
                while (sorX < maxX) {
                    boolean pozicioFedve = false;
                    for (ElhelyezettDeszka elhelyezett : elhelyezettDeszkak) {
                        if (Math.abs(elhelyezett.getY() - y) < 0.001 && 
                            elhelyezett.getX() <= sorX && 
                            elhelyezett.getX() + elhelyezett.getDeszka().getSzelesseg() > sorX) {
                            pozicioFedve = true;
                            break;
                        }
                    }

                    if (!pozicioFedve) {
                        return false;
                    }

                    sorX += 1.0;
                }

                y += sorMagassag;
            }
        } else {
            double maxX = DILATACIO + effektivSzelesseg;
            double maxY = DILATACIO + effektivHossz;

            for (double x = DILATACIO; x < maxX;) {
                boolean oszlopFedve = false;
                for (ElhelyezettDeszka elhelyezett : elhelyezettDeszkak) {
                    if (Math.abs(elhelyezett.getX() - x) < 0.001) {
                        oszlopFedve = true;
                        break;
                    }
                }

                if (!oszlopFedve) {
                    return false;
                }

                double oszlopSzelesseg = 0.0;
                for (ElhelyezettDeszka elhelyezett : elhelyezettDeszkak) {
                    if (Math.abs(elhelyezett.getX() - x) < 0.001) {
                        oszlopSzelesseg = elhelyezett.getDeszka().getHosszusag();
                        break;
                    }
                }

                double oszlopY = DILATACIO;
                while (oszlopY < maxY) {
                    boolean pozicioFedve = false;
                    for (ElhelyezettDeszka elhelyezett : elhelyezettDeszkak) {
                        if (Math.abs(elhelyezett.getX() - x) < 0.001 && 
                            elhelyezett.getY() <= oszlopY && 
                            elhelyezett.getY() + elhelyezett.getDeszka().getSzelesseg() > oszlopY) {
                            pozicioFedve = true;
                            break;
                        }
                    }

                    if (!pozicioFedve) {
                        return false;
                    }

                    oszlopY += 1.0;
                }

                x += oszlopSzelesseg;
            }
        }

        return true;
    }

    private void betoltesSzelteben(Raktar raktar, double effektivSzelesseg, double effektivHossz) {
        double currentY = DILATACIO;  // Start from the dilatation margin

        // Continue until we've filled the room lengthwise
        while (currentY < DILATACIO + effektivHossz) {
            double currentX = DILATACIO;  // Start from the dilatation margin

            // Continue until we've filled the current row widthwise
            while (currentX < DILATACIO + effektivSzelesseg) {
                // Calculate remaining width in this row
                double remainingWidth = (DILATACIO + effektivSzelesseg) - currentX;

                // Create a request for a plank with the required width
                DeszkaIgeny igeny = DeszkaIgeny.builder()
                        .szelesseg(remainingWidth)
                        .build();

                // Get a plank from the warehouse
                Raktar.KeresesEredmeny eredmeny = raktar.keresDeszka(igeny);

                // If no plank is available, try to get the longest available plank
                if (eredmeny == null) {
                    Deszka deszka = getLongestPlank(raktar);
                    if (deszka == null) {
                        // No planks available, move to the next row
                        break;
                    }
                    eredmeny = new Raktar.KeresesEredmeny(deszka, false);
                }

                Deszka deszka = eredmeny.getDeszka();

                // Place the plank in the room
                elhelyezettDeszkak.add(new ElhelyezettDeszka(deszka, currentX, currentY));

                // Move to the next position
                currentX += deszka.getSzelesseg();

                // If the plank was cut, increment the cut counter
                if (eredmeny.isVagva()) {
                    vagasokSzama++;
                }
            }

            // Move to the next row if we have planks in the current row
            if (!elhelyezettDeszkak.isEmpty()) {
                // Get the height of the last placed plank to determine the next row position
                double lastPlankHeight = elhelyezettDeszkak.get(elhelyezettDeszkak.size() - 1).getDeszka().getHosszusag();
                currentY += lastPlankHeight;
            } else {
                // No planks were placed in this row, exit the loop
                break;
            }
        }
    }

    private void betoltesHosszaban(Raktar raktar, double effektivSzelesseg, double effektivHossz) {
        double currentX = DILATACIO;  // Start from the dilatation margin

        // Continue until we've filled the room widthwise
        while (currentX < DILATACIO + effektivSzelesseg) {
            double currentY = DILATACIO;  // Start from the dilatation margin

            // Continue until we've filled the current column lengthwise
            while (currentY < DILATACIO + effektivHossz) {
                // Calculate remaining length in this column
                double remainingLength = (DILATACIO + effektivHossz) - currentY;

                // Create a request for a plank with the required length
                DeszkaIgeny igeny = DeszkaIgeny.builder()
                        .hosszusag(remainingLength)
                        .build();

                // Get a plank from the warehouse
                Raktar.KeresesEredmeny eredmeny = raktar.keresDeszka(igeny);

                // If no plank is available, try to get the longest available plank
                if (eredmeny == null) {
                    Deszka deszka = getLongestPlank(raktar);
                    if (deszka == null) {
                        // No planks available, move to the next column
                        break;
                    }
                    eredmeny = new Raktar.KeresesEredmeny(deszka, false);
                }

                Deszka deszka = eredmeny.getDeszka();

                // Place the plank in the room
                elhelyezettDeszkak.add(new ElhelyezettDeszka(deszka, currentX, currentY));

                // Move to the next position
                currentY += deszka.getSzelesseg();

                // If the plank was cut, increment the cut counter
                if (eredmeny.isVagva()) {
                    vagasokSzama++;
                }
            }

            // Move to the next column if we have planks in the current column
            if (!elhelyezettDeszkak.isEmpty()) {
                // Get the width of the last placed plank to determine the next column position
                double lastPlankWidth = elhelyezettDeszkak.get(elhelyezettDeszkak.size() - 1).getDeszka().getHosszusag();
                currentX += lastPlankWidth;
            } else {
                // No planks were placed in this column, exit the loop
                break;
            }
        }
    }

    /**
     * Get the longest plank from the warehouse
     * @param raktar the warehouse to get the plank from
     * @return the longest plank, or null if no planks are available
     */
    private Deszka getLongestPlank(Raktar raktar) {
        List<Deszka> raktarozott = raktar.getRaktarozott();
        if (raktarozott.isEmpty()) {
            return null;
        }

        // Find the plank with the maximum width
        Deszka longest = raktarozott.get(0);
        for (Deszka deszka : raktarozott) {
            if (deszka.getSzelesseg() > longest.getSzelesseg()) {
                longest = deszka;
            }
        }

        // Remove the plank from the warehouse
        raktarozott.remove(longest);

        return longest;
    }
}
