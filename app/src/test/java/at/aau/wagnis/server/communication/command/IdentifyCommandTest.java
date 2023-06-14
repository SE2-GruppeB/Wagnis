package at.aau.wagnis.server.communication.command;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

class IdentifyCommandTest {

    String playerIpAddress;
    IdentifyCommand command;

    @BeforeEach
    void setUp() {
        playerIpAddress = "999.99.10.1";
        command = new IdentifyCommand(playerIpAddress);
    }

    @Test
    void getIpAddress() {
        assertEquals(playerIpAddress, command.getIpAddress());
    }

    @Test
    void testGetClientIdIfNotSet() {
        assertThrows(IllegalStateException.class, command::getClientId);
    }

    @Test
    void testGetSetClientId() {
        int expectedId = 7;
        command.setClientId(expectedId);

        assertEquals(expectedId, command.getClientId());
    }

    @Test
    void testTargetClass() {
        IdentifyCommand.CommandSerializer commandSerializer = new IdentifyCommand.CommandSerializer();

        assertEquals(IdentifyCommand.class, commandSerializer.getTargetClass());
    }

    @ParameterizedTest
    @ValueSource(strings = "identify-command")
    void testTypeTag(String expectedTypeTag) {
        IdentifyCommand.CommandSerializer commandSerializer = new IdentifyCommand.CommandSerializer();

        assertEquals(expectedTypeTag, commandSerializer.getTypeTag());
    }

    @Test
    void testTypeTag() throws IOException {
        IdentifyCommand.CommandSerializer commandSerializer = new IdentifyCommand.CommandSerializer();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        commandSerializer.writeToStream(command, dataOutputStream);

        byte[] bytes = byteArrayOutputStream.toByteArray();

        InputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
        IdentifyCommand result = commandSerializer.readFromStream(dataInputStream);


        assertEquals(playerIpAddress, result.getIpAddress());
    }
}