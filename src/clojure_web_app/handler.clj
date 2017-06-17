(ns clojure-web-app.handler
    (:require [compojure.core :refer :all]
      [ring.middleware.json :refer [wrap-json-response]]
      [ring.middleware.json :refer [wrap-json-body]]
      [compojure.handler :as handler]
      [ring.middleware.json :refer [wrap-json-params]]
      [ring.util.response :refer [response status]]
      [compojure.route :as route]
      [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))


(defroutes app-routes
           (GET "/user/:id" [id greeting]
                (response {:userId   id
                           :greeting greeting}))
           (GET "/my-ip" [] (fn [request]
                                (status (response {:requested-by (:remote-addr request)}) 301)))
           (route/not-found "Not Found"))

;; define the ring application
(def app
  (-> (handler/api app-routes)
      wrap-json-body
      wrap-json-params
      wrap-json-response))

