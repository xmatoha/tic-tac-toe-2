(ns tic-tac-toe-2.api-test
  (:require
   [clojure.test :refer [deftest is]]
   [clj-http.client :as http-client]
   [cheshire.core :refer [generate-string]])

  (:import [au.com.dius.pact.consumer
            ConsumerPactBuilder ConsumerPactRunnerKt PactTestRun PactVerificationResult$Ok]
           [au.com.dius.pact.consumer.model MockProviderConfig]))

(deftest healthcheck-test
  (let [consumer-pact (-> "clojure_test_consumer"
                          ConsumerPactBuilder/consumer
                          (.hasPactWith "test_provider")
                          (.uponReceiving "clojure test interaction")
                          (.path "/health")
                          (.method "GET")
                          .willRespondWith
                          (.status 200)
                          (.body (generate-string {:msg "ok"}))
                          .toPact)
        config (-> (MockProviderConfig/createDefault))]
    (is (instance? PactVerificationResult$Ok
                   (ConsumerPactRunnerKt/runConsumerTest
                    consumer-pact config
                    (proxy [PactTestRun] []
                      (run [mock-server _]
                        (#(is (= (generate-string {:msg "ok"})
                                 (:body
                                  (http-client/get
                                   (str (.getUrl mock-server) "/health")))))))))))))

;(deftest game-creation-test)
