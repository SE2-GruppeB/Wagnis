package at.aau.wagnis.server.communication.command;

import androidx.annotation.NonNull;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import at.aau.wagnis.gamestate.GameLogicState;
import at.aau.wagnis.server.communication.serialization.Serializer;

public class UseCardsCommand implements ClientOriginatedServerCommand{

    private Integer clientId;
    private int index1;
    private int index2;
    private int index3;

    public UseCardsCommand(int index1, int index2, int index3) {
        this.index1 = index1;
        this.index2 = index2;
        this.index3 = index3;
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
        gameLogicState.useCards(index1, index2, index3);
    }

    public static class CommandSerializer implements Serializer<UseCardsCommand> {

        @NonNull
        @Override
        public Class<UseCardsCommand> getTargetClass() {
            return UseCardsCommand.class;
        }

        @NonNull
        @Override
        public String getTypeTag() {
            return "use-cards-command";
        }

        @Override
        public void writeToStream(@NonNull UseCardsCommand obj, @NonNull DataOutputStream stream) throws IOException {
            stream.writeInt(obj.index1);
            stream.writeInt(obj.index2);
            stream.writeInt(obj.index3);
        }

        @NonNull
        @Override
        public UseCardsCommand readFromStream(@NonNull DataInputStream stream) throws IOException {
            return new UseCardsCommand(stream.readInt(), stream.readInt(), stream.readInt());
        }
    }
}
