package at.aau.wagnis.server.communication.command;

import androidx.annotation.NonNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;

import at.aau.wagnis.client.ClientLogic;
import at.aau.wagnis.gamestate.ChatMessage;
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
        clientLogic.updateGameData(gameData);
    }

    public static class CommandSerializer implements Serializer<SendGameDataCommand>{

        public  void setGameDataSupplier(Supplier<GameData> gameDataSupplier) {
            this.gameDataSupplier = gameDataSupplier;
        }

        private  Supplier<GameData> gameDataSupplier = GameData::new;

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
            List<ChatMessage> messages = obj.getGameData().getMessages();
            stream.writeInt(messages.size());
            for (ChatMessage message : messages) {
                stream.writeInt(message.getClientId());
                stream.writeUTF(message.getMessage());
            }
        }

        @NonNull
        @Override
        public SendGameDataCommand readFromStream(@NonNull DataInputStream stream) throws IOException {
            GameData gameData = gameDataSupplier.get();
            gameData.deserialize(stream.readUTF());
            int messageCount = stream.readInt();
            for (int i = 0; i < messageCount; i++) {
                int clientId = stream.readInt();
                String message = stream.readUTF();
                gameData.addMessage(clientId,message);
            }
            return new SendGameDataCommand(gameData);
        }
    }
}
