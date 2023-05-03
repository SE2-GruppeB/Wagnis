package at.aau.wagnis;

import android.widget.Button;

public class Hub {

    private final int id;
    private final Button hubButton;
    private Player owner;
    private int amountTroops;

    public Hub(Button hubButton) {
        this.hubButton = hubButton;
        this.id = hubButton.getId();
        this.amountTroops = 0;
        this.owner = new Player();
    }

    public Button getHubButton() {
        return this.hubButton;
    }

    public int getId() {
        return this.id;
    }

    public void setText(int troops) {
        this.hubButton.setText(troops);
    }

    public int getAmountTroops() {
        return this.amountTroops;
    }

    public void setAmountTroops(int amountTroops){
        this.amountTroops = amountTroops;
    }

    //to do:
    public void setOwner(Player hubOwner) {
        this.owner = hubOwner;
    }

    //Todo
    public void setTroops(int amountTroops) {
        this.amountTroops = amountTroops;
    }

    public Player getOwner() {
        return this.owner;
    }
    public void setHubImage(String agency){
        switch(agency){
            case("ESA"):
               this.getHubButton().setCompoundDrawablesWithIntrinsicBounds(0,0,0, R.drawable.dome_x42);
                break;
            case("NASA"):
                this.getHubButton().setCompoundDrawablesWithIntrinsicBounds(0,0,0, R.drawable.dome6_x42);
                break;
            case("ISRO"):
                this.getHubButton().setCompoundDrawablesWithIntrinsicBounds(0,0,0, R.drawable.dome5_x42);
                break;
            case("JAXA"):
                this.getHubButton().setCompoundDrawablesWithIntrinsicBounds(0,0,0, R.drawable.dome4_x42);
                break;
            case("Roskosmos"):
                this.getHubButton().setCompoundDrawablesWithIntrinsicBounds(0,0,0, R.drawable.dome3_x42);
                break;
            case("China Manned Space Agency"):
                this.getHubButton().setCompoundDrawablesWithIntrinsicBounds(0,0,0, R.drawable.dome2_x42);
                break;
            default:
                this.getHubButton().setCompoundDrawablesWithIntrinsicBounds(0,0,0, R.drawable.dome_x42);
        }
    }

    @Override
    public String toString() {
        return "Hub{" +
                "id=" + id +

                ", amountTroops=" + amountTroops +
                '}';
    }
}


