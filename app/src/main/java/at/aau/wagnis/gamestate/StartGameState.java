package at.aau.wagnis.gamestate;

import android.util.Log;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import at.aau.wagnis.GlobalVariables;
import at.aau.wagnis.Hub;
import at.aau.wagnis.Player;

public class StartGameState extends GameLogicState {

    private static Map<Integer, Integer> hubOwners;

    private Map<Integer, Map<String, Integer>> hubTroops;

    private  Map<Integer, Map<String, Integer>> playerTroops;


    public StartGameState(List<Hub> unassignedCountries, List<Player> players) {
        this.hubOwners = assignCountries(unassignedCountries, players);
        this.playerTroops = new HashMap<>();
        this.hubTroops = new HashMap<>();
    }

    @Override
    public void start() {
        assignTroopsToHubs(hubOwners);
    }

    private Map<Integer, Integer> assignCountries(List<Hub> unassignedCountries, List<Player> players) {
            hubOwners = new HashMap<>();
        for (int i = 0; i < unassignedCountries.size(); i++) {
            Hub hub = unassignedCountries.get(i);
            Player player = players.get(i % players.size());
            int playerId = player.getPlayerId();
            hubOwners.put(hub.getId(), playerId);
            player.setPlayerId(playerId);
        }
        Log.d("TAG", "Hub owner contents: " + hubOwners);
        return hubOwners;
    }

    private Map<Integer, Map<String, Integer>> assignTroopsToHubs(Map<Integer, Integer> hubOwners) {

        Random ran = new SecureRandom();

        for (Integer playerId : hubOwners.values()) {
            Map<String, Integer> troops = new HashMap<>();
            troops.put(GlobalVariables.troop, 60);
            playerTroops.put(playerId, troops);
        }

        Set<Integer> playerIds = playerTroops.keySet();

        for (Integer playerId : playerIds) {
            Map<String, Integer> troops = playerTroops.get(playerId);
            while (hasTroops(troops)) {
                for (Map.Entry<Integer, Integer> entry : hubOwners.entrySet()) {
                    Integer hubId = entry.getKey();
                    if (!hasTroops(troops)) {
                        continue;
                    }
                    if (hubOwners.get(hubId).equals(playerId)) {
                        Map<String, Integer> currentHubTroops = hubTroops.get(hubId);
                        int amount = 0;
                        if (currentHubTroops == null) {
                            currentHubTroops = new HashMap<>();
                        }
                        if (currentHubTroops.containsKey(GlobalVariables.troop)) {
                            amount = currentHubTroops.get(GlobalVariables.troop);
                        }
                        if (amount == 0) {
                            int troopsToAdd = 1;
                            currentHubTroops.put(GlobalVariables.troop, troopsToAdd);
                            hubTroops.put(hubId, currentHubTroops);
                            removeTroopAmount(troops, GlobalVariables.troop, troopsToAdd);
                        } else {
                            int troopsToAdd = ran.nextInt(3) + 1;
                            currentHubTroops.put(GlobalVariables.troop, amount + troopsToAdd);
                            hubTroops.put(hubId, currentHubTroops);
                            removeTroopAmount(troops, GlobalVariables.troop, troopsToAdd);
                        }
                    }
                }
            }
        }
        Log.d("TAG", "Hub troops contents: " + hubTroops);
        return hubTroops;
    }

    private boolean hasTroops(Map<String, Integer> troops) {
        return (troops.values().stream().mapToInt(Integer::intValue).sum() > 0);
    }

    private void removeTroopAmount(Map<String, Integer> troops, String TROOP, int amount) {
        int currentTroopAmount = troops.get(TROOP);
        troops.put(TROOP, currentTroopAmount - amount);
    }

    public Map<Integer, Integer> getHubOwners() {
        return hubOwners;
    }

    public Map<Integer, Map<String, Integer>> getHubTroops() {
        return hubTroops;
    }

}
