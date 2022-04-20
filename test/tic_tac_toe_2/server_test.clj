(ns tic-tac-toe-2.server-test
  (:require  [clojure.test :refer :all]
             [tic-tac-toe-2.server :refer [server-start server-stop]]
             [clj-http.client :as http-client]))

(def port "3001")

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

(deftest create-new-game-api-tests
  (testing "given valid board size it should return "))
