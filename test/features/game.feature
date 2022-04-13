Feature: Game rules
    Scenario: Game initiation
        Given Two users willing to play
         When User X creates new game
         Then User X and O see empty board
         And User X is first player to make a move


    # Scenario: Player change turns after each round
    #     Given Current X is user who suppose to make a move
    #     When User X makes move
    #     Then Next player who suppose to make a muve is user O

