package at.aau.wagnis;

public class Adjacency {
    Hub hub1;
    Hub hub2;
    String id;

    public Adjacency(Hub hub1, Hub hub2) {
        this.hub1 = hub1;
        this.hub2 = hub2;
        this.id = hub1 + "" + hub2;
    }

    public Hub getHub1() {
        return hub1;
    }

    public Hub getHub2() {
        return hub2;
    }
}
