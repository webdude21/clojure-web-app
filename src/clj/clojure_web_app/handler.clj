(ns clojure-web-app.handler
  (:require [compojure.core :refer :all]
            [clojure-web-app.request-utils :refer [get-ip-from]]
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

(def fucks-given (atom 0))

(defroutes app-routes
           (GET "/rest/fuel-near-me" [lat lon limit distance fuel]
             (fn [request]
               (response (service/nearby-fuel-prices lat lon limit distance fuel (get-ip-from request)))))
           (GET "/rest/my-location" []
             (fn [request] (response (service/location-by-ip (get-ip-from request)))))
           (GET "/rest/fuck", []
             (fn [_]
               (do (swap! fucks-given inc)
                   (response {:fucksGiven @fucks-given}))))
           (GET "/" [] (clojure.java.io/resource "public/index.html"))
           (route/not-found (clojure.java.io/resource "public/404.html")))

(def app
  (-> (handler/api app-routes)
      wrap-json-body
      wrap-json-params
      wrap-json-response
      (wrap-defaults site-defaults)))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 3000))]
    (jetty/run-jetty (site #'app) {:port port :join? false})))