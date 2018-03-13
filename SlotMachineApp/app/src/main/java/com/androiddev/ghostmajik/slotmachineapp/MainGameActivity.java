/***************************************************
 Slot Machine App
 Created by Colin Pugh and Antonio Yumbla
 Student ID's 101083975 &&
 Modified example logic provided by professor
 to create working slot machine app.  Created
 variables and functions that were needed to fill out
 program and adapt the example code from javascript.

 March 10th 2018 All Rights Reserved
 ***************************************************/

package com.androiddev.ghostmajik.slotmachineapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
//import android.view.ViewDebug;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

//import org.w3c.dom.Text;

public class MainGameActivity extends AppCompatActivity {

    //image reel vars
    private ImageView _reel1;
    private ImageView _reel2;
    private ImageView _reel3;

    //player vars
    private int playerMoney = 1000;
    private int winnings = 0;
    private int betAmnt = 0;

    //jackpot vars
    private int jackpot = 5000;
    private boolean jackpotWinner = false;

    //arrays used for determining prizes and prize Image
    private int spinResult[] = {0,0,0};
    private int prizeImages[] = {R.drawable.blank,R.drawable.lemon,R.drawable.coin,R.drawable.orange,R.drawable.cherry,R.drawable.bar,R.drawable.bell,R.drawable.seven};

    //vars for prizes
    int prizeID = 0;
    private int lemons = 0;
    private int cherries = 0;
    private int bells = 0;
    private int sevens = 0;
    private int coins = 0;
    private int oranges = 0;
    private int bars = 0;
    private int blanks = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main_game);

        //set up texts field vars
        TextView betText = (TextView) findViewById(R.id.betAmountText);
        TextView betOutcomeText = (TextView) findViewById(R.id.betOutcomeText);

        //set up image reel vars
        _reel1 = (ImageView) findViewById(R.id.Reel1);
        _reel2 = (ImageView) findViewById(R.id.Reel2);
        _reel3 = (ImageView) findViewById(R.id.Reel3);

        final Button spinButton = (Button)findViewById(R.id.spinButton);
        spinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playerMoney <= 0 || betAmnt <= 0) {
                    spinButton.setEnabled(false);
                }
                else if(playerMoney >= betAmnt) {
                    UpdateStats(spinButton);
                    SpinReels();

                    //set the image reels to prizeID decided by SpinReels function
                    _reel1.setImageResource(prizeImages[spinResult[0]]);
                    _reel2.setImageResource(prizeImages[spinResult[1]]);
                    _reel3.setImageResource(prizeImages[spinResult[2]]);
                    determineWinnings();
                    UpdateStats(spinButton);
                }
            }
        });

        //bet min and bet max buttons, if player has enough money, update their bet amount and display it to the screen
        Button betMinButton = (Button)findViewById(R.id.minBetButton);
        betMinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView betText = (TextView) findViewById(R.id.betAmountText);
                if(playerMoney >= 10){
                    betAmnt = 10;
                    betText.setText("Bet: " + betAmnt);
                    spinButton.setEnabled(true);
                }
                else if (playerMoney < betAmnt){
                    betText.setText("No money");
                }
            }
        });

        Button betMaxButton = (Button)findViewById(R.id.maxBetButton);
        betMaxButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                TextView betText = (TextView) findViewById(R.id.betAmountText);
                if(playerMoney >= 100){
                    betAmnt = 100;
                    betText.setText("Bet: " + betAmnt);
                    spinButton.setEnabled(true);
                }
                else {
                    betText.setText("Money low");
                }
            }
        });

        Button resetButton = (Button)findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                spinButton.setEnabled(true);
                TextView betOutcomeText = (TextView) findViewById(R.id.betOutcomeText);
                betOutcomeText.setText("$0");
                ResetGame();

                //set image reels to blank on reset
                _reel1.setImageResource(prizeImages[0]);
                _reel2.setImageResource(prizeImages[0]);
                _reel3.setImageResource(prizeImages[0]);

                UpdateStats(spinButton);
            }
        });

        //simple quit button to exit game, game will be restarted on reload
        Button quitButton = (Button)findViewById(R.id.QuitButton);
        quitButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });

    }

    //passing spin button to check if player has enough money to make the bet, if not, bet will not be allowed
    //if player is out of money, this will cause spin button to disable
    protected void UpdateStats(Button button){
        TextView walletText = (TextView) findViewById(R.id.currentMoneyText);
        walletText.setText("Money: " + playerMoney);

        TextView jackPotText = (TextView) findViewById(R.id.jackPotText);
        jackPotText.setText("Jackpot: " + jackpot);

        TextView betText = (TextView) findViewById(R.id.betAmountText);
        betText.setText("Bet: " + betAmnt);
        if(playerMoney < betAmnt){
            button.setEnabled(false);
        }
    }

    protected void ResetGame(){
        playerMoney = 1000;
        winnings = 0;
        jackpot = 5000;
        betAmnt = 0;
    }

    protected void resetFruitTally() {
        oranges = 0;
        coins = 0;
        oranges = 0;
        cherries = 0;
        bars = 0;
        bells = 0;
        sevens = 0;
        blanks = 0;
    }

