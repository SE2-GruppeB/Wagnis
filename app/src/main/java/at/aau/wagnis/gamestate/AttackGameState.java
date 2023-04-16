package at.aau.wagnis.gamestate;

import java.util.ArrayList;
import java.util.Map;

import at.aau.wagnis.GlobalVariables;
import at.aau.wagnis.Hub;
import at.aau.wagnis.PLRNG;
import at.aau.wagnis.Player;

/**
 * In der Attacker Game-State Klasse werden Angriffe auf Hubs abgehandelt.
 */

public class AttackGameState extends GameLogicState {
private static final PLRNG RNG= new PLRNG();
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

    public void attack () {
        int attackerDiceRolls = RNG.diceRoll();
        int defenderDiceRolls = RNG.diceRoll();

        Map<String, Integer> attackerTroops = sourceHub.getTroops();
        Map<String, Integer> defenderTroops = targetHub.getTroops();

        if (attackerTroops.get(GlobalVariables.troop) == 1 || defenderTroops.get(GlobalVariables.troop) <= 0) {
            throw new IllegalArgumentException("Illegal attack");
        }

        if (attackerDiceRolls > defenderDiceRolls) {
            defenderTroops.put(GlobalVariables.troop, defenderTroops.get(GlobalVariables.troop) - 1);
        } else if (attackerDiceRolls < defenderDiceRolls) {
            attackerTroops.put(GlobalVariables.troop, attackerTroops.get(GlobalVariables.troop) - 1);
        }

        if (attackerTroops.get(GlobalVariables.troop) == 1 && defenderTroops.get(GlobalVariables.troop) >= 1) {
            throw new IllegalArgumentException("Attack failed");
        }

        if (defenderTroops.get(GlobalVariables.troop) <= 0) {
            Player attacker = sourceHub.getOwner();
            ArrayList<Hub> attackerHubs = attacker.getOwnedHubs();
            attackerHubs.add(targetHub);
            Player defender = targetHub.getOwner();
            ArrayList<Hub> defenderHubs = defender.getOwnedHubs();
            defenderHubs.remove(targetHub);
            throw new IllegalArgumentException("Attack won");
        }
    }
}

