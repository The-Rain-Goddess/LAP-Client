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
    }

    public Match get(int index){ return playersInGame.get(index);}

    public List<Match> getMatches(){return playersInGame;}
}
