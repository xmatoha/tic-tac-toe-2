(ns tic-tac-toe-2.cli
  (:require [clojure.string :refer [join upper-case]]
            [tic-tac-toe-2.game :refer :all]))

(defn row-to-string [row]
  (->>
   (map (fn [e] (if (= :e (:state e)) " " (upper-case (name (:state e))))) row)
   (join "|")))

(defn row-separator [size]
  (join "+" (map (fn [_] "-") (range 0 size))))

(defn board-to-string [board]
  (join (str "\n" (row-separator (calc-board-size board)) "\n")
        (for [r (range 0 (calc-board-size board))]
          (row-to-string (row board r)))))

(defn print-winner [game-state]
  (cond (= (:winner game-state) nil) "GAME ENDS WITH A DRAW!"
        :else (str "PLAYER " (:winner game-state) " WON")))

(defn display-game [rounds]
  (println "**************************************")
  (println "new game ")
  (println "**************************************")
  (doseq [r rounds]
    (println (board-to-string (:current-board r)))
    (println "----------------------------")
    (Thread/sleep 2000))
  (println (print-winner (last rounds))))
