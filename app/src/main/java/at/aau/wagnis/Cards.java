package at.aau.wagnis;

public class Cards {
    private int id;
    private Troops type;
    private Deck deck;

    public Cards(int id, Troops type, Deck deck) {
        if (id < 0 || type == null || deck == null){throw new IllegalArgumentException("Invalid Id can not be negative, Type and Deck can not be null");}
        this.id = id;
        this.type = type;
        this.deck = deck;
    }

    public Deck getDeck() {
        return deck;
    }

    public void setDeck(Deck deck) {
        if (deck == null) {throw new IllegalArgumentException("Deck can not be null");}
        this.deck = deck;
    }

    public Troops getType() {
        return type;
    }

    public void setType(Troops type) {
        if (type == null) {throw new IllegalArgumentException("Type can not be null");}
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id < 0) {throw new IllegalArgumentException("Id can not be negative");}
        this.id = id;
    }

    public static boolean checkIfCardSameType(Cards first, Cards second, Cards third){
        if (first.equals(second)&& first.equals(third)){
            throw new IllegalArgumentException("you cant use the same card thrice");
        }
        return first.type.equals(second.type) && first.type.equals(third.type);
    }

    public static boolean checkIfEachCardDiffType(Cards first, Cards second, Cards third){
        if (first.equals(second)&& first.equals(third)){
            throw new IllegalArgumentException("you cant use the same card thrice");
        }
        return !first.type.equals(second.type) && !first.type.equals(third.type) && !second.type.equals(third.type);
    }

    public void placeCardInDeck(){
        deck.placeCardInDeck(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cards)) return false;

        Cards cards = (Cards) o;

        if (id != cards.id) return false;
        if (type != cards.type) return false;
        return deck.equals(cards.deck);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + type.hashCode();
        return result;
    }
}
