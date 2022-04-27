(ns tic-tac-toe-2.repo)

(defonce game-store (atom {}))

(defn game-id []
  (java.util.UUID/randomUUID))

(defn persist-game [game-id game-state]
  (reset! game-store {(.toString game-id) game-state}))

(defn game-by-id [game-id]
  (println "game-store: " @game-store)
  (get @game-store game-id))
