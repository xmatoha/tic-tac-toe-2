(ns steps-definitions.game-steps
  (:require [lambdaisland.cucumber.dsl :refer :all]
            [clojure.test :refer :all]
            [tic-tac-toe-2.game :refer :all]))

(Given "Two users willing to play" [state]
       state)

(When "User X creates new game" [state]
      (assoc state :game-state (new-game 3)))

(Then "User X and O see empty board" [state]
      (is (= 9 (count (->> (get-in state [:game-state :current-board])
                           (filter #(= :e (:state %))))))) state)
(And "User X is first player to make a move" [state]
     (is (= :x (get-in state [:game-state  :next-player]))) state)
