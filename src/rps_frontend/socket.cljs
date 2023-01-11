(ns rps-frontend.socket
  (:require [taoensso.sente :as sente]
            [re-frame.core :as re-frame]
            [cljs.core.async :as a]
            [rps-frontend.auth :as auth]
            [rps-frontend.config :as config]))

(let [{:keys [chsk ch-recv send-fn state]}
      (sente/make-channel-socket-client!
        "/chsk"
        nil
        {:type   :auto
         :port   config/BACKEND_PORT
         :params {:user (auth/get-token)}
         :host   config/BACKEND_HOST})]

  (def chsk chsk)
  (def ch-chsk ch-recv)                                     ; ChannelSocket's receive channel
  (def send! send-fn)                                       ; ChannelSocket's send API fn
  (def chsk-state state)                                    ; Watchable, read-only atom
  )

(defn event-loop
  "Handle inbound events."
  []
  (a/go
    (loop [[op arg :as evt] (:event (a/<! ch-chsk))]
      (println "EVT" evt)
      (case op
        :chsk/state (let [[{was-open? :open?} {is-open? :open?}] arg]
                      (when (not= was-open? is-open?)
                        (re-frame/dispatch (if is-open?
                                             [:rps-frontend.events/socket-open]
                                             [:rps-frontend.events/socket-closed]))))
        :chsk/recv (if (= :chsk/ws-ping (first arg))
                     (send! [:channel/ws-pong])
                     (re-frame/dispatch arg))
        nil)
      (recur (:event (a/<! ch-chsk))))))

(event-loop)