//runs if player wins on their spin
    protected void playerWin(){
        TextView betOutcomeText = (TextView) findViewById(R.id.betOutcomeText);
        checkJackPot();
        if(jackpotWinner == true)
            betOutcomeText.setText("JACKPOT!!");
        else
            betOutcomeText.setText("WIN: " + winnings);
        playerMoney += winnings;
        resetFruitTally();
    }
//runs if player loses on thier spin
    protected void playerLose(){
        TextView betOutcomeText = (TextView) findViewById(R.id.betOutcomeText);
        betOutcomeText.setText("Lost: " + -betAmnt);
        playerMoney -= betAmnt;
        resetFruitTally();
    }

//jackpot win is checked when player wins, if a jackpot is won, the next jackpot is worth half as much.
    protected void checkJackPot(){
        jackpotWinner = false;
        int jackPotCheck = (int)Math.floor(Math.random() * 51 + 1);
        int jackPotWin = (int)Math.floor(Math.random() * 51 + 1);
        if(jackPotCheck == jackPotWin) {
            jackpotWinner = true;
            playerMoney += jackpot;
            jackpot = jackpot / 2;
        }
    }

    protected boolean CheckRange(int value, int lower, int upper){
        if(value >= lower && value <= upper){
            return true;
        }
        else return false;
    }

    protected void SpinReels(){
        int[] outCome = {0,0,0};
        for(int spin = 0; spin < 3; spin++){
            outCome[spin] = (int)Math.floor((Math.random() * 65)+1);
            if(CheckRange(outCome[spin],1,27)){         //roll logic, if number exists between range, update the prizeID for that reel and increase number of those prizes
                blanks++;
                prizeID = 0;
            }
            else if(CheckRange(outCome[spin],28,37)){
                lemons++;
                prizeID = 1;
            }
            else if(CheckRange(outCome[spin],38,46)){
                coins++;
                prizeID = 2;
            }
            else if(CheckRange(outCome[spin],47,54)){
                oranges++;
                prizeID = 3;
            }
            else if(CheckRange(outCome[spin],55,59)){
                cherries++;
                prizeID = 4;
            }
            else if(CheckRange(outCome[spin],60,62)){
                bars++;
                prizeID = 5;
            }
            else if(CheckRange(outCome[spin],63,64)){
                bells++;
                prizeID = 6;
            }
            else if(CheckRange(outCome[spin],65,65)){
                sevens++;
                prizeID = 7;
            }
            spinResult[spin] = prizeID;  //add the roll outcome to an array to use to select what image to display on each reel
        }

    }

    protected void determineWinnings() {
        if (blanks == 0)
        {
            if (lemons == 3) {
                winnings = betAmnt * 10;
            }
            else if(coins == 3) {
                winnings = betAmnt * 20;
            }
            else if (oranges == 3) {
                winnings = betAmnt * 30;
            }
            else if (cherries == 3) {
                winnings = betAmnt * 40;
            }
            else if (bars == 3) {
                winnings = betAmnt * 50;
            }
            else if (bells == 3) {
                winnings = betAmnt * 100;
            }
            else if (sevens == 3) {
                winnings = betAmnt * 500;
            }
            else if (lemons == 2) {
                winnings = betAmnt * 2;
            }
            else if (coins == 2) {
                winnings = betAmnt * 2;
            }
            else if (oranges == 2) {
                winnings = betAmnt * 3;
            }
            else if (cherries == 2) {
                winnings = betAmnt * 4;
            }
            else if (bars == 2) {
                winnings = betAmnt * 5;
            }
            else if (bells == 2) {
                winnings = betAmnt * 10;
            }
            else if (sevens == 2) {
                winnings = betAmnt * 20;
            }
            else if (sevens == 1) {
                winnings = betAmnt * 5;
            }
            else {
                winnings = betAmnt * 1;
            }
            playerWin();
        }
        else
        {
            playerLose();
        }

    }

}
