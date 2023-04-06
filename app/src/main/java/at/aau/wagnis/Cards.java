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


}
