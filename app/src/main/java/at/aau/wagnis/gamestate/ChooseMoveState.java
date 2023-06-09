package at.aau.wagnis.gamestate;

public class ChooseMoveState extends GameLogicState {

    @Override
    public void chooseMove(int playerId, int sourceHubId, int targetHubId, int numTroops) {
        // Holt die Spieldaten vom GameServer
        this.gameServer.getGameData();

        // Setzt den Spielzustand auf MoveTroopsState und übergibt die Hub-IDs
        this.gameServer.setGameLogicState(new MoveTroopsState(sourceHubId, targetHubId));

        // Verschiebt die Truppen
        this.move(numTroops);
    }
}