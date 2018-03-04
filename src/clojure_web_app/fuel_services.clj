(ns clojure-web-app.fuel-services
  (:require [clj-http.client :as client]
            [environ.core :refer [env]]
            [clojure.data.json :as json]))

(defn location-by-ip [ip]
  (json/read-str (:body (client/get (format "http://freegeoip.net/json/%s" ip))) :key-fn keyword))

(defn nearby-fuel-prices [lat lon limit distance fuel fallback-location]
  (json/read-str
    (let [apiResult (client/get "http://fuelo.net/api/near" {:query-params {:key      (env :fuelo-api-key)
                                                                            :lat      (or lat (fallback-location :latitude))
                                                                            :lon      (or lon (fallback-location :longitude))
                                                                            :limit    (or limit "10")
                                                                            :distance (or distance "10")
                                                                            :fuel     (or fuel "lpg")}})]
      (apiResult :body))))
