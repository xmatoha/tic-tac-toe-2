(ns tic-tac-toe-2.game-test
  (:require [tic-tac-toe-2.game :refer :all]
            [clojure.test :refer :all]))

(deftest game-initialization-tests
  (testing "given board size"
    (let [game (new-game  3)]
      (testing "we should get back initial game state with following properties"
        (testing "game is not over"
          (is (= false (:game-over game))))
        (testing "winner is not defined"
          (is (= nil (:winner game))))
        (testing "first player to take a turn is x"
          (is (= :x (:next-player game))))
        (testing "board is empty"
          (is (= 9 (->> (:current-board game)
                        (filter #(= :e (:state %)))
                        (count)))))))))

(deftest game-rules-tests
  (testing "if current player is x after game-round next player is o"
    (is (= :o (:next-player (game-round (new-game 3) {:offset 1 :player :x}))))))

(deftest game-loop-tests
  (testing "game should finish "
    (is (= true (:game-over (last  (bot-game 3))))))
  (testing "game should have a winner declared"
    (is (some? (:winner (last  (bot-game 3)))))))

(deftest game-state-tests
  (testing "given row winnig board it should evaluate it properly"
    (is (= true (-> (empty-board 3)
                    (occupy 0 0 :x)
                    (occupy 1 0 :x)
                    (occupy 2 0 :x)
                    (won? :x)))))
  (testing "given column winnig board it should evaluate it properly"
    (is (= true (-> (empty-board 3)
                    (occupy 0 0 :x)
                    (occupy 0 1 :x)
                    (occupy 0 2 :x)
                    (won? :x)))))
  (testing "given asc diagnoale winnig board it should evaluate it properly"
    (is (= true (-> (empty-board 3)
                    (occupy 0 0 :x)
                    (occupy 1 1 :x)
                    (occupy 2 2 :x)
                    (won? :x)))))
  (testing "given desc diagnoale winnig board it should evaluate it properly"
    (is (= true (-> (empty-board 3)
                    (occupy 0 2 :x)
                    (occupy 1 1 :x)
                    (occupy 2 0 :x)
                    (won? :x)))))
  (testing "given not winning board won should be false"
    (is (= false (-> (won? (empty-board 3) :x))))))

(deftest board-operations-tests
  (testing "given coordinates it should return cell on board"
    (is (= {:state :x :offset 4} (-> (empty-board 3)
                                     (occupy 1 1 :x)
                                     (board-elem-at 1 1))))))

(deftest player-move-tests
  (testing "given board player when player random move there is one less free cells on board"
    (is (= 8  (->> (:current-board (make-random-move (new-game 3)))
                   (filter #(= (:state %) :e))
                   (count))))))

(deftest eval-game-round-tests
  (testing "after game round finished evaluate if game ended"
    (testing "if there is winner defined, game ends"
      (is (= true
             (game-over? (-> (new-game 3)
                             (occupy-game 0 0 :x)
                             (occupy-game 0 1 :x)
                             (game-round {:offset 2 :player :x}))))))

    (testing "if there is not free cell on board and there is no winner defined, game ends with draw"
      (is (= true
             (game-over? (-> (new-game 3)
                             (occupy-game 0 0 :x)
                             (occupy-game 0 1 :o)
                             (occupy-game 0 2 :x)
                             (occupy-game 1 0 :x)
                             (occupy-game 1 1 :o)
                             (occupy-game 1 2 :x)
                             (occupy-game 2 0 :o)
                             (occupy-game 2 1 :o)
                             (game-round {:offset 8 :player :x}))))))))
