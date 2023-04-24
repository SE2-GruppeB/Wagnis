package at.aau.wagnis.gamestate;

import android.util.Log;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import at.aau.wagnis.Hub;
import at.aau.wagnis.Player;

public class StartGameState extends GameLogicState {

    private final ArrayList<Hub> hubs;
    private final ArrayList<Player> players;

    public StartGameState(ArrayList<Hub> unassignedCountries, ArrayList<Player> players) {
        this.hubs = unassignedCountries;
        this.players = players;
        assignCountries();
    }

    @Override
    public void start() {
        assignTroopsToHubs();
    }

    private void assignCountries() {
        for (int i = 0; i < this.hubs.size(); i++) {
            Hub hub = this.hubs.get(i);
            Player player = this.players.get(i % this.players.size());
            hub.setOwner(player);
            player.addHub(hub);
        }
        Log.d("TAG", "Hub owner contents: " + hubs);
        //Log.d("TAG", "Players contents: " + this.players);
    }

    private void assignTroopsToHubs() {

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
                hub.setTroops(hub.getAmountTroops() + troopsToPlace);
                player.setUnassignedAvailableTroops(player.getUnassignedAvailableTroops() - troopsToPlace);
                Log.d("TAG", "" + player.getUnassignedAvailableTroops());
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

    private void removeTroopAmount(Map<String, Integer> troops, String TROOP, int amount) {
        int currentTroopAmount = troops.get(TROOP);
        troops.put(TROOP, currentTroopAmount - amount);
    }

    public ArrayList<Hub> getHubs() {
        return this.hubs;
    }

    public ArrayList<Player> getPlayers() {
        return this.players;
    }
}