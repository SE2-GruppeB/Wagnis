package at.aau.wagnis.gamestate;

public class EndGameState extends GameLogicState {


    public EndGameState(){
        super();

    }

    //Todo
    @Override
    public void end() {
        //gameServer.broadcastCommand(null);  // send a command to all players to close the connection

        gameServer.close(); // close the gameserver
    }
}
