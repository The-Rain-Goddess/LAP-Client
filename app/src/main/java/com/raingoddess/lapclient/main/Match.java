package com.raingoddess.lapclient.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Double.parseDouble;

/**
 * Created by Black Lotus on 7/21/2016.
 */
public class Match {
    public String Assists = "";
    public String ChampLevel = "";
    public String CombatPlayerScore = "";
    public String Deaths = "";
    public String DoubleKills = "";
    public String GoldEarned = "";
    public String InhibKills = "";
    public String Item0 = "";
    public String Item1 = "";
    public String Item2 = "";
    public String Item3 = "";
    public String Item4 = "";
    public String Item5 = "";
    public String Item6 = "";
    public String Item7 = "";
    public String KillingSprees = "";
    public String Kills = "";
    public String LargestCrit = "";
    public String LargestKillSpree = "";
    public String LargestMultiKill = "";
    public String MagicDmgDealt = "";
    public String MagicDmgDealtToChamps = "";
    public String MagicDmgTaken = "";
    public String MinionsKilled = "";
    public String NeutralMinionsKilled = "";
    public String NeutralMinionsKilledEnemyJngl = "";
    public String NeutralMinionsKilledTeamJngl = "";
    public String NodeCaptures = "";
    public String NodeCaptureAssists = "";
    public String NodeNeutralizes = "";
    public String NodeNeutralizeAssists = "";
    public String ObjectivePlayerScore = "";
    public String PentaKills = "";
    public String PhysicalDmgDealt = "";
    public String PhysicalDmgDealtToChamps = "";
    public String PhysicalDmgTaken = "";
    public String QuadraKills = "";
    public String SightWardBought = "";
    public String TeamObjectives = "";
    public String TotalDmgDealt = "";
    public String TotalDmgDealtToChamps = "";
    public String TotalDmgTaken = "";
    public String TotalHeals = "";
    public String TotalPlayerScore = "";
    public String TotalScoreRank = "";
    public String TotalTimeCrowdControlDealt = "";
    public String TotalUnitsHealed = "";
    public String TowerKills = "";
    public String TripleKills = "";
    public String TrueDmgDealt = "";
    public String TrueDmgDealtToChamps = "";
    public String UnrealKills = "";
    public String VisionWardsBought = "";
    public String WardsKilled = "";
    public String WardsPlaced = "";
    public String IsFirstBloodAssist = "";
    public String IsFirstBloodKill = "";
    public String IsFirstInhibAssist = "";
    public String IsFirstInhibKill = "";
    public String IsFirstTowerAssist = "";
    public String ISFirstTowerKill = "";
    public String IsWinner = "";
    public String Cs = "";
    public String Champion = "";
    public String SSpell1 = "", SSpell2 = "";
    public String MatchLength = "";
    public String TotalTeamDmg = "";
    public String TotalEnemyDmg = "";
    public String MatchId, MatchMode, MatchType, MatchStartTime, QueueType;

    private List<String> statLine = new ArrayList<>(70);

    private boolean isClient = false;

