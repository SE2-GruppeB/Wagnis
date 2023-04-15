package at.aau.wagnis.gamestate;

import static java.util.Collections.reverse;


import android.util.Log;

import java.util.Arrays;
import java.util.Map;

import at.aau.wagnis.DefaultTroop;
import at.aau.wagnis.Hub;
import at.aau.wagnis.Player;

public class AttackGameState extends GameLogicState {

    /*

    Dice dice = new Dice();

    private Hub sourceHub;
    private Hub targetHub;

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

        int attackerLosses = 0;
        int defenderLosses = 0;

        for (int i = 0; i < attackerDiceRolls && i < attackerTroops.get(DefaultTroop.TROOP); i++) {
            int attackerRoll = dice.roll();
            int defenderRoll = dice.roll();

            if (attackerRoll > defenderRoll) {
                defenderLosses++;
            } else {
                attackerLosses++;
            }
        }

        for (int i = 0; i < defenderDiceRolls && i < defenderTroops.get(DefaultTroop.TROOP); i++) {
            int attackerRoll = dice.roll();
            int defenderRoll = dice.roll();

            if (attackerRoll > defenderRoll) {
                defenderLosses++;
            } else {
                attackerLosses++;
            }
        }

        Log.d("TAG", "Attacker losses: " + attackerLosses);
        Log.d("TAG", "Defender losses: " + defenderLosses);

        attackerTroops.put(DefaultTroop.TROOP, attackerTroops.get(DefaultTroop.TROOP) - attackerLosses);
        defenderTroops.put(DefaultTroop.TROOP, defenderTroops.get(DefaultTroop.TROOP) - defenderLosses);

        if (defenderTroops.get(DefaultTroop.TROOP) <= 0) {
            // Attacker wins
            int capturedTroops = targetHub.getTroops().get(DefaultTroop.TROOP) / 2;
            capturedTroops = Math.max(capturedTroops, 1); // Ensure at least one troop is captured
            attackerTroops.put(DefaultTroop.TROOP, attackerTroops.get(DefaultTroop.TROOP) + capturedTroops);
            defenderTroops.put(DefaultTroop.TROOP, 0);

            Player defender = targetHub.getownedHub();
            defender.removeHub(targetHub);
            sourceHub.addHub(targetHub);
            targetHub.setOwner(sourceHub.getHubOwner());
        } else if (attackerTroops.get(DefaultTroop.TROOP) <= 0) {
            // Defender wins
            attackerTroops.put(DefaultTroop.TROOP, 0);
        }

        sourceHub.setTroops(attackerTroops);
        targetHub.setTroops(defenderTroops);


        if (sourceHub.getTroops().get(DefaultTroop.TROOP) <= 1) {
            // Attacker no longer has enough troops to attack
            sourceHub = null;
            GameState.setAttackState(false);
            return;
        }
        Hub destinationHub = null;
        if (destinationHub.getOwner() == Player.getPlayerId()) {
            // Defender cannot be attacked as they own the hub
            sourceHub = null;
            GameState.setAttackState(false);
            return;
        }

        DefaultTroop attackingTroop = DefaultTroop.TROOP;
        int attackingTroopCount = sourceHub.getTroops().get(attackingTroop);

        DefaultTroop defendingTroop = DefaultTroop.TROOP;
        int defendingTroopCount = destinationHub.getTroops().get(defendingTroop);

// Attack with up to three troops, but not more than the number of troops on the hub
        int attackingCount = Math.min(attackingTroopCount - 1, 3);

        if (attackingCount == 0) {
            // Attacker no longer has enough troops to attack
            sourceHub = null;
            GameState.setAttackState(false);
            return;
        }

// Roll dice for attacker
        int[] attackerDiceRolls = dice.roll(attackingCount);

// Roll dice for defender
        int[] defenderDiceRolls = dice.roll(Math.min(defendingTroopCount, 2));

// Sort dice rolls in descending order
        Arrays.sort(attackerDiceRolls);
        Arrays.sort(defenderDiceRolls);

        reverse(attackerDiceRolls);
        reverse(defenderDiceRolls);

// Determine number of troops lost by attacker and defender
        attackerLosses = 0;
        defenderLosses = 0;
        for (int i = 0; i < Math.min(attackingCount, defenderDiceRolls.length); i++) {
            if (attackerDiceRolls[i] > defenderDiceRolls[i]) {
                defenderLosses++;
            } else {
                attackerLosses++;
            }
        }

// Update troop counts on hubs
        sourceHub.getTroops().put(attackingTroop, attackingTroopCount - attackerLosses);
        destinationHub.getTroops().put(defendingTroop, defendingTroopCount - defenderLosses);

        if (destinationHub.getTroops().get(DefaultTroop.TROOP) == 0) {
            // Attacker has won the battle and conquered the destination hub
            destinationHub.setOwner(Player.getPlayerId());
            destinationHub.getTroops().put(DefaultTroop.TROOP, attackingCount - attackerLosses);
            sourceHub.getTroops().put(DefaultTroop.TROOP, sourceHub.getTroops().get(DefaultTroop.TROOP) - attackingCount + attackerLosses);
            GameState.setAttackState(false);
            GameState.checkForVictory();
        }
*/

    }

