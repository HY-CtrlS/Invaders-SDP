package CtrlS;

import engine.Core;
import engine.GameState;
// item level Bonus
import inventory_develop.ShipStatus;

public class RoundState {
    private final GameState prevState;
    private final GameState currState;
    private final int roundScore;
    private final int roundBulletsShot;
    private final int roundHitCount;
    private final int roundCoin;

    private final float roundHitRate;
    private final long roundTime;

    private ShipStatus shipStatus;   //inventory team
    private static double levelBonus2 = 1;

    public RoundState(GameState prevState, GameState currState) {
        this.prevState = prevState;
        this.currState = currState;
        this.roundScore = currState.getScore() - prevState.getScore();
        this.roundBulletsShot = currState.getBulletsShot() - prevState.getBulletsShot();
        this.roundHitCount = currState.getHitCount() - prevState.getHitCount();
        this.roundHitRate = roundHitCount / (float) roundBulletsShot;
        this.roundTime = currState.getTime() - prevState.getTime();
        this.roundCoin = calculateCoin();

        //Coin Bonus increase by Level
        shipStatus = new ShipStatus();
        shipStatus.loadStatus();
    }

    private int calculateCoin() {
        int coin = 0;




        int baseCoin = roundScore / 10;
        //레벨 보너스(levelBonus)
        int levelBonus = baseCoin * currState.getLevel();
        coin += levelBonus;

        //정확도 보너스(accuracyBonus)
        int accuracyBonus = 0;

        if (roundHitRate > 0.9) {
            accuracyBonus += (int) (coin * 0.3);
            coin += (int) (coin * 0.3); // 30% 보너스 지급
            Core.getLogger().info("hitRate bonus occurs (30%).");
        } else if (roundHitRate > 0.8) {
            accuracyBonus += (int) (coin * 0.3);
            coin += (int) (coin * 0.2); // 20% 보너스 지급
            Core.getLogger().info("hitRate bonus occurs (20%).");
        } else if (roundHitRate > 0.7) {
            accuracyBonus += (int) (coin * 0.3);
            coin += (int) (coin * 0.1); // 10% 보너스 지급
            Core.getLogger().info("hitRate bonus occurs (10%).");
        }

        // Round clear time in seconds
        // DEBUGGING NEEDED(playTime)
        long timeDifferenceInSeconds = (currState.getTime() - prevState.getTime()) / 1000;
        //시간 보너스(timeBonus)
        int timeBonus = 0;

        /*
          clear time   : 0 ~ 50    : +50
                       : 51 ~ 80   : +30
                       : 81 ~ 100  : +10
                       : 101 ~     : 0
         */
        if (timeDifferenceInSeconds <= 50) {
            timeBonus = 50;
        } else if (timeDifferenceInSeconds <= 80) {
            timeBonus = 30;
        } else if (timeDifferenceInSeconds <= 100) {
            timeBonus = 10;
        }
        coin += timeBonus;

        if (levelBonus2 > 1){
            coin = (int) (coin * levelBonus2);
            Core.getLogger().info("item level bonus occurs (" + (int) ((levelBonus2 - 1) * 100) + "%).");
        }

        return coin;
    }

    public int getRoundScore() {
        return roundScore;
    }

    public float getRoundHitRate() {
        return roundHitRate;
    }

    public long getRoundTime() {
        return roundTime;
    }

    public int getRoundCoin() {
        return roundCoin;
    }

    public void levelBonusIN(){
        levelBonus2 += shipStatus.getCoinIn();
    }
}
