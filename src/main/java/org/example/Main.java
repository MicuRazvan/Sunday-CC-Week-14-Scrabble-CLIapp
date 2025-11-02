package org.example;

import java.util.ArrayList;
import java.util.List;

public class Main {
    static void main() {
        List<Player> players = new ArrayList<>();
        players.add(new Player("Panda"));
        players.add(new Player("Grizzly"));
        players.add(new Player("Brown Bear"));
        players.add(new Player("Polay Bear"));

        Scrabble scrabbleGame = new Scrabble(players);
        scrabbleGame.startGame();
    }
}
