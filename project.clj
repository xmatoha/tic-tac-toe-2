(defproject tic-tac-toe-2 "1.0.0"
  :description "tic tac toe kata"
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [clj-kondo "2022.03.09"]
                 [org.clojure/clojure "1.10.0"]
                 [ring/ring-jetty-adapter "1.7.1"]
                 [babashka/babashka.curl "0.1.2"]
                 [aleph "0.4.7-alpha5"]
                 [metosin/reitit "0.5.17"]
                 [clj-http "3.12.3"]
                 [cheshire "5.10.2"]
                 [org.clojure/tools.cli "1.0.206"]
                 [lambdaisland/kaocha-cucumber "0.0-53"]
                 [lambdaisland/kaocha-cloverage "1.0.75"]
                 [nubank/mockfn "0.7.0"]
                 [au.com.dius.pact.consumer/junit "4.2.9"]]
  :main ^:skip-aot tic-tac-toe-2.core
  :target-path "target/%s"
  :aliases {"test" ["with-profile" "+kaocha" "run" "-m" "kaocha.runner" "unit"]
            "test-all" ["with-profile" "+kaocha" "run" "-m" "kaocha.runner" "unit" "integration"]
            "itest" ["with-profile" "+kaocha" "run" "-m" "kaocha.runner" "integration"]
            "features" ["with-profile" "+kaocha" "run" "-m" "kaocha.runner" "features"]
            "watch" ["with-profile" "+kaocha" "run" "-m" "kaocha.runner" "--fail-fast" "--watch" "unit"]
            "lint" ["run" "-m" "clj-kondo.main" "--lint" "src"]}
  :plugins [;[refactor-nrepl "3.5.2"]
 ;           [cider/cider-nrepl "0.28.3"]
            [lein-bump-version "0.1.6"]]
  :jvm-opts ["-XX:TieredStopAtLevel=1", "-Xmx512m"]
  :pact {:service-providers {:test_provider {:protocol "http"
                                             :host "localhost"
                                             :port 3000
                                             :path  "/"
                                             :has-pact-with
                                             {:clojure_test_consumer {:pact-source "file:./target/pacts/test_consumer-test_provider.json"}}}}}

  :profiles {:pact {:plugins [[au.com.dius.pact.provider/lein "4.1.20"
                               :exclusions [commons-logging]]] :dependencies
                    [[ch.qos.logback/logback-core "1.2.3"]
                     [ch.qos.logback/logback-classic "1.2.3"]]}
             :kaocha {:dependencies [[lambdaisland/kaocha-cucumber "0.0-53"]
                                     [lambdaisland/kaocha "1.64.1010"]
                                     [lambdaisland/kaocha-cloverage "1.0.75"]]}
             :uberjar {:aot :all}})


