(ns tic-tac-toe-2.game)

(defn empty-board [board-size]
  (->>
   (range (* board-size board-size))
   (map (fn [idx] {:offset idx :state :e}))
   (into [])))

(defn calc-board-size [board]
  (int (Math/sqrt (count board))))

(defn calc-offset [row column board]
  (+ (* (calc-board-size board) row)  column))

(defn occupy [board row column who]
  (let [offset (calc-offset row column board)]
    (assoc-in board [offset :state] who)))

(defn occupy-game [game row column who]
  (let [offset (calc-offset row column (:current-board game))]
    (assoc-in game [:current-board offset :state] who)))

(defn board-elem-at [board row col]
  (get board (calc-offset row col board)))

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

(defn empty-cell? [board offset]
  (if (= :e (:state (nth board offset))) true  false))

(defn make-move [game-state action]
  (assoc-in game-state
            [:current-board (:offset action)
             :state] (:next-player game-state)))

(defn random-board-offset [game-state]
  (let [ec (empty-cells (:current-board game-state))]
    (:offset (get ec (Math/round (rand (- (count ec) 1)))))))

(defn make-random-move [game-state]
  (make-move game-state {:offset (random-board-offset game-state)}))

(defn new-game [size]
  {:next-player :x
   :current-board (empty-board size)
   :winner nil
   :game-over false})

(defn winner! [game-state]
  (cond (won? (:current-board game-state) :x)
        (assoc game-state :winner :x)
        (won? (:current-board game-state) :o)
        (assoc game-state :winner :o)
        :else game-state))

(defn winner? [game-state]
  (:winner game-state))

(defn board-full! [game-state]
  (if (= (count (empty-cells (:current-board game-state))) 0)
    (assoc game-state :board-full true) game-state))

(defn board-full? [game-state]
  (if (:board-full game-state) true false))

(defn switch-player! [game-state]
  (if (= :x (:next-player game-state))
    (assoc game-state :next-player :o)
    (assoc game-state :next-player :x)))

(defn draw? [game-state]
  (if (and (board-full? game-state) (not (winner? game-state))) true false))

(defn game-over! [game-state]
  (cond
    (winner? game-state) (assoc game-state :game-over true)
    (board-full? game-state) (assoc game-state :game-over true)
    :else game-state))

(defn game-over? [game-state]
  (:game-over game-state))

(defn validate-action [game-state action]
  (cond (not (empty-cell? (:current-board game-state) (:offset action)))
        (assoc game-state :error "Invalid move")
        (not (= (:next-player game-state) (:player action)))
        (assoc game-state :error "Invalid user")
        :else game-state))

(defn clear-round-state [game-state]
  (assoc game-state :error nil))

(defn has-error? [game-state]
  (:error game-state))

(defn game-round [game-state action]
  (let [validated-state (-> game-state
                            (clear-round-state)
                            (validate-action action))]
    (if (has-error? validated-state) validated-state
        (-> validated-state
            (make-move action)
            (switch-player!)
            (board-full!)
            (winner!)
            (game-over!)))))

(defn random-move [game-state]
  {:offset (random-board-offset game-state) :player (:next-player game-state)})

(defn bot-game [size]
  (loop [game-state (new-game size)
         game []]
    (if (game-over? game-state)
      (conj game game-state)
      (recur  (game-round game-state (random-move game-state)) (conj game game-state)))))


