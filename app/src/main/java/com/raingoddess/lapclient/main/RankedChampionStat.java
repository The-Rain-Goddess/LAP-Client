package com.raingoddess.lapclient.main;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Black Lotus on 8/25/2016.
 */
public class RankedChampionStat {
    private List<String> stats;
    private int statSize;

//public constructor
    public RankedChampionStat(String input) {
        stats = Arrays.asList(input.split("/"));
        statSize = stats.size();
    }

//getters
    public String getStat(String key){
        for(int i = 0; i<stats.size(); i++){
            if(stats.get(i).contains(key))
                return stats.get(i).replace(key + ":", "");
        } return null;
    }

    public List<String> getStats(){ return stats; }

    public int getStatSize(){ return statSize; }

    public String getStatAtIndex(int in){ return stats.get(in); }

    @Override
    public String toString(){
        String reString = "";
        for(int i = 0; i < stats.size(); i++){
            reString = reString + " " + stats.get(i);
        } return reString;
    }
}
