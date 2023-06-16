package at.aau.wagnis.gamestate;


import java.util.List;
import at.aau.wagnis.Hub;

public class ReinforceGameState extends GameLogicState{



    public void reinforceSingle (int hubId, int troops){
        List<Hub> hubsFromServer = this.gameServer.getGameData().getHubs();
        for (Hub hub : hubsFromServer) {
            if (hub.getId() == hubId){
                hub.addTroops(troops);
            }
        }
    }
    @Override
    public void next(){
        gameServer.setGameLogicState(new ChooseAttackGameState());
    }
}
