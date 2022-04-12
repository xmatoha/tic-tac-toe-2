(ns tic-tac-toe-2.core
  (:require [clojure.string :refer [join upper-case]]))

(defn empty-board [board-size]
  (->>
   (range (* board-size board-size))
   (map (fn [idx] {:offset idx :state :e}))
   (into [])))

(defn calc-board-size [board]
  (int (Math/sqrt (count board))))

(defn calc-offset [row column board]
  (+ (* (calc-board-size board) row)   column))

(defn occupy [board row column who]
  (let [offset (calc-offset row column board)]
    (assoc-in board [offset :state] who)))

(defn row [board row-offset]
  (subvec board
          (calc-offset row-offset 0 board)
          (+ (calc-offset row-offset 0 board) (calc-board-size board))))

(defn col [board col-offset]
  (flatten (for [row-offset (range 0 (calc-board-size board))]
             (subvec board
                     (calc-offset row-offset col-offset board)
                     (+ 1 (calc-offset row-offset col-offset board))))))

(defn asc-diagonale [board]
  (flatten (for [row-offset (range 0 (calc-board-size board))]
             (subvec board
                     (calc-offset row-offset row-offset board)
                     (+ 1 (calc-offset row-offset row-offset board))))))

(defn desc-diagonale [board]
  (flatten (for [row-offset (range (- (calc-board-size board) 1) -1 -1)]
             (subvec board
                     (calc-offset
                      (- (calc-board-size board) row-offset 1) row-offset board)
                     (+ 1
                        (calc-offset
                         (- (calc-board-size board) row-offset 1) row-offset board))))))

(defn row-won? [board who]
  (->>
   (for [row-offset (range 0 (calc-board-size board))]
     (->>
      (row board row-offset)
      (every? (fn [e] (= (:state e) who)))))
   (some true?)))

(defn col-won? [board who]
  (->>
   (for [row-offset (range 0 (calc-board-size board))]
     (->>
      (col board row-offset)
      (every? (fn [e] (= (:state e) who)))))
   (some true?)))

(defn asc-diagnoale-won? [board who]
  (every? (fn [e] (= (:state e) who))
          (asc-diagonale board)))

(defn desc-diagnoale-won? [board who]
  (every? (fn [e] (= (:state e) who))
          (desc-diagonale board)))

(defn won? [board who]
  (cond (row-won? board who) true
        (col-won? board who) true
        (asc-diagnoale-won? board who) true
        (desc-diagnoale-won? board who) true
        :else false))

(defn empty-cells [board]
  (vec (filter (fn [e]  (= (:state e) :e)) board)))

(defn make-move [board who]
  (let [ec (empty-cells board)]
    (if (> (count ec) 0)
      (assoc-in board
                [(:offset (get ec (Math/round (rand (- (count ec) 1)))))
                 :state] who) board)))

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

(defn new-game [player]
  {:next-player player :current-board (empty-board 3)})

(defn winner? [game-state]
  (cond (won? (:current-board game-state) :x)
        (assoc game-state :winner :x :game-over true)
        (won? (:current-board game-state) :o)
        (assoc game-state :winner :o :game-over true)
        :else game-state))

;; (defn game-over? [game-state]
;;   (cond (not (= nil (:winner game-state)))
;;         (assoc game-state :game-over true)
;;         :else game-state))

(defn board-full? [game-state]
  (if (= (count (empty-cells (:current-board game-state))) 0) (assoc game-state :board-full true :game-over true) game-state))

(defn game-round [game-state]
  (-> game-state
      (assoc  :current-board
              (make-move (:current-board game-state) (:next-player game-state)))
      (assoc
       :next-player
       (if (= :x (:next-player game-state)) :o :x))
      (board-full?)
      (winner?)))

(defn print-winner [game-state]
  (cond (= (:winner game-state) nil) "GAME ENDS WITH A DRAW!"
        :else (str "PLAYER " (:winner game-state) " WON")))

(defn game-loop [starts]
  (loop [game-state (new-game starts) game []]
    (if (= nil (:game-over game-state))
      (recur  (game-round game-state) (conj game game-state))
      (conj game game-state))))

(defn display-game [rounds]
  (println "**************************************")
  (println "new game ")
  (println "**************************************")
  (doseq [r rounds]
    (println (board-to-string (:current-board r)))
    (println "----------------------------")
    (Thread/sleep 2000))
  (println (print-winner (last rounds))))

