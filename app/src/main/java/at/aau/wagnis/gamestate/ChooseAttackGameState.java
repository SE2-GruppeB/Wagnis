package at.aau.wagnis.gamestate;

public class ChooseAttackGameState extends GameLogicState {
    /**
     * Wählt einen Angriff aus und ändert den Spielzustand entsprechend.
     *
     * @param playerId    Die ID des Spielers, der den Angriff auswählt.
     * @param sourceHubId Die ID des Hubs, von dem aus der Angriff erfolgt.
     * @param targetHubId Die ID des Ziels des Angriffs.
     */
    @Override
    public void chooseAttack(int playerId, int sourceHubId, int targetHubId) {
        this.gameServer.getGameData();
        this.gameServer.setGameLogicState(new AttackGameState(sourceHubId, targetHubId));
    }
}
