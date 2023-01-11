(ns rps-frontend.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::name
 (fn [db]
   (:name db)))

(re-frame/reg-sub
 ::active-page
 (fn [db _]
   (println (:socket-open? db))
   (if (:socket-open? db)
     (:active-page db)
     {:page :loading})))

(re-frame/reg-sub
  ::game
  (fn [db [_ game-name]]
    (get-in db [:games game-name])))

(re-frame/reg-sub
  ::player
  (fn [db _]
    (:player db)))
