package at.aau.wagnis.gamestate;

import java.util.Map;

import at.aau.wagnis.DefaultTroop;

public class MoveTroopsState {
    public void move(Map<Integer,Map<DefaultTroop, Integer>> hubTroops, Map<Integer, Integer> hubOwners, int fromHub, int toHub, int numTroops) {
        int playerId = hubOwners.get(fromHub);

        Map<DefaultTroop, Integer> fromTroops = hubTroops.get(fromHub);


        if (!hubOwners.containsKey(toHub)) {
            throw new IllegalArgumentException("The destination hub does not exist.");
        }
        if (!hubOwners.get(toHub).equals(playerId)) {
            throw new IllegalArgumentException("You can only move troops to a hub you own.");
        }

        int availableTroops = fromTroops.getOrDefault(DefaultTroop.TROOP, 0);
        if (numTroops <= 0 || numTroops > availableTroops) {
            throw new IllegalArgumentException("Invalid number of troops to move.");
        }
    }
}

