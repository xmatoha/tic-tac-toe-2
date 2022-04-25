(ns tic-tac-toe-2.api-contract-test
  (:require
   [clojure.test :refer [deftest is]]
   [clj-http.client :as http-client]
   [tic-tac-toe-2.game :refer [game-round new-game]]
   [cheshire.core :refer [generate-string parse-string]])

  (:import [au.com.dius.pact.consumer
            ConsumerPactBuilder ConsumerPactRunnerKt PactTestRun PactVerificationResult$Ok]
           [au.com.dius.pact.consumer.model MockProviderConfig]))

(defn consumer-pact-builder [] (-> "clojure_test_consumer"
                                   ConsumerPactBuilder/consumer
                                   (.hasPactWith "test_provider")))

(def config (-> (MockProviderConfig/createDefault)))

(deftest ^:contract healthcheck-test
  (is (instance? PactVerificationResult$Ok
                 (ConsumerPactRunnerKt/runConsumerTest
                  (-> "clojure_test_consumer"
                      ConsumerPactBuilder/consumer
                      (.hasPactWith "test_provider")
                      (.uponReceiving "healtcheck")
                      (.path "/health")
                      (.method "GET")
                      .willRespondWith
                      (.status 200)
                      (.body (generate-string {:msg "ok"}))
                      .toPact)
                  config
                  (proxy [PactTestRun] []
                    (run [mock-server _]
                      (#(is (= (generate-string {:msg "ok"})
                               (:body
                                (http-client/get
                                 (str (.getUrl mock-server) "/health"))))))))))))

(deftest ^:contract game-creation-test
  (is (instance? PactVerificationResult$Ok
                 (ConsumerPactRunnerKt/runConsumerTest
                  (-> "clojure_test_consumer"
                      ConsumerPactBuilder/consumer
                      (.hasPactWith "test_provider")
                      (.uponReceiving "game creation request")
                      (.path "/game")
                      (.method "PUT")
                      .willRespondWith
                      (.status 201)
                      (.body (generate-string {:game-id "id"}))
                      .toPact)
                  config
                  (proxy [PactTestRun] []
                    (run [mock-server _]
                      (let [response (http-client/put
                                      (str (.getUrl mock-server) "/game"))]
                        (#(is (= (generate-string {:game-id "id"})
                                 (:body response))))
                        (#(is (= 201
                                 (:status response)))))))))))

(deftest ^:contract game-move-test
  (is (instance? PactVerificationResult$Ok
                 (ConsumerPactRunnerKt/runConsumerTest
                  (-> "clojure_test_consumer"
                      ConsumerPactBuilder/consumer
                      (.hasPactWith "test_provider")
                      (.uponReceiving "game move request")
                      (.path "/game")
                      (.method "POST")
                      (.body (generate-string {:player :x :move {:row 0 :col 0}}))
                      .willRespondWith
                      (.status 200)
                      (.body  (generate-string (game-round (new-game 3) {:offset 0 :player :x})))
                      .toPact)
                  config
                  (proxy [PactTestRun] []
                    (run [mock-server _]
                      (let [response (http-client/post
                                      (str (.getUrl mock-server) "/game")
                                      {:content-type "application/json"
                                       :body (generate-string {:player :x :move {:row 0 :col 0}})})]
                        (#(is (=
                               (parse-string (generate-string (game-round (new-game 3) {:offset 0 :player :x})) true)
                               (parse-string (:body  response) true))))
                        (#(is (= 200
                                 (:status response)))))))))))



