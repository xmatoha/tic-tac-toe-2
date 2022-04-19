Feature: Game rules

    Scenario: Game initiation

        Given Player X willing to play
         When Player X creates new game
         Then Player X sees empty board
         And Player X is first player to take turn
         And Game is not over
         And Winner is not defined


    Scenario: Player change turns after each round
        Given X is player who suppose to make a move
        When Player X makes move
        Then Next player who suppose to make a move is player O


    Scenario: Vertical win
      Given Player X owns 2 cells in top row
      When Player X places 3rd cell in top row
      Then Player X is winer of the game

    Scenario: Horizontal win
      Given Player X owns 2 cells in first column
      When Player X places 3rd cell in first column
      Then Player X is winer of the game


      Scenario: Main diagonale win
        Given Player X owns two cells in main diagonale
        When Player X places 3rd cell in main diagonale
        Then Player X is winer of the game

      Scenario: Anti diagonale win
        Given Player X owns two cells in anti diagonale
        When Player X places 3rd cell in anti diagonale
        Then Player X is winer of the game

      Scenario: Game ending with user X win
        Given There is free cell on game board
        When When user X makes a move
        And User X wins game
        Then Game ends

      Scenario: Game ending with draw
        Given There only one free cell
        When When user X makes a move
        And User X does not win
        Then Game ends with draw
        

