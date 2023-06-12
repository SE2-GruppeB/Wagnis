package at.aau.wagnis.server.communication.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
import java.util.ArrayList;
import java.util.List;

class ReinforceCommandTest {

    ReinforceCommand command;

    private  List<Integer> hubs;
    private  List<Integer> troopsToDeploy;

    @BeforeEach
    void setUp() {
        hubs = new ArrayList<Integer>();
        troopsToDeploy= new ArrayList<Integer>();
        hubs.add(1);
        troopsToDeploy.add(1);
        command = new ReinforceCommand(hubs,troopsToDeploy);
    }



     @Test
     void getHubsTest() {
         assertEquals(command.getHubs(),hubs);
     }


     @Test
     void getTroopsTest() {
         assertEquals(command.getTroopsToDeploy(),troopsToDeploy);
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
    void testTargetClass() {
        ReinforceCommand.CommandSerializer commandSerializer = new ReinforceCommand.CommandSerializer();
        assertEquals(ReinforceCommand.class, commandSerializer.getTargetClass());
    }

     @ParameterizedTest
     @ValueSource(strings = "reinforce-command")
     void testTypeTag(String expectedTypeTag) {
         ReinforceCommand.CommandSerializer commandSerializer = new ReinforceCommand.CommandSerializer();

         assertEquals(expectedTypeTag, commandSerializer.getTypeTag());
     }

     @Test
     void readWriteStream() throws IOException {
         ReinforceCommand.CommandSerializer commandSerializer = new ReinforceCommand.CommandSerializer();

         ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
         DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
         commandSerializer.writeToStream(command, dataOutputStream);

         byte[] bytes = byteArrayOutputStream.toByteArray();

         InputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
         DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
         ReinforceCommand result = commandSerializer.readFromStream(dataInputStream);
         assertEquals(hubs, result.getHubs());
         assertEquals(troopsToDeploy, result.getTroopsToDeploy());
     }
}
