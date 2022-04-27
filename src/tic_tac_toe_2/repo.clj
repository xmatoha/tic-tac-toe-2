(ns tic-tac-toe-2.repo)

(defonce game-store (atom {}))

(defn game-id []
  (java.util.UUID/randomUUID))

(defn persist-game [game-id game-state]
  (reset! game-store {game-id game-state}))

(defn game-by-id [game-id]
  (get @game-store game-id))
