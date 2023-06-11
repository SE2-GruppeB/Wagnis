package at.aau.wagnis.server.communication.command;

import androidx.annotation.NonNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import at.aau.wagnis.gamestate.GameLogicState;
import at.aau.wagnis.server.communication.serialization.Serializer;

public class ChooseAttackCommand implements ClientOriginatedServerCommand {

    private final int sourceHubId;
    private final int targetHubId;

    public ChooseAttackCommand(int sourceHubId, int targetHubId) {
        this.sourceHubId = sourceHubId;
        this.targetHubId = targetHubId;
    }

    @Override
    public int getClientId() {
        return 0;
    }

    @Override
    public void setClientId(int clientId) {
        /*Empty because useless, required because of implements*/
    }

    @Override
    public void execute(@NonNull GameLogicState gameLogicState) {
        gameLogicState.chooseAttack(getClientId(), sourceHubId, targetHubId);
    }

    public static class CommandSerializer implements Serializer<ChooseAttackCommand> {

        @NonNull
        @Override
        public Class<ChooseAttackCommand> getTargetClass() {
            return ChooseAttackCommand.class;
        }

        @NonNull
        @Override
        public String getTypeTag() {
            return "choose-attack";
        }

        @Override
        public void writeToStream(
                @NonNull ChooseAttackCommand command,
                @NonNull DataOutputStream stream
        ) throws IOException {
            stream.writeInt(command.sourceHubId);
            stream.writeInt(command.targetHubId);
        }

        @NonNull
        @Override
        public ChooseAttackCommand readFromStream(@NonNull DataInputStream stream) throws IOException {
            return new ChooseAttackCommand(stream.readInt(), stream.readInt());
        }
    }
}
