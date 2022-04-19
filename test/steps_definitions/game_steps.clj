(ns steps-definitions.game-steps
  (:require [lambdaisland.cucumber.dsl :refer :all]
            [clojure.test :refer :all]
            [tic-tac-toe-2.game :refer :all]))

(Given "Player X willing to play" [state]
       state)

(When "Player X creates new game" [state]
      (assoc state :game-state (new-game 3)))

(Then "Player X sees empty board" [state]
      (is (= 9 (count (->> (get-in state [:game-state :current-board])
                           (filter #(= :e (:state %)))))))
      state)

(And "Player X is first player to take turn" [state]
     (is (= :x (get-in state [:game-state :next-player])))
     state)

(And "Game is not over" [state]
     (is (= false (get-in state [:game-state :game-over])))
     state)

(And "Winner is not defined" [state]
     (is (= nil (get-in state [:game-state :winner])))
     state)

(Given "X is player who suppose to make a move" [state]
       (assoc state :game-state (new-game 3)))

(When "Player X makes move" [state]
      (assoc state
             :game-state
             (game-round (:game-state state) {:offset 0 :player :x})))

(Then "Next player who suppose to make a move is player O" [state]
      (is (= :o  (:next-player (:game-state state)))) state)

(Given "Player X owns {int} cells in top row" [state int1]
       (assoc state
              :game-state
              (-> (new-game 3)
                  (occupy-game 0 0 :x)
                  (occupy-game 0 1 :x))))

(When "Player X places {int}rd cell in top row" [state int1]
      (assoc state :game-state
             (game-round (:game-state state) {:offset 2 :player :x})))

(Then "Player X is winer of the game" [state]
      (is (= true (won? (get-in state [:game-state :current-board]) :x)))
      state)

(Given "Player X owns {int} cells in first column" [state int1]
       (assoc state
              :game-state
              (-> (new-game 3)
                  (occupy-game 0 0 :x)
                  (occupy-game 1 0 :x))))

(When "Player X places {int}rd cell in first column" [state int1]
      (assoc state :game-state
             (game-round (:game-state state) {:offset 6 :player :x})))

(Given "Player X owns two cells in anti diagonale" [state]
       (assoc state
              :game-state
              (-> (new-game 3)
                  (occupy-game 0 2 :x)
                  (occupy-game 1 1 :x))))

(When "Player X places {int}rd cell in anti diagonale" [state int1]
      (assoc state :game-state
             (game-round (:game-state state) {:offset 6 :player :x})))

(Given "Player X owns two cells in main diagonale" [state]
       (assoc state
              :game-state
              (-> (new-game 3)
                  (occupy-game 0 0 :x)
                  (occupy-game 1 1 :x))))

(When "Player X places {int}rd cell in main diagonale" [state int1]
      (assoc state :game-state
             (game-round (:game-state state) {:offset 8 :player :x})))

(Given "There is free cell on game board" [state]
       (let [state (assoc state
                          :game-state
                          (-> (new-game 3)
                              (occupy-game 0 0 :x)
                              (occupy-game 0 1 :x)))]

         (is (= false (board-full? (:game-state state))))
         state))

(When "When user X makes a move" [state]
      (game-round (:game-state state) {:offset 2 :player :x}))

(And "User X wins game" [state]
     (is (= true (won? (:game-state state) :x)))
     state)

(Then "Game ends" [state]
      (game-over? (:game-state state)) state)

(Given "There only one free cell" [state]
       (assoc state :game-state (-> (new-game 3)
                                    (occupy-game 0 0 :x)
                                    (occupy-game 0 1 :x)
                                    (occupy-game 0 2 :x)
                                    (occupy-game 1 0 :x)
                                    (occupy-game 1 1 :x)
                                    (occupy-game 1 2 :x)
                                    (occupy-game 2 0 :x)
                                    (occupy-game 2 1 :x))))

(And "User X does not win" [state]
     (occupy-game (:game-state state) 1 2 :o))

(Then "Game ends with draw" [state]
      draw
      (pending!))
