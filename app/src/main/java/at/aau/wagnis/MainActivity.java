package at.aau.wagnis;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;


import at.aau.wagnis.application.GameManager;
import at.aau.wagnis.application.WagnisApplication;
import at.aau.wagnis.gamestate.GameData;
import at.aau.wagnis.server.communication.command.ProcessChatMessageCommand;


public class MainActivity extends AppCompatActivity {


    FloatingActionButton endTurn, btnCards, btnSettings, btnChat;
    ImageView adjacencyView;
    GameData currentState;
    boolean wasDrawn = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GlobalVariables.baseContext = this;

        adjacencyView = findViewById(R.id.adjacenciesView);
        endTurn = findViewById(R.id.btn_EndTurn);
        btnCards = findViewById(R.id.btn_Cards);
        btnSettings = findViewById(R.id.btn_Settings);
        btnChat = findViewById(R.id.btn_Chat);

        setDisplayMetrics();
        /*if(!GlobalVariables.getIsClient()){

            GlobalVariables.seedGenerator();
        }
        drawHubs(GlobalVariables.getSeed());
        GlobalVariables.setAdjacencies();
        drawAdjacencies();*/

        btnCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupCards();
                return;
            }
        });
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupSettings();
                return;
            }
        });

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupChat();
                return;
            }
        });


        ((WagnisApplication)getApplication()).getGameManager().setGameStateListener(newGameState -> runOnUiThread(() -> {
            // code to be executed on the UI thread
            currentState = newGameState;
            if(newGameState != null){
                System.out.println(currentState.getMessages());
                if(!wasDrawn){
                    generateMap(newGameState.getSeed());
                    wasDrawn = true;
                }
            }

        }));

        popupStart(btnCards);

    }
    private void generateMap(String seed) {
        GlobalVariables.setSeed(seed);
        drawHubs(GlobalVariables.getSeed());
        GlobalVariables.setAdjacencies();
        drawAdjacencies();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public void onResume() {
        super.onResume();


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
    public PopupWindow createPopUp(int popupId){

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popUp = inflater.inflate(popupId, null);
        PopupWindow popupWindow = new PopupWindow(popUp, FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, true);
        return popupWindow;
    }
    private GameManager getGameManager() {
        return ((WagnisApplication) getApplication()).getGameManager();
    }



    public void drawHubs(String seed){

        ConstraintLayout layout = findViewById(R.id.layout_activity_main);
        ConstraintSet cs = new ConstraintSet();

        for (int i = 1; i <= seed.length(); i++) {
            if (i % 2 == 0) {
                GlobalVariables.seeds.add(seed.substring(i - 2, i));
            }
        }

        int hubs = 0;
        GlobalVariables.hubsPerLine = (int) Math.ceil(GlobalVariables.seeds.size() / 6f);
        int lineHubCount = 0;

        int hubWidthSpace = (GlobalVariables.getDisplayWidthPx() - dpToPx(100)) / GlobalVariables.hubsPerLine;
        int height = GlobalVariables.getDisplayHeightPx();
        int heightSpace = height / 6;

        //System.out.println("HubsPerLine:" + hubsPerLine);
        //System.out.println("HubWidthSpace"+hubWidthSpace);
        //System.out.println("HeightSpace:"+heightSpace);

        for (String s : GlobalVariables.seeds) {
            Button hub = new Button(new ContextThemeWrapper(this, R.style.btn_hub_style), null, R.style.btn_hub_style);
            hub.setId(100 + hubs);

            //hub.setText("Hub: " + hub.getId());

            hub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    /*switch(currentState){
                        case ReinforceGameState:
                            reinforceTroops(hub);
                            break;
                        case AttackGameState:
                            moveTroops();
                            break;
                    }

                 -------------*/


                    /* Testing Grounds;*/
                    int[] v = {1,2,3,4,5};
                    popupDiceRoll(v);
                    GlobalVariables.findHubById(hub.getId()).setHubImage(GlobalVariables.getAgency());

                    System.out.println("Hub:" +hub.getId());

                }
            });

            GlobalVariables.hubs.add(new Hub(hub));


            layout.addView(hub);

            int top = (hubs / GlobalVariables.hubsPerLine) * heightSpace;
            int pos = hubWidthSpace / 100 * Integer.parseInt(s);
            int left = hubWidthSpace * lineHubCount + pos;
            //System.out.println("Position:" + top+","+left);

            cs.clone(layout);
            cs.connect(hub.getId(), ConstraintSet.TOP, layout.getId(), ConstraintSet.TOP, top);
            cs.connect(hub.getId(), ConstraintSet.LEFT, layout.getId(), ConstraintSet.LEFT, left);
            cs.applyTo(layout);
            hubs++;
            lineHubCount++;
            if (lineHubCount == GlobalVariables.hubsPerLine) {
                lineHubCount = 0;
            }
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
            int pxWidth = dpToPx(21);
            int pxHeight = dpToPx(60);

            int startX = ((ConstraintLayout.LayoutParams) adjacency.getHub1().getHubButton().getLayoutParams()).leftMargin + pxWidth;
            int startY = ((ConstraintLayout.LayoutParams) adjacency.getHub1().getHubButton().getLayoutParams()).topMargin + pxHeight;
            int endX = ((ConstraintLayout.LayoutParams) adjacency.getHub2().getHubButton().getLayoutParams()).leftMargin + pxWidth;
            int endY = ((ConstraintLayout.LayoutParams) adjacency.getHub2().getHubButton().getLayoutParams()).topMargin + pxHeight;
            // System.out.println(startX + "," +startY + ","+endX+ ","+endY);
            canvas.drawLine(startX, startY, endX, endY, paint);

        }

        adjacencyView.setImageBitmap(bitmap);
    }
    public void popupStart(View view){
        LayoutInflater inflater = this.getLayoutInflater();
        final View layout = inflater.inflate(R.layout.popup_start, null);
        final PopupWindow popupWindow = new PopupWindow(layout ,  FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT,false);
        Button btnClose = popupWindow.getContentView().findViewById(R.id.btn_start);
        TextView playerCount = popupWindow.getContentView().findViewById(R.id.txtPlayerCount);
        ImageView qrCode = popupWindow.getContentView().findViewById(R.id.qrCode);

       view.post(() -> popupWindow.showAtLocation(view,Gravity.CENTER, 0, 0));//Call popUp after setup has finished

        if(GlobalVariables.isClient){
            btnClose.setEnabled(false);
        }
       //TODO: update playerCount
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO:startNewGame

                popupWindow.dismiss();
                return;
            }
        });

        MultiFormatWriter mWriter = new MultiFormatWriter();
        try {
            BitMatrix mMatrix = mWriter.encode(GlobalVariables.getIpAddress(), BarcodeFormat.QR_CODE, 500,500);
            BarcodeEncoder mEncoder = new BarcodeEncoder();
            Bitmap mBitmap = mEncoder.createBitmap(mMatrix);
            qrCode.setImageBitmap(mBitmap);


        } catch (Exception e) {
            restart();
        }
    }

    public void restart(){
        Intent restartActivity = new Intent(getApplicationContext(), MenuActivity.class);
        int pendingIntent = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), pendingIntent,restartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager manager = (AlarmManager)getApplicationContext().getSystemService(getApplicationContext().ALARM_SERVICE);
        manager.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }
    public void popupSettings(){

        PopupWindow popupWindow= createPopUp(R.layout.popup_settings);

        popupWindow.showAtLocation(new View(GlobalVariables.baseContext), Gravity.CENTER, 0, 0);
        Button btnClose = popupWindow.getContentView().findViewById(R.id.btn_Close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                return;
            }
        });

        Switch switchMusic = popupWindow.getContentView().findViewById(R.id.switch_Music);
        switchMusic.setTextOn("On");
        switchMusic.setTextOff("Off");
        String switchStatus = switchMusic.getText().toString();

        switchMusic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    GlobalVariables.mediaPlayer.start();
                }else{
                    GlobalVariables.mediaPlayer.pause();
                }
            }
        });

        FloatingActionButton btnRestart = popupWindow.getContentView().findViewById(R.id.btn_Restart);
        btnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restart();
            }
        });

    }
    public  void popupCards(){
        PopupWindow popupWindow= createPopUp(R.layout.popup_cards);
        popupWindow.showAtLocation(new View(GlobalVariables.baseContext), Gravity.CENTER, 0, 0);

        Button btnBack = popupWindow.getContentView().findViewById(R.id.btn_Close);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                return;
            }
        });

        Cards c1 = new Cards(4000,Troops.INFANTRY,null);
        Cards c2 = new Cards(4001,Troops.ARTILLERY,null);
        Cards c3 = new Cards(4002,Troops.CAVALRY,null);
        Cards[] cards = {c1,c2,c3};

        Button btn0 = popupWindow.getContentView().findViewById(R.id.btn_Card0);
        Button btn1 = popupWindow.getContentView().findViewById(R.id.btn_Card1);
        Button btn2 = popupWindow.getContentView().findViewById(R.id.btn_Card2);
        Button btn3 = popupWindow.getContentView().findViewById(R.id.btn_Card3);
        Button btn4 = popupWindow.getContentView().findViewById(R.id.btn_Card4);
        Button[] btns = {btn0,btn1,btn2,btn3,btn4};

        for(int i = 0;i<btns.length;i++){

            try {
                switch (cards[i].getType()) {
                    case INFANTRY:
                        btns[i].setText("Infantry");
                        btns[i].setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.infantry, 0, 0);

                        break;
                    case CAVALRY:
                        btns[i].setText("Cavallary");
                        btns[i].setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.cavalry, 0, 0);

                        break;
                    case ARTILLERY:
                        btns[i].setText("Artillery");
                        btns[i].setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.artillery, 0, 0);

                        break;
                    default:
                        btns[i].setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.placeholder_card, 0, 0);
                        break;
                }
            }catch(Exception e ){
                btns[i].setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.placeholder_card, 0, 0);
            }

        }

    }

    public  void popupDiceRoll(int[] values) {
        PopupWindow popupWindow= createPopUp(R.layout.popup_diceroll);
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

    public void popupReinforceTroops(Button hubButton){
        PopupWindow popupWindow= createPopUp(R.layout.popup_movetroops);
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
                //selected.setText(selected.getHubButton().getText().toString()+troops);
                //selected.setAmountTroops(selected.getAmmountTroops+troops);   //set new troop count
                popupWindow.dismiss();
                return;
            }
        });
    }

    public void popupMovetroops(){
        PopupWindow popupWindow= createPopUp(R.layout.popup_movetroops);
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
    public  void popupChat(){
        PopupWindow popupWindow= createPopUp(R.layout.popup_chat);
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
                getGameManager().postCommand(new ProcessChatMessageCommand(sendMsg.getText().toString()));
                return;
            }
        });
    }
}