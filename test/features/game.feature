Feature: Game rules
    Scenario: Game initiation
        Given Board size 3
         When User creates new game
         Then User is assigned player name X
          And New board of size 3 is created
          And Board is empty

    Scenario: Player change turns after each round
        Given Current round user is X
        When User makes move
        Then Current round player changes to O

    Scenario: Player occupies free cell after move
        Given Some valid game state
        And There is at least one empty cell
        When User X makes a move to that cell
        Then Cell should be occupied by user X

    # Scenario: 
    #     Given Some valid game state
    #     When User makes a move to occupied cell
    #     Then Game status should indicate invalid move
