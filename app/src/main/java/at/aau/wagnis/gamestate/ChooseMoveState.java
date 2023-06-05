package at.aau.wagnis.gamestate;

public class ChooseMoveState extends GameLogicState {

    @Override
    public void chooseMove(int playerId, int sourceHubId, int targetHubId, int numTroops) {
        this.gameServer.getGameData();
        this.gameServer.setGameLogicState(new MoveTroopsState(sourceHubId, targetHubId));
        this.move(numTroops);

    }
}
