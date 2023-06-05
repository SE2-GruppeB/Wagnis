package at.aau.wagnis.server.communication.command;

import androidx.annotation.NonNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import at.aau.wagnis.gamestate.GameLogicState;
import at.aau.wagnis.gamestate.LobbyState;
import at.aau.wagnis.server.communication.serialization.Serializer;

public class StartGameCommand implements ClientOriginatedServerCommand{
    private Integer clientId = null;

    public StartGameCommand(){
        super();
    }
    @Override
    public int getClientId() {
        return this.clientId;
    }

    @Override
    public void setClientId(int clientId) {
        this.clientId=clientId;
    }

    @Override
    public void execute(@NonNull GameLogicState gameLogicState) {
        ((LobbyState) gameLogicState).next();
    }

    public static class CommandSerializer implements Serializer<StartGameCommand> {

        @NonNull
        @Override
        public Class<StartGameCommand> getTargetClass() {
            return StartGameCommand.class;
        }

        @NonNull
        @Override
        public String getTypeTag() {
            return "start-game-command";
        }

        @Override
        public void writeToStream(@NonNull StartGameCommand obj, @NonNull DataOutputStream stream) throws IOException {
                stream.writeUTF("startGame");
        }

        @NonNull
        @Override
        public StartGameCommand readFromStream(@NonNull DataInputStream stream) throws IOException {
            String s = stream.readUTF();
            return new StartGameCommand();
        }
    }
}
