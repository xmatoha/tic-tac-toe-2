(ns tic-tac-toe-2.game-test
  (:require [tic-tac-toe-2.game :as sut]
            [clojure.test :as :t]))

(t/deftest game-initialization-tests
  (t/testing "given board size and player we should get back initial game state")
  (t/is (= {:next-player :x,
            :current-board
            [{:offset 0, :state :e}
             {:offset 1, :state :e}
             {:offset 2, :state :e}
             {:offset 3, :state :e}
             {:offset 4, :state :e}
             {:offset 5, :state :e}
             {:offset 6, :state :e}
             {:offset 7, :state :e}
             {:offset 8, :state :e}]} (sut/new-game :x 3))))

(t/deftest game-loop-tests
  (t/testing "game should finish "
    (t/is (= true (:game-over (last  (sut/game-loop 3))))))
  (t/testing "game should have a winner declared"
    (t/is (some? (:winner (last  (sut/game-loop 3)))))))

