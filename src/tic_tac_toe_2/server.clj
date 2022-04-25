(ns tic-tac-toe-2.server
  (:require
   [muuntaja.interceptor]
   [reitit.http :as http]
   [reitit.interceptor.sieppari :as sieppari]
   [reitit.ring :as ring]
   [muuntaja.core :as m]
   [reitit.coercion.schema]
   [reitit.http.interceptors.exception :as exception]
   [reitit.coercion.spec]
   [reitit.http.coercion :as coercion]
   [reitit.http.interceptors.muuntaja :as muuntaja]
   [reitit.http.interceptors.parameters :as parameters]
   [ring.adapter.jetty :as jetty]
   [reitit.dev.pretty :as pretty]
   [reitit.swagger :as swagger]
   [reitit.swagger-ui :as swagger-ui]
   [tic-tac-toe-2.api :refer [health-handler]]
   [clojure.spec.alpha :as s]))

(defonce server-handle (atom {}))

(s/def ::board-size pos-int?)

(def app
  (http/ring-handler
   (http/router
    [["/"
      {:get {:summary "root"
             :handler (fn [_] {:status 200 :body {}})}}]
     ["/health"
      {:get {:summary "healthcheck endpoint"
             :handler health-handler}}]
     ["/game"
      {:put {:summary "creates new game"
             :coercion reitit.coercion.spec/coercion
             :parameters {:body {:board-size ::board-size}}
             :handler (fn [_])}}]]
    {:exception pretty/exception
     :data {:coercion reitit.coercion.schema/coercion
            :muuntaja m/instance
            :interceptors [swagger/swagger-feature
                           (parameters/parameters-interceptor)
                           (muuntaja/format-negotiate-interceptor)
                           (muuntaja/format-response-interceptor)
                           (exception/exception-interceptor)
                           (muuntaja/format-request-interceptor)
                           (coercion/coerce-response-interceptor)
                           (coercion/coerce-request-interceptor)]}})

   (ring/routes
    (swagger-ui/create-swagger-ui-handler
     {:path "/"
      :config {:validatorUrl nil
               :operationsSorter "alpha"}})
    (ring/create-default-handler))
   {:executor sieppari/executor}))

(defn server-start [options]
  (println (str "Starting server on port " (get options "PORT")))
  (reset! server-handle (jetty/run-jetty #'app {:host "0.0.0.0" :port (Integer/parseInt (get options "PORT")), :join? false, :async true})))

(defn server-stop []
  (println "Stopping server ...")
  (.stop @server-handle))


