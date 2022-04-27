(ns tic-tac-toe-2.server-test
  (:require  [clojure.test :refer :all]
             [mockfn.macros :as mfn]
             [tic-tac-toe-2.game :refer [new-game]]
             [tic-tac-toe-2.api :refer [create-new-game-handler game-move-handler]]
             [tic-tac-toe-2.server :refer [server-start server-stop routes app-with-deps]]
             [clj-http.client :as http-client]
             [tic-tac-toe-2.repo :refer [game-id]]
             [cheshire.core :refer [generate-string parse-string]]))

(def port "3000")

(defn server-fixture [f]
  (mfn/providing
   [(game-id) "game-id"]
   (server-start {"PORT" port} (routes (create-new-game-handler (fn [_ _]) game-id) (game-move-handler (fn [_] (new-game 3)))))
   (f)
   (server-stop)))

(use-fixtures :each server-fixture)

(deftest ^:integration test-server-healtcheck
  (testing "given port as env var it should start server at that port and respond to healtcheck endpoint"
    (is (= 200 (:status
                (http-client/get
                 (str "http://localhost:" port "/health")))))))

(deftest ^:integration create-game-test
  (testing "given board size it should create new game board"
    (is (=
         {:game-id "game-id"}
         (parse-string  (:body (http-client/put (str "http://localhost:" port "/game") {:throw-exceptions false :content-type :json :body (generate-string {:board-size 1})})) true)))))

