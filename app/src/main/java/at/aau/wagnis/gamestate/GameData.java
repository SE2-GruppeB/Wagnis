package at.aau.wagnis.gamestate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import at.aau.wagnis.Adjacency;
import at.aau.wagnis.Cards;
import at.aau.wagnis.Deck;
import at.aau.wagnis.Hub;
import at.aau.wagnis.Player;
import at.aau.wagnis.Troops;

public class GameData {

    private static final String IDENTIFIER_STRING  = "IDENTIFIER";
    private static final String SEED_STRING  = "SEED";
    private static final String GAMESTATE_STRING  = "STATE";
    private static final String PLAYER_STRING  = "PLAYER";
    private static final String CARD_STRING  = "CARD";
    private static final String HUB_STRING  = "HUB";
    private static final String CURRENT_PLAYER_STRING = "ACTIVE";
    protected static final int MSG_HISTORY_LENGTH = 10;

    private String seed;
    private List<Hub> hubs;
    private List<Player> players;
    private List<Adjacency> adjacencies;
    private List<ChatMessage> messages;
    private Map<Integer, String> playerIdentifier;
    private String currentGameLogicState;
    private int currentPlayerID;
    private Deck deck;

    public GameData() {
        super();
        hubs = new ArrayList<>();
        players = new ArrayList<>();
        adjacencies = new ArrayList<>();
        playerIdentifier = new HashMap<>();
        messages = new ArrayList<>();
        currentGameLogicState = "LobbyState";
        currentPlayerID = 0;
    }

    public String getCurrentGameLogicState() {
        return currentGameLogicState;
    }

    public void setCurrentGameLogicState(String currentGameLogicState) {
        this.currentGameLogicState = currentGameLogicState;
    }

    public void setSeed(String seed) {
        this.seed = seed;
    }

    public void setPlayers(List<Player> players){
        this.players = new ArrayList<>(players);
    }

    public void setHubs(List<Hub> hubs){
        this.hubs = new ArrayList<>(hubs);
    }

    public void setAdjacencies(List<Adjacency> adjacencies) {
        this.adjacencies = adjacencies;
    }

