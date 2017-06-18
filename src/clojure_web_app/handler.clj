(ns clojure-web-app.handler
  (:require [compojure.core :refer :all]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.middleware.json :refer [wrap-json-body]]
            [compojure.handler :as handler]
            [compojure.handler :refer [site]]
            [ring.middleware.json :refer [wrap-json-params]]
            [ring.util.response :refer [response status]]
            [compojure.route :as route]
            [clojure.data.json :as json]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [ring.adapter.jetty :as jetty]
            [clj-http.client :as client]
            [environ.core :refer [env]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(def production (or (env :production) false))

(defn location-by-ip [ip]
  (json/read-str ((client/get (format "http://freegeoip.net/json/%s" ip)) :body) :key-fn keyword))

(defn nearby-fuel-prices [lat lon limit distance fuel]
  (json/read-str
    ((client/get "http://fuelo.net/api/near" {:query-params {:key      (env :fuelo-api-key)
                                                             :lat      lat
                                                             :lon      lon
                                                             :limit    limit
                                                             :distance distance
                                                             :fuel     fuel}}) :body)))
(defroutes app-routes
           (GET "/user/:id" [id greeting]
             {:body {:userId   id
                     :greeting greeting}})
           (GET "/print-query-params" [& args] (response args))
           (GET "/cheapest-near-me" [limit distance fuel]
             (fn [request]
               (let [location (location-by-ip (if production
                                                ((:headers request) "http_x_forwarded_for")
                                                (:remote-addr request)))]
                 (response (nearby-fuel-prices (location :latitude) (location :longitude) limit distance fuel)))))
           (GET "/my-location" []
             (fn [request] (response (location-by-ip (if production
                                                       ((:headers request) "http_x_forwarded_for")
                                                       (:remote-addr request))))))
           (GET "/my-ip" []
             (fn [request]
               {:status 200
                :body   {:requested-by (:remote-addr request)}}))
           (route/not-found "Not Found"))

;; define the ring application
(def app
  (-> (handler/api app-routes)
      wrap-json-body
      wrap-json-params
      wrap-json-response))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 5000))]
    (jetty/run-jetty (site #'app) {:port port :join? false})))