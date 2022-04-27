(ns tic-tac-toe-2.game-cli
  (:require [clojure.tools.cli :refer [parse-opts]]
            [tic-tac-toe-2.game :refer [calc-board-size row]]
            [clojure.string :refer [join upper-case]]
            [cheshire.core :as json]
            [babashka.curl :as curl])
  (:gen-class))

(def api-endpoint "http://localhost:3000")

(defn row-to-string [row]
  (->>
   (map (fn [e] (if (= "e" (:state e)) " " (upper-case (name (:state e))))) row)
   (join "|")))

(defn row-separator [size]
  (join "+" (map (fn [_] "-") (range 0 size))))

(defn board-to-string [board]
  (join (str "\n" (row-separator (calc-board-size board)) "\n")
        (for [r (range 0 (calc-board-size board))]
          (row-to-string (row board r)))))

(defn game-round-to-string [game]
  (str "Game board: \n\n" (board-to-string (:current-board game))
       "\n\nNext player: " (:next-player game) "\n"
       (when (:game-over game) (str "Game over!" "\n"))
       (when  (:winner game) (str "Game won " (:winner game) "\n"))
       (when   (:error game) (str "Error: " (:error game) "\n"))
       (when   (and (not (:winner game)) (:board-full game)) (str "Game ended with draw")) "\n"))

(defn new-game-to-string [new-game]
  (str (game-round-to-string (:game new-game))
       "\nGame ID: " (:game-id new-game)))

(defn print-help [_]
  (println "\nusage: game-cli action <options>\n\nactions: \n\tnew-game\n\tgame-move"))

(defn game-move-help [config]
  (println (str "\nusage: game-cli game-move <options>\n\noptions:\n" (:summary config))))

(defn new-game-help [config]
  (println (str "\nusage: game-cli new-game <options>\n\noptions:\n" (:summary config))))

(defn game-move-api-call [config]
  (curl/post
   (str api-endpoint "/game")
   {:throw true
    :headers {:content-type "application/json"}
    :body (json/generate-string
           {:game-id (get-in config [:options :game-id])
            :player (get-in config [:options :player])
            :row (Integer/parseInt (get-in config [:options :row]))
            :col (Integer/parseInt (get-in config [:options :col]))})}))

(defn new-game-api-call [config]
  (curl/put
   (str api-endpoint "/game")
   {:headers {:content-type "application/json"}
    :body (json/generate-string
           {:board-size
            (Integer/parseInt  (get-in config [:options :board-size]))})}))

(defn call-new-game [config]
  (new-game-to-string
   (json/parse-string
    (:body ((:api-call-fn config) config)) true)))

(defn call-game-move [config]
  (game-round-to-string
   (json/parse-string
    (:body ((:api-call-fn config) config)) true)))

(defn new-game-args [args]
  (-> (parse-opts args [["-h" "--help" "help"]
                        ["-b" "--board-size board-size" "board size" :missing "Game board size"]])
      (assoc :action-fn call-new-game)
      (assoc :help-fn new-game-help)
      (assoc :api-call-fn new-game-api-call)
      (assoc :out-fn print)))

(defn game-move-args [args]
  (-> (parse-opts
       args
       [["-h" "--help" "help"]
        ["-g" "--game-id game-id" "game identifier" :missing "game identifier is required"]
        ["-p" "--player player" "player identifier" :missing "player is required"]
        ["-r" "--row row" "board row" :missing "row is required"]
        ["-c" "--col col" "board column" :missing "column is required"]])
      (assoc :action-fn call-game-move)
      (assoc :help-fn game-move-help)
      (assoc :api-call-fn game-move-api-call)
      (assoc :out-fn print)))

(defn exit-fn [])

(defn parse-cli-args [args exit-fn]
  (case (get args 1)
    "new-game" (new-game-args (subvec args 2))
    "game-move" (game-move-args (subvec args 2))
    "-h" {:help-action print-help :action-fn print-help}
    {:action-fn (fn [config] (.println *err* (:error config)) (exit-fn)) :error (str "Unknown command: '" (get args 1) "'")}))

(defn cli-logic [args]
  (let [config (parse-cli-args args exit-fn)]
    (cond (get-in config [:options :help])
          ((:help-fn config) config)
          :else
          (let [ret ((:action-fn config) config)]
            (when-let [out-fn (:out-fn config)] (out-fn ret))))))

(defn -main [& args]
  (cli-logic (into [] args)))

