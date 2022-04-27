(ns tic-tac-toe-2.game-cli-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [tic-tac-toe-2.game :refer [new-game game-round]]
   [cheshire.core :as json]
   [tic-tac-toe-2.game-cli
    :refer
    [-main
     call-game-move
     call-new-game
     game-round-to-string
     new-game-help
     new-game-to-string
     game-move-api-call
     new-game-api-call
     parse-cli-args]]))

(deftest parse-cli-args-tests
  (testing "given new-game command it should call new game creation command"
    (is (=
         {:options {:board-size "3"}
          :arguments []
          :summary
          "  -h, --help                   help\n  -b, --board-size board-size  board size"
          :errors nil
          :action-fn call-new-game
          :api-call-fn new-game-api-call
          :out-fn print
          :help-fn new-game-help}
         (parse-cli-args ["game-clj" "new-game" "-b" "3"] (fn [_])))))
  (testing "given game-move command should properly configure game move"
    (is (=
         {:options {:help true, :player "x", :game-id "id", :row 1, :col 1},
          :arguments [],
          :summary
          "  -h, --help             help\n  -g, --game-id game-id  game identifier\n  -p, --player player    player identifier\n  -r, --row row          board row\n  -c, --col col          board column",
          :errors nil,
          :action-fn call-game-move,
          :api-call-fn game-move-api-call
          :out-fn print
          :help-fn tic-tac-toe-2.game-cli/game-move-help}

         (parse-cli-args ["game-clj" "game-move" "-h" "-p" "x" "-g" "id" "--row" 1 "--col" 1] (fn [_]))))))

(deftest main-tests
  (testing "when command is resolved it should call proper command action function from main method"
    (let [called (atom 0)]
      (with-redefs [call-new-game (fn [_] (swap! called inc))]
        (-main "game-clj" "new-game" "-g" "id1")
        (is (= 1 @called)))))
  (testing "when help switch is provided call help fn instead action fn"
    (let [called (atom 0)]
      (with-redefs [new-game-help (fn [_] (swap! called inc))]
        (-main "game-clj" "new-game" "-h" "id1")
        (is (= 1 @called))))))

(deftest game-rendering-tests
  (testing "given game state it should properly render it to screen"
    (is (=
         "Game board: \n\nE|E|E\n-+-+-\nE|E|E\n-+-+-\nE|E|E\n\nNext player: :x\n\n"
         (game-round-to-string (new-game 3)))))
  (testing "given game state is won"
    (is (=
         "Game board: \n\nE|E|E\n-+-+-\nE|E|E\n-+-+-\nE|E|E\n\nNext player: :x\nGame over!\nGame won o\n\n"
         (game-round-to-string
          (->
           (new-game 3)
           (assoc :game-over true)
           (assoc :winner "o"))))))
  (testing "it should properly render game in draw state"
    (is (=
         "Game board: \n\nE|E|E\n-+-+-\nE|E|E\n-+-+-\nE|E|E\n\nNext player: :x\nGame over!\nGame ended with draw\n"
         (game-round-to-string
          (->
           (new-game 3)
           (assoc :game-over true)
           (assoc :winner nil)
           (assoc :board-full true)))))))

(deftest new-game-rendering
  (is (=
       "Game board: \n\nE|E|E\n-+-+-\nE|E|E\n-+-+-\nE|E|E\n\nNext player: :x\n\n\nGame ID: id"
       (new-game-to-string {:game-id "id" :game (new-game 3)}))))

(deftest api-interactions-tests
  (testing "it should call new-game endpoint and return string representation of new board "
    (is (=
         "Game board: \n\n | | \n-+-+-\n | | \n-+-+-\n | | \n\nNext player: x\n\n\nGame ID: id"
         (call-new-game {:options {:board-size 3} :api-call-fn (fn [config] {:body (json/generate-string {:game-id "id" :game (new-game 3)})})}))))
  (testing "it should parse new-geam api call response"
    (is (=
         "Game board: \n\n | | \n-+-+-\n |X| \n-+-+-\n | | \n\nNext player: o\n\n"
         (call-game-move
          {:options {:game-id "id" :player "x" :row 1 :col 1} :api-call-fn (fn [config] {:body (json/generate-string (-> (new-game 3) (game-round {:player :x :offset 4})))})})))))

