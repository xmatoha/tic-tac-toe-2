(ns tic-tac-toe-2.core
  (:require [tic-tac-toe-2.server :refer [server-start routes]]
            [tic-tac-toe-2.repo :refer [persist-game game-id game-by-id]]
            [tic-tac-toe-2.api :refer [create-new-game-handler game-move-handler]])
  (:gen-class))

(defn default-routes []
  (routes (create-new-game-handler persist-game game-id)
          (game-move-handler game-by-id)))

(defn -main [& _]
  (server-start (merge  (into  {} (System/getenv)) {}) (default-routes)))
