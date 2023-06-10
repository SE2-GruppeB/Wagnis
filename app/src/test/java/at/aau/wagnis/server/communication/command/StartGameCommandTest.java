package at.aau.wagnis.server.communication.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

import at.aau.wagnis.gamestate.LobbyState;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class StartGameCommandTest {
    private StartGameCommand command;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);

        command = new StartGameCommand();
    }

    @Test
    void testClientId() {
        command.setClientId(1);
        assertEquals(1,command.getClientId());
    }

    @ParameterizedTest
    @ValueSource(strings = "start-game-command")
    void testGetTypeTag(String expected){
        StartGameCommand.CommandSerializer commandSerializer = new StartGameCommand.CommandSerializer();

        assertEquals(expected, commandSerializer.getTypeTag());
    }

    @Test
    void testGetTargetClass(){
        StartGameCommand.CommandSerializer commandSerializer = new StartGameCommand.CommandSerializer();

        assertEquals(StartGameCommand.class, commandSerializer.getTargetClass());
    }

    @Test
    void testExecute(){
        LobbyState gameLogicState = mock(LobbyState.class);
        command.execute(gameLogicState);
        verify(gameLogicState).next();
    }

    @Test
    void testReadWriteToStream() throws IOException {
        StartGameCommand.CommandSerializer commandSerializer = new StartGameCommand.CommandSerializer();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        commandSerializer.writeToStream(command, dataOutputStream);

        byte[] bytes = byteArrayOutputStream.toByteArray();

        InputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
        assertEquals(StartGameCommand.class,commandSerializer.readFromStream(dataInputStream).getClass());
    }
}