package at.aau.wagnis;

import android.widget.Button;

import java.util.Objects;

/**
 * Die Klasse Hub repräsentiert einen Hub im Spiel.
 */
public class Hub implements Comparable<Hub>{

    private final int id;
    private final Button hubButton;
    private Player owner;
    private int amountTroops;

    /**
     * Erzeugt einen Hub mit einem Button-Objekt.
     *
     * @param hubButton Der Button, der den Hub repräsentiert.
     */
    public Hub(Button hubButton) {
        this.hubButton = hubButton;
        this.id = hubButton.getId();
        this.amountTroops = 0;
        this.owner = new Player();
    }

    /**
     * Erzeugt einen Hub mit einer ID.
     *
     * @param id Die ID des Hubs.
     */
    public Hub(int id) {
        this.hubButton = null;
        this.id = id;
        this.amountTroops = 0;
        this.owner = new Player();
    }

    /**
     * Überprüft, ob ein anderes Objekt gleich ist.
     *
     * @param o Das zu vergleichende Objekt.
     * @return true, wenn das Objekt gleich diesem Hub ist, sonst false.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if(o instanceof Hub){
            Hub hub = (Hub) o;
            return id == hub.id && amountTroops == hub.amountTroops && Objects.equals(owner, hub.owner);
        }
        return false;
    }

    /**
     * Berechnet den Hash-Code dieses Hubs.
     *
     * @return Der berechnete Hash-Code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, owner);
    }

    /**
     * Gibt den Button des Hubs zurück.
     *
     * @return Der Button des Hubs.
     */
    public Button getHubButton() {
        return this.hubButton;
    }

    /**
     * Gibt die ID des Hubs zurück.
     *
     * @return Die ID des Hubs.
     */
    public int getId() {
        return this.id;
    }

    /**
     * Setzt den Text des Hubs auf die angegebene Anzahl von Truppen.
     *
     * @param troops Die Anzahl der Truppen als String.
     */
    public void setText(String troops) {
        this.hubButton.setText(troops);
    }

    /**
     * Gibt die Anzahl der Truppen auf dem Hub zurück.
     *
     * @return Die Anzahl der Truppen auf dem Hub.
     */
    public int getAmountTroops() {
        return this.amountTroops;
    }

    /**
     * Setzt die Anzahl der Truppen auf dem Hub.
     *
     * @param amountTroops Die Anzahl der Truppen.
     */
    public void setAmountTroops(int amountTroops) {
        this.amountTroops = amountTroops;
    }

    /**
     * Gibt den Besitzer des Hubs zurück.
     *
     * @return Der Besitzer des Hubs.
     */
    public Player getOwner() {
        return this.owner;
    }

    public void setOwner(Player hubOwner) {
        this.owner = hubOwner;
    }

    /**
     * Setzt das Bild des Hubs entsprechend der angegebenen Teams(Auswählbare Raumfart-Agenturen).
     *
     * @param agency Der Name der Agentur.
     */
    public void setHubImage(String agency) {
        switch (agency) {
            case ("ESA"):
                this.getHubButton().setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.dome_x42);
                break;
            case ("NASA"):
                this.getHubButton().setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.dome6_x42);
                break;
            case ("ISRO"):
                this.getHubButton().setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.dome5_x42);
                break;
            case ("JAXA"):
                this.getHubButton().setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.dome4_x42);
                break;
            case ("Roskosmos"):
                this.getHubButton().setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.dome3_x42);
                break;
            default:
                this.getHubButton().setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.dome2_x42);
        }
    }

    /**
     * Fügt die angegebene Anzahl von Truppen zu den bestehenden Truppen auf dem Hub hinzu.
     *
     * @param troops Die Anzahl der Truppen, die hinzugefügt werden sollen.
     */
    public void addTroops(int troops) {
        this.amountTroops += troops;
    }

    @Override
    public String toString() {
        return "Hub{" +
                "id=" + id +
                ", amountTroops=" + amountTroops +
                '}';
    }

    /**
     * Vergleicht diesen Hub mit einem anderen Hub.
     *
     * @param hub Der zu vergleichende Hub.
     * @return Eine negative ganze Zahl, wenn dieser HubId kleiner ist als der des andere Hub;
     *         eine positive ganze Zahl, wenn dieser HubId größer ist als der des andere Hub;
     *         andernfalls 0, wenn beide HubIds gleich sind.
     */
    @Override
    public int compareTo(Hub hub) {
        if(this.getId()>hub.getId()){
            return 1;
        } else if (this.getId()<hub.getId()) {
            return -1;
        }
        return 0;
    }
}
