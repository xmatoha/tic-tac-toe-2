(defproject tic-tac-toe-2 "0.1.0-SNAPSHOT"
  :description "tic-tac-toe-2"
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [clj-kondo "2022.03.09"]
                 [nubank/mockfn "0.7.0"]
                 [defn-spec "0.2.0"]]
  :main ^:skip-aot tic_tac_toe_2.core
  :target-path "target/%s"
  :aliases {"test" ["with-profile" "+kaocha" "run" "-m" "kaocha.runner" "unit"]
            "features" ["with-profile" "+kaocha" "run" "-m" "kaocha.runner" "features"]
            "watch" ["with-profile" "+kaocha" "run" "-m" "kaocha.runner" "--fail-fast" "--watch" "unit"]
            "lint" ["run" "-m" "clj-kondo.main" "--lint" "src"]}
  :plugins [[refactor-nrepl "3.5.2"]
            [cider/cider-nrepl "0.28.3"]
            [lein-bump-version "0.1.6"]]
  :profiles {:kaocha {:dependencies [[lambdaisland/kaocha-cucumber "0.0-53"]
                                     [lambdaisland/kaocha "1.64.1010"]
                                     [lambdaisland/kaocha-cloverage "1.0.75"]]}
             :uberjar {:aot :all}})


