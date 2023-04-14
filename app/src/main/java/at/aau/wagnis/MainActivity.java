package at.aau.wagnis;

import static at.aau.wagnis.GlobalVariables.hubs;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupWindow;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import at.aau.wagnis.gamestate.MoveTroopsState;
import at.aau.wagnis.gamestate.StartGameState;


public class MainActivity extends AppCompatActivity {

    FloatingActionButton endTurn;
    ImageView adjacencyView;

    public static void setDice(NumberPicker[] dice, int[] values) {
        for (int i = 0; i < dice.length; i++) {
            dice[i].setMaxValue(6);
            dice[i].setMinValue(1);
            dice[i].setValue(values[i]);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hideNavigationBar();
        adjacencyView = findViewById(R.id.adjacenciesView);
        endTurn = findViewById(R.id.btn_EndTurn);

        setDisplayMetrics();
        drawHubs("100;100/200;200/150;100");

        //Adjacency Test
        GlobalVariables.adjacencies.add(new Adjacency(GlobalVariables.findHubById(82), GlobalVariables.findHubById(83)));
        GlobalVariables.adjacencies.add(new Adjacency(GlobalVariables.findHubById(81), GlobalVariables.findHubById(88)));
        GlobalVariables.adjacencies.add(new Adjacency(GlobalVariables.findHubById(83), GlobalVariables.findHubById(91)));
        GlobalVariables.adjacencies.add(new Adjacency(GlobalVariables.findHubById(81), GlobalVariables.findHubById(82)));
        //
        GlobalVariables.findHubById(81).setText(1, 4, 5);

        drawAdjacencies();

        List<Hub> unassignedCountries = new ArrayList<>(hubs);
        List<Player> players = new ArrayList<>();

        players.add(new Player(1));
        players.add(new Player(2));

        StartGameState startState = new StartGameState();

        startState.start(unassignedCountries, players);

        Map<Integer, Integer> hubOwners = startState.getHubOwners(unassignedCountries, players);

        MoveTroopsState moveTroops = new MoveTroopsState();

        moveTroops.move(hubOwners);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        hideNavigationBar();
    }

    private void hideNavigationBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    public void diceRollPopUp(View view) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popUp = inflater.inflate(R.layout.popup_diceroll, null);


        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 240, getResources().getDisplayMetrics());
        int width = (int) px;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = false; // true lets tap outside the popup and dismiss it
        PopupWindow popupWindow = new PopupWindow(popUp, width, height, focusable);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);


        Button btnBack = popUp.findViewById(R.id.btn_Back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                return;
            }
        });


        NumberPicker n1 = popUp.findViewById(R.id.dice1);
        NumberPicker n2 = popUp.findViewById(R.id.dice2);
        NumberPicker n3 = popUp.findViewById(R.id.dice3);
        NumberPicker n4 = popUp.findViewById(R.id.dice4);
        NumberPicker n5 = popUp.findViewById(R.id.dice5);

        NumberPicker[] dice = {n1, n2, n3, n4, n5};

        int[] values = {1, 2, 3, 4, 5};
        setDice(dice, values);
    }

    public void drawHubs(String seed) {//seed: margin TOP; margin LEFT / distance to previous TOP;distance to previous LEFT;...
        ConstraintLayout layout = findViewById(R.id.layout_activity_main);
        ConstraintSet cs = new ConstraintSet();
       /*
        int nextId = 80;
        int lastTOP = 0;
        int lastLEFT = 0;



        for(String s : seed.split("/")){
            String[] coords = s.split(";");
            lastTOP = lastTOP+Integer.parseInt(coords[0]);
            lastLEFT = lastLEFT+Integer.parseInt(coords[1]);


            Button hub = new Button(new ContextThemeWrapper(this, R.style.btn_hub_style), null, R.style.btn_hub_style);
            nextId++;
            hub.setId(nextId);
            hub.setText("Hub: "+hub.getId());
            hub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    diceRollPopUp(hub);
                }
            });
            GlobalVariables.hubs.add(new Hub(hub));
            layout.addView(hub);


            cs.clone(layout);
            cs.connect(hub.getId(),ConstraintSet.TOP,layout.getId(),ConstraintSet.TOP,lastTOP);
            cs.connect(hub.getId(),ConstraintSet.LEFT,layout.getId(),ConstraintSet.LEFT,lastLEFT);
            cs.applyTo(layout);
        }*/

        int hubs = 1;
        int width = GlobalVariables.getDisplayWidthPx();
        int height = GlobalVariables.getDisplayHeightPx();

        int dpWidth = 63;
        int pxWidth = dpToPx(dpWidth);

        int widthSpace = ((width - (7 * pxWidth)) / 7);
        int heightSpace = height / 6;

        int lineCount = 0;
        int hubCount = 0;
        while (hubs <= 42) {

            Button hub = new Button(new ContextThemeWrapper(this, R.style.btn_hub_style), null, R.style.btn_hub_style);
            hub.setId(80 + hubs);
            hub.setText("Hub: " + hub.getId());
            hub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    diceRollPopUp(hub);
                    System.out.println("Hub:" + hub.getId()); //Test Button Accuracy
                }
            });
            GlobalVariables.hubs.add(new Hub(hub));
            layout.addView(hub);

            cs.clone(layout);
            cs.connect(hub.getId(), ConstraintSet.TOP, layout.getId(), ConstraintSet.TOP, lineCount * heightSpace);
            cs.connect(hub.getId(), ConstraintSet.LEFT, layout.getId(), ConstraintSet.LEFT, hubCount * widthSpace + hubCount * pxWidth);

            cs.applyTo(layout);
            hubCount++;
            if (hubs % 7 == 0) {
                lineCount++;
                hubCount = 0;
            }

            hubs++;
        }
    }

    public void drawAdjacencies() {
        int height = GlobalVariables.getDisplayHeightPx();
        int width = GlobalVariables.getDisplayWidthPx();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(8);
        paint.setAntiAlias(true);

        for (Adjacency adjacency : GlobalVariables.adjacencies) {
            int pxWidth = dpToPx(31);
            int pxHeight = dpToPx(50);

            int startX = ((ConstraintLayout.LayoutParams) adjacency.getHub1().getHubButton().getLayoutParams()).leftMargin + pxWidth;
            int startY = ((ConstraintLayout.LayoutParams) adjacency.getHub1().getHubButton().getLayoutParams()).topMargin + pxHeight;
            int endX = ((ConstraintLayout.LayoutParams) adjacency.getHub2().getHubButton().getLayoutParams()).leftMargin + pxWidth;
            int endY = ((ConstraintLayout.LayoutParams) adjacency.getHub2().getHubButton().getLayoutParams()).topMargin + pxHeight;
            // System.out.println(startX + "," +startY + ","+endX+ ","+endY);
            canvas.drawLine(startX, startY, endX, endY, paint);

        }

        adjacencyView.setImageBitmap(bitmap);
    }

    public void setDisplayMetrics() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        GlobalVariables.setDisplayWidthPx(displayMetrics.widthPixels);
        GlobalVariables.setDisplayHeightPx(displayMetrics.heightPixels);
    }

    public int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}