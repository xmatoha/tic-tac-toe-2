(ns tic-tac-toe-2.core-test
  (:require [clojure.test :refer [deftest testing is]]
            [tic-tac-toe-2.core :refer [hello]]))

(deftest hello-test
  (testing "hello"
    (is (= (hello) "hello"))))
