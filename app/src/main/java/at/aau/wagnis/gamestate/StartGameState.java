package at.aau.wagnis.gamestate;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

import at.aau.wagnis.Hub;
import at.aau.wagnis.Player;

public class StartGameState extends GameLogicState {

    private final List<Hub> hubs;
    private final List<Player> players;

    public StartGameState(List<Hub> unassignedCountries, List<Player> players) {
        this.hubs = unassignedCountries;
        this.players = players;
        assignCountries();
    }

    @Override
    public void start() {
        // Generate new seed
        // get all Player and hubs

        assignTroopsToHubs();
    }

    public void assignCountries() {
        for (int i = 0; i < this.hubs.size(); i++) {
            Hub hub = this.hubs.get(i);
            Player player = this.players.get(i % this.players.size());
            hub.setOwner(player);
            player.addHub(hub);
        }
    }

    public void assignTroopsToHubs() {

        Random ran = new SecureRandom();

        for (Player player : this.players) {
            assignOneTroopEach(player);
            while (hasTroops(player)) {
                int i = ran.nextInt(player.getOwnedHubs().size());
                Hub hub = player.getOwnedHubs().get(i);
                if (!hasTroops(player)) {
                    break;
                }
                int troopsToPlace = ran.nextInt(3) + 1;
                if(troopsToPlace > player.getUnassignedAvailableTroops()){
                    troopsToPlace = player.getUnassignedAvailableTroops();
                }
                hub.setAmountTroops(hub.getAmountTroops() + troopsToPlace);
                player.setUnassignedAvailableTroops(player.getUnassignedAvailableTroops() - troopsToPlace);
            }
        }
    }

    private void assignOneTroopEach(Player player) {
        if (player.getOwnedHubs().size() <= player.getUnassignedAvailableTroops()) {
            for (Hub hub : player.getOwnedHubs()) {
                hub.setAmountTroops(hub.getAmountTroops() + 1);
                player.setUnassignedAvailableTroops(player.getUnassignedAvailableTroops() - 1);
            }
        }
    }

    private boolean hasTroops(Player player) {
        return (player.getUnassignedAvailableTroops() > 0);
    }

    public List<Hub> getHubs() {
        return this.hubs;
    }

    public List<Player> getPlayers() {
        return this.players;
    }
}