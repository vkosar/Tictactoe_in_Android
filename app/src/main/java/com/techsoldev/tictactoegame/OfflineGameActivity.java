package com.techsoldev.tictactoegame;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.w3c.dom.Text;

import pl.droidsonroids.gif.GifImageView;

public class OfflineGameActivity extends AppCompatActivity implements View.OnClickListener  {



    // Initialize the boxes
    private ImageView Box_1,Box_2,Box_3,Box_4,Box_5,Box_6,Box_7,Box_8,Box_9, backBtn;

    private GifImageView  settingsGifView;
    private CircularImageView playerOneImg , playerTwoImg;

    private TextView playerOneWins, playerTwoWins;
    private TextView playerOneName, playerTwoName;
    Vibrator vibrator;

    Dialog dialog , drawdialog,quitdialog;

    int playerOneWinCount=0;
    int playerTwoWinCount=0;

    int PICK_SIDE ;
    private String playerOne;
    private String playerTwo;

    // Intialize the player X and O with 0 and 1 respectively
    int Player_X = 0;
    int Player_0 = 1;

    int storeActivePlayer ;
    int ActivePlayer ;

    // No player wins the game the isGameActive is true when the player X or O wins it will be false
    boolean isGameActive =true;


    // Initialize array with -1 when Player X or O fill click on the box it turn 0 and 1 respectively
    int[] filledPos = {-1,-1,-1,-1,-1,-1,-1,-1,-1};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



       requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
         getSupportActionBar().hide(); // hide the title bar
       this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
               WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen


        setContentView(R.layout.activity_offline_game);


        dialog = new Dialog(this);
        drawdialog = new Dialog(this);
        quitdialog = new Dialog(this);


        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        // link all the Boxes with Design (boxes in the activity_game.Xml  has the id so link with each Box)
        Box_1= (ImageView) findViewById(R.id.img_1);
        Box_2= (ImageView) findViewById(R.id.img_2);
        Box_3= (ImageView) findViewById(R.id.img_3);
        Box_4= (ImageView) findViewById(R.id.img_4);
        Box_5= (ImageView) findViewById(R.id.img_5);
        Box_6= (ImageView) findViewById(R.id.img_6);
        Box_7= (ImageView) findViewById(R.id.img_7);
        Box_8= (ImageView) findViewById(R.id.img_8);
        Box_9= (ImageView) findViewById(R.id.img_9);

        backBtn= (ImageView) findViewById(R.id.offline_game_back_btn);
        settingsGifView = (GifImageView) findViewById(R.id.offline_game_seting_gifview);

        playerOneImg = (CircularImageView) findViewById(R.id.player_one_img);
        playerTwoImg = (CircularImageView) findViewById(R.id.player_two_img);



        playerOneName = (TextView)findViewById(R.id.player_one_name_txt);
        playerTwoName = (TextView)findViewById(R.id.player_two_name_txt);
        playerOneWins = (TextView)findViewById(R.id.player_one_win_count_txt);
        playerTwoWins = (TextView)findViewById(R.id.player_two_won_txt);


        // if user click on particular Box the tag basically value of box (Box_1 has vlaue 1,Box_2 has vlaue 2 ,... ) send to the onClick function
        Box_1.setOnClickListener(this);
        Box_2.setOnClickListener(this);
        Box_3.setOnClickListener(this);
        Box_4.setOnClickListener(this);
        Box_5.setOnClickListener(this);
        Box_6.setOnClickListener(this);
        Box_7.setOnClickListener(this);
        Box_8.setOnClickListener(this);
        Box_9.setOnClickListener(this);


        playerOneWins.setText(String.valueOf(playerOneWinCount));
        playerTwoWins.setText(String.valueOf(playerTwoWinCount));

        playerOne = getIntent().getStringExtra("p1");
        playerTwo = getIntent().getStringExtra("p2");
        PICK_SIDE = getIntent().getIntExtra("ps",0);
        playerOneName.setText(playerOne);
        playerTwoName.setText(playerTwo);
        ActivePlayer = PICK_SIDE;
        storeActivePlayer = PICK_SIDE;

