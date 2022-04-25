(ns tic-tac-toe-2.server-test
  (:require  [clojure.test :refer :all]
             [tic-tac-toe-2.server :refer [server-start server-stop]]
             [clj-http.client :as http-client]
             [cheshire.core :refer [generate-string parse-string]]))

(def port "3000")

(defn server-fixture [f]
  (server-start {"PORT" port})
  (f)
  (server-stop))

(use-fixtures :once server-fixture)

(deftest ^:integration test-server-healtcheck
  (testing "given port as env var it should start server at that port and respond to healtcheck endpoint"
    (is (= 200 (:status
                (http-client/get
                 (str "http://localhost:" port "/health")))))))

(deftest ^:integration create-game-test
  (testing "given board size it should create new game board"
    (is (=
         {"spec"
          "(spec-tools.core/spec {:spec (clojure.spec.alpha/keys :req-un [:spec$22177/board-size]), :type :map, :leaf? false})",
          "problems"
          [{"path" ["board-size"],
            "pred" "clojure.core/pos-int?",
            "val" -1,
            "via" ["spec$22177/board-size"],
            "in" ["board-size"]}],
          "type" "reitit.coercion/request-coercion",
          "coercion" "spec",
          "value" {"board-size" -1},
          "in" ["request" "body-params"]}

         (get-in (parse-string  (:body (http-client/put (str "http://localhost:" port "/game") {:throw-exceptions false :content-type :json :body (generate-string {:board-size -1})}))) [:spec 0 :path [0]])))))
