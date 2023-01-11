(ns rps-frontend.events
  (:require
    [re-frame.core :as re-frame]
    [rps-frontend.db :as db]
    [rps-frontend.fx]
    [rps-frontend.cofx :as cofx]
    [day8.re-frame.tracing :refer-macros [fn-traced]]))

(re-frame/reg-event-db
  ::initialize-db
  (fn-traced [_ _]
    db/default-db))

(re-frame/reg-event-fx
  ::navigate
  (fn-traced [_ [_ handler]]
    {:navigate handler}))

(defmulti new-page-fx (fn [page-data] (:page page-data)))

(defmethod new-page-fx :game [{:keys [params]}]
  {:socket/subscribe [{:sub         [:game (:name params)]
                       :on-complete ::game-updated}]})

(defmethod new-page-fx :default [_]
  nil)

(defmulti leave-page-fx (fn [page-data] (:page page-data)))

(defmethod leave-page-fx :game [{:keys [params]}]
  {:socket/unsubscribe [{:sub [:game (:name params)]}]})

(defmethod leave-page-fx :default [_]
  nil)

(re-frame/reg-event-db
  ::game-updated
  (fn [db [_ {sub :sub game :data}]]
    (println sub)
    (assoc-in db [:games (:name game)] game)))

(re-frame/reg-event-db
  ::user-updated
  (fn [db [_ {player :data}]]
    (assoc db :player player)))

(defmulti update-db (fn [_ {[sub-type] :sub}] sub-type))

(defmethod update-db :game [db {game :data}]
  (assoc-in db [:games (:name game)] game))

(defmethod update-db :player [db {player :data}]
  (assoc db :player player))

(defmethod update-db :default [_ _] nil)

(re-frame/reg-event-db
  :rps.server/push
  (fn [db [_ evt]]
    (update-db db evt)))

(re-frame/reg-event-fx
  ::set-active-page
  (fn-traced [{:keys [db]} [_ active-page params]]
    (let [new-page {:page   active-page
                    :params params}
          old-page (:active-page db)]
      (merge
        {:db (assoc db :active-page new-page)}
        (when (:socket-open? db)
          (new-page-fx new-page))
        (when (:socket-open? db)
          (leave-page-fx old-page))))))

(re-frame/reg-event-fx
  ::socket-open
  [(re-frame/inject-cofx ::cofx/user-token)]
  (fn-traced [{:keys [db user-token] :as xs} [_ active-page params]]
    (println xs)
    (merge-with concat
                {:db (assoc db :socket-open? true)}
                (new-page-fx (:active-page db))
                {:socket/subscribe [{:sub [:player user-token]
                                     :on-complete ::user-updated}]})))

(re-frame/reg-event-fx
  ::socket-closed
  (fn-traced [{:keys [db]} _]
    {:db (assoc db :socket-open? false)}))

(re-frame/reg-event-fx
  ::choice-made
  (fn-traced [{:keys [db]} [_ game-name choice]]
    {:db                db
     :socket/send-event {:event :game/select-choice
                         :data  {:game-name game-name
                                 :choice    choice}}}))
