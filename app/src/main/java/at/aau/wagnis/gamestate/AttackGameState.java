package at.aau.wagnis.gamestate;

import java.util.ArrayList;
import java.util.Map;

import at.aau.wagnis.DefaultTroop;
import at.aau.wagnis.Hub;
import at.aau.wagnis.Player;

public class AttackGameState extends GameLogicState {

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

    public void attack(int attackerDiceRolls, int defenderDiceRolls) {
        if (attackerDiceRolls <= 0 || defenderDiceRolls <= 0) {
            throw new IllegalArgumentException("Invalid dice rolls");
        }

        Map<DefaultTroop, Integer> attackerTroops = sourceHub.getTroops();
        Map<DefaultTroop, Integer> defenderTroops = targetHub.getTroops();

        if (attackerTroops.get(DefaultTroop.TROOP) == 1 || defenderTroops.get(DefaultTroop.TROOP) <= 0) {
            throw new IllegalArgumentException("Illegal attack");
        }

        if (attackerDiceRolls > defenderDiceRolls) {
            defenderTroops.put(DefaultTroop.TROOP, defenderTroops.get(DefaultTroop.TROOP) - 1);
        } else if (attackerDiceRolls < defenderDiceRolls) {
            attackerTroops.put(DefaultTroop.TROOP, attackerTroops.get(DefaultTroop.TROOP) - 1);
        }

        if (attackerTroops.get(DefaultTroop.TROOP) == 1 && defenderTroops.get(DefaultTroop.TROOP) >= 1) {
            throw new IllegalArgumentException("Attack failed");
        }

        if (defenderTroops.get(DefaultTroop.TROOP) <= 0) {
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

