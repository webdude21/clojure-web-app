(ns clojure-web-app.fuel-services
  (:require [clj-http.client :as client]
            [environ.core :refer [env]]
            [clojure.tools.logging :as log]
            [clojure.data.json :as json]))

(def location-by-ip (memoize
                      (fn [ip]
                        (do
                          (log/info "Getting location for ip:" ip)
                          (json/read-str (:body (client/get (format "http://freegeoip.net/json/%s" ip))) :key-fn keyword)))))

(defn nearby-fuel-prices [lat lon limit distance fuel ip]
  (json/read-str
    (let [apiResult (client/get
                      "http://fuelo.net/api/near"
                      {:query-params {:key      (env :fuelo-api-key)
                                      :lat      (or lat (:latitude (location-by-ip ip)))
                                      :lon      (or lon (:longitude (location-by-ip ip)))
                                      :limit    (or limit "10")
                                      :distance (or distance "10")
                                      :fuel     (or fuel "lpg")}})]
      (apiResult :body))))