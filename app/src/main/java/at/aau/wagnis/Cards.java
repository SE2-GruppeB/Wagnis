package at.aau.wagnis;

public class Cards {
    private int id;
    private Troops type;

    public Cards(int id, Troops type) {
        this.id = id;
        this.type = type;
    }

    public Troops getType() {
        return type;
    }

    public void setType(Troops type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static boolean checkIfCardSameType(Cards first, Cards second, Cards third){
        if (first.type.equals(second.type) && first.type.equals(third.type)){
            return true;
        }
        return false;
    }

    public static boolean checkIfEachCardDiffType(Cards first, Cards second, Cards third){
        if (!first.type.equals(second.type) && !first.type.equals(third.type) && !second.type.equals(third.type)){
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cards)) return false;

        Cards cards = (Cards) o;

        if (id != cards.id) return false;
        return type == cards.type;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + type.hashCode();
        return result;
    }
}
