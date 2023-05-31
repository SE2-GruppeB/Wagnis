package at.aau.wagnis.gamestate;

import java.security.SecureRandom;
import java.util.Random;

import at.aau.wagnis.Hub;
import at.aau.wagnis.Player;

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

    public int testAttackerDiceRoll;

    public int testDefenderDiceRoll;



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

        int attackerDiceRolls;
        int defenderDiceRolls;
        int attackerTroops = sourceHub.getAmountTroops();
        int defenderTroops = targetHub.getAmountTroops();

        if (this.sourceHub.getAmountTroops() == 1 || this.targetHub.getAmountTroops() <= 0) {
            throw new IllegalArgumentException("Illegal attack");
        }

        switch (attackerTroops){
            case 2:{
                attackerDiceRolls = diceRoll();
                testAttackerDiceRoll = 1;
                // Attacker greift mit einer Truppe an
            }
            case 3:{
                attackerDiceRolls = diceRoll() + diceRoll();
                testAttackerDiceRoll = 2;
                //Attacker greift mit zwei Truppen an
            }
            default:{
                attackerDiceRolls = diceRoll() + diceRoll() + diceRoll();
                testAttackerDiceRoll = 3;
                //Attacker greift mit mehr als zwei Truppen an
            }
        }

        switch (defenderTroops){
            case 2:{
                defenderDiceRolls = diceRoll();
                testDefenderDiceRoll = 1;
                //Verteidiger besitzt nur eine Truppe am Feld
            }
            default:{
                defenderDiceRolls = diceRoll() + diceRoll();
                testDefenderDiceRoll = 2;
                //Verteidiger besitzt mehr als eine Truppe am Feld
            }
        }

        if (attackerDiceRolls > defenderDiceRolls) {
            this.targetHub.setAmountTroops(this.targetHub.getAmountTroops() - 1);
        } else {
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
                /*this.gameServer.broadcastCommand(new ClientCommand() {
                    @Override
                    public void execute(@NonNull ClientLogic clientLogic) {
                        clientLogic.updateGameLogicState(new VictoryState(attackingPlayer));
                    }
                });*/
            } else {
                this.gameServer.setGameLogicState(new ChooseAttackGameState());
            }
        }
    }

    public boolean gameWon(Player player) {
        return player.getOwnedHubs().size() == 42;
    }

    private int diceRoll() {
        Random randomGen = new Random();
        int diceValue = randomGen.nextInt(6) + 1;
        //Log.d("Info :","" + diceValue);
        return diceValue;
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

