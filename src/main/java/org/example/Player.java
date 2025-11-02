package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Player {
    private List<Character> letters;
    private String name;
    private int points;

    public Player(String name) {
        this.name = name;
        this.points = 0;
        letters = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public int getPoints() {
        return points;
    }

    public void addLetter(Character letter){
        letters.add(letter);
    }

    public void addScore(int pointsToAdd ){
        this.points += pointsToAdd;
    }

    public void showLetters(){
        System.out.print("Your letters: ");
        for(int i = 0; i < letters.size(); i++){
            System.out.print(letters.get(i) + " ");
        }
        System.out.println();
    }

    public void removeLetters(String word){
        for(int i = 0; i < word.length(); i++){
            letters.remove((Character)word.charAt(i));
        }
    }

    public boolean hasPlayerAllTheLetters(String word){
        List<Character> lettersCopy = new ArrayList<>(letters);
        for(int i = 0; i < word.length(); i++){
            if(lettersCopy.contains(word.charAt(i))) {
                lettersCopy.remove((Character)word.charAt(i));
            }
            else{
                return false;
            }
        }

        return true;
    }

    public void sortLetters() {
        Collections.sort(this.letters);
    }
}
