package at.aau.wagnis.gamestate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import at.aau.wagnis.DefaultTroop;
import at.aau.wagnis.Hub;
import at.aau.wagnis.Player;

public class StartGameState extends GameLogicState{

    @Override
    public void start(List<Hub> unassignedCountries, List<Player> players) {
        Map<Integer, Integer> hubOwners = assignCountries(unassignedCountries, players);
        assignTroopsToHubs(hubOwners);
    }

    private Map<Integer, Integer> assignCountries(List<Hub> unassignedCountries, List<Player> players) {
        Map<Integer, Integer> hubOwners = new HashMap<>();
        for (int i = 0; i < unassignedCountries.size(); i++) {
            Hub hub = unassignedCountries.get(i);
            Player player = players.get(i % players.size());
            int playerId = player.getPlayerId();
            hubOwners.put(hub.getId(), playerId);
            player.setPlayerId(playerId);
        }
        System.out.println(hubOwners);
        return hubOwners;
    }

    private void assignTroopsToHubs(Map<Integer, Integer> hubOwners) {
        Map<Integer, Map<DefaultTroop, Integer>> playerTroops = new HashMap<>();
        Map<Integer, Map<DefaultTroop, Integer>> hubTroops = new HashMap<>();

        for (Integer playerId : hubOwners.values()) {
            Map<DefaultTroop, Integer> troops = new HashMap<>();
            troops.put(DefaultTroop.troop, 60);
            playerTroops.put(playerId, troops);
        }

        Set<Integer> playerIds = playerTroops.keySet();

        for (Integer playerId : playerIds) {
            Map<DefaultTroop, Integer> troops = playerTroops.get(playerId);
            while (hasTroops(troops)) {
                for (Integer hubId : hubOwners.keySet()) {
                    if (!hasTroops(troops)) {
                        continue;
                    }
                    if (hubOwners.get(hubId) == playerId) {
                        Map<DefaultTroop, Integer> currentHubTroops = hubTroops.get(hubId);
                        int amount = 0;
                        if (currentHubTroops == null) {
                            currentHubTroops = new HashMap<>();
                        }
                        if (currentHubTroops.containsKey(DefaultTroop.troop)) {
                            amount = currentHubTroops.get(DefaultTroop.troop);
                        }
                        if (amount == 0 ) {
                            int troopsToAdd = 1;
                            currentHubTroops.put(DefaultTroop.troop, troopsToAdd);
                            hubTroops.put(hubId, currentHubTroops);
                            removeTroopAmount(troops, DefaultTroop.troop, troopsToAdd);
                        }else {
                            int troopsToAdd = new Random().nextInt(3) + 1;
                            currentHubTroops.put(DefaultTroop.troop, amount + troopsToAdd);
                            hubTroops.put(hubId, currentHubTroops);
                            removeTroopAmount(troops, DefaultTroop.troop, troopsToAdd);


                        }

                    }
                }
            }
        }
        System.out.println(hubTroops);
    }

    private boolean hasTroops(Map<DefaultTroop, Integer> troops) {
        return (troops.values().stream().mapToInt(Integer::intValue).sum() > 0);
    }

    private void removeTroopAmount(Map<DefaultTroop, Integer> troops, DefaultTroop troop, int amount) {
        int currentTroopAmount = troops.get(troop);
        troops.put(troop, currentTroopAmount - amount);
    }
}
