(ns rps-frontend.routes
  (:require
   [bidi.bidi :as bidi]
   [pushy.core :as pushy]
   [re-frame.core :as re-frame]
   [rps-frontend.events :as events]))

(def routes
  ["/" {""         :home
        "about"    :about
        ["game/" :name] :game}])

(defn parse
  [url]
  (bidi/match-route routes url))

(defn url-for
  [& args]
  (apply bidi/path-for (into [routes] args)))

(defn dispatch
  [route]
  (let [panel (:handler route)
        params (:route-params route)]
    (re-frame/dispatch [::events/set-active-page panel params])))

(defonce history
  (pushy/pushy dispatch parse))

(defn navigate!
  [handler]
  (pushy/set-token! history (url-for handler)))

(defn start!
  []
  (pushy/start! history))

(re-frame/reg-fx
  :navigate
  (fn [handler]
    (navigate! handler)))
