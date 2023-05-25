package at.aau.wagnis.gamestate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import at.aau.wagnis.Cards;
import at.aau.wagnis.Hub;
import at.aau.wagnis.Player;


class GameDataTest {

    @Mock
    private Hub hub1;
    @Mock private Hub hub2;
    @Mock private Hub hub3;

    @Mock private Player player1;
    @Mock private Player player2;

    private GameData gameData1;
    private GameData gameData2;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);

        gameData1 = initGameData();
        gameData2 = new GameData();
    }

    private GameData initGameData() {
        gameData1 = new GameData();

        String seed = "1234567890";
        gameData1.setSeed(seed);

        List<Hub> hubs = new ArrayList<>();
        hubs.add(hub1);
        hubs.add(hub2);
        hubs.add(hub3);
        gameData1.setHubs(hubs);

        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        gameData1.setPlayers(players);

        return gameData1;
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

        String serialize = gameData1.serialize();
        gameData2.deserialize(serialize);

        assertTrue(checkEqualGameData(gameData1,gameData2));

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

}