package at.aau.wagnis.gamestate;

import java.util.List;
import at.aau.wagnis.Hub;

public class ChooseMoveState extends GameLogicState {

    Hub sourceHub;
    Hub targetHub;


    @Override
    public void chooseMove(int playerId, int sourceHubId, int targetHubId, int numTroops) {

        List<Hub> hubs = this.gameServer.getGameData().getHubs();

        for(Hub h : hubs){
            if(h.getId()==sourceHubId){
                sourceHub=h;
                // Findet den Hub welcher übereinstimmt und weise ihn sourceHub zu
            }
            if(h.getId()==targetHubId){
                targetHub=h;
                // Findet den Hub welcher übereinstimmt und weise ihn targetHub zu
            }
        }
        this.gameServer.setGameLogicState(new MoveTroopsState(sourceHubId, targetHubId, numTroops));
    }



    @Override
    public void next(){
        gameServer.getGameData().nextPlayer();
        gameServer.setGameLogicState(new ReinforceGameState());
    }
}