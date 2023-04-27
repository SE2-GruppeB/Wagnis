package at.aau.wagnis;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import static at.aau.wagnis.GlobalVariables.findHubById;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import at.aau.wagnis.gamestate.AttackGameState;
import at.aau.wagnis.gamestate.MoveTroopsState;
import at.aau.wagnis.gamestate.StartGameState;

public class MainActivity extends AppCompatActivity {


    FloatingActionButton endTurn,btnCards,btnSettings, btnChat;
    ImageView adjacencyView;
    ArrayList<Hub> selectedHubs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GlobalVariables.baseContext = this;
        hideNavigationBar();
        adjacencyView = findViewById(R.id.adjacenciesView);
        endTurn = findViewById(R.id.btn_EndTurn);
        btnCards=findViewById(R.id.btn_Cards);
        btnSettings=findViewById(R.id.btn_Settings);
        btnChat=findViewById(R.id.btn_Chat);

        setDisplayMetrics();
        if(!GlobalVariables.getIsClient()){
            GlobalVariables.seedGenerator();
        }
        drawHubs(GlobalVariables.getSeed());
        GlobalVariables.setAdjacencies();
        drawAdjacencies();

        List<Hub> unassignedCountries = new ArrayList<>(hubs);

        players.add(new Player(1));
        players.add(new Player(2));

        StartGameState startGameState = new StartGameState(unassignedCountries, players);

        startGameState.start();

        btnCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCards();
                return;
            }
        });
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSettings();
                return;
            }
        });

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChat();
                return;
            }
        });
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
    public static void showSettings(){

       PopupWindow popupWindow= createPopUp(R.layout.popup_settings,300,450,false);

        /*LayoutInflater inflater = (LayoutInflater) GlobalVariables.baseContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popUp = inflater.inflate(R.layout.popup_settings, null);



        int width = dpToPx(300);
        int height = dpToPx(450);
        boolean focusable = false;
        PopupWindow popupWindow = new PopupWindow(popUp, width, height, focusable);
        popupWindow.showAtLocation(new View(GlobalVariables.baseContext), Gravity.CENTER, 0, 0);
    */
        popupWindow.showAtLocation(new View(GlobalVariables.baseContext), Gravity.CENTER, 0, 0);
        Button btnClose = popupWindow.getContentView().findViewById(R.id.btn_Close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                return;
            }
        });

        ImageView qrCode = popupWindow.getContentView().findViewById(R.id.qrCode);
        MultiFormatWriter mWriter = new MultiFormatWriter();
        try {
            BitMatrix mMatrix = mWriter.encode(GlobalVariables.getIpAddress(), BarcodeFormat.QR_CODE, 500,500);
            BarcodeEncoder mEncoder = new BarcodeEncoder();
            Bitmap mBitmap = mEncoder.createBitmap(mMatrix);
            qrCode.setImageBitmap(mBitmap);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static void showCards(){
        PopupWindow popupWindow= createPopUp(R.layout.popup_cards,550,500,false);
        /*
        LayoutInflater inflater = (LayoutInflater) GlobalVariables.baseContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popUp = inflater.inflate(R.layout.popup_cards, null);


        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 420, GlobalVariables.baseContext.getResources().getDisplayMetrics());
        int width = (int) px;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = false; // true lets tap outside the popup and dismiss it
        PopupWindow popupWindow = new PopupWindow(popUp, width, height, focusable);*/
        popupWindow.showAtLocation(new View(GlobalVariables.baseContext), Gravity.CENTER, 0, 0);

        Button btnBack = popupWindow.getContentView().findViewById(R.id.btn_Close);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                return;
            }
        });

    }

    public static void diceRollPopUp(int[] values) {
        PopupWindow popupWindow= createPopUp(R.layout.popup_diceroll,300,350,false);
/*
        LayoutInflater inflater = (LayoutInflater) GlobalVariables.baseContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popUp = inflater.inflate(R.layout.popup_diceroll, null);


        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 240, GlobalVariables.baseContext.getResources().getDisplayMetrics());
        int width = (int) px;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = false; // true lets tap outside the popup and dismiss it
        PopupWindow popupWindow = new PopupWindow(popUp, width, height, focusable);*/
        popupWindow.showAtLocation(new View(GlobalVariables.baseContext), Gravity.CENTER, 0, 0);


        Button btnBack = popupWindow.getContentView().findViewById(R.id.btn_Back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                return;
            }
        });


        NumberPicker n1 = popupWindow.getContentView().findViewById(R.id.dice1);
        NumberPicker n2 = popupWindow.getContentView().findViewById(R.id.dice2);
        NumberPicker n3 = popupWindow.getContentView().findViewById(R.id.dice3);
        NumberPicker n4 = popupWindow.getContentView().findViewById(R.id.dice4);
        NumberPicker n5 = popupWindow.getContentView().findViewById(R.id.dice5);

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

                 /*Pseudo Logic Integration

                    if(gamesate == reinforceGamestate)
                        reinforceTroops(hub);
                    else if(gamestate == moveTroopsState|| gamestate == attackgamestate)
                        if(selectedHubs.size()<=2){
                            selectedHubs.add(GlobalVariables.findHubById(hub.getId()));
                        }else{
                            moveTroops();
                            selectedHubs.removeAll(selectedHubs);
                        }
                 -------------*/


                    /* Testing Grounds
                    int[] v = {1,2,3,4,5};
                    MainActivity.diceRollPopUp(v);
                    GlobalVariables.findHubById(hub.getId()).setHubImage(GlobalVariables.getAgency());
                    System.out.println("Hub:" +hub.getId());*/
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
            int pxWidth = dpToPx(21);
            int pxHeight = dpToPx(60);

            int startX = ((ConstraintLayout.LayoutParams) adjacency.getHub1().getHubButton().getLayoutParams()).leftMargin+pxWidth;
            int startY = ((ConstraintLayout.LayoutParams) adjacency.getHub1().getHubButton().getLayoutParams()).topMargin+pxHeight;
            int endX = ((ConstraintLayout.LayoutParams) adjacency.getHub2().getHubButton().getLayoutParams()).leftMargin+pxWidth;
            int endY = ((ConstraintLayout.LayoutParams) adjacency.getHub2().getHubButton().getLayoutParams()).topMargin+pxHeight;
            // System.out.println(startX + "," +startY + ","+endX+ ","+endY);
            canvas.drawLine(startX, startY, endX, endY, paint);

        }

        adjacencyView.setImageBitmap(bitmap);
    }

    public void reinforceTroops(Button hubButton){
        PopupWindow popupWindow= createPopUp(R.layout.popup_movetroops,300,350,false);
        popupWindow.showAtLocation(new View(GlobalVariables.baseContext), Gravity.CENTER, 0, 0);

        Button btnClose = popupWindow.getContentView().findViewById(R.id.btn_Close);
        NumberPicker np = popupWindow.getContentView().findViewById(R.id.np_troops);
        np.setMaxValue(10);     //setMaxValue(Player.getUnassignedAvailableTroops)
        np.setMinValue(0);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               int troops = np.getValue();
                //player.setUnassindeAvailableTroops(=-troops)      //delete now used troops
                Hub selected = GlobalVariables.findHubById(hubButton.getId());
                selected.setText(selected.getText()+troops);
                //selected.setAmountTroops(selected.getAmmountTroops+troops);   //set new troop count
                popupWindow.dismiss();
                return;
            }
        });
    }

    public void moveTroops(){
        PopupWindow popupWindow= createPopUp(R.layout.popup_movetroops,300,350,false);
        popupWindow.showAtLocation(new View(GlobalVariables.baseContext), Gravity.CENTER, 0, 0);
        Button btnClose = popupWindow.getContentView().findViewById(R.id.btn_Close);
        NumberPicker np = popupWindow.getContentView().findViewById(R.id.np_troops);
        np.setMaxValue(10);     //setMaxValue(Hub.getAmountTroops)
        np.setMinValue(1);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int troops = np.getValue();

               // if(gamestate == AttackGameState){
                    //calc battle
               // }else{
                //selectedHubs.get(0).setAmountTroops(selectedHubs.get(0).getAmountTroops()-troops);
                //selectedHubs.get(1).setAmountTroops(selectedHubs.get(1).getAmountTroops()+troops);
                //selectedHubs.get(0).setText(selectedHubs.get(0).getAmountTroops());
                //selectedHubs.get(1).setText(selectedHubs.get(1).getAmountTroops());
                //}
                popupWindow.dismiss();
                return;
            }
        });
    }

    public void setDisplayMetrics(){
        DisplayMetrics displayMetrics= new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        GlobalVariables.setDisplayWidthPx(displayMetrics.widthPixels);
        GlobalVariables.setDisplayHeightPx(displayMetrics.heightPixels);
    }

    public static int dpToPx(int dp){
        return dp *(GlobalVariables.baseContext.getResources().getDisplayMetrics().densityDpi/160);
    }

    public static PopupWindow createPopUp(int popupId,int width,int height,boolean focusable){

        LayoutInflater inflater = (LayoutInflater) GlobalVariables.baseContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popUp = inflater.inflate(popupId, null);
        PopupWindow popupWindow = new PopupWindow(popUp, dpToPx(width), dpToPx(height), focusable);
        return popupWindow;
    }

    public static void showChat(){
        PopupWindow popupWindow= createPopUp(R.layout.popup_chat,450,400,true);
        popupWindow.showAtLocation(new View(GlobalVariables.baseContext), Gravity.CENTER, 0, 0);

        Button btnExit = popupWindow.getContentView().findViewById(R.id.btn_Exit);
        Button btnSend = popupWindow.getContentView().findViewById(R.id.btn_Send);
        TextView msg = popupWindow.getContentView().findViewById(R.id.chatMsg);
        EditText sendMsg = popupWindow.getContentView().findViewById(R.id.sendMsg);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                return;
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(sendMsg.getText());
                return;
            }
        });
        
    }
}