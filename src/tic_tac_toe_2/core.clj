(ns tic-tac-toe-2.core
  (:require [tic-tac-toe-2.server :refer [server-start]])
  (:gen-class))

(defn -main [& _]
  (server-start (merge  (into  {} (System/getenv)) {})))
