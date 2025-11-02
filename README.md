# Week 14 Project — Scrabble CLI app

## Context
I lost a bet with a friend and he challenged me that for the next 52 weeks, during weekends I need to create from scratch a new project.

For this week we decided on a simplified Scrabble game in console and I'm allowed to work on it only Sunday.

## The rules are the following:
*   Each Friday night, me and him will talk about what project I need to do.
*   Mostly he will decide for me, but I’m allowed to suggest and do my own ideas if he agrees on them.
*   Once the project is decided, he will tell me if I’m allowed to work Saturday and Sunday, or only Sunday.
(Surely this won’t backfire at some point by underestimating a project, right? 😅)

## About the project
The game can be played with atleast 2 players(object of class Player) and uses WordsAPI to check if a word is valid.

## How to use it

### 1.  Get Your an API Key and create the .env file
1.   Go to https://www.wordsapi.com/ to create an account and generate your key.
2.   Create an .env file in the root directory of the project
3.   Add to the .env file the line: ```WORDSAPI_KEY=your_hey```, replacing "your_key" with your key

### 2. Compile and run it
1.   Take turns and create words using your letters, for every turn a message with the player's name and it's available letters will be displayed.
2.   If you can't think of a word enter 0 which will skip your turn.
3.   Game ends after n consecutive rounds with no new word found(n being the number of the players).
