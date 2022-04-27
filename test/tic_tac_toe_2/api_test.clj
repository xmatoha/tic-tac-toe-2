(ns tic-tac-toe-2.api-test
  (:require [clojure.test :refer [deftest testing is]]
            [tic-tac-toe-2.game :refer [new-game]]
            [tic-tac-toe-2.api :refer [create-new-game-handler game-move-handler]]))

(deftest create-new-game-hadnler-tests
  (testing "given board-size it should create new game persist it and return game id"
    (is (=
         "game-id"
         (->
          ((create-new-game-handler
            (fn [_ _])
            (fn [] "game-id"))
           {:parameters {:body {:board-size 3}}})
          :body
          :game-id)))))

(deftest game-round-tests
  (testing "given existing game-id and valid game move we should progress game"
    (is (= {:next-player :o,
            :current-board
            [{:offset 0, :state :e}
             {:offset 1, :state :x}
             {:offset 2, :state :e}
             {:offset 3, :state :e}
             {:offset 4, :state :e}
             {:offset 5, :state :e}
             {:offset 6, :state :e}
             {:offset 7, :state :e}
             {:offset 8, :state :e}],
            :winner nil,
            :game-over false,
            :error nil} (:body ((game-move-handler (fn [_] (new-game 3))) {:parameters {:body  {:player :x :row 0 :col 1}}}))))))
