package at.aau.wagnis.gamestate;

import at.aau.wagnis.Hub;
import at.aau.wagnis.PLRNG;
import at.aau.wagnis.Player;

/**
 * In der Attacker Game-State Klasse werden Angriffe auf Hubs abgehandelt.
 */

public class  AttackGameState extends GameLogicState {
    private static final PLRNG RNG = new PLRNG();
    private Hub sourceHub, targetHub;
    private boolean attacker = false, defender = false;

    public AttackGameState(Hub sourceHub, Hub targetHub) {
        this.sourceHub = sourceHub;
        this.targetHub = targetHub;
    }


    public Hub getSourceHub() {
        return sourceHub;
    }

    public Hub getTargetHub() {
        return targetHub;
    }

    public void attack() {
        int attackerDiceRolls = RNG.diceRoll();
        int defenderDiceRolls = RNG.diceRoll();

        if (this.sourceHub.getAmountTroops() == 1 || this.targetHub.getAmountTroops() <= 0) {
            throw new IllegalArgumentException("Illegal attack");
        }

        if (attackerDiceRolls > defenderDiceRolls) {
            this.targetHub.setAmountTroops(this.targetHub.getAmountTroops() - 1);
        } else if (attackerDiceRolls < defenderDiceRolls) {
            this.sourceHub.setAmountTroops(this.sourceHub.getAmountTroops() - 1);
        }

        if (this.sourceHub.getAmountTroops() == 1 && this.targetHub.getAmountTroops() >= 1) {
            throw new IllegalArgumentException("Attack failed");
        }

        if (this.targetHub.getAmountTroops() <= 0) {
            Player attacker = this.sourceHub.getOwner();
            attacker.addHub(this.targetHub);
            Player defender = targetHub.getOwner();
            defender.removeHub(this.targetHub);
            if (gamewon(attacker)) {
                throw new IllegalArgumentException("Attack and Game won!");
            }
            throw new IllegalArgumentException("Attack won");
        }
    }

    public boolean gamewon(Player player) {
        if (player.getOwnedHubs().size() == 42) {
            return true;
        }
        return false;

    }
}

