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
    public void throwsForNullMessage() {
        // when & then
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new ProcessChatMessageCommand(null)
        );

        assertEquals("Message can't be null!", ex.getMessage());
    }

    @Test
    public void throwsForEmptyMessage() {
        // when & then
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new ProcessChatMessageCommand("")
        );

        assertEquals("Message can't be empty!", ex.getMessage());
    }

    @Test
    public void throwsForNewLine() {
        // when & then
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new ProcessChatMessageCommand("\n")
        );

        assertEquals("Message can't contain new line!", ex.getMessage());
    }

    @Test
    public void throwsForCarriageReturn() {
        // when & then
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new ProcessChatMessageCommand("\r")
        );

        assertEquals("Message can't contain carriage return!", ex.getMessage());
    }


    @Test
    public void throwsForLongMessage() {
        //given
        StringBuilder sb = new StringBuilder(201);

        for (int i = 0; i < 201; i++) {
            sb.append("a");
        }

        String input = sb.toString();

        // when & then
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new ProcessChatMessageCommand(input)
        );

        assertEquals("Message too long!", ex.getMessage());
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
