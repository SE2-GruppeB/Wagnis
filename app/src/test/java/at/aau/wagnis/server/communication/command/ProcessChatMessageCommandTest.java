package at.aau.wagnis.server.communication.command;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ProcessChatMessageCommandTest {

    ProcessChatMessageCommand.CommandSerializer commandSerializer;

    @Before
    public void setup() {
        commandSerializer = new ProcessChatMessageCommand.CommandSerializer();
    }

    @Test
    public void canBeSerializedAndDeserialized() throws IOException {
        // given
        ProcessChatMessageCommand input = new ProcessChatMessageCommand("Hello, World!");

        // when
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        commandSerializer.writeToStream(input, dataOutputStream);

        byte[] bytes = byteArrayOutputStream.toByteArray();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
        ProcessChatMessageCommand result = commandSerializer.readFromStream(dataInputStream);

        // then
        assertEquals(input, result);
    }

}