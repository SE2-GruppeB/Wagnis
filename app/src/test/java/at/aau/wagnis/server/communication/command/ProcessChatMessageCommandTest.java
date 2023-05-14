package at.aau.wagnis.server.communication.command;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ProcessChatMessageCommandTest {

    private ProcessChatMessageCommand command;

    @BeforeEach
    public void setup() {
        command = new ProcessChatMessageCommand("Hello, World!");
    }

    @Test
    public void getClientIdThrowsIfNotSet() {
        // when & then
        assertThrows(
                IllegalStateException.class,
                command::getClientId
        );
    }

    @Test
    public void getClientIdReturnsIdIfSet() {
        // given
        int clientId = 5;
        command.setClientId(clientId);

        // when
        int result = command.getClientId();

        // then
        assertEquals(clientId, result);
    }

    @Test
    public void canBeSerializedAndDeserialized() throws IOException {
        // given
        ProcessChatMessageCommand.CommandSerializer commandSerializer = new ProcessChatMessageCommand.CommandSerializer();

        // when
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        commandSerializer.writeToStream(command, dataOutputStream);

        byte[] bytes = byteArrayOutputStream.toByteArray();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
        ProcessChatMessageCommand result = commandSerializer.readFromStream(dataInputStream);

        // then
        assertEquals(command, result);
    }
}
