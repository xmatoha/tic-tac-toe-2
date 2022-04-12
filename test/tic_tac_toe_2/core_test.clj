(ns tic-tac-toe-2.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [tic-tac-toe-2.core :refer [asc-diagonale board-to-string col desc-diagonale empty-board game-loop game-round make-move new-game occupy row row-separator row-to-string won?]]))

(deftest board-test
  (testing "describe board"
    (testing "empty board of size 3x3 should contain 9 slots"
      (is (= 9 (->> (empty-board 3)
                    (count)))))
    (testing "all board cells should contain offset property"
      (is (= 9
             (->> (empty-board 3)
                  (filter (fn [e] (:offset e)))
                  (count)))))
    (testing "all board cells on newly created board should be empty"
      (is (= 9
             (->> (empty-board 3)
                  (filter (fn [e] (= :e (:state e))))
                  (count)))))))

(deftest board-operations
  (testing "define board operations"
    (testing "occupy row 0 and column 0 by player X"
      (is (= :x
             (->
              (empty-board 3)
              (occupy 0 0 :x)
              (subvec 0 1)
              (first)
              (:state)))))
    (testing "should return row identified by index"
      (is (= [{:offset 0 :state :x} {:offset 1 :state :e} {:offset 2 :state :e}]
             (->
              (empty-board 3)
              (occupy 0 0 :x)
              (row 0)))))
    (testing "should return column by col index"
      (is (= [{:offset 1 :state :e} {:offset 4 :state :x} {:offset 7 :state :e}]
             (->
              (empty-board 3)
              (occupy 1 1 :x)
              (col 1)))))
    (testing "should return ascending diagnolae"
      (is (= [{:offset 0 :state :e} {:offset 4 :state :x} {:offset 8 :state :e}]
             (->
              (empty-board 3)
              (occupy 1 1 :x)
              (asc-diagonale)))))
    (testing "should return descending diagnolae"
      (is (= [{:offset 2 :state :e} {:offset 4 :state :x} {:offset 6 :state :e}]
             (->
              (empty-board 3)
              (occupy 1 1 :x)
              (desc-diagonale)))))))

(deftest winning-scenarios
  (testing "describe winning scenarios"
    (testing "player X won if all cells horizontally are occupied by player X"
      (is (= true (->
                   (empty-board 3)
                   (occupy 0 0 :x)
                   (occupy 0 1 :x)
                   (occupy 0 2 :x)
                   (won? :x)))))

    (testing "player X won if all vertical cells are occupied by player X"
      (is (= true (->
                   (empty-board 3)
                   (occupy 0 0 :x)
                   (occupy 1 0 :x)
                   (occupy 2 0 :x)
                   (won? :x)))))
    (testing "player X won if asc diagonale is all X"
      (is (= true (->
                   (empty-board 3)
                   (occupy 0 0 :x)
                   (occupy 1 1 :x)
                   (occupy 2 2 :x)
                   (won? :x)))))
    (testing "player X won if desc diagonale is all X"
      (is (= true (->
                   (empty-board 3)
                   (occupy 0 2 :x)
                   (occupy 1 1 :x)
                   (occupy 2 0 :x)
                   (won? :x)))))))

(deftest player-making-move
  (testing "describe how player makes move"
    (testing "player move is picked random from empty cells"
      (is (= 1
             (->
              (filter (fn [e] (= (:state e) :x))
                      (->
                       (empty-board 3)
                       (make-move :x)))
              (count)))))
    (testing "player X should pick last empty cell if other cells are taken"
      (is (= 1
             (->
              (filter (fn [e] (= (:state e) :x))
                      (->
                       (vec (map (fn [e] (assoc e :state :o)) (empty-board 3)))
                       (occupy 0 0 :e)
                       (make-move :x)))
              (count)))))))

(deftest display-board-test
  (testing "describe how board is displayed on screen"
    (testing "row X,empty,O should render as follows 'X| |O' "
      (is (= "X| |O" (row-to-string [{:state :x} {:state :e} {:state :o}]))))
    (testing "row separator should be -+-+-"
      (is (= "-+-+-" (row-separator 3))))
    (testing "should display empty board using row and row separator"
      (is (= " | | \n-+-+-\n | | \n-+-+-\n | | " (board-to-string (empty-board 3)))))))

(deftest game-test
  (testing "describe game"
    (testing "game start with empty board and player with first move"
      (is {:next-player :x :current-board (empty-board 3)}
          (new-game :x))))

  (testing "describe game round"
    (testing "if current player is :x next player should be :o after game-round"
      (is (= :o (:next-player (game-round (new-game :x))))))
    (testing "if it is player :x turn  board should contain player :x move after round"
      (is (= 1 (count (filter (fn [e] (= (:state e) :x)) (:current-board (game-round (new-game :x))))))))
    (testing "game state should indicate  if there is a winner after game round is finished"
      (let [winning-board (->
                           (empty-board 3)
                           (occupy 0 2 :x)
                           (occupy 1 1 :x)
                           (occupy 2 0 :x))

            winning-game-state (assoc (new-game :x) :current-board winning-board)]
        (is (= :x (:winner (game-round winning-game-state))))))

    (testing "if there is a winner game state should indicate game over"
      (let [winning-board (->
                           (empty-board 3)
                           (occupy 0 2 :x)
                           (occupy 1 1 :x)
                           (occupy 2 0 :x))

            winning-game-state (assoc (new-game :x) :current-board winning-board)]
        (is (= true (:game-over (game-round winning-game-state))))))
    (testing "if there are no more free cells game state should indicate game over"
      (let [game-over-board (vec (map (fn [e] (assoc e :state :o)) (empty-board 3)))
            game-over-state  (assoc (new-game :x) :current-board game-over-board)]
        (is (= true (:game-over (game-round game-over-state)))))))
  (testing "game loop"
    (testing "game loop should end with game state game-over"
      (is (= true (:game-over (last (game-loop :x))))))
    (testing "game should have maximum 9 moves 9 + 1 (history)"
      (is (>= 10  (count (game-loop :x)))))))


