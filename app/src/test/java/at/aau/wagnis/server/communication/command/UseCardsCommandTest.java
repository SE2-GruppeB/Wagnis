package at.aau.wagnis.server.communication.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

import at.aau.wagnis.gamestate.ChooseAttackGameState;
import at.aau.wagnis.gamestate.GameLogicState;

class UseCardsCommandTest {

    private UseCardsCommand command;

    @BeforeEach
    void setup() {
        command = new UseCardsCommand(0, 1, 2);
    }

    @Test
    void testGetClientIdIfNotSet() {
        assertThrows(IllegalStateException.class, command::getClientId);
    }

    @Test
    void testGetClientIdIfNotSetMessage() {
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            command.getClientId();
        });
        String expectedMessage = "ClientId has not been set";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void testGetSetClientId() {
        int expectedId = 7;
        command.setClientId(expectedId);

        assertEquals(expectedId, command.getClientId());
    }

    @Test
    void testGetTypeTag(){
        UseCardsCommand.CommandSerializer commandSerializer = new UseCardsCommand.CommandSerializer();

        assertEquals("use-cards-command", commandSerializer.getTypeTag());
    }

    @Test
    void testGetTargetClass(){
        UseCardsCommand.CommandSerializer commandSerializer = new UseCardsCommand.CommandSerializer();

        assertEquals(UseCardsCommand.class, commandSerializer.getTargetClass());
    }

    @Test
    void testExecute(){
        GameLogicState gameLogicState = mock(ChooseAttackGameState.class);
        command.execute(gameLogicState);
        verify(gameLogicState).useCards(anyInt(), anyInt(), anyInt());
    }

    @Test
    void testReadWriteToStream() throws IOException {
        UseCardsCommand.CommandSerializer commandSerializer = new UseCardsCommand.CommandSerializer();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        commandSerializer.writeToStream(command, dataOutputStream);

        byte[] bytes = byteArrayOutputStream.toByteArray();

        InputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
        assertEquals(UseCardsCommand.class, commandSerializer.readFromStream(dataInputStream).getClass());
    }
}
