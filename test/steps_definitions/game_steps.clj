(ns steps-definitions.game-steps
  (:require [lambdaisland.cucumber.dsl :refer :all]
            [clojure.test :refer :all]
            [tic-tac-toe-2.game :as sut]))

(Given "board size {int}" [state int1]
       (assoc state :board-size int1))

(When "user creates new game" [state]
      (assoc state :game-state (sut/new-game 3)))

(Then "user is assigned player name X" [state]
      (is (= :x (get-in state [:game-state :next-player])))
      state)

(And "new board of size 3 is created" [state int1]
     (is (= 9 (count (get-in state [:game-state :current-board]))))
     state)

(And "board is empty" [state]
     (is (= 9 (count (filter (fn [e] (= (:state e) :e)) (get-in state [:game-state :current-board]))))) state)



