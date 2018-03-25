(ns clojure_front_end_app.core
  (:require-macros [cljs.core.async.macros :refer [go alt!]])
  (:require [goog.events :as events]
            [clojure.string :as string]
            [cljs.core.async :refer [put! <! >! chan timeout]]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs-http.client :as http]
            [clojure.string :as string]))

(enable-console-print!)

(defn fetch-gas-stations
  [url]
  (let [c (chan)]
    (go (let [{gas-stations :body} (<! (http/get url))]
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

(defn gas-stations-box [app _ {:keys [url poll-interval]}]
  (reify
    om/IWillMount
    (will-mount [_]
      (om/transact! app [:gas-stations] (fn [] []))
      (go (while true
            (let [gas-stations (<! (fetch-gas-stations url))]
              (println gas-stations)
              (om/update! app (assoc app :gas-stations gas-stations)))
            (<! (timeout poll-interval)))))
    om/IRender
    (render [_]
      (dom/h1 nil "Бензиностанции")
      (om/build gas-stations-list app))))

(defn om-app [app _]
  (om/component
    (dom/div nil
             (om/build gas-stations-box app
                       {:opts {:url           "/rest/fuel-near-me?lat=42.6713757&lon=23.2672629&limit=10&distance=10&fuel=lpg"
                               :poll-interval 1000000}}))))

(def app-state
  (atom {}))

(om/root om-app app-state {:target (.getElementById js/document "content")})