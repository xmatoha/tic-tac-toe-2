(ns tic-tac-toe-2.game-cli
  (:require [clojure.tools.cli :refer [parse-opts]])
  (:gen-class))

(defn cli-options []
  [[]])

(defn call-new-game [_])

(defn call-game-move [_])

(defn new-game-args [args]
  (-> (parse-opts args [["-b" "--board-size board-size" "board size" :missing "Game board size"]])
      (assoc :action-fn call-new-game)))

(defn game-move-args [args]
  (-> (parse-opts args [["-g" "--game-id game-id" "game identifier" :missing "game identifier is required"]
                        ["-x" "--x x" "x" :missing "x is required"]
                        ["-y" "--y y" "y" :missing "y is required"]])
      (assoc :action-fn call-game-move)))

(defn exit-fn [])

(defn parse-cli-args [args exit-fn]
  (case (get args 1)
    "new-game" (new-game-args (subvec args 2))
    "move" (game-move-args (subvec args 2))
    {:action-fn (fn [config] (.println *err* (:error config)) (exit-fn)) :error (str "Unknown command: '" (get args 1) "'")}))

(defn main [& args]
  (let [config (parse-cli-args (into [] args) exit-fn)]
    ((:action-fn config) config)))
