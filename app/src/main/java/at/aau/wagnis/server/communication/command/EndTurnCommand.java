package at.aau.wagnis.server.communication.command;

import androidx.annotation.NonNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import at.aau.wagnis.gamestate.GameLogicState;
import at.aau.wagnis.server.communication.serialization.Serializer;

public class EndTurnCommand implements ClientOriginatedServerCommand {
    private Integer clientId = null;

    public EndTurnCommand(){
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
        gameLogicState.next();
    }

    public static class CommandSerializer implements Serializer<EndTurnCommand> {

        @NonNull
        @Override
        public Class<EndTurnCommand> getTargetClass() {
            return EndTurnCommand.class;
        }

        @NonNull
        @Override
        public String getTypeTag() {
            return "end-turn-command";
        }

        @Override
        public void writeToStream(@NonNull EndTurnCommand obj, @NonNull DataOutputStream stream) throws IOException {
            stream.writeUTF("endTurn");
        }

        @NonNull
        @Override
        public EndTurnCommand readFromStream(@NonNull DataInputStream stream) throws IOException {
            stream.readUTF();
            return new EndTurnCommand();
        }
    }
}
