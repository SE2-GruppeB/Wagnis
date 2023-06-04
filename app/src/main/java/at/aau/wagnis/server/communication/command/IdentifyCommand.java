package at.aau.wagnis.server.communication.command;

import androidx.annotation.NonNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import at.aau.wagnis.gamestate.GameLogicState;
import at.aau.wagnis.gamestate.LobbyState;
import at.aau.wagnis.server.communication.serialization.Serializer;

public class IdentifyCommand implements ClientOriginatedServerCommand{

    private String ipAddress;
    private int clientId;

    public IdentifyCommand(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    @Override
    public int getClientId() {
        return this.clientId;
    }

    @Override
    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    @Override
    public void execute(@NonNull GameLogicState gameLogicState) {
        if(gameLogicState instanceof LobbyState) {
            ((LobbyState) gameLogicState).addPlayer(ipAddress);
        }
    }

    public class CommandSerializer implements Serializer<IdentifyCommand>{

        @NonNull
        @Override
        public Class<IdentifyCommand> getTargetClass() {
            return IdentifyCommand.class;
        }

        @NonNull
        @Override
        public String getTypeTag() {
            return "identify-command";
        }

        @Override
        public void writeToStream(@NonNull IdentifyCommand obj, @NonNull DataOutputStream stream) throws IOException {
            stream.writeUTF(obj.getIpAddress());
        }

        @NonNull
        @Override
        public IdentifyCommand readFromStream(@NonNull DataInputStream stream) throws IOException {
            return new IdentifyCommand(stream.readUTF());
        }
    }
}
