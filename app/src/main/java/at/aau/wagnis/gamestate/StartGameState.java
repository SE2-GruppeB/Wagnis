package at.aau.wagnis.gamestate;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

import at.aau.wagnis.Hub;
import at.aau.wagnis.Player;

public class StartGameState extends GameLogicState {

    private final List<Hub> hubs;
    private final List<Player> players;

    public StartGameState(GameData gameData) {
        this.hubs = gameData.getHubs();
        this.players = gameData.getPlayers();
        assignCountries();
    }

    @Override
    public void start() {
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

    Random ran = new Random();
    public void assignTroopsToHubs() {
        for (Player player : players) {
            assignOneTroopEach(player);
            while (hasTroops(player)) {
                Hub hub = getRandomHub(player, ran);
                if (!hasTroops(player)) { // This is used to stop placing troops on hubs for a player if they have no remaining unassigned troops available
                    break;
                }
                int troopsToPlace = getRandomTroopsToPlace(player, ran);
                placeTroopsOnHub(player, hub, troopsToPlace);
            }
        }
        updateHubText();
    }

    private Hub getRandomHub(Player player, Random ran) {
        int i = ran.nextInt(player.getOwnedHubs().size());
        return player.getOwnedHubs().get(i);
    }

    private int getRandomTroopsToPlace(Player player, Random ran) {
        int troopsToPlace = ran.nextInt(3) + 1;
        return Math.min(troopsToPlace, player.getUnassignedAvailableTroops());
    }

    private void placeTroopsOnHub(Player player, Hub hub, int troopsToPlace) {
        hub.addTroops(troopsToPlace);
        player.setUnassignedAvailableTroops(player.getUnassignedAvailableTroops() - troopsToPlace);
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
        return player.getUnassignedAvailableTroops() > 0;
    }

    private void updateHubText() {
        for (Player player : players) {
            for (Hub hub : player.getOwnedHubs()) {
                hub.setText("Troops: " + hub.getAmountTroops());
            }
        }
    }

    public List<Hub> getHubs() {
        return hubs;
    }

    public List<Player> getPlayers() {
        return players;
    }
}