package at.aau.wagnis.server.communication.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
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
import java.util.ArrayList;
import java.util.List;

import at.aau.wagnis.Cards;
import at.aau.wagnis.Hub;
import at.aau.wagnis.Player;
import at.aau.wagnis.client.ClientLogic;
import at.aau.wagnis.gamestate.GameData;

class SendGameDataCommandTest {


    @Mock private Hub hub1;
    @Mock private Hub hub2;
    @Mock private Hub hub3;

    @Mock private Player player1;
    @Mock private Player player2;

    private GameData gameData;
    private SendGameDataCommand command;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);

        gameData = initGameData();

        command = new SendGameDataCommand(gameData);
    }

    private GameData initGameData() {
        gameData = new GameData();

        String seed = "1234567890";
        gameData.setSeed(seed);

        List<Hub> hubs = new ArrayList<>();
        hubs.add(hub1);
        hubs.add(hub2);
        hubs.add(hub3);
        gameData.setHubs(hubs);

        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        gameData.setPlayers(players);

        return gameData;
    }


    @Test
    void testSerialiseAndDeserializeGameState() throws IOException {
        when(player1.getPlayerId()).thenReturn(1);
        when(player2.getPlayerId()).thenReturn(2);
        when(player1.getUnassignedAvailableTroops()).thenReturn(11);
        when(player2.getUnassignedAvailableTroops()).thenReturn(12);
        when(player1.getHand()).thenReturn(new Cards[5]);
        when(player2.getHand()).thenReturn(new Cards[5]);

        when(hub1.getId()).thenReturn(1);
        when(hub2.getId()).thenReturn(2);
        when(hub3.getId()).thenReturn(3);
        when(hub1.getOwner()).thenReturn(player1);
        when(hub2.getOwner()).thenReturn(player2);
        when(hub3.getOwner()).thenReturn(player1);
        when(hub1.getAmountTroops()).thenReturn(21);
        when(hub2.getAmountTroops()).thenReturn(22);
        when(hub3.getAmountTroops()).thenReturn(23);

        SendGameDataCommand.CommandSerializer commandSerializer = new SendGameDataCommand.CommandSerializer();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        commandSerializer.writeToStream(command, dataOutputStream);

        byte[] bytes = byteArrayOutputStream.toByteArray();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
        SendGameDataCommand result = commandSerializer.readFromStream(dataInputStream);

        assertTrue(checkEqualGameData(command.getGameData(), result.getGameData()));

        verify(player1, times(6)).getPlayerId();
        verify(player2, times(4)).getPlayerId();
        verify(player1, times(2)).getUnassignedAvailableTroops();
        verify(player2, times(2)).getUnassignedAvailableTroops();
        verify(player1, times(2)).getHand();
        verify(player2, times(2)).getHand();

        verify(hub1, times(2)).getId();
        verify(hub2, times(2)).getId();
        verify(hub3, times(2)).getId();
        verify(hub1, times(2)).getOwner();
        verify(hub2, times(2)).getOwner();
        verify(hub3, times(2)).getOwner();
        verify(hub1, times(2)).getAmountTroops();
        verify(hub2, times(2)).getAmountTroops();
        verify(hub3, times(2)).getAmountTroops();
    }

    private boolean checkEqualGameData(GameData data1, GameData data2){
        if(data1.getHubs().size() != data2.getHubs().size())
            return false;
        if(data1.getPlayers().size() != data2.getPlayers().size())
            return false;

        for(int i = 0; i < data1.getPlayers().size(); i++){
            if(data1.getPlayers().get(i).getPlayerId() != data2.getPlayers().get(i).getPlayerId())
                return false;
            if(data1.getPlayers().get(i).getUnassignedAvailableTroops() != data2.getPlayers().get(i).getUnassignedAvailableTroops())
                return false;
            if(data1.getPlayers().get(i).getHand().length != data2.getPlayers().get(i).getHand().length)
                return false;
        }

        for(int i = 0; i < data1.getHubs().size(); i++){
            if(data1.getHubs().get(i).getId() != data2.getHubs().get(i).getId())
                return false;
            if(data1.getHubs().get(i).getAmountTroops() != data2.getHubs().get(i).getAmountTroops())
                return false;
            if(data1.getHubs().get(i).getOwner().getPlayerId() != data2.getHubs().get(i).getOwner().getPlayerId())
                return false;
        }
        return true;
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
}