    public Match(String inputData){
        isClient = true;
        //System.out.println(inputData);
        String[] input = inputData.split("/");
        Assists = input[0];                     statLine.add(Assists);
        ChampLevel = input[1];                  statLine.add(ChampLevel);
        Cs = input[2];                          statLine.add(Cs);
        Deaths = input[3];                      statLine.add(Deaths);
        TotalDmgDealtToChamps = input[4];       statLine.add(TotalDmgDealtToChamps);
        TotalDmgTaken = input[5];               statLine.add(TotalDmgTaken);
        DoubleKills = input[6];                 statLine.add(DoubleKills);
        IsFirstBloodAssist = input[7];          statLine.add(IsFirstBloodAssist);
        IsFirstBloodKill = input[8];            statLine.add(IsFirstBloodKill);
        GoldEarned = input[9];                  statLine.add(GoldEarned);
        InhibKills = input[19];
        Item0 = input[10];                      statLine.add(Item0);
        Item1 = input[11];                      statLine.add(Item1);
        Item2 = input[12];                      statLine.add(Item2);
        Item3 = input[13];                      statLine.add(Item3);
        Item4 = input[14];                      statLine.add(Item4);
        Item5 = input[15];                      statLine.add(Item5);
        Item6 = input[16];                      statLine.add(Item6);
        Kills = input[17];                      statLine.add(Kills);
        KillingSprees = input[18];              statLine.add(KillingSprees);statLine.add(InhibKills);
        LargestCrit = input[20];                statLine.add(LargestCrit);
        MagicDmgDealt = input[21];              statLine.add(MagicDmgDealt);
        MagicDmgDealtToChamps = input[22];      statLine.add(MagicDmgDealtToChamps);
        MagicDmgTaken = input[23];              statLine.add(MagicDmgTaken );
        MinionsKilled = input[24];              statLine.add(MinionsKilled);
        NeutralMinionsKilled = input[25];       statLine.add(NeutralMinionsKilled);
        NeutralMinionsKilledEnemyJngl = input[26];  statLine.add(NeutralMinionsKilledEnemyJngl);
        NeutralMinionsKilledTeamJngl = input[27];   statLine.add(NeutralMinionsKilledTeamJngl);
        NodeCaptures = input[28];               statLine.add(NodeCaptures);
        NodeCaptureAssists = input[29];         statLine.add(NodeCaptureAssists);
        NodeNeutralizes = input[30];            statLine.add(NodeNeutralizes);
        NodeNeutralizeAssists = input[31];      statLine.add(NodeNeutralizeAssists);
        ObjectivePlayerScore = input[32];       statLine.add(ObjectivePlayerScore);
        PentaKills = input[33];                 statLine.add(PentaKills);
        PhysicalDmgDealt = input[34];           statLine.add(PhysicalDmgDealt);
        PhysicalDmgDealtToChamps = input[35];   statLine.add(PhysicalDmgDealtToChamps);
        PhysicalDmgTaken = input[36];           statLine.add(PhysicalDmgTaken);
        QuadraKills = input[37];                statLine.add(QuadraKills);
        SightWardBought = input[38];            statLine.add(SightWardBought);
        TeamObjectives = input[39];             statLine.add(TeamObjectives);
        TotalDmgDealt = input[40];              statLine.add(TotalDmgDealt);
        TotalDmgDealtToChamps = input[41];      statLine.add(TotalDmgDealtToChamps);
        TotalDmgTaken = input[42];              statLine.add(TotalDmgTaken);
        TotalHeals = input[43];                 statLine.add(TotalHeals);
        TotalPlayerScore = input[44];           statLine.add(TotalPlayerScore);
        TotalScoreRank = input[45];             statLine.add(TotalPlayerScore);
        TotalTimeCrowdControlDealt = input[46]; statLine.add(TotalTimeCrowdControlDealt);
        TotalUnitsHealed = input[47];           statLine.add(TotalUnitsHealed);
        TowerKills = input[48];                 statLine.add(TowerKills);
        TripleKills = input[49];                statLine.add(TripleKills);
        TrueDmgDealt = input[50];               statLine.add(TrueDmgDealt);
        TrueDmgDealtToChamps = input[51];       statLine.add(TrueDmgDealtToChamps);
        UnrealKills = input[52];                statLine.add(UnrealKills);
        VisionWardsBought = input[53];          statLine.add(VisionWardsBought);
        WardsKilled = input[54];                statLine.add(WardsKilled);
        WardsPlaced = input[55];                statLine.add(WardsPlaced);
        IsWinner = input[57];                   statLine.add(IsWinner);
        Champion = input[58].replace("|", "").replace("'", "").replace(" ", "").toLowerCase();
        statLine.add(Champion);
        SSpell1 = input[59];                    statLine.add(SSpell1);
        SSpell2 = input[60];                    statLine.add(SSpell2);
        MatchLength = input[61];                statLine.add(MatchLength);
        MatchId = input[62];                    statLine.add(MatchId);
        MatchMode = input[63];                  statLine.add(MatchMode);
        MatchType = input[64];                  statLine.add(MatchType);
        MatchStartTime = input[65];             statLine.add(MatchStartTime);
        QueueType = input[66];                  statLine.add(QueueType);
        TotalTeamDmg = input[62];               statLine.add(TotalTeamDmg);
        TotalEnemyDmg = input[63];              statLine.add(TotalEnemyDmg);

        statLine = null;
        statLine = Arrays.asList(inputData.split("/"));
    }

    public Match(String in, boolean isNotMainPlayer){
        statLine = Arrays.asList(in.split("/"));
        isClient =  !isNotMainPlayer;
    }

    boolean isClient(){ return isClient; }

    int getStatAsInteger(String s){ return Integer.parseInt(getStat(s));}

    double getStatAsDouble(String s) {return Double.parseDouble(getStat(s));}

    boolean isWinner(){ return getStat("winner").contains("true"); }

    String getChampion(){ return getStat("champion").toLowerCase().replace(" ", "").replace("'", "").replace(".", ""); }

    String getStat(String s){
        for(String t : statLine){
            if(t.contains(s))
                return t.replace(s + ":", "");
        } return null;
    }

    String getStat(int i){
        return statLine.get(i);
    }
}
