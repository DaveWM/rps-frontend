(ns rps-frontend.styles
  (:require
    [garden.units :refer [deg px percent]]
    [spade.core :refer [defclass defglobal]]))

(defglobal defaults
  ["@font-face" [:font-family "'Uni Neue'"
                 :src "local ('Uni Neue Regular'), local ('UniNeueRegular'),
   url ('/fonts/UniNeueRegular.eot?#iefix') format ('embedded-opentype'),
   url ('/fonts/UniNeueRegular.woff2') format ('woff2'),
   url ('/fonts/UniNeueRegular.woff') format ('woff'),
   url ('/fonts/UniNeueRegular.ttf') format ('truetype')"
                 :font-weight 500
                 :font-style "normal"]]
  [:body
   {:height      (percent 100)
    :font-family "'Roboto', sans-serif"}]
  [:html
   {:height (percent 100)}]
  [:body :p :h1 :h2 :h3 :h4 :h5 :h6 :a :button
   {:font-family "'Uni Neue', \"Source Sans Pro\", \"Kozuka Gothic Pr6N\", Meiryo, sans-serif !important"}])

(defclass app-bar []
  {:display :flex
   :flex-direction "row"
   :justify-content "center"
   :background-color "#0072BB !important"}
  [:.uk-logo {:color       "white !important"
              :font-family "'Uni Neue', \"Source Sans Pro\", \"Kozuka Gothic Pr6N\", Meiryo, sans-serif !important"}])

(defclass round-image-button [background-image size]
  {:display             "block"
   :height              (px size)
   :width               (px size)
   :border-radius       "50%"
   :background-image    (str "url('" background-image "')")
   :background-size     "contain"
   :background-repeat   "no-repeat"
   :background-position "center"
   :background-color    "white"})