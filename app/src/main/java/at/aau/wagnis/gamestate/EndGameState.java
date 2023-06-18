package at.aau.wagnis.gamestate;

/**
 * Der Zustand des Spiellogik in welchem das Spiel beendet ist.
 */
public class EndGameState extends GameLogicState {

    /**
     * Erzeugt eine neue Instanz des EndGameState.
     */
    public EndGameState() {
        super();
    }

    /**
     * Beendet das Spiel, indem der GameServer geschlossen wird.
     */
    @Override
    public void end() {
        //gameServer.broadcastCommand(null);  // send a command to all players to close the connection

        gameServer.close();
        // Schließt den GameServer
    }
}
