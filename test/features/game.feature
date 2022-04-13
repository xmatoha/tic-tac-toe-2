Feature: Game rules
    Scenario: Game initiation
        Given board size 3
        When user creates new game
        Then user is assigned player name X
        And new board of size 3 is created
        And board is empty

    Scenario: Game rules - player turns
        Given Current round user is x
        When When user makes move
        Then Current round player changes to o
