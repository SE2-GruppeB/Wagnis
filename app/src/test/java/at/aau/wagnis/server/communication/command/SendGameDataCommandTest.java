package at.aau.wagnis.server.communication.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import at.aau.wagnis.client.ClientLogic;
import at.aau.wagnis.gamestate.ChatMessage;
import at.aau.wagnis.gamestate.GameData;

class SendGameDataCommandTest {

    @Mock private GameData gameData;
    @Mock private ChatMessage chatMessage;
    @Mock private GameData gameData2;

    private SendGameDataCommand command;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);

        command = new SendGameDataCommand(gameData);
    }


    @Test
    void testExecute(){
        ClientLogic clientLogic = mock(ClientLogic.class);

        command.execute(clientLogic);

        verify(clientLogic).updateGameState(any());
    }

    @Test
    void testGetTargetClass(){
        SendGameDataCommand.CommandSerializer commandSerializer = new SendGameDataCommand.CommandSerializer();

        assertEquals(SendGameDataCommand.class, commandSerializer.getTargetClass());
    }

    @ParameterizedTest
    @ValueSource(strings = "send-game-state")
    void testGetTypeTag(String expected){
        SendGameDataCommand.CommandSerializer commandSerializer = new SendGameDataCommand.CommandSerializer();

        assertEquals(expected, commandSerializer.getTypeTag());
    }

    @Test
    void serializeAndderserializeWithMsg() throws IOException {
        when(gameData.serialize()).thenReturn("serialzed");
        when((gameData.getMessages())).thenReturn(Collections.singletonList(chatMessage));
        when(chatMessage.getClientId()).thenReturn(1);
        when(chatMessage.getMessage()).thenReturn("hello");

        SendGameDataCommand.CommandSerializer commandSerializer = new SendGameDataCommand.CommandSerializer();
        commandSerializer.setGameDataSupplier(() -> gameData2);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        commandSerializer.writeToStream(command, dataOutputStream);

        byte[] bytes = byteArrayOutputStream.toByteArray();

        InputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
        SendGameDataCommand result = commandSerializer.readFromStream(dataInputStream);

        verify(gameData2).addMessage(1,"hello");
        verify(gameData2).deserialize("serialzed");
    }

    @Test
    void serializeAndderserializeWithoutMsg() throws IOException {
        when(gameData.serialize()).thenReturn("serialzed");
        when((gameData.getMessages())).thenReturn(Collections.emptyList());

        SendGameDataCommand.CommandSerializer commandSerializer = new SendGameDataCommand.CommandSerializer();
        commandSerializer.setGameDataSupplier(() -> gameData2);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        commandSerializer.writeToStream(command, dataOutputStream);

        byte[] bytes = byteArrayOutputStream.toByteArray();

        InputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
        SendGameDataCommand result = commandSerializer.readFromStream(dataInputStream);

        verify(gameData2,never()).addMessage(anyInt(),any());
        verify(gameData2).deserialize("serialzed");
    }
}
