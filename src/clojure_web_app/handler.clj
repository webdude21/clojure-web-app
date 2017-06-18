(ns clojure-web-app.handler
  (:require [compojure.core :refer :all]
            [clojure-web-app.fuel-services :as service]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.middleware.json :refer [wrap-json-body]]
            [ring.middleware.json :refer [wrap-json-params]]
            [ring.util.response :refer [response status]]
            [compojure.handler :as handler]
            [compojure.handler :refer [site]]
            [compojure.route :as route]
            [ring.adapter.jetty :as jetty]
            [environ.core :refer [env]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(def production (or (env :production) false))

(defn get-ip-from [request]
  (if production
    ((:headers request) "x-forwarded-for")
    (:remote-addr request)))

(defroutes app-routes
           (GET "/print-query-params" [& args] (response args))
           (GET "/fuel-near-me" [lat lon limit distance fuel]
             (fn [request]
               (let [location (service/location-by-ip (get-ip-from request))]
                 (response (service/nearby-fuel-prices lat lon limit distance fuel location)))))
           (GET "/my-location" []
             (fn [request] (response (service/location-by-ip (get-ip-from request)))))
           (route/not-found "Not Found"))

(def app
  (-> (handler/api app-routes)
      wrap-json-body
      wrap-json-params
      wrap-json-response))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 3000))]
    (jetty/run-jetty (site #'app) {:port port :join? false})))