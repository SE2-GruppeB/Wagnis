package at.aau.wagnis.server.communication.command;

import androidx.annotation.NonNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import at.aau.wagnis.gamestate.GameLogicState;
import at.aau.wagnis.server.communication.serialization.Serializer;

public class SelectHubCommand implements ClientOriginatedServerCommand{

    private int hubId;
    private int clientId;

    public SelectHubCommand(int hubId){
        this.hubId = hubId;
    }

    public int getHubId() {
        return hubId;
    }

    @Override
    public int getClientId() {
        return clientId;
    }

    @Override
    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    @Override
    public void execute(@NonNull GameLogicState gameLogicState) { // das was auf dem Server ausgefuert wird
        gameLogicState.selectHub(this.hubId);
    }

    public static class CommandSerializer implements Serializer<SelectHubCommand>{
        //wie es vom Sever zum Client geschickt wird, der Spieler der gerade dran ist, die uebertargung vom Server

        @NonNull
        @Override
        public Class<SelectHubCommand> getTargetClass() {
            return SelectHubCommand.class;
        }

        @NonNull
        @Override
        public String getTypeTag() {
            return "select-hub-command";
        }

        @Override
        public void writeToStream(@NonNull SelectHubCommand obj, @NonNull DataOutputStream stream) throws IOException {
            stream.writeInt(obj.getHubId());
        }

        @NonNull
        @Override
        public SelectHubCommand readFromStream(@NonNull DataInputStream stream) throws IOException {
            return new SelectHubCommand(stream.readInt());
        }
    }
}
