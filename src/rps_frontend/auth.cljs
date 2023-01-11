(ns rps-frontend.auth)

(def token-storage-key "user-token")

(defn get-token []
  (if-let [existing-token (.getItem js/localStorage token-storage-key)]
    existing-token
    (let [new-token (.randomUUID js/crypto)]
      (.setItem js/localStorage token-storage-key new-token)
      new-token)))