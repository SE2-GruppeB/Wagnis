package at.aau.wagnis.gamestate;

import at.aau.wagnis.server.GameServer;

public class EndGameState extends GameLogicState{

    GameServer gameServer;

    public EndGameState(GameServer gameServer){
        this.gameServer = gameServer;
    }
    //Todo
    @Override
    public void end(){
        //gameServer.broadcastCommand(null);  // send a command to all players to close the connection

        gameServer.close(); // close the gameserver
    }
}
