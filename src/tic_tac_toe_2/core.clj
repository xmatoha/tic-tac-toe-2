(ns tic-tac-toe-2.core
  (:require [tic-tac-toe-2.server :refer [server-start]])
  (:gen-class))

(defn -main [& args]
  (server-start (merge  (into  {} (System/getenv)) {"PORT" "3001"})))
