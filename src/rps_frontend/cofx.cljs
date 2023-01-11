(ns rps-frontend.cofx
  (:require [rps-frontend.auth :as auth]
            [re-frame.cofx :refer [reg-cofx]]))

(reg-cofx
  ::user-token
  (fn [coeffects _]
    (assoc coeffects :user-token (auth/get-token))))