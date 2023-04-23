package at.aau.wagnis;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import static at.aau.wagnis.GlobalVariables.getAgency;
import static at.aau.wagnis.GlobalVariables.hubs;
import static at.aau.wagnis.GlobalVariables.players;

import android.content.Context;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GlobalVariables.baseContext = this;
        hideNavigationBar();
        adjacencyView = findViewById(R.id.adjacenciesView);
        endTurn = findViewById(R.id.btn_EndTurn);

        setDisplayMetrics();
        GlobalVariables.seedGenerator();
        drawHubs(GlobalVariables.getSeed());
        GlobalVariables.setAdjacencies();
        drawAdjacencies();

        List<Hub> unassignedCountries = new ArrayList<>(hubs);

        players.add(new Player(1));
        players.add(new Player(2));

        StartGameState startGameState = new StartGameState(unassignedCountries, players);

        startGameState.start();

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
    private void hideNavigationBar(){
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    public static void diceRollPopUp(int[] values) {

        LayoutInflater inflater = (LayoutInflater) GlobalVariables.baseContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popUp = inflater.inflate(R.layout.popup_diceroll, null);


        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 240, GlobalVariables.baseContext.getResources().getDisplayMetrics());
        int width = (int) px;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = false; // true lets tap outside the popup and dismiss it
        PopupWindow popupWindow = new PopupWindow(popUp, width, height, focusable);
        popupWindow.showAtLocation(new View(GlobalVariables.baseContext), Gravity.CENTER, 0, 0);


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

        NumberPicker[] dice = {n1,n2,n3,n4,n5};
        setDice(dice,values);
    }

    public static void setDice(NumberPicker[] dice, int[] values){
        for(int i = 0;i<dice.length;i++){
            dice[i].setMaxValue(6);
            dice[i].setMinValue(1);
            dice[i].setValue(values[i]);
        }
    }

    public void drawHubs(String seed){
        ConstraintLayout layout = findViewById(R.id.layout_activity_main);
        ConstraintSet cs = new ConstraintSet();

        for(int i=1;i<=seed.length();i++){
            if(i%2==0){
                GlobalVariables.seeds.add(seed.substring(i-2,i));
            }
        }

        int hubs = 0;
        GlobalVariables.hubsPerLine = (int)Math.ceil(GlobalVariables.seeds.size()/6f);
        int lineHubCount = 0;

        int hubWidthSpace = (GlobalVariables.getDisplayWidthPx()-180)/GlobalVariables.hubsPerLine;
        int height = GlobalVariables.getDisplayHeightPx();
        int heightSpace = height/6;

        //System.out.println("HubsPerLine:" + hubsPerLine);
        //System.out.println("HubWidthSpace"+hubWidthSpace);
        //System.out.println("HeightSpace:"+heightSpace);

        for(String s : GlobalVariables.seeds){
            Button hub = new Button(new ContextThemeWrapper(this, R.style.btn_hub_style), null, R.style.btn_hub_style);
            hub.setId(100+hubs);
            hub.setText("Hub: "+hub.getId());
            hub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int[] v = {1,2,3,4,5};
                    MainActivity.diceRollPopUp(v);
                    GlobalVariables.findHubById(hub.getId()).setHubImage(GlobalVariables.getAgency());
                    System.out.println("Hub:" +hub.getId());
                }
            });
            GlobalVariables.hubs.add(new Hub(hub));
            layout.addView(hub);

            int top = (hubs/GlobalVariables.hubsPerLine)*heightSpace;
            int pos = hubWidthSpace/100*Integer.parseInt(s);
            int left = hubWidthSpace*lineHubCount+pos;
            //System.out.println("Position:" + top+","+left);

            cs.clone(layout);
            cs.connect(hub.getId(),ConstraintSet.TOP,layout.getId(),ConstraintSet.TOP,top);
            cs.connect(hub.getId(),ConstraintSet.LEFT,layout.getId(),ConstraintSet.LEFT,left);
            cs.applyTo(layout);
            hubs++;
            lineHubCount++;
            if(lineHubCount==GlobalVariables.hubsPerLine){
                lineHubCount=0;
            }
        }
    }

    public void drawAdjacencies(){
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
            int pxWidth = dpToPx(25);
            int pxHeight = dpToPx(40);

            int startX = ((ConstraintLayout.LayoutParams) adjacency.getHub1().getHubButton().getLayoutParams()).leftMargin + pxWidth;
            int startY = ((ConstraintLayout.LayoutParams) adjacency.getHub1().getHubButton().getLayoutParams()).topMargin + pxHeight;
            int endX = ((ConstraintLayout.LayoutParams) adjacency.getHub2().getHubButton().getLayoutParams()).leftMargin + pxWidth;
            int endY = ((ConstraintLayout.LayoutParams) adjacency.getHub2().getHubButton().getLayoutParams()).topMargin + pxHeight;
            // System.out.println(startX + "," +startY + ","+endX+ ","+endY);
            canvas.drawLine(startX, startY, endX, endY, paint);

        }

        adjacencyView.setImageBitmap(bitmap);
    }

    public void setDisplayMetrics(){
        DisplayMetrics displayMetrics= new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        GlobalVariables.setDisplayWidthPx(displayMetrics.widthPixels);
        GlobalVariables.setDisplayHeightPx(displayMetrics.heightPixels);
    }

    public int dpToPx(int dp){
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}