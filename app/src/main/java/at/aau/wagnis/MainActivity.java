package at.aau.wagnis;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Insets;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.Network;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowMetrics;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import at.aau.wagnis.application.GameManager;
import at.aau.wagnis.application.WagnisApplication;
import at.aau.wagnis.gamestate.ChatMessage;
import at.aau.wagnis.gamestate.GameData;
import at.aau.wagnis.server.communication.command.ChooseAttackCommand;
import at.aau.wagnis.server.communication.command.ChooseMoveCommand;
import at.aau.wagnis.server.communication.command.EndTurnCommand;
import at.aau.wagnis.server.communication.command.IdentifyCommand;
import at.aau.wagnis.server.communication.command.ProcessChatMessageCommand;
import at.aau.wagnis.server.communication.command.ReinforceCommand;
import at.aau.wagnis.server.communication.command.StartGameCommand;
import at.aau.wagnis.server.communication.command.UseCardsCommand;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton btnEndTurn;
    FloatingActionButton btnCards;
    FloatingActionButton btnSettings;
    FloatingActionButton btnChat;
    ImageView adjacencyView;
    GameData currentGameData;
    boolean wasDrawn = false;
    PopupWindow startPopup;
    TextView playerCount;
    String lastState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                moveTaskToBack(true);
            }
        });

        getGameManager().postCommand(new IdentifyCommand(GlobalVariables.getLocalIpAddress()));

        setContentView(R.layout.activity_main);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        adjacencyView = findViewById(R.id.adjacenciesView);
        btnEndTurn = findViewById(R.id.btn_EndTurn);
        btnCards = findViewById(R.id.btn_Cards);
        btnSettings = findViewById(R.id.btn_Settings);
        btnChat = findViewById(R.id.btn_Chat);


        setDisplayMetrics();

        btnSettings.setOnClickListener(view -> popupSettings());

        btnChat.setOnClickListener(view -> popupChat());
      btnCards.setOnClickListener(view -> popupCards(new Player()));


        btnEndTurn.setOnClickListener(view -> {
            lastClickedHub=null;
            getGameManager().postCommand(new EndTurnCommand());
            
        });

        ((WagnisApplication) getApplication()).getGameManager().setGameDataListener(newGameData -> runOnUiThread(() -> {
            /*Code to be executed on UI thread*/

            if (newGameData != null && currentGameData != null && !(currentGameData.getMessages().equals(newGameData.getMessages()))) {
                btnChat.setCustomSize(300);
            }

            currentGameData = newGameData;

            if (!wasDrawn) {
                generateMap(currentGameData.getSeed());
                popupStart(btnCards);
                wasDrawn = true;
            } else {
                for (Hub h : currentGameData.getHubs()) {
                    Hub uiHub = GlobalVariables.findHubById(h.getId());
                    uiHub.setText(h.getAmountTroops() + ", " + h.getId());
                    if (h.getOwner() != null) {
                        switch (h.getOwner().getPlayerId()) {
                            case 0:
                                uiHub.setHubImage("ESA");
                                break;
                            case 1:
                                uiHub.setHubImage("NASA");
                                break;
                            case 2:
                                uiHub.setHubImage("JAXA");
                                break;
                            case 3:
                                uiHub.setHubImage("ISRO");
                                break;
                            case 4:
                                uiHub.setHubImage("Roskosmos");
                                break;
                            default:
                                uiHub.setHubImage("China manned space program");
                        }
                    }
                }
                btnCards.setOnClickListener(view -> popupCards(currentGameData.getPlayers().get(currentGameData.getCurrentPlayer())));
                enableButtons();
                showState();
            }

            if (startPopup.isShowing()) {
                updatePlayerCount();
                if (!(currentGameData.getCurrentGameLogicState().equals("LobbyState"))) {
                    startPopup.dismiss();
                }
            }

            if (currentGameData.getCurrentGameLogicState().equals("VictoryState")) {
                String ip = currentGameData.getPlayerIdentifier().get(currentGameData.getCurrentPlayer());
                showEndGame(ip.equals(getIpAddress()));
            }
        }));


    }

    // Überprüft, ob der aktuelle Spieler der Spieler des Geräts ist
    private boolean isCurrentPlayer() {
        // Vergleicht die IP-Adresse des aktuellen Geräts mit der IP-Adresse des aktuellen Spielers
        return currentGameData.getPlayerIdentifier().get(currentGameData.getCurrentPlayer()).equals(getIpAddress());
    }


    private void generateMap(String seed) {
        GlobalVariables.setSeed(seed);
        drawHubs(GlobalVariables.getSeed());
        GlobalVariables.setAdjacencies();
        drawAdjacencies();
    }

    private void enableButtons() {
        if (isCurrentPlayer()) {
            btnEndTurn.setEnabled(true);
            btnCards.setEnabled(true);
            for (int i = 0; i < GlobalVariables.getHubs().size(); i++) {
                GlobalVariables.getHubs().get(i).getHubButton().setEnabled(true);
            }
        } else {
            btnEndTurn.setEnabled(false);
            btnCards.setEnabled(false);
            for (int i = 0; i < GlobalVariables.getHubs().size(); i++) {
                GlobalVariables.getHubs().get(i).getHubButton().setEnabled(false);
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        /*System Bars appearing when focusable popups are opened appears to be a bug*/
        WindowInsetsControllerCompat windowInsetsController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
    }


    @Override
    public void onResume() {
        GlobalVariables.getMediaPlayer().start();
        super.onResume();
    }
    @Override
    protected void onPause() {
        GlobalVariables.getMediaPlayer().pause();
        super.onPause();
    }

    public void setDisplayMetrics() {
        WindowMetrics windowMetrics = getWindowManager().getCurrentWindowMetrics();
        Insets insets = windowMetrics.getWindowInsets().getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
        GlobalVariables.setDisplayWidthPx(windowMetrics.getBounds().width() - insets.top - insets.bottom);          /*width & height swapped because landscape mode*/
        GlobalVariables.setDisplayHeightPx(windowMetrics.getBounds().height() - insets.left - insets.right);
    }


    private int dpToPx(int dp) {
        return dp * (getResources().getDisplayMetrics().densityDpi / 160);
    }

    public PopupWindow createPopUp(int popupId) {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popUp = inflater.inflate(popupId, null);
        return new PopupWindow(popUp, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, true);
    }

    private GameManager getGameManager() {
        return ((WagnisApplication) getApplication()).getGameManager();
    }

    private void showState(){
        if(!currentGameData.getCurrentGameLogicState().equals("LobbyState") && (!currentGameData.getCurrentGameLogicState().equals(lastState))){
            if (isCurrentPlayer()) {
                Toast.makeText(MainActivity.this, "Your Turn: " + currentGameData.getCurrentGameLogicState(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Player " + currentGameData.getCurrentPlayer() +" "+currentGameData.getCurrentGameLogicState()+" turn!", Toast.LENGTH_SHORT).show();
            }
            lastState=currentGameData.getCurrentGameLogicState();
        }
    }
    private Hub lastClickedHub = null;
    public void drawHubs(String seed) {

        ConstraintLayout layout = findViewById(R.id.layout_activity_main);
        ConstraintSet cs = new ConstraintSet();

        for (int i = 1; i <= seed.length(); i++) {
            if (i % 2 == 0) {
                GlobalVariables.getSeeds().add(seed.substring(i - 2, i));
            }
        }

        int hubs = 0;
        GlobalVariables.setHubsPerLine((int) Math.ceil(GlobalVariables.getSeeds().size() / 6f));
        int lineHubCount = 0;

        int hubWidthSpace = (GlobalVariables.getDisplayWidthPx() - dpToPx(100)) / GlobalVariables.getHubsPerLine();
        int height = GlobalVariables.getDisplayHeightPx();
        int heightSpace = height / 6;

        // Für jeden Eintrag in der Liste Seeds von GlobalVariables durchlaufen
        for (String s : GlobalVariables.getSeeds()) {
            // Erzeugen einer Schaltfläche (Button) mit einem speziellen Stil (btn_hub_style)
            Button hub = new Button(new ContextThemeWrapper(this, R.style.btn_hub_style), null, R.style.btn_hub_style);
            hub.setId(100 + hubs);  // Eindeutige ID für die Schaltfläche setzen

            // OnClickListener für die Schaltfläche festlegen
            hub.setOnClickListener(view -> {
                    Hub clickedHub = null;
                    for (Hub h : currentGameData.getHubs()) {// Überprüfen, welcher Hub mit der geklickten Schaltfläche übereinstimmt
                        if (h.getId() == hub.getId()) {
                            clickedHub = h;

                        }
                    }

                    if (lastClickedHub != null) {

                        if(currentGameData.getCurrentGameLogicState().equals("ChooseAttackGameState")&&currentGameData.getCurrentPlayer() != clickedHub.getOwner().getPlayerId()){
                             // Überprüfen, ob der aktuelle Spieler nicht der Besitzer des Zielhubs ist
                                getGameManager().postCommand(new ChooseAttackCommand(lastClickedHub.getId(), clickedHub.getId())); // Befehl zum Angriff senden und den letzten ausgewählten Hub zurücksetzen


                        } else if (currentGameData.getCurrentGameLogicState().equals("ChooseMoveState")&&currentGameData.getCurrentPlayer() == clickedHub.getOwner().getPlayerId()&&lastClickedHub!=clickedHub) {
                            popupMoveTroops(lastClickedHub, clickedHub);

                        } else{
                            Toast.makeText(MainActivity.this, "Invalid Hub!", Toast.LENGTH_SHORT).show();
                        }
                        lastClickedHub = null;
                    } else {
                        // Überprüfen, ob der aktuelle Spieler der Besitzer des Quellhubs ist
                        if (currentGameData.getCurrentPlayer() == clickedHub.getOwner().getPlayerId()) {
                            if(currentGameData.getCurrentGameLogicState().equals("ReinforceGameState")){
                                popupReinforceTroops(clickedHub);
                                lastClickedHub=null;
                            }else{
                                lastClickedHub = clickedHub;  // Den zuletzt geklickten Hub speichern
                            }
                             } else {
                            Toast.makeText(MainActivity.this, "Source must be in your possession!\nChoose a new Hub!", Toast.LENGTH_SHORT).show();
                        }
                    }
            });
            GlobalVariables.getHubs().add(new Hub(hub));
            layout.addView(hub);

            int top = (hubs / GlobalVariables.getHubsPerLine()) * heightSpace;
            int pos = hubWidthSpace / 100 * Integer.parseInt(s);
            int left = hubWidthSpace * lineHubCount + pos;

            cs.clone(layout);
            cs.connect(hub.getId(), ConstraintSet.TOP, layout.getId(), ConstraintSet.TOP, top);
            cs.connect(hub.getId(), ConstraintSet.LEFT, layout.getId(), ConstraintSet.LEFT, left);
            cs.applyTo(layout);
            hubs++;
            lineHubCount++;
            if (lineHubCount == GlobalVariables.getHubsPerLine()) {
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

        for (Adjacency adjacency : GlobalVariables.getAdjacencies()) {
            int pxWidth = dpToPx(21);
            int pxHeight = dpToPx(60);

            int startX = ((ConstraintLayout.LayoutParams) adjacency.getHub1().getHubButton().getLayoutParams()).leftMargin + pxWidth;
            int startY = ((ConstraintLayout.LayoutParams) adjacency.getHub1().getHubButton().getLayoutParams()).topMargin + pxHeight;
            int endX = ((ConstraintLayout.LayoutParams) adjacency.getHub2().getHubButton().getLayoutParams()).leftMargin + pxWidth;
            int endY = ((ConstraintLayout.LayoutParams) adjacency.getHub2().getHubButton().getLayoutParams()).topMargin + pxHeight;
            canvas.drawLine(startX, startY, endX, endY, paint);

        }
        adjacencyView.setImageBitmap(bitmap);
    }

    private void updatePlayerCount() {
        playerCount.setText("PlayerCount: " + currentGameData.getPlayers().size());
    }

    public void popupStart(View view) {
        LayoutInflater inflater = this.getLayoutInflater();
        final View layout = inflater.inflate(R.layout.popup_start, null);
        startPopup = new PopupWindow(layout, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, false);
        Button btnClose = startPopup.getContentView().findViewById(R.id.btn_start);
        playerCount = startPopup.getContentView().findViewById(R.id.txtPlayerCount);
        ImageView qrCode = startPopup.getContentView().findViewById(R.id.qrCode);


        if (view.post(() -> startPopup.showAtLocation(new View(this), Gravity.CENTER, 0, 0))) { //Call popUp after setup has finished
            updatePlayerCount();
        }

        if (Boolean.TRUE.equals(GlobalVariables.getIsClient())) {
            btnClose.setEnabled(false);
        }

        btnClose.setOnClickListener(view1 -> {
            getGameManager().postCommand(new StartGameCommand());
            btnClose.setEnabled(false);
        });

        MultiFormatWriter mWriter = new MultiFormatWriter();
        try {
            BitMatrix mMatrix;
            if (Boolean.TRUE.equals(GlobalVariables.getIsClient())) {
                mMatrix = mWriter.encode(GlobalVariables.getHostIP(), BarcodeFormat.QR_CODE, 500, 500);
            } else {
                mMatrix = mWriter.encode(getIpAddress(), BarcodeFormat.QR_CODE, 500, 500);
            }

            BarcodeEncoder mEncoder = new BarcodeEncoder();
            Bitmap mBitmap = mEncoder.createBitmap(mMatrix);
            qrCode.setImageBitmap(mBitmap);

        } catch (Exception e) {
            restart();
        }
    }

    private String getIpAddress() {
        ConnectivityManager manager = getSystemService(ConnectivityManager.class);
        Network network = manager.getActiveNetwork();
        LinkProperties prop = manager.getLinkProperties(network);
        for (LinkAddress linkAddress : prop.getLinkAddresses()) {
            InetAddress inetAddress = linkAddress.getAddress();
            if (inetAddress instanceof Inet4Address) {
                return inetAddress.getHostAddress();
            }
        }
        return "no fitting ip address found";
    }

    public void restart() {
        System.exit(0);
    }

    private void showEndGame(boolean victor) {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popUp;
        if (victor) {
            popUp = inflater.inflate(R.layout.win_screen, null);
        } else {
            popUp = inflater.inflate(R.layout.lose_screen, null);
        }
        PopupWindow popupWindow = new PopupWindow(popUp, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        popupWindow.showAtLocation(new View(this), Gravity.CENTER, 0, 0);
        Button btnClose = popupWindow.getContentView().findViewById(R.id.buttonHome);
        btnClose.setOnClickListener(view -> System.exit(0));
    }

    public void popupSettings() {

        PopupWindow popupWindow = createPopUp(R.layout.popup_settings);

        popupWindow.showAtLocation(new View(this), Gravity.CENTER, 0, 0);


        SwitchCompat switchMusic = popupWindow.getContentView().findViewById(R.id.switch_Music);
        switchMusic.setText(R.string.music);

        switchMusic.setChecked(GlobalVariables.getMediaPlayer().isPlaying());

        switchMusic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                GlobalVariables.getMediaPlayer().start();
            } else {
                GlobalVariables.getMediaPlayer().pause();
            }
        });

        FloatingActionButton btnRestart = popupWindow.getContentView().findViewById(R.id.btn_Restart);
        btnRestart.setOnClickListener(view -> restart());

    }

    public void popupCards(Player player) {
        PopupWindow popupWindow = createPopUp(R.layout.popup_cards);
        popupWindow.showAtLocation(new View(this), Gravity.CENTER, 0, 0);

        Button btnPlay = popupWindow.getContentView().findViewById(R.id.btn_play);
        Button btnBack = popupWindow.getContentView().findViewById(R.id.btn_Close);
        btnBack.setOnClickListener(view -> popupWindow.dismiss());
        Cards[] cards = player.getHand();

        Button[] btns = new Button[5];
        btns[0] = popupWindow.getContentView().findViewById(R.id.btn_Card0);
        btns[1] = popupWindow.getContentView().findViewById(R.id.btn_Card1);
        btns[2] = popupWindow.getContentView().findViewById(R.id.btn_Card2);
        btns[3] = popupWindow.getContentView().findViewById(R.id.btn_Card3);
        btns[4] = popupWindow.getContentView().findViewById(R.id.btn_Card4);

        for(Button b : btns){
            b.setBackgroundColor(Color.GRAY);
        }

        updateCards(btns, cards);

        Button[] selectedButtons = new Button[3];

        for (int i = 0; i < btns.length; i++) {
            int index = i;
            btns[i].setOnClickListener(view -> {
                for(int j = 0; j < selectedButtons.length; j++) {
                    if (player.getHand()[index] != null) {
                        if (selectedButtons[j] == null) {
                            selectedButtons[j] = btns[index];
                            btns[index].setBackgroundColor(Color.RED);
                            return;
                        } else if (selectedButtons[j].equals(btns[index])) {
                            selectedButtons[j] = null;
                            btns[index].setBackgroundColor(Color.GRAY);
                            return;
                        }
                    }
                }
            });
        }

        btnPlay.setOnClickListener(view -> {
            int[] indexes = new int[3];
            int counter = 0;
            for(int i = 0; i < btns.length; i++) {
                for (Button button : selectedButtons) {
                    if (button == null)
                        return;
                    if (button.equals(btns[i])) {
                        indexes[counter++] = i;
                    }
                }
            }
            getGameManager().postCommand(new UseCardsCommand(indexes[0], indexes[1], indexes[2]));
            popupWindow.dismiss();
        });
    }

    public void updateCards(Button[] btns, Cards[] cards) {
        for (int i = 0; i < btns.length; i++) {
            drawCards(cards[i], btns[i]);
        }
    }

    public void drawCards(Cards card, Button btn) {
        try {
            switch (card.getType()) {
                case INFANTRY:
                    btn.setText(R.string.infantry);
                    btn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.infantry, 0, 0);

                    break;
                case CAVALRY:
                    btn.setText(R.string.cavalry);
                    btn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.cavalry, 0, 0);

                    break;
                case ARTILLERY:
                    btn.setText(R.string.artillery);
                    btn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.artillery, 0, 0);

                    break;
                default:
                    btn.setText(R.string.empty);
                    btn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.placeholder_card, 0, 0);
                    break;
            }
        } catch (Exception e) {
            btn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.placeholder_card, 0, 0);
        }
    }

    public void popupDiceRoll(int[] values) {
        PopupWindow popupWindow = createPopUp(R.layout.popup_diceroll);
        popupWindow.showAtLocation(new View(this), Gravity.CENTER, 0, 0);

        Button btnBack = popupWindow.getContentView().findViewById(R.id.btn_Back);
        btnBack.setOnClickListener(view -> popupWindow.dismiss());

        NumberPicker n1 = popupWindow.getContentView().findViewById(R.id.dice1);
        NumberPicker n2 = popupWindow.getContentView().findViewById(R.id.dice2);
        NumberPicker n3 = popupWindow.getContentView().findViewById(R.id.dice3);
        NumberPicker n4 = popupWindow.getContentView().findViewById(R.id.dice4);
        NumberPicker n5 = popupWindow.getContentView().findViewById(R.id.dice5);

        NumberPicker[] dice = {n1, n2, n3, n4, n5};
        setDice(dice, values);
    }

    public static void setDice(NumberPicker[] dice, int[] values) {
        for (int i = 0; i < dice.length; i++) {
            dice[i].setMaxValue(6);
            dice[i].setMinValue(1);
            dice[i].setValue(values[i]);
        }
    }

    public void popupReinforceTroops(Hub hub) {
        PopupWindow popupWindow = createPopUp(R.layout.popup_movetroops);
        popupWindow.showAtLocation(new View(this), Gravity.CENTER, 0, 0);

        Button btnClose = popupWindow.getContentView().findViewById(R.id.btn_Close);
        NumberPicker np = popupWindow.getContentView().findViewById(R.id.np_troops);
        Player currentPlayer=null;
        for(Player p : currentGameData.getPlayers()){
            if(p.getPlayerId()==currentGameData.getCurrentPlayer()){
                currentPlayer=p;
            }
        }
        if(currentPlayer!=null){
            np.setMaxValue(currentPlayer.getUnassignedAvailableTroops());
            np.setMinValue(0);
            btnClose.setOnClickListener(view -> {
                int troop = np.getValue();
                if(troop > 0) {
                    List<Integer> hubs = new ArrayList<>();
                    hubs.add(hub.getId());
                    List<Integer> troops = new ArrayList<>();
                    troops.add(troop);
                    getGameManager().postCommand(new ReinforceCommand(hubs, troops));
                }
                popupWindow.dismiss();
            });
        }else{
            popupWindow.dismiss();
        }


    }

    public void popupMoveTroops(Hub source,Hub target) {
        PopupWindow popupWindow = createPopUp(R.layout.popup_movetroops);
        if(source.getAmountTroops()>1){
            popupWindow.showAtLocation(new View(this), Gravity.CENTER, 0, 0);
        }else{
            popupWindow.dismiss();
        }


        Button btnClose = popupWindow.getContentView().findViewById(R.id.btn_Close);
        NumberPicker np = popupWindow.getContentView().findViewById(R.id.np_troops);
        np.setMaxValue(source.getAmountTroops()-1);     //setMaxValue
        np.setMinValue(1);
        btnClose.setOnClickListener(view -> {

            int troops =np.getValue();
            getGameManager().postCommand(new ChooseMoveCommand(source.getId(), target.getId(),troops));
            popupWindow.dismiss();


        });
    }


    public void popupChat() {
        PopupWindow popupWindow = createPopUp(R.layout.popup_chat);
        popupWindow.showAtLocation(new View(this
        ), Gravity.CENTER, 0, 0);

        btnChat.clearCustomSize();

        Button btnExit = popupWindow.getContentView().findViewById(R.id.btn_Exit);
        Button btnSend = popupWindow.getContentView().findViewById(R.id.btn_Send);

        TextView msg = popupWindow.getContentView().findViewById(R.id.chatMsg);
        if (currentGameData != null) {
            String messages = currentGameData.getMessages()

                    .stream()
                    .map(ChatMessage::toString)
                    .collect(Collectors.joining("\n"));

            msg.setText(messages);
        }

        EditText sendMsg = popupWindow.getContentView().findViewById(R.id.sendMsg);
        btnExit.setOnClickListener(view -> popupWindow.dismiss());

            btnSend.setOnClickListener(view -> {
                try{
                getGameManager().postCommand(new ProcessChatMessageCommand(sendMsg.getText().toString()));
                }catch (Exception e ){
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }});
    }


}