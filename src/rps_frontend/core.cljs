(ns rps-frontend.core
  (:require
    [re-frame.core :as re-frame]
    [reagent.dom :as rdom]
    [rps-frontend.config :as config]
    [rps-frontend.events :as events]
    [rps-frontend.routes :as routes]
    [rps-frontend.views :as views]
    [rps-frontend.fx]
    [rps-frontend.cofx]
    [rps-frontend.socket]))


(defn dev-setup []
  (when config/debug?
    (println "dev mode")))

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render [views/main-panel] root-el)))

(defn init []
  (routes/start!)
  (re-frame/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (mount-root))
