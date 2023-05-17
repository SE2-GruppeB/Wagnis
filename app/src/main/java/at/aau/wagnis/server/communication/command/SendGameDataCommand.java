package at.aau.wagnis.server.communication.command;

import androidx.annotation.NonNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import at.aau.wagnis.client.ClientLogic;
import at.aau.wagnis.gamestate.GameData;
import at.aau.wagnis.server.communication.serialization.Serializer;

public class SendGameDataCommand implements ClientCommand{

    private final GameData gameData;

    public SendGameDataCommand(GameData gameData){
        this.gameData = gameData;
    }

    public GameData getGameData() {
        return gameData;
    }

    @Override
    public void execute(@NonNull ClientLogic clientLogic) {
        clientLogic.updateGameState(gameData);
    }

    public static class CommandSerializer implements Serializer<SendGameDataCommand>{

        @NonNull
        @Override
        public Class<SendGameDataCommand> getTargetClass() {
            return SendGameDataCommand.class;
        }

        @NonNull
        @Override
        public String getTypeTag() {
            return "send-game-state";
        }

        @Override
        public void writeToStream(@NonNull SendGameDataCommand obj, @NonNull DataOutputStream stream) throws IOException {
            stream.writeUTF(obj.getGameData().serialize());
        }

        @NonNull
        @Override
        public SendGameDataCommand readFromStream(@NonNull DataInputStream stream) throws IOException {
            GameData gameData = new GameData();
            gameData.deserialize(stream.readUTF());
            return new SendGameDataCommand(gameData);
        }
    }
}
