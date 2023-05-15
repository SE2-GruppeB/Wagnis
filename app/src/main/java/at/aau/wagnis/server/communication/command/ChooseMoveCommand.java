package at.aau.wagnis.server.communication.command;

import androidx.annotation.NonNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import at.aau.wagnis.gamestate.GameLogicState;
import at.aau.wagnis.server.communication.serialization.Serializer;

public class ChooseMoveCommand implements ClientOriginatedServerCommand {

    private final int sourceHubId;
    private final int targetHubId;
    private final int numTroops;

    public ChooseMoveCommand(int sourceHubId, int targetHubId, int numTroops) {
        this.sourceHubId = sourceHubId;
        this.targetHubId = targetHubId;
        this.numTroops = numTroops;
    }

    @Override
    public int getClientId() {
        return 0;
    }

    @Override
    public void setClientId(int clientId) {

    }

    @Override
    public void execute(@NonNull GameLogicState gameLogicState) {
        gameLogicState.chooseMove(getClientId(), sourceHubId, targetHubId, numTroops);

    }

    public static class CommandSerializer implements Serializer<ChooseMoveCommand> {

        @NonNull
        @Override
        public Class<ChooseMoveCommand> getTargetClass() {
            return ChooseMoveCommand.class;
        }

        @NonNull
        @Override
        public String getTypeTag() {
            return "choose-move";
        }

        @Override
        public void writeToStream(
                @NonNull ChooseMoveCommand command,
                @NonNull DataOutputStream stream
        ) throws IOException {
            stream.writeInt(command.sourceHubId);
            stream.writeInt(command.targetHubId);
            stream.writeInt(command.numTroops);
        }


        @NonNull
        @Override
        public ChooseMoveCommand readFromStream(@NonNull DataInputStream stream) throws IOException {
            return new ChooseMoveCommand(stream.readInt(), stream.readInt(), stream.readInt());
        }
    }
}