        Drawable drawable = settingsGifView.getDrawable();
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).stop();
        }

        if(PICK_SIDE==0)
        {


            playerOneImg.setBorderWidth(10f);
            playerOneImg.setBorderColorStart(Color.parseColor("#EB469A"));
            playerOneImg.setBorderColorEnd(Color.parseColor("#7251DF"));

            playerOneImg.setBorderColorDirection(CircularImageView.GradientDirection.TOP_TO_BOTTOM);


            // Set Border
            playerTwoImg.setBorderWidth(10f);
            playerTwoImg.setBorderColorStart(Color.parseColor("#F7A27B"));
            playerTwoImg.setBorderColorEnd(Color.parseColor("#FF3D00"));
            playerTwoImg.setBorderColorDirection(CircularImageView.GradientDirection.TOP_TO_BOTTOM);

            playerTwoImg.setAlpha(0.6f);



            storeActivePlayer = 0;
            ActivePlayer =  0 ;
        }
        else  if(PICK_SIDE == 1)
        {

            // Set Border
            playerOneImg.setBorderWidth(10f);
            playerOneImg.setBorderColorStart(Color.parseColor("#F7A27B"));
            playerOneImg.setBorderColorEnd(Color.parseColor("#FF3D00"));
            playerOneImg.setBorderColorDirection(CircularImageView.GradientDirection.TOP_TO_BOTTOM);


            playerTwoImg.setBorderWidth(10f);
            playerTwoImg.setBorderColorStart(Color.parseColor("#EB469A"));
            playerTwoImg.setBorderColorEnd(Color.parseColor("#7251DF"));

            playerOneImg.setBorderColorDirection(CircularImageView.GradientDirection.TOP_TO_BOTTOM);

            playerTwoImg.setAlpha(0.6f);
            storeActivePlayer = 1;
            ActivePlayer = 1 ;
        }


        settingsGifView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable drawable = settingsGifView.getDrawable();
                if (drawable instanceof Animatable) {
                    ((Animatable) drawable).start();


                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Drawable drawable = settingsGifView.getDrawable();
                        if (drawable instanceof Animatable) {
                            ((Animatable) drawable).stop();
                        }
                        Intent intent = new Intent(OfflineGameActivity.this,SettingsActivity.class);
                        startActivity(intent);

                    }
                }, 750);
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               quitDialogfun();
            }
        });


    }

    @Override
    public void onClick(View view) {


        // if isGameActive is false when the user click on button nothing can do and program exit from function
        if (!isGameActive)
            return;


        ImageView clickImg = (ImageView) findViewById(view.getId());
        // get the tag of button which user click
        int gettingTag = Integer.parseInt(view.getTag().toString());


        // check the Active player  and checked whether it already with X or O
        // if Active player is X than set the text to X , set its color to red and filled position to 0
        // and change the Active player O
        if(ActivePlayer ==  Player_X   &&  filledPos[gettingTag-1] == -1 )
        {
            if(MyServices.SOUND_CHECK) {
                final MediaPlayer mp = MediaPlayer.create(this, R.raw.x);
                mp.start();
            }

            if(MyServices.VIBRATION_CHECK) {
                if (Build.VERSION.SDK_INT >= 26) {
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(200);
                }
            }

            if(PICK_SIDE ==0)
            {
                playerOneImg.setAlpha(0.6f);
                playerTwoImg.setAlpha(1.0f);
            }
            else  if(PICK_SIDE ==1)
            {
                playerTwoImg.setAlpha(0.6f);
                playerOneImg.setAlpha(1.0f);
            }
            clickImg.setImageResource(R.drawable.cross);
           // clickBtn.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.design_default_color_error));
            // clickBtn.setBackground(getDrawable(android.R.color.holo_red_dark));

            storeActivePlayer =ActivePlayer;
            ActivePlayer = Player_0;
            int value = gettingTag -1;
            filledPos[value]= Player_X;
        }

        // check the Active player  and checked whether it already with X or O
        // if Active player is O than set the text to 0 , set its color to Blue and filled position to 1
        // and change the Active player X
        else  if(ActivePlayer == Player_0  && filledPos[gettingTag-1] == -1)
        {

            if(MyServices.SOUND_CHECK) {
                final MediaPlayer mp = MediaPlayer.create(this, R.raw.o);
                mp.start();
            }

            if(MyServices.VIBRATION_CHECK) {

                if (Build.VERSION.SDK_INT >= 26) {
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(200);
                }
            }

            if(PICK_SIDE ==0)
            {
                playerTwoImg.setAlpha(0.6f);
                playerOneImg.setAlpha(1.0f);
            }
            else  if(PICK_SIDE ==1)
            {

                playerOneImg.setAlpha(0.6f);
                playerTwoImg.setAlpha(1.0f);

            }
            clickImg.setImageResource(R.drawable.circle);
           // clickBtn.setText("O");
           // clickBtn.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.design_default_color_primary_dark));


            storeActivePlayer =ActivePlayer;
            ActivePlayer = Player_X;
            int value =gettingTag -1;
            filledPos[value]= Player_0;

        }


        // check the win condition
        checkForWin();

        if(isGameActive)
        {
            checkdraw();
        }
    }


    private void checkForWin(){

        // Store all the Winning conditions in 2D array
        int[][] winningPos = {{1,2,3},{4,5,6},{7,8,9},{1,4,7},{2,5,8},{3,6,9},{1,5,9},{3,5,7}};

        for(int i =0 ;i<8;i++){
            int val0  = winningPos[i][0];
            int val1  = winningPos[i][1];
            int val2  = winningPos[i][2];


            if(filledPos[val0-1] == filledPos[val1-1] && filledPos[val1-1] == filledPos[val2-1]){

                if( filledPos[val0-1] != -1){
                    //winner declare

                    if(storeActivePlayer ==Player_X)
                    {

                        if(PICK_SIDE ==0)
                        {
                            playerOneWinCount=+1;
                            playerOneWins.setText(String.valueOf(playerOneWinCount));
                        }
                        if(PICK_SIDE ==1)
                        {

                            playerTwoWinCount=+1;
                            playerTwoWins.setText(String.valueOf(playerTwoWinCount));
                        }


                        if(val0==1 && val1 ==2 && val2==3) {

                            Box_1.setBackgroundResource(R.drawable.cross_background);
                            Box_2.setBackgroundResource(R.drawable.cross_background);
                            Box_3.setBackgroundResource(R.drawable.cross_background);
                        }
                        else if(val0==4 && val1==5 && val2== 6) {
                            Box_4.setBackgroundResource(R.drawable.cross_background);
                            Box_5.setBackgroundResource(R.drawable.cross_background);
                            Box_6.setBackgroundResource(R.drawable.cross_background);

                        }
                        else if(val0==7 && val1==8 && val2== 9) {
                            Box_7.setBackgroundResource(R.drawable.cross_background);
                            Box_8.setBackgroundResource(R.drawable.cross_background);
                            Box_9.setBackgroundResource(R.drawable.cross_background);

                        }
                        else if(val0==1 && val1==4 && val2== 7) {
                            Box_1.setBackgroundResource(R.drawable.cross_background);
                            Box_4.setBackgroundResource(R.drawable.cross_background);
                            Box_7.setBackgroundResource(R.drawable.cross_background);

                        }
                        else if(val0==2 && val1==5 && val2== 8) {
                            Box_2.setBackgroundResource(R.drawable.cross_background);
                            Box_5.setBackgroundResource(R.drawable.cross_background);
                            Box_8.setBackgroundResource(R.drawable.cross_background);

                        }
                        else if(val0==3 && val1==6 && val2== 9) {
                            Box_3.setBackgroundResource(R.drawable.cross_background);
                            Box_6.setBackgroundResource(R.drawable.cross_background);
                            Box_9.setBackgroundResource(R.drawable.cross_background);

                        }
                        else if(val0==1 && val1==5 && val2== 9) {
                            Box_1.setBackgroundResource(R.drawable.cross_background);
                            Box_5.setBackgroundResource(R.drawable.cross_background);
                            Box_9.setBackgroundResource(R.drawable.cross_background);

                        }
                        else if(val0==3 && val1==5 && val2== 7) {
                            Box_3.setBackgroundResource(R.drawable.cross_background);
                            Box_5.setBackgroundResource(R.drawable.cross_background);
                            Box_7.setBackgroundResource(R.drawable.cross_background);

                        }

                        Handler handler = new Handler();
                        if(MyServices.SOUND_CHECK) {
                            final MediaPlayer mp = MediaPlayer.create(this, R.raw.click);
                            mp.start();
                        }
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                celebrateDialog(0);
                            }
                        }, 750);


                       // Toast.makeText(this, R.string.player_x_wins, Toast.LENGTH_SHORT).show();
                    }

                   else if(storeActivePlayer ==Player_0)
                    {
                        if(PICK_SIDE ==0)
                        {
                            playerTwoWinCount=+1;
                            playerTwoWins.setText(String.valueOf(playerTwoWinCount));

                        }
                        if(PICK_SIDE ==1)
                        {
                            playerOneWinCount=+1;
                            playerOneWins.setText(String.valueOf(playerOneWinCount));

                        }

                        if(val0==1 && val1 ==2 && val2==3) {

                            Box_1.setBackgroundResource(R.drawable.circle_background);
                            Box_2.setBackgroundResource(R.drawable.circle_background);
                            Box_3.setBackgroundResource(R.drawable.circle_background);
                        }
                        else if(val0==4 && val1==5 && val2== 6) {
                            Box_4.setBackgroundResource(R.drawable.circle_background);
                            Box_5.setBackgroundResource(R.drawable.circle_background);
                            Box_6.setBackgroundResource(R.drawable.circle_background);

                        }
                        else if(val0==7 && val1==8 && val2== 9) {
                            Box_7.setBackgroundResource(R.drawable.circle_background);
                            Box_8.setBackgroundResource(R.drawable.circle_background);
                            Box_9.setBackgroundResource(R.drawable.circle_background);

                        }
                        else if(val0==1 && val1==4 && val2== 7) {
                            Box_1.setBackgroundResource(R.drawable.circle_background);
                            Box_4.setBackgroundResource(R.drawable.circle_background);
                            Box_7.setBackgroundResource(R.drawable.circle_background);

                        }
                        else if(val0==2 && val1==5 && val2== 8) {
                            Box_2.setBackgroundResource(R.drawable.circle_background);
                            Box_5.setBackgroundResource(R.drawable.circle_background);
                            Box_8.setBackgroundResource(R.drawable.circle_background);

                        }
                        else if(val0==3 && val1==6 && val2== 9) {
                            Box_3.setBackgroundResource(R.drawable.circle_background);
                            Box_6.setBackgroundResource(R.drawable.circle_background);
                            Box_9.setBackgroundResource(R.drawable.circle_background);

                        }
                        else if(val0==1 && val1==5 && val2== 9) {
                            Box_1.setBackgroundResource(R.drawable.circle_background);
                            Box_5.setBackgroundResource(R.drawable.circle_background);
                            Box_9.setBackgroundResource(R.drawable.circle_background);

                        }
                        else if(val0==3 && val1==5 && val2== 7) {
                            Box_3.setBackgroundResource(R.drawable.circle_background);
                            Box_5.setBackgroundResource(R.drawable.circle_background);
                            Box_7.setBackgroundResource(R.drawable.circle_background);
                        }

                        Handler handler = new Handler();
                        if(MyServices.SOUND_CHECK) {
                            final MediaPlayer mp = MediaPlayer.create(this, R.raw.click);
                            mp.start();
                        }
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                celebrateDialog(1);
                            }
                        }, 750);
                        //Toast.makeText(this, R.string.player_0_wins, Toast.LENGTH_SHORT).show();
                    }
                    isGameActive = false;
                }

            }


        }
    }

    void checkdraw()
    {
        boolean check = true;
        for(int i =0 ;i<=8;i++){
            if(filledPos[i]== -1)
            {
                check= false;
            }
        }
        if(check)
        {
          //  Toast.makeText(getBaseContext(), R.string.match_draw, Toast.LENGTH_SHORT).show();
            isGameActive = false;
            if(MyServices.SOUND_CHECK) {
                final MediaPlayer mp = MediaPlayer.create(this, R.raw.click);
                mp.start();
            }
            DrawDialogfun();

        }
    }


    private void celebrateDialog(int player_check) {


        dialog.setContentView(R.layout.celebrate_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);


        LottieAnimationView animationView = dialog.findViewById(R.id.celebrate_animationView);
        LinearLayout linearLayout = dialog.findViewById(R.id.container_1);
        Button quitBtn = dialog.findViewById(R.id.offline_game_quit_btn);
        Button continueBtn = dialog.findViewById(R.id.offline_game_continue_btn);
        ImageView playerImg = dialog.findViewById(R.id.offline_game_player_img);





        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
              animationView.setVisibility(View.GONE);
              linearLayout.setVisibility(View.VISIBLE);
                if(player_check==0) {
                    playerImg.setImageResource(R.drawable.cross);
                } else  if(player_check==1) {
                    playerImg.setImageResource(R.drawable.circle);
                }
            }
        }, 2300);



        quitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(OfflineGameActivity.this, OfflineGameMenuActivity.class);
                startActivity(intent);
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
               Restart();
            }
        });

        dialog.show();
    }




    private void    DrawDialogfun() {


        drawdialog.setContentView(R.layout.draw_dialog);
        drawdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);


        Button quitBtn = drawdialog.findViewById(R.id.offline_game_draw_quit_btn);
        Button continueBtn = drawdialog.findViewById(R.id.offline_game_draw_continue_btn);

        quitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawdialog.dismiss();
                Intent intent = new Intent(OfflineGameActivity.this, OfflineGameMenuActivity.class);
                startActivity(intent);
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawdialog.dismiss();
                Restart();
            }
        });
        drawdialog.show();
    }


    private void Restart()
    {

        for(int i =0 ;i<=8;i++){
           filledPos[i]= -1;
        }

        Box_1.setBackgroundResource(0);
        Box_2.setBackgroundResource(0);
        Box_3.setBackgroundResource(0);
        Box_4.setBackgroundResource(0);
        Box_5.setBackgroundResource(0);
        Box_6.setBackgroundResource(0);
        Box_7.setBackgroundResource(0);
        Box_8.setBackgroundResource(0);
        Box_9.setBackgroundResource(0);



       Box_1.setImageResource(0);
       Box_2.setImageResource(0);
       Box_3.setImageResource(0);
       Box_4.setImageResource(0);
       Box_5.setImageResource(0);
       Box_6.setImageResource(0);
       Box_7.setImageResource(0);
       Box_8.setImageResource(0);
       Box_9.setImageResource(0);


        isGameActive =true;



    }


    private void    quitDialogfun() {


        quitdialog.setContentView(R.layout.quit_dialog);
        quitdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        quitdialog.setCanceledOnTouchOutside(false);


        Button quitBtn = quitdialog.findViewById(R.id.quit_btn);
        Button continueBtn = quitdialog.findViewById(R.id.continue_btn);

        quitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quitdialog.dismiss();
                Intent intent = new Intent(OfflineGameActivity.this, OfflineGameMenuActivity.class);
                startActivity(intent);
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quitdialog.dismiss();
            }
        });
        quitdialog.show();
    }

}