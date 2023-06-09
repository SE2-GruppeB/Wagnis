package at.aau.wagnis.server.communication.command;

import androidx.annotation.NonNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import at.aau.wagnis.Hub;
import at.aau.wagnis.gamestate.GameLogicState;
import at.aau.wagnis.gamestate.ReinforceGameState;
import at.aau.wagnis.server.communication.serialization.Serializer;

public class ReinforceCommand implements ClientOriginatedServerCommand{

    private final List<Hub> hubs;
    private final List<Integer> troopsToDeploy;

    public ReinforceCommand(List<Hub> hubs, List<Integer> troopsToDeploy) {
        this.hubs = hubs;
        this.troopsToDeploy = troopsToDeploy;
    }



    @Override
    public int getClientId() {
        return 0 ;
    }

    @Override
    public void setClientId(int clientId) {

    }

    @Override
    public void execute(@NonNull GameLogicState gameLogicState) {
        gameLogicState.reinforce();
    }

    public static class CommandSerializer implements Serializer<ReinforceGameState> {

        @NonNull
        @Override
        public Class<ReinforceGameState> getTargetClass() {
            return ReinforceGameState.class;
        }

        @NonNull
        @Override
        public String getTypeTag() {
            return "choose-move";
        }

        @Override
        public void writeToStream(@NonNull ReinforceGameState obj, @NonNull DataOutputStream stream) throws IOException {
            List<Integer> hubs = obj.getHubs();
            List<Integer> troops = obj.getTroopsToDeploy();
            stream.write(hubs.size());
            for (int i = 0; i < hubs.size(); i++) {
                stream.writeInt(hubs.get(i));
                stream.writeInt(troops.get(i));
            }

        }

        @NonNull
        @Override
        public ReinforceGameState readFromStream(@NonNull DataInputStream stream) throws IOException {
            int hubCount = stream.readInt();
            List<Integer> troops = new ArrayList<>();
            List<Integer> hubIDs = new ArrayList<>();
            for (int i = 0; i < hubCount; i++) {
                int hubId = stream.readInt();
                int troop = stream.readInt();
                hubIDs.add(hubId);
                troops.add(troop);
            }
            return new ReinforceGameState(hubIDs, troops);
        }
    }




}
