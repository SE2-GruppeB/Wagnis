package at.aau.wagnis.server.communication.command;

import androidx.annotation.NonNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import at.aau.wagnis.gamestate.GameLogicState;
import at.aau.wagnis.server.communication.serialization.Serializer;

public class ReinforceCommand implements ClientOriginatedServerCommand{

    private final List<Integer> hubs;
    private final List<Integer> troopsToDeploy;
    private Integer clientId;

    public ReinforceCommand(List<Integer> hubs, List<Integer> troopsToDeploy) {
        this.hubs = hubs;
        this.troopsToDeploy = troopsToDeploy;
    }


    public List<Integer> getHubs() {
        return hubs;
    }

    public List<Integer> getTroopsToDeploy() {
        return troopsToDeploy;
    }

    @Override
    public int getClientId() {
        if (this.clientId != null) {
            return clientId;
        } else {
            throw new IllegalStateException("ClientId has not been set");
        }
    }

    @Override
    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    @Override
    public void execute(@NonNull GameLogicState gameLogicState) {
        gameLogicState.reinforce(hubs, troopsToDeploy);
    }

    public static class CommandSerializer implements Serializer<ReinforceCommand> {

        @NonNull
        @Override
        public Class<ReinforceCommand> getTargetClass() {
            return ReinforceCommand.class;
        }

        @NonNull
        @Override
        public String getTypeTag() {
            return "reinforce-command";
        }

        @Override
        public void writeToStream(@NonNull ReinforceCommand obj, @NonNull DataOutputStream stream) throws IOException {
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
        public ReinforceCommand readFromStream(@NonNull DataInputStream stream) throws IOException {
            int hubCount = stream.readInt();
            List<Integer> troops = new ArrayList<>();
            List<Integer> hubIDs = new ArrayList<>();
            for (int i = 0; i < hubCount; i++) {
                int hubId = stream.readInt();
                int troop = stream.readInt();
                hubIDs.add(hubId);
                troops.add(troop);
            }
            return new ReinforceCommand(hubIDs, troops);
        }
    }




}
