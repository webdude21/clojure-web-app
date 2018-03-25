(ns clojure_front_end_app.core
  (:require-macros [cljs.core.async.macros :refer [go alt!]])
  (:require [goog.events :as events]
            [clojure.string :as string]
            [cljs.core.async :refer [put! <! >! chan timeout]]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs-http.client :as http]
            [clojure.string :as string]))

(def app-state
  (atom {}))

(enable-console-print!)

(defn get-position []
  (let [c (chan)
        success (fn [position] (go (>! c {:lon (.-longitude js/position.coords)
                                          :lat (.-latitude js/position.coords)})))]
    (.getCurrentPosition (.-geolocation js/navigator) success) c))

(defn fetch-gas-stations
  [url query-params]
  (let [c (chan)]
    (go
      (let [{gas-stations :body} (<! (http/get url {:query-params query-params}))]
        (>! c (nth (nth (vec gas-stations) 6) 1))))
    c))

(defn gas-station-line [kv-pair]
  (let [span (partial dom/span nil)
        name (-> kv-pair key name string/capitalize (str ": "))
        val (val kv-pair)]
    (om/component
      (dom/li
        #js {:className "name-value-pair"}
        (span name) (span val)))))

(defn gas-station [props]
  (let [gas-station-props (select-keys props [:name :city :address :distance])]
    (om/component
      (dom/li
        #js {:className "gas-station"}
        (apply dom/ul nil (om/build-all gas-station-line gas-station-props))))))

(defn gas-stations-list [{:keys [gas-stations]}]
  (om/component
    (apply dom/ul nil (om/build-all gas-station gas-stations))))

(defn gas-stations-box [app _ {:keys [url params]}]
  (reify
    om/IWillMount
    (will-mount [_]
      (om/transact! app [:gas-stations] (fn [] []))
      (go
        (let [pos-params (<! (get-position))
              gas-stations (<! (fetch-gas-stations url (merge params pos-params)))]
          (om/update! app (assoc app :gas-stations gas-stations)))))
    om/IRender
    (render [_]
      (dom/h2 #js {:className "title"} "Бензиностанции"
              (om/build gas-stations-list app)))))

(defn om-app [app _]
  (om/component
    (dom/div nil
             (om/build gas-stations-box app
                       {:opts {:url    "/rest/fuel-near-me"
                               :params {:lon      25.55
                                        :lat      43.45
                                        :limit    50
                                        :distance 50
                                        :fuel     "lpg"}}}))))

(om/root om-app app-state {:target (.getElementById js/document "content")})