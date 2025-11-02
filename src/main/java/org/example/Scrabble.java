package org.example;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class Scrabble {
    private List<Player> players;
    private Player currentPlayer;

    private List<Character> lettersInBag;
    Map<Character, Integer> letterPoints;

    private boolean moveHasBeenDoneThisRound;
    private int round;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final String WORDSAPI_KEY;

    Scanner scanner = new Scanner(System.in);

    public Scrabble(List<Player> players) {
        Dotenv dotenv = Dotenv.load();
        WORDSAPI_KEY = Objects.requireNonNullElse(dotenv.get("WORDSAPI_KEY"), "");

        if(players == null || players.size() < 2){
            System.out.println("Not enough players to start the game.");
        }
        else{
            this.players = players;
            createLetterPoints();
            createLettersInBag();
            shareLetters();
        }
    }

    private void createLetterPoints(){
        letterPoints = new HashMap<>();

        letterPoints.put('a', 1);
        letterPoints.put('e', 1);
        letterPoints.put('i', 1);
        letterPoints.put('o', 1);
        letterPoints.put('n', 1);
        letterPoints.put('r', 1);
        letterPoints.put('t', 1);
        letterPoints.put('l', 1);
        letterPoints.put('s', 1);
        letterPoints.put('u', 1);

        letterPoints.put('d', 2);
        letterPoints.put('g', 2);

        letterPoints.put('b', 3);
        letterPoints.put('c', 3);
        letterPoints.put('m', 3);
        letterPoints.put('p', 3);

        letterPoints.put('f', 4);
        letterPoints.put('h', 4);
        letterPoints.put('v', 4);
        letterPoints.put('w', 4);
        letterPoints.put('y', 4);

        letterPoints.put('k', 5);

        letterPoints.put('j', 8);
        letterPoints.put('x', 8);

        letterPoints.put('q', 10);
        letterPoints.put('z', 10);
    }

    private void createLettersInBag(){
        //map for how many times each letter repeats
        Map<Character, Integer> distribution = new HashMap<>();

        distribution.put('a', 9);
        distribution.put('e', 12);
        distribution.put('i', 9);
        distribution.put('o', 8);
        distribution.put('n', 6);
        distribution.put('r', 6);
        distribution.put('t', 6);
        distribution.put('l', 4);
        distribution.put('s', 4);
        distribution.put('u', 4);

        distribution.put('d', 4);
        distribution.put('g', 3);

        distribution.put('b', 2);
        distribution.put('c', 2);
        distribution.put('m', 2);
        distribution.put('p', 2);

        distribution.put('f', 2);
        distribution.put('h', 2);
        distribution.put('v', 2);
        distribution.put('w', 2);
        distribution.put('y', 2);

        distribution.put('k', 1);

        distribution.put('j', 1);
        distribution.put('x', 1);

        distribution.put('q', 1);
        distribution.put('z', 1);

        lettersInBag = new ArrayList<>();

        for (Map.Entry<Character, Integer> entry : distribution.entrySet()) {
            char letter = entry.getKey();
            int count = entry.getValue();
            for (int i = 0; i < count; i++) {
                this.lettersInBag.add(letter);
            }
        }

        Collections.shuffle(this.lettersInBag);
    }

    private void shareLetters(){
        int playerToReceiveLetter = 0;
        while(!lettersInBag.isEmpty()){
            if(playerToReceiveLetter >= players.size()){
                playerToReceiveLetter = 0;
            }

            players.get(playerToReceiveLetter).addLetter(lettersInBag.getLast());
            lettersInBag.removeLast();
            playerToReceiveLetter++;
        }

        for (Player player : players) {
            player.sortLetters();
        }
    }

    public void startGame(){
        System.out.println("Game started with players: ");
        showPlayers();
        String word;
        round = 0;
        do{
            System.out.println("=======================================================================");
            round++;
            moveHasBeenDoneThisRound = false;

            System.out.println("Round " + round);
            for(int i = 0; i < players.size(); i++){
                currentPlayer = players.get(i);
                System.out.println(currentPlayer.getName() + "'s turn." );
                currentPlayer.showLetters();
                System.out.print(">");

                do {
                    word = scanner.next();
                    word = word.toLowerCase();
                    if(word.equals("0")){
                        System.out.println(currentPlayer.getName() + "skipped his turn.");
                        break;
                    }
                }while(!isValidWord(word) || !currentPlayer.hasPlayerAllTheLetters(word));

                if(!word.equals("0")){
                    System.out.println(currentPlayer.getName() + " played: "  + word);
                    currentPlayer.addScore(calculatePoints(word));
                    currentPlayer.removeLetters(word);
                    moveHasBeenDoneThisRound = true;
                }
                System.out.println();
                System.out.println();
            }
        }while(moveHasBeenDoneThisRound);

        showWinner();
    }

    private boolean isValidWord(String word){
        String apiUrl = "https://wordsapiv1.p.rapidapi.com/words/" + word;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("x-rapidapi-host", "wordsapiv1.p.rapidapi.com")
                .header("x-rapidapi-key", WORDSAPI_KEY)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            int statusCode = response.statusCode();
            // WordsAPI returns 200 for a valid word, 404 for not found
            if (statusCode == 200) {
                System.out.println(currentPlayer.getName() + " chose the word " + word + " for " + calculatePoints(word) + " points.");
                return true;
            } else if (statusCode == 404) {
                System.out.println(word + " is not a valid word, try again or enter 0 to skip your turn.");
                System.out.print(">");
                return false;
            } else {
                //other API errors (rate limit, invalid key, server error)
                System.err.println("WordsAPI error for '" + word + "'. Status: " + statusCode + ", Response: " + response.body());
                return false;
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Network or API call interrupted/failed for '" + word + "': " + e.getMessage());
            Thread.currentThread().interrupt();
            return false;
        }
    }

    private int calculatePoints(String word){
        int score = 0;
        for(int i = 0 ; i < word.length(); i++){
            score += letterPoints.getOrDefault(word.charAt(i), 0);
        }

        return score;
    }

    private void showWinner(){
        players.sort(Comparator.comparing(Player::getPoints).reversed());
        System.out.println("Game has ended, scores:");
        for(int i = 0; i < players.size(); i++){
            System.out.println((i + 1) + ". " + players.get(i).getName() + " " + players.get(i).getPoints() + " points.");
        }
    }

    private void showPlayers(){
        for(int i = 0; i < players.size(); i++){
            System.out.println((i + 1) + ". " + players.get(i).getName());
        }
    }
}
