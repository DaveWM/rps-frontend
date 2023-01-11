(ns rps-frontend.config
  (:require-macros [adzerk.env :as env]))

(def debug?
  ^boolean goog.DEBUG)

(goog-define BACKEND_HOST "localhost")

(goog-define BACKEND_PORT 8082)

(println BACKEND_HOST BACKEND_PORT)
