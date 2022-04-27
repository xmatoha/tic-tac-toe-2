(ns tic-tac-toe-2.game-cli-test
  (:require [tic-tac-toe-2.game-cli :refer [main call-new-game call-game-move parse-cli-args]]

            [clojure.test :refer [deftest testing is]]))

(deftest parse-cli-args-tests
  (testing "given new-game command it should call new game creation command"
    (is (= {:action-fn call-new-game
            :options {:board-size "3"},
            :arguments [],
            :summary "  -b, --board-size board-size  board size",
            :errors nil}  (parse-cli-args ["game-clj" "new-game" "-b" "3"] (fn [_])))))
  (testing "given game-move command should properly configure game move"
    (is (= {:action-fn call-game-move
            :options {:game-id "id", :x 1, :y 1},
            :arguments [],
            :summary
            "  -g, --game-id game-id  game identifier\n  -x, --x x              x\n  -y, --y y              y",
            :errors nil}  (parse-cli-args ["game-clj" "move" "-g" "id" "-x" 1 "-y" 1] (fn [_]))))))

(deftest main-tests
  (testing "when command is resolved it should call proper command action function from main method"
    (let [called (atom 0)]
      (with-redefs [call-new-game (fn [_] (swap! called inc))]
        (main "game-clj" "new-game" "-g" "id1")
        (is (= 1 @called))))))


