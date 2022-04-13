(ns steps-definitions.game-steps
  (:require [lambdaisland.cucumber.dsl :refer :all]
            [clojure.test :refer :all]
            [tic-tac-toe-2.game :as sut]))

(defn parse-coords [string]
  (map (fn [e] (- (Integer/parseInt e) 1)) (clojure.string/split string #",")))

(Given "Board size {int}" [state int1]
       (assoc state :board-size int1))

(When "User creates new game" [state]
      (assoc state :game-state (sut/new-game 3)))

(Then "User is assigned player name X" [state]
      (is (= :x (get-in state [:game-state :next-player])))
      state)

(And "New board of size 3 is created" [state int1]
     (is (= 9 (count (get-in state [:game-state :current-board]))))
     state)

(And "Board is empty" [state]
     (is (= 9 (count (filter (fn [e] (= (:state e) :e)) (get-in state [:game-state :current-board]))))) state)

(Given "Current round user is X" [state]
       (assoc state :game-state (sut/new-game 3)))

(When "User makes move" [state]
      (assoc state :game-state (sut/game-round (get-in state [:game-state]))))

(Then "Current round player changes to O" [state]
      (is (= :o (get-in state [:game-state :next-player])))
      state)

(Given "Some valid game state" [state]
       (assoc state :game-state (sut/new-game 3)))
(Integer/parseInt "1")

(And "There is at least one empty cell" [state]
     (is (< 0 (count (filter (fn [e] (= :e (:state e))) (get-in state [:game-state :current-board]))))) state)

(When "User X makes a move to that cell" [state]
      (assoc-in
       state
       [:game-state :current-board]
       (apply sut/occupy (get-in state [:game-state :current-board])
              (concat
               [1 1]
               [:x]))))

(Then "Cell should be occupied by user X" [state]
      (let [coords [1 1]]
        (is (= :x (:state
                   (sut/board-elem-at
                    (get-in state [:game-state :current-board])
                    (first coords)
                    (second coords)))))) state)

