(ns clojure-web-app.try-out-channels
  (:require [clj-http.client :as client]
            [environ.core :refer [env]]
            [clojure.core.async :refer [go put! <! >! chan timeout]]
            [clojure.tools.logging :as log]
            [clojure.data.json :as json])
  (:import (java.io BufferedReader)))

(defn async-get [url & r]
  (let [c (chan)
        callback (fn [v] (put! c v))
        params (merge (or r {}) {:async? true})]
    (client/get url params callback callback)
    c))

(defn get-stuff [ip]
  (let [c (chan)]
    (go
      (let [url (format "http://freegeoip.net/json/%s" ip)
            res (<! (async-get url))
            parsed-val (json/read-str (:body res) :key-fn keyword)]
        (>! c parsed-val)))
    c))

(defn -main [&]
  (log/info "Starting")
  (doseq [ln (line-seq (BufferedReader. *in*))]
    (go
      (let [res (<! (get-stuff ln))]
        (log/info res)))))