(ns tic-tac-toe-2.server
  (:require [reitit.ring :as ring]
            [reitit.http :as http]
            [muuntaja.interceptor]
            [reitit.interceptor.sieppari :as sieppari]
            [ring.adapter.jetty :as jetty]
            [tic-tac-toe-2.api :refer [health-handler]]))

(defonce server-handle (atom {}))

(def app
  (http/ring-handler
   (http/router
    [["/"
      {:get {:summary "root"
             :handler (fn [_] {:status 200 :body {}})}}]
     ["/health"
      {:get {:summary "healthcheck endpoint"
             :handler health-handler}}]])

   (ring/create-default-handler)
   {:executor reitit.interceptor.sieppari/executor
    :interceptors [(muuntaja.interceptor/format-interceptor)]}))

(defn server-start [options]
  (println (str "Starting server on port " (get options "PORT")))
  (reset! server-handle (jetty/run-jetty #'app {:host "0.0.0.0" :port (Integer/parseInt (get options "PORT")), :join? false, :async true})))

(defn server-stop []
  (println "Stopping server ...")
  (.stop @server-handle))

