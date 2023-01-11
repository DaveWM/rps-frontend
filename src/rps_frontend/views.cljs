(ns rps-frontend.views
  (:require
    [clojure.string :as str]
    [re-frame.core :as re-frame]
    [rps-frontend.events :as events]
    [rps-frontend.subs :as subs]
    [rps-frontend.styles :as styles]
    ["random-token" :as random-token]))

(defmulti page (fn [{:keys [page]}]
                 page))

(defmethod page :home [_]
  [:div.uk-flex.uk-flex-center
   [:a.uk-button.uk-button-primary.uk-button-large
    {:href (str "/game/" (random-token 16))}
    "New Game"]])

;; about

(defn about-panel []
  [:div
   [:h1 "About"]
   [:p "This is a "]])

(defn choice-icon [choice & classes]
  (let [icon-name (get {:choice/rock     "hand-rock"
                        :choice/paper    "hand-paper"
                        :choice/scissors "hand-scissors"}
                       choice)]
    [:i {:class (concat ["fa-regular"
                         (str "fa-" icon-name)]
                        classes)}]))

(defn player-avatar [player-name & [{:keys [size] :or {size 50}}]]
  [:img
   {:src         (str "https://avatars.dicebear.com/api/gridy/" player-name ".svg?size=" size "&mood=happy")
    "uk-tooltip" (str "title: " player-name)}])

(defmethod page :about [_]
  [about-panel])

(defmethod page :game [{{game-name :name} :params}]
  (let [game-sub (re-frame/subscribe [::subs/game game-name])]
    [:<>
     (if (:winner @game-sub)
       [:div.uk-flex.uk-flex-middle
        [:span.uk-heading-medium.uk-flex-1 "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89"]
        [:div.uk-flex.uk-flex-column.uk-flex-middle
         [player-avatar (:name (:winner @game-sub)) {:size 150}]
         [:span.uk-text-large (:name (:winner @game-sub))]]
        [:span.uk-heading-medium.uk-flex-1 {:style {:transform "scale(-1,1)"}} "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89"]]
       [:<>
        [:div
         [:div.uk-flex.uk-flex-middle
          [:span.uk-text-muted.uk-margin-right "Players"]
          (->> (:players @game-sub)
               (map (fn [player]
                      [player-avatar (:name player)])))]
         [:hr]]
        (if (:user-playing? @game-sub)
          [:div
           [:h3 (str "Round " (inc (:current-round-index @game-sub)))]
           [:div.uk-grid-medium.uk-grid-match {"uk-grid" ""
                                               :class    "uk-child-width-expand@s"}
            (->> [:choice/rock
                  :choice/paper
                  :choice/scissors]
                 (map (fn [choice]
                        [:div
                         [:div.uk-card.uk-card-hover.uk-card-body.uk-text-center
                          {:class    (if (= choice (:current-round-choice @game-sub))
                                       "uk-card-primary"
                                       "uk-card-default")
                           :on-click #(re-frame/dispatch [::events/choice-made game-name choice])}
                          [:h3.uk-card-title (str/capitalize (name choice))]
                          [choice-icon choice "fa-5x"]]])))]]
          [:h4 "You're Out! Game still in progress..."])])
     (when (seq (:previous-rounds @game-sub))
       [:div.uk-margin-top
        [:hr]
        [:h3 "Previous Rounds"]
        (->> (:previous-rounds @game-sub)
             (map (fn [round]
                    [:div.uk-flex.uk-flex-middle.uk-card.uk-card-default.uk-padding-small
                     [:h4.uk-margin-right.uk-margin-remove-bottom (str "Round " (inc (:index round)))]
                     (->> (:_round round)
                          (map (fn [{:keys [choice player]}]
                                 [:div.uk-flex.uk-flex-middle
                                  [player-avatar (:name player)]
                                  [choice-icon choice "fa-3x"]]))
                          (interpose [:hr.uk-divider-vertical.uk-margin-left.uk-margin-right
                                      ]))
                     [:span.uk-flex-1]])))])]))

(defmethod page :loading [_]
  [:span "Loading..."])

(defmethod page :default [_]
  [:p "Page not found"])

;; main

(defn main-panel []
  (let [active-page (re-frame/subscribe [::subs/active-page])
        player (re-frame/subscribe [::subs/player])]
    [:div
     [:div.uk-navbar-container {"uk-navbar" ""
                                :class (styles/app-bar)}
      [:div.uk-flex-1]
      [:a.uk-navbar-item.uk-logo {:href "/"}
       "Rock Paper Scissors"]
      [:div.uk-flex-1.uk-flex.uk-flex-middle.uk-flex-right
       [:span.uk-navbar-item.uk-margin-right
        [:a.uk-button.uk-button-default {:style {:background-color "white"}
                                         :href  (str "/game/" (random-token 16))}
         "New Game"]]
       (when @player
         [:span.uk-navbar-item.uk-margin-right
          [player-avatar (:name @player) {:size 35}]])
       [:span.uk-navbar-item.uk-margin-right
        [:a {:href   "https://davemartin.me/"
             :target "_blank"
             :class  (styles/round-image-button "/images/logo.png" 35)}]]]]
     [:div.uk-container.uk-padding
      (page @active-page)]]))
