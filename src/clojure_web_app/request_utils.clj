(ns clojure-web-app.request-utils)

(defn get-ip-from [request]
  (let [forwarded-for ((:headers request) "x-forwarded-for")
        remote-addr (:remote-addr request)]
    (or forwarded-for remote-addr)))
