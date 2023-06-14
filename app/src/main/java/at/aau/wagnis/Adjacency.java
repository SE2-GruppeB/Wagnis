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

    public void setHub1(Hub hub1) {
        this.hub1 = hub1;
    }

    public Hub getHub2() {
        return hub2;
    }

    public void setHub2(Hub hub2) {
        this.hub2 = hub2;
    }

    /**
     * Überprüft, ob die beiden übergebenen Hubs ein Paar bilden.
     * Ein Paar liegt vor, wenn entweder hub1 mit this.hub1 und hub2 mit this.hub2 übereinstimmt
     * oder hub1 mit this.hub2 und hub2 mit this.hub1 übereinstimmt.
     *
     * @param hub1 Der erste Hub, der überprüft werden soll.
     * @param hub2 Der zweite Hub, der überprüft werden soll.
     * @return True, wenn die beiden Hubs ein Paar bilden, ansonsten False.
     */
    public boolean isInPair(Hub hub1, Hub hub2) {
        return ((this.hub1.getId()==hub1.getId() && this.hub2.getId()==hub2.getId()) || (this.hub1.getId()==hub2.getId() && this.hub2.getId()==hub1.getId()));
    }
}
