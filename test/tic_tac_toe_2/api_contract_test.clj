(ns tic-tac-toe-2.api-contract-test
  (:require
   [clojure.test :refer [deftest is]]
   [clj-http.client :as http-client]
   [tic-tac-toe-2.game :refer [game-round new-game]]
   [cheshire.core :refer [generate-string parse-string]])

  (:import
   [au.com.dius.pact.consumer.dsl PactDslJsonBody]
   [au.com.dius.pact.consumer
    ConsumerPactBuilder ConsumerPactRunnerKt PactTestRun PactVerificationResult$Ok]
   [au.com.dius.pact.consumer.model MockProviderConfig]))

(defn consumer-pact-builder [] (-> "test_consumer"
                                   ConsumerPactBuilder/consumer
                                   (.hasPactWith "test_provider")))

(def config (-> (MockProviderConfig/createDefault)))

(deftest ^:contract healthcheck-test
  (is (instance?
       PactVerificationResult$Ok
       (ConsumerPactRunnerKt/runConsumerTest
        (-> "test_consumer"
            ConsumerPactBuilder/consumer
            (.hasPactWith "test_provider")
            (.uponRecieving "healtcheck")
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
  (is (instance?
       PactVerificationResult$Ok
       (ConsumerPactRunnerKt/runConsumerTest
        (-> "test_consumer"
            ConsumerPactBuilder/consumer
            (.hasPactWith "test_provider")
            (.uponReceiving "game creation request")
            (.path "/game")
            (.method "PUT")
            (.body (generate-string {:board-size 3}))
            .willRespondWith
            (.status 201)
            (.body (-> (new PactDslJsonBody) (.object "game")))
            .toPact)
        config
        (proxy [PactTestRun] []
          (run [mock-server _]
            (let [response (http-client/put
                            (str (.getUrl mock-server) "/game")
                            {:content-type "application/json"
                             :body (generate-string
                                    {:board-size 3})})]
              ;(#(is (=   (parse-string (generate-string (new-game 3)) true)
              ;           (:game (parse-string (:body  response) true)))))
              (#(is (= 201
                       (:status response)))))))))))

(deftest ^:contract game-move-test
  (is (instance?
       PactVerificationResult$Ok
       (ConsumerPactRunnerKt/runConsumerTest
        (-> "test_consumer"
            ConsumerPactBuilder/consumer
            (.hasPactWith "test_provider")
            (.uponReceiving "game move request")
            (.path "/game")
            (.method "POST")
            (.body (generate-string {:game-id "id" :player :x  :row 0 :col 0}))
            .willRespondWith
            (.status 200)
            (.body  (generate-string
                     (game-round (new-game 3) {:offset 0 :player :x})))
            .toPact)
        config
        (proxy [PactTestRun] []
          (run [mock-server _]
            (let [response
                  (http-client/post
                   (str (.getUrl mock-server) "/game")
                   {:content-type "application/json"
                    :body (generate-string
                           {:game-id "id" :player :x :row 0 :col 0})})]
              (#(is (=
                     (parse-string (generate-string (game-round (new-game 3) {:offset 0 :player :x})) true)
                     (parse-string (:body  response) true))))
              (#(is (= 200
                       (:status response)))))))))))

(comment (deftest ^:contract game-move-invalid-move
           (is (instance?
                PactVerificationResult$Ok
                (ConsumerPactRunnerKt/runConsumerTest
                 (-> "test_consumer"
                     ConsumerPactBuilder/consumer
                     (.hasPactWith "test_provider")
                     (.uponReceiving "game creation request")
                     (.path "/game")
                     (.method "PUT")
                     (.body (generate-string {:board-size 3}))
                     .willRespondWith
                     (.status 201)
                     (.body (-> (new PactDslJsonBody) (.object "game")))
                     (.uponReceiving "game move request valid")
                     (.path "/game")
                     (.method "POST")
                     (.body (generate-string {:game-id "id" :player :x :row 0 :col 0}))
                     .willRespondWith
                     (.status 200)
                     (.body  (generate-string
                              (-> (new-game 3)
                                  (game-round  {:offset 0 :player :x}))))
                     (.uponReceiving "game move request same cell")
                     (.path "/game")
                     (.method "POST")
                     (.body (generate-string {:game-id "id" :player :o  :row 0 :col 0}))
                     .willRespondWith
                     (.status 400)
                     (.body  (generate-string
                              (-> (new-game 3)
                                  (game-round  {:offset 0 :player :x})
                                  (game-round  {:offset 0 :player :o}))))
                     .toPact)
                 config
                 (proxy [PactTestRun] []
                   (run [mock-server _]
                     (let [game-id (get-in (http-client/put
                                            (str (.getUrl mock-server) "/game")
                                            {:throw-exceptions false
                                             :content-type "application/json"
                                             :body (generate-string
                                                    {:board-size 3})}) [:body :game-id])]
                       (let [response

                             (http-client/post
                              (str (.getUrl mock-server) "/game")
                              {:throw-exceptions false
                               :content-type "application/json"
                               :body (generate-string
                                      {:game-id game-id :player :x  :row 0 :col 0})})]

                         (#(is (= 200
                                  (:status response)))))
                       (let [response
                             (http-client/post
                              (str (.getUrl mock-server) "/game")
                              {:throw-exceptions false
                               :content-type "application/json"
                               :body (generate-string
                                      {:game-id game-id :player :o  :row 0 :col 0})})]

                         (#(is (= 400
                                  (:status response))))

                         (#(is (= "Invalid move"
                                  (get-in (parse-string (:body response) true) [:error])))))))))))))

