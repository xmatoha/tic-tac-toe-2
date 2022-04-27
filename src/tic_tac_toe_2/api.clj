(ns tic-tac-toe-2.api
  (:require [tic-tac-toe-2.game :refer [new-game game-round calc-offset]]))

(defn health-handler [_]
  {:status 200
   :body {:msg "ok"}})

(defn create-new-game-handler [persist-game game-id]
  (fn [{{{:keys [board-size]} :body} :parameters}]
    (let [game-id (game-id)
          game (new-game board-size)]
      (persist-game game-id game)
      {:status 201 :body {:game-id (.toString game-id) :game game}})))

(defn game-move-handler [persist-game game-by-id]
  (fn [{{{:keys [game-id player row col]} :body} :parameters}]
    (let [game (game-by-id game-id)
          offset (calc-offset row col (:current-board game))
          game-round-result (game-round game {:player (keyword player) :offset  offset})]
      (persist-game game-id game-round-result)
      {:status 200 :body game-round-result})))


