(ns tic-tac-toe-2.game-test
  (:require [tic-tac-toe-2.game :refer :all]
            [clojure.test :refer :all]))

(deftest game-initialization-tests
  (testing "given board size and player we should get back initial game state")
  (is (= {:next-player :x,
          :current-board
          [{:offset 0, :state :e}
           {:offset 1, :state :e}
           {:offset 2, :state :e}
           {:offset 3, :state :e}
           {:offset 4, :state :e}
           {:offset 5, :state :e}
           {:offset 6, :state :e}
           {:offset 7, :state :e}
           {:offset 8, :state :e}]} (new-game  3))))

(deftest game-loop-tests
  (testing "game should finish "
    (is (= true (:game-over (last  (game-loop 3))))))
  (testing "game should have a winner declared"
    (is (some? (:winner (last  (game-loop 3)))))))

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
