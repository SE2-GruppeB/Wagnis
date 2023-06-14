package at.aau.wagnis.server.communication.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

 class UseCardsCommandTest {

    private UseCardsCommand command;


    @BeforeEach
    void setup() {
        command = new UseCardsCommand();
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
}