    public void setCurrentPlayer(int currentPlayerID) {
        this.currentPlayerID = currentPlayerID;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public void addPlayerIdentifier(int playerId, String ipAddress) {
        playerIdentifier.put(playerId, ipAddress);
        players.add(new Player(playerId));
    }

    public void nextPlayer() {
        Player currentPlayer = players.get(currentPlayerID);
        Cards c = deck.drawCardFromDeck();
        if(!currentPlayer.addCardToHand(c))
            deck.placeCardInDeck(c);
        currentPlayerID = (++currentPlayerID) % players.size();
    }

    public String getSeed() {
        return seed;
    }

    public List<Hub> getHubs() {
        return hubs;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Adjacency> getAdjacencies() {
        return adjacencies;
    }

    public Map<Integer, String> getPlayerIdentifier() {
        return playerIdentifier;
    }

    public int getCurrentPlayer() {
        return currentPlayerID;
    }

    public Deck getDeck() {
        return deck;
    }

    public String serialize() {
        // TYPE1<VALUE1>TYPE1TYPE2<VALUE2>TYPE2...
        // SEED(string), PLAYER[i](id, unassigned, cards[i](type)), HUB[i](hubID, ownerID, troops)
        StringBuilder builder = new StringBuilder();

        // Player identification
        builder.append(IDENTIFIER_STRING);
        for(Map.Entry<Integer, String> i : playerIdentifier.entrySet()) {
            builder.append(i.getKey()).append(";");
            builder.append(i.getValue());
            builder.append(IDENTIFIER_STRING);
        }

        // Current player turn
        if(!players.isEmpty()) {
            builder.append(CURRENT_PLAYER_STRING);
            Player currentPlayer = players.get(currentPlayerID);
            builder.append(currentPlayer.getPlayerId());
            builder.append(CURRENT_PLAYER_STRING);
        }

        // current GameLogicState
        builder.append(GAMESTATE_STRING);
        builder.append(this.currentGameLogicState);
        builder.append(GAMESTATE_STRING);

        // Seed
        builder.append(SEED_STRING);
        builder.append(this.getSeed());
        builder.append(SEED_STRING);

        // Player
        builder.append(PLAYER_STRING);
        for(Player player : this.players){
            builder.append(player.getPlayerId()).append(";");
            builder.append(player.getUnassignedAvailableTroops()).append(";");
            builder.append(CARD_STRING);
            for (Cards card : player.getHand()) {
                if(card == null) {
                    builder.append("null");
                }else {
                    builder.append(card.getType());
                }
                builder.append(CARD_STRING);
            }
            builder.append(PLAYER_STRING);
        }

        // Hub
        builder.append(HUB_STRING);
        for(Hub hub : this.hubs){
            builder.append(hub.getId()).append(";");
            builder.append(hub.getOwner().getPlayerId()).append(";");
            builder.append(hub.getAmountTroops()).append(";");
            builder.append(HUB_STRING);
        }
        builder.append("END");
        return builder.toString();


    }

    public void deserialize(String input){
        // Player identification
        String[] identifiingData = input.split(IDENTIFIER_STRING);
        for(int i = 1; i < identifiingData.length -1; i++) {
            String[] data = identifiingData[i].split(";");
            int key = Integer.parseInt(data[0]);
            String value = data[1];
            playerIdentifier.put(key, value);
        }

        // current Players turn
        String[] currentPlayerData = input.split(CURRENT_PLAYER_STRING);
        if(currentPlayerData.length > 2) {
            setCurrentPlayer(Integer.parseInt(currentPlayerData[1]));
        }

        // current GameLocigState
        setCurrentGameLogicState(input.split(GAMESTATE_STRING)[1]);

        // Seed
        String[] seedData = input.split(SEED_STRING);
        setSeed(seedData[1]);

        // Player
        String[] playerData = input.split(PLAYER_STRING);
        List<Player> playerList = new ArrayList<>();
        for(int i = 1; i< playerData.length-1; i++){
            String[] data = playerData[i].split(";");
            Player player;
            if((player = getPlayerById(Integer.parseInt(data[0]))) == null){
                player = new Player(Integer.parseInt(data[0]));
            }
            player.setUnassignedAvailableTroops(Integer.parseInt(data[1]));

            // Card
            player.setHand(getHandByString(playerData[i]));
            playerList.add(player);
        }
        setPlayers(playerList);

        // Hub
        String[] hubData = input.split(HUB_STRING);
        List<Hub> hubList = new ArrayList<>();
        for(int i = 1; i < hubData.length-1; i++){
            String[] data = hubData[i].split(";");
            Hub hub;
            if((hub = getHubById(Integer.parseInt(data[0]))) == null){
                hub = new Hub(Integer.parseInt(data[0]));
            }
            hub.setOwner(getPlayerById(Integer.parseInt(data[1])));
            hub.setAmountTroops(Integer.parseInt(data[2]));
            hubList.add(hub);
        }
        setHubs(hubList);
    }

    private Player getPlayerById(int playerId){
        for(Player player : this.getPlayers()){
            if(player.getPlayerId() == playerId){
                return player;
            }
        }
        return null;
    }

    private Hub getHubById(int hubId){
        for(Hub hub : this.getHubs()){
            if(hub.getId() == hubId){
                return hub;
            }
        }
        return null;
    }

    private Cards[] getHandByString(String playerData){
        String[] cardData = playerData.split(CARD_STRING);
        Cards[] hand = new Cards[cardData.length-1];
        for(int j = 0; j < hand.length; j++){
            hand[j] = getCardByString(cardData[j+1]);
        }
        return hand;
    }

    private Cards getCardByString(String card){
        Troops troop;
        switch(card){
            case "ARTILLERY":
                troop = Troops.ARTILLERY;
                break;
            case "CAVALRY":
                troop = Troops.CAVALRY;
                break;
            case "INFANTRY":
                troop = Troops.INFANTRY;
                break;
            default:
                return null;
        }
        return new Cards(0, troop, new Deck(1));
    }

    public void addMessage(int clientId, String message) {
        if(messages.size() >= MSG_HISTORY_LENGTH) {
            messages.remove(0);
        }
        this.messages.add(new ChatMessage(clientId, message));

    }

    public List<ChatMessage> getMessages() {
        return this.messages;
    }

}
