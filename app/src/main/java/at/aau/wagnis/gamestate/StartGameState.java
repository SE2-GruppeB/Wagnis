package at.aau.wagnis.gamestate;

import java.util.List;
import java.util.Random;

import at.aau.wagnis.Deck;
import at.aau.wagnis.Hub;
import at.aau.wagnis.Player;

public class StartGameState extends GameLogicState {
    // Eine Liste der Hubs und Spieler
    private final List<Hub> hubs;
    private final List<Player> players;

    private final GameData gameData;

    @Override
    public void onEntry() {
        assignCountries();
        assignTroopsToHubs();
        Deck mainDeck = new Deck(hubs.size());
        gameData.setMainDeck(mainDeck);
        this.gameServer.setGameLogicState(new ChooseAttackGameState());
        //this.gameServer.setGameLogicState(new ReinforceGameState());
    }

    public StartGameState(GameData gameData) {
        this.hubs = gameData.getHubs();
        this.players = gameData.getPlayers();
        this.gameData = gameData;
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

    // Weist den Hubs Truppen zu
    public void assignTroopsToHubs() {
        for (Player player : players) {
            assignOneTroopEach(player);
            while (hasTroops(player)) {
                Hub hub = getRandomHub(player, ran);
                if (!hasTroops(player)) {
                    // Stoppt das Zuweisen wenn keine Truppen mehr zur verfügung stehen
                    break;
                }
                int troopsToPlace = getRandomTroopsToPlace(player, ran);
                placeTroopsOnHub(player, hub, troopsToPlace);
            }
        }
    }

    // Gibt einen zufälligen Hub zurück
    private Hub getRandomHub(Player player, Random ran) {
        int i = ran.nextInt(player.getOwnedHubs().size());
        return player.getOwnedHubs().get(i);
    }

    // Gibt eine zufällige Anzahl an Truppen zurück, die der Spieler platzieren kann
    private int getRandomTroopsToPlace(Player player, Random ran) {
        int troopsToPlace = ran.nextInt(3) + 1;
        return Math.min(troopsToPlace, player.getUnassignedAvailableTroops());
    }

    // Platziert Truppen auf einem Hub
    private void placeTroopsOnHub(Player player, Hub hub, int troopsToPlace) {
        hub.addTroops(troopsToPlace);
        player.setUnassignedAvailableTroops(player.getUnassignedAvailableTroops() - troopsToPlace);
    }

    // Weist jedem Hub eine Truppe zu
    private void assignOneTroopEach(Player player) {
        if (player.getOwnedHubs().size() <= player.getUnassignedAvailableTroops()) {
            for (Hub hub : player.getOwnedHubs()) {
                hub.setAmountTroops(hub.getAmountTroops() + 1);
                player.setUnassignedAvailableTroops(player.getUnassignedAvailableTroops() - 1);
            }
        }
    }

    // Überprüft, ob ein Spieler noch Truppen hat, welche nicht zugewiesen sind
    private boolean hasTroops(Player player) {
        return player.getUnassignedAvailableTroops() > 0;
    }

    // Getter für die Hubs und Spieler
    public List<Hub> getHubs() {
        return hubs;
    }

    public List<Player> getPlayers() {
        return players;
    }
}
