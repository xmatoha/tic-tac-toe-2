Feature: Game rules
    Scenario: Game initiation
        Given board size 3
        When user creates new game
        Then user is assigned player name X
        And new board of size 3 is created
        And board is empty


