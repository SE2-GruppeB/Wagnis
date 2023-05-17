package at.aau.wagnis.gamestate;

import androidx.annotation.NonNull;

import java.security.SecureRandom;
import java.util.Random;

import at.aau.wagnis.Hub;
import at.aau.wagnis.Player;
import at.aau.wagnis.client.ClientLogic;
import at.aau.wagnis.server.communication.command.ClientCommand;

/**
 * In der Attacker Game-State Klasse werden Angriffe auf Hubs abgehandelt.
 */

public class AttackGameState extends GameLogicState {
    private boolean attacker = false;
    private boolean defender = false;
    private Hub sourceHub;
    private Hub targetHub;
    private int sourceHubId;
    private int targetHubId;

    public AttackGameState(int sourceHubId, int targetHubId) {
        this.sourceHubId = sourceHubId;
        this.targetHubId = targetHubId;
    }

    public AttackGameState(Hub sourceHub, Hub targetHub) {
        this.sourceHub = sourceHub;
        this.targetHub = targetHub;
    }

    @Override
    public void attack() {
        int attackerDiceRolls = diceRoll();
        int defenderDiceRolls = diceRoll();

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
            Player attackingPlayer = this.sourceHub.getOwner();
            attackingPlayer.addHub(this.targetHub);
            Player defendingPlayer = targetHub.getOwner();
            defendingPlayer.removeHub(this.targetHub);
            if (gameWon(attackingPlayer)) {
                this.gameServer.setGameLogicState(new VictoryState(attackingPlayer));
                //Send winner id via broadcast to all clients
                this.gameServer.broadcastCommand(new ClientCommand() {
                    @Override
                    public void execute(@NonNull ClientLogic clientLogic) {
                        clientLogic.updateGameLogicState(new VictoryState(attackingPlayer));
                    }
                });
            } else {
                this.gameServer.setGameLogicState(new ChooseAttackGameState());
            }
        }
    }

    public boolean gameWon(Player player) {
        return player.getOwnedHubs().size() == 42;
    }

    private int diceRoll() {
        Random randomGen = new SecureRandom();
        int diceValue = randomGen.nextInt(6) + 1;
        //Log.d("Info :","" + diceValue);
        return diceValue;
    }

    public Hub getSourceHub() {
        return sourceHub;
    }

    public Hub getTargetHub() {
        return targetHub;
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

    public int getSourceHubId() {return sourceHubId;}

    public void setSourceHubId(int sourceHubId) {
        this.sourceHubId = sourceHubId;
    }

    public int getTargetHubId() {
        return targetHubId;
    }

    public void setTargetHubId(int targetHubId) {
        this.targetHubId = targetHubId;
    }

    public void setSourceHub(Hub sourceHub) {
        this.sourceHub = sourceHub;
    }

    public void setTargetHub(Hub targetHub) {
        this.targetHub = targetHub;
    }
}

