(ns tic-tac-toe-2.server
  (:require
   [muuntaja.interceptor]
   [reitit.http :as http]
   [malli.util :as mu]
   [reitit.ring.middleware.parameters :as parameters]
   [reitit.interceptor.sieppari :as sieppari]
   [reitit.ring :as ring]
   [muuntaja.core :as m]
   [reitit.coercion.schema]
   [reitit.ring.middleware.exception :as exception]
   [reitit.coercion.spec]
   [reitit.ring.coercion :as coercion]
   [reitit.coercion.malli]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [ring.adapter.jetty :as jetty]
   [reitit.dev.pretty :as pretty]
   [reitit.swagger :as swagger]
   [reitit.swagger-ui :as swagger-ui]
   [tic-tac-toe-2.repo :refer [persist-game game-by-id game-id]]
   [tic-tac-toe-2.api :refer [health-handler create-new-game-handler game-move-handler]]))

(defonce server-handle (atom {}))

(defn routes [create-new-game-handler game-move-handler]
  [["/"
    {:get {:summary "root"
           :handler (fn [_] {:status 200 :body {}})}}]
   ["/swagger.json"
    {:get {:no-doc true
           :swagger {:info {:title "tic tac toe game api "
                            :description "tic tac toe game api"}}
           :handler (swagger/create-swagger-handler)}}]
   ["/health"
    {:get {:summary "healthcheck endpoint"

           :coercion reitit.coercion.malli/coercion
           :responses {200 {:body [:map [:msg string?]]}}
           :handler health-handler}}]
   ["/game"
    {:put {:summary "creates new game"
           :responses
           {201
            {:body
             [:map
              [:game-id string?]
              [:game [:map
                      [:next-player keyword?]
                      [:winner {:optional true} any?]
                      [:error {:optional true} string?]
                      [:game-over {:optional true} boolean?]
                      [:current-board
                       [:sequential [:map
                                     [:offset int?]
                                     [:state keyword?]]]]]]]}}

           :parameters {:body [:map [:board-size pos-int?]]}
           :handler create-new-game-handler}
     :post {:summary "make game move"

            :parameters {:body

                         [:map
                          [:game-id string?]
                          [:player string?]
                          [:row int?]
                          [:col int?]]}

            :responses {200 {:body
                             [:map
                              [:next-player keyword?]
                              [:winner {:optional true} any?]
                              [:error {:optional true} any?]
                              [:game-over {:optional true} boolean?]
                              [:current-board
                               [:sequential [:map
                                             [:offset int?]
                                             [:state keyword?]]]]]}}

            :handler game-move-handler}}]])

(defn app-with-deps [routes]
  (http/ring-handler
   (http/router
    routes
    {;;:reitit.middleware/transform dev/print-request-diffs ;; pretty diffs
       ;;:validate spec/validate ;; enable spec validation for route data
       ;;:reitit.spec/wrap spell/closed ;; strict top-level validation
     :exception pretty/exception
     :data {:coercion (reitit.coercion.malli/create
                       {;; set of keys to include in error messages
                        :error-keys #{#_:type :coercion :in :schema :value :errors :humanized #_:transformed}
                           ;; schema identity function (default: close all map schemas)
                        :compile mu/closed-schema
                           ;; strip-extra-keys (effects only predefined transformers)
                        :strip-extra-keys true
                           ;; add/set default values
                        :default-values true
                           ;; malli options
                        :options nil})
            :muuntaja m/instance
            :middleware [;; swagger feature
                         swagger/swagger-feature
                           ;; query-params & form-params
                         parameters/parameters-middleware
                           ;; content-negotiation
                         muuntaja/format-negotiate-middleware
                           ;; encoding response body
                         muuntaja/format-response-middleware
                           ;; exception handling
                         exception/exception-middleware
                           ;; decoding request body
                         muuntaja/format-request-middleware
                           ;; coercing response bodys
                         coercion/coerce-response-middleware
                           ;; coercing request parameters
                         coercion/coerce-request-middleware
                           ;; multipart
                         ]}})
   (ring/routes
    (swagger-ui/create-swagger-ui-handler
     {:path "/api"
      :config {:validatorUrl nil
               :operationsSorter "alpha"}})
    (ring/create-default-handler))
   {:executor sieppari/executor}))

(defn server-start [options routes]
  (println (str "Starting server on port " (get options "PORT")))
  (reset! server-handle (jetty/run-jetty (app-with-deps routes) {:host "0.0.0.0" :port (Integer/parseInt (get options "PORT")), :join? false, :async true})))

(defn server-stop []
  (println "Stopping server ...")
  (.stop @server-handle))

(defn restart-server []
  (when @server-handle (server-stop))
  (server-start {"PORT" "3000"}
                (routes (create-new-game-handler persist-game  game-id) (game-move-handler persist-game game-by-id))))

(comment (restart-server))
(comment (server-stop))

