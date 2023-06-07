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
import java.util.Collections;

import at.aau.wagnis.client.ClientLogic;
import at.aau.wagnis.gamestate.GameLogicState;
import at.aau.wagnis.gamestate.LobbyState;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StartGameCommandTest {
    private StartGameCommand command;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);

        command = new StartGameCommand();
    }
    @Test
    void getClientId() {
        assertNull(command.getClientId());
    }

    @Test
    void setClientId() {
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
}