package at.aau.wagnis.gamestate;

import java.util.Random;
import at.aau.wagnis.Hub;
import at.aau.wagnis.Player;

/**
 * In der Attacker Game-State Klasse werden Angriffe auf Hubs abgehandelt.
 */
public class AttackGameState extends GameLogicState {
    private int testAttackerDiceRoll;
    private int testDefenderDiceRoll;
    private boolean attacker = false;
    private boolean defender = false;
    private Hub sourceHub;
    private Hub targetHub;
    private int sourceHubId;
    private int targetHubId;

    /**
     * Konstruktor für AttackGameState mit Hub-ID als Parameter.
     *
     * @param sourceHubId Die ID des angreifenden Hubs.
     * @param targetHubId Die ID des verteidigenden Hubs.
     */
    public AttackGameState(int sourceHubId, int targetHubId) {
        this.sourceHubId = sourceHubId;
        this.targetHubId = targetHubId;
    }

    /**
     * Konstruktor für AttackGameState mit Hubs als Parameter.
     *
     * @param sourceHub Der angreifende Hub.
     * @param targetHub Der verteidigende Hub.
     */
    public AttackGameState(Hub sourceHub, Hub targetHub) {
        this.sourceHub = sourceHub;
        this.targetHub = targetHub;
    }

    @Override
    public void onEntry() {
        gameServer.getGameData().setSelectedHub(-1); // selectedHub wird immer auf -1 gesetzt, damit kein Hub ausgewaelt ist
        if (sourceHub == null) {
            this.sourceHub = gameServer.getGameData().getHubs().stream().filter(h -> h.getId() == sourceHubId).findFirst().orElseThrow(() -> new IllegalStateException("Hub not found"));
        }
        if (targetHub == null) {
            this.targetHub = gameServer.getGameData().getHubs().stream().filter(h -> h.getId() == targetHubId).findFirst().orElseThrow(() -> new IllegalStateException("Hub not found"));
        }
        try {
            attack();
            if (gameWon(targetHub.getOwner())) {
                this.gameServer.setGameLogicState(new VictoryState(targetHub.getOwner()));
                //Send winner id via broadcast to all clients
            } else {
                this.gameServer.setGameLogicState(new ChooseAttackGameState());
            }
        } catch(IllegalArgumentException e) {
            this.gameServer.setGameLogicState(new ChooseAttackGameState());
        }
    }

    @Override
    public void attack() {
        // Anzahl der Würfe und Truppen berechnen
        int attackerDiceRolls;
        int defenderDiceRolls;
        int attackerTroops = sourceHub.getAmountTroops();
        int defenderTroops = targetHub.getAmountTroops();

        // Überprüfen, ob der Angriff gültig ist
        if (this.sourceHub.getAmountTroops() == 1 || this.targetHub.getAmountTroops() <= 0) {
            throw new IllegalArgumentException("Illegal attack");
        }

        // Würfe für den Angreifer basierend auf Truppenzahl festlegen
        switch (attackerTroops) {
            case 2: {
                attackerDiceRolls = diceRoll();
                testAttackerDiceRoll = 1;
                break;
                // Attacker greift mit einer Truppe an
            }
            case 3: {
                attackerDiceRolls = diceRoll() + diceRoll();
                testAttackerDiceRoll = 2;
                break;
                //Attacker greift mit zwei Truppen an
            }
            default: {
                attackerDiceRolls = diceRoll() + diceRoll() + diceRoll();
                testAttackerDiceRoll = 3;
                break;
                //Attacker greift mit mehr als zwei Truppen an
            }
        }

        // Würfe für den Verteidiger basierend auf Truppenzahl festlegen
        switch (defenderTroops) {
            case 1: {
                defenderDiceRolls = diceRoll();
                testDefenderDiceRoll = 1;
                break;
                //Verteidiger besitzt nur eine Truppe am Fe ld
            }
            default: {
                defenderDiceRolls = diceRoll() + diceRoll();
                testDefenderDiceRoll = 2;
                break;
                //Verteidiger besitzt mehr als eine Truppe am Feld
            }
        }

        // Truppen aktualisieren basierend auf den Würfen
        if (attackerDiceRolls > defenderDiceRolls) {
            this.targetHub.setAmountTroops(this.targetHub.getAmountTroops() - 1);
        } else {
            this.sourceHub.setAmountTroops(this.sourceHub.getAmountTroops() - 1);
        }


        // Überprüfen, ob der Angriff fehlgeschlagen ist
        if (this.sourceHub.getAmountTroops() == 1 && this.targetHub.getAmountTroops() >= 1) {
            throw new IllegalArgumentException("Attack failed");
        }

        // Überprüfen, ob der Verteidiger besiegt wurde
        if (this.targetHub.getAmountTroops() <= 0) {

            Player defendingPlayer = this.targetHub.getOwner();
            defendingPlayer.removeHub(this.targetHub);
            Player attackingPlayer = this.sourceHub.getOwner();
            attackingPlayer.addHub(this.targetHub);

            // Die Hälfte der Hubs, des Angreifer-Hubs werden dem Verteidiger-Hub bei einer Übernahmen zugewiesen
            this.targetHub.setAmountTroops(this.sourceHub.getAmountTroops()/2);
            this.sourceHub.setAmountTroops(this.sourceHub.getAmountTroops() - (this.sourceHub.getAmountTroops()/2));
        }
    }

    /**
     * Überprüft, ob ein Spieler das Spiel gewonnen hat.
     *
     * @param player Der Spieler, der überprüft werden soll.
     * @return true, wenn der Spieler alle Hubs besitzt, sonst false.
     */
    public boolean gameWon(Player player) {
        return player.getOwnedHubs().size() == 42;
    }

    /**
     * Führt einen Würfelwurf aus und gibt das Ergebnis zurück.
     *
     * @return Das Ergebnis des Würfelwurfs.
     */
    private int diceRoll() {
        Random randomGen = new Random();
        return randomGen.nextInt(6) + 1;
    }

    // Getter und Setter


    public int getTestAttackerDiceRoll() {
        return testAttackerDiceRoll;
    }

    public int getTestDefenderDiceRoll() {
        return testDefenderDiceRoll;
    }

    public Hub getSourceHub() {
        return sourceHub;
    }

    public void setSourceHub(Hub sourceHub) {
        this.sourceHub = sourceHub;
    }

    public Hub getTargetHub() {
        return targetHub;
    }

    public void setTargetHub(Hub targetHub) {
        this.targetHub = targetHub;
    }

    public boolean isAttacker() {
        return attacker;
    }

    public void setAttacker(boolean attacker) {
        this.attacker = attacker;
    }

    public boolean isDefender() {
        return defender;
    }

    public void setDefender(boolean defender) {
        this.defender = defender;
    }

    public int getSourceHubId() {
        return sourceHubId;
    }

    public void setSourceHubId(int sourceHubId) {
        this.sourceHubId = sourceHubId;
    }

    public int getTargetHubId() {
        return targetHubId;
    }

    public void setTargetHubId(int targetHubId) {
        this.targetHubId = targetHubId;
    }
}
