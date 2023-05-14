package at.aau.wagnis.server.communication.command;

import androidx.annotation.NonNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import at.aau.wagnis.client.ClientLogic;
import at.aau.wagnis.gamestate.GameData;
import at.aau.wagnis.server.communication.serialization.Serializer;

public class SendGameStateCommand implements ClientCommand{

    private final GameData gameData;

    public SendGameStateCommand(GameData gameData){
        this.gameData = gameData;
    }

    public GameData getGameState() {
        return gameData;
    }

    @Override
    public void execute(@NonNull ClientLogic clientLogic) {
        clientLogic.updateGameState(gameData);
    }

    public static class CommandSerializer implements Serializer<SendGameStateCommand>{

        @NonNull
        @Override
        public Class<SendGameStateCommand> getTargetClass() {
            return SendGameStateCommand.class;
        }

        @NonNull
        @Override
        public String getTypeTag() {
            return "send-game-state";
        }

        @Override
        public void writeToStream(@NonNull SendGameStateCommand obj, @NonNull DataOutputStream stream) throws IOException {
            stream.writeUTF(obj.getGameState().serialize());
        }

        @NonNull
        @Override
        public SendGameStateCommand readFromStream(@NonNull DataInputStream stream) throws IOException {
            GameData gameData = new GameData();
            gameData.deserialize(stream.readUTF());
            return new SendGameStateCommand(gameData);
        }
    }
}
