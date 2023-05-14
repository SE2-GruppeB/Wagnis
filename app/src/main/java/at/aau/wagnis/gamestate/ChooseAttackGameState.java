package at.aau.wagnis.gamestate;

public class ChooseAttackGameState extends GameLogicState {
    @Override
    public void chooseAttack(int playerId, int sourceHubId, int targetHubId) {
        this.gameServer.getGameState();
        this.gameServer.setGameLogicState(new AttackGameState(sourceHubId, targetHubId));
    }
}
