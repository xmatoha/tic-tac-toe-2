(ns tic-tac-toe-2.repo-test
  (:require  [clojure.test :refer [deftest testing is use-fixtures]]
             [tic-tac-toe-2.repo :refer [game-store persist-game game-by-id game-id]]
             [tic-tac-toe-2.game :refer [new-game]]))

(defn reset-store-fixture [f]
  (reset! game-store {})
  (f))

(use-fixtures :each reset-store-fixture)

(deftest ^:integration game-state-tests
  (testing "given game state it should persist it"
    (let [game-state (new-game 3)
          game-id (game-id)]
      (is (= (persist-game game-id game-state) @game-store))))
  (testing "retrieve game state by id"
    (let [game-state (new-game 3)
          game-id (game-id)
          game (persist-game game-id game-state)]
      (is (= game-state (game-by-id game-id))))))

