package com.raingoddess.lapclient.main;

/**
 * Created by Black Lotus on 2/14/2017.
 */

import java.util.ArrayList;
import java.util.List;

/**
 * will contain 10 Match objects for all 10 players in a single game.
 */
public class Game {
    private List<Match> playersInGame;
    private double totalDamageTeamVictory, totalDamageTeamDefeat, totalGoldTeamVictory, totalGoldTeamDefeat;

    public Game(String match1, String match2, String match3, String match4, String match5, String match6, String match7, String match8, String match9, String match10){
        playersInGame = new ArrayList<>(10);
        Match temp_match = new Match(match1);
        playersInGame.add(temp_match);

        temp_match = new Match(match2, true);
        playersInGame.add(temp_match);

        temp_match = new Match(match3, true);
        playersInGame.add(temp_match);

        temp_match = new Match(match4, true);
        playersInGame.add(temp_match);

        temp_match = new Match(match5, true);
        playersInGame.add(temp_match);

        temp_match = new Match(match6, true);
        playersInGame.add(temp_match);

        temp_match = new Match(match7, true);
        playersInGame.add(temp_match);

        temp_match = new Match(match8, true);
        playersInGame.add(temp_match);

        temp_match = new Match(match9, true);
        playersInGame.add(temp_match);

        temp_match = new Match(match10, true);
        playersInGame.add(temp_match);

        setAggregatedStats();
    }

    private void setAggregatedStats(){
        setTeamDamageTotals();
        setTeamGoldTotals();
    }

    private void setTeamDamageTotals(){
        if(playersInGame.get(0).getStat("winner").contains("true")){
            totalDamageTeamVictory = playersInGame.get(0).getStatAsDouble("totalTeamDmg");
            totalDamageTeamDefeat = playersInGame.get(0).getStatAsDouble("totalEnemyDmg");
        } else{
            totalDamageTeamVictory = playersInGame.get(0).getStatAsDouble("totalEnemyDmg");
            totalDamageTeamDefeat =  playersInGame.get(0).getStatAsDouble("totalTeamDmg");
        }
    }

    private void setTeamGoldTotals(){
        totalGoldTeamVictory = aggregateStatsForVictory("goldEarned");
        totalGoldTeamDefeat = aggregateStatsForDefeat("goldEarned");
    }

    private double aggregateStatsForVictory(String statKey){
        double total = 0.0;
        for(int i = 0; i<playersInGame.size(); i++){
            if(playersInGame.get(i).isWinner())
                total+=playersInGame.get(i).getStatAsDouble(statKey);
        } return total;
    }

    private double aggregateStatsForDefeat(String statKey){
        double total = 0.0;
        for(int i = 0; i<playersInGame.size(); i++){
            if(!playersInGame.get(i).isWinner())
                total+=playersInGame.get(i).getStatAsDouble(statKey);
        } return total;
    }

    int findPlayerIndex(String name){
        for(int i = 0; i<playersInGame.size(); i++){
            String name2 = playersInGame.get(i).getStat("playerName");
            if(name2!=null && name2.trim().equals(name))
                return i;
        } return 0;
    }

    double getTeamTotal(boolean isWinner, String statKey){
        if(statKey.equals("dmgToChamp"))
            return getTotalDamage(isWinner);
        else if(statKey.equals("goldEarned"))
            return getTotalGold(isWinner);
        else return 0.0;
    }

    double getTotalDamage(boolean isWinner){
        if(isWinner)
            return totalDamageTeamVictory;
        else return totalDamageTeamDefeat;
    }

    double getTotalGold(boolean isWinner){
        if(isWinner)
            return totalGoldTeamVictory;
        else return totalGoldTeamDefeat;
    }

    double getGameLengthInMinutes(){
        int min = Integer.parseInt(playersInGame.get(0).getStat("matchLength"))/60;
        int sec = Integer.parseInt(playersInGame.get(0).getStat("matchLength"))%60;
        return ((double) min + (double) sec / 60.0);
    }

    public double getTotalDamageTeamVictory(){ return totalDamageTeamVictory; }

    public double getTotalDamageTeamDefeat(){ return totalDamageTeamDefeat; }

    public Match get(int index){ return playersInGame.get(index);}

    public List<Match> getMatches(){return playersInGame;}
}
