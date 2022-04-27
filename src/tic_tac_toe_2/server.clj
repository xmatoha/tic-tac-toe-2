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
   [reitit.coercion.malli]
   [reitit.http.interceptors.muuntaja :as muuntaja]
   [reitit.http.interceptors.parameters :as parameters]
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
           :reponses
           {200
            {:body
             [:map
              [:game-id string?]
              [:game [:map
                      [:next-player string?]
                      [:winner {:optional true} string?]
                      [:error {:optional true} string?]
                      [:game-over {:optional true} boolean?]
                      [:current-board
                       [:vector [:map
                                 [:offset int?]
                                 [:state string?]]]]]]]}}
           :coercion reitit.coercion.malli/coercion
           :parameters {:body [:map [:board-size pos-int?]]}
           :handler create-new-game-handler}
     :post {:summary "make game move"
            :coercion reitit.coercion.malli/coercion
            :parameters {:body

                         [:map
                          [:game-id string?]
                          [:player string?]
                          [:row int?]
                          [:col int?]]}

            ;; :responses {200 {:body
            ;;                  [:map
            ;;                   [:next-player string?]
            ;;                   [:winner {:optional true} string?]
            ;;                   [:error {:optional true} string?]
            ;;                   [:game-over {:optional true} boolean?]
            ;;                   [:current-board
            ;;                    [:vector [:map
            ;;                              [:offset int?]
            ;;                              [:state string?]]]]]}}

            :handler game-move-handler}}]])

(defn app-with-deps [routes]
  (http/ring-handler
   (http/router
    routes
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

