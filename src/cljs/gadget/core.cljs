(ns gadget.core
  (:require
   [day8.re-frame.http-fx]
   [reagent.dom :as rdom]
   [reagent.core :as r]
   [re-frame.core :as rf]
   [goog.events :as events]
   [goog.history.EventType :as HistoryEventType]
   [gadget.ajax :as ajax]
   [gadget.events]
   [gadget.components :refer [toast]]
   [reitit.core :as reitit]
   [reitit.frontend.easy :as rfe]
   [clojure.string :as string])
  (:import goog.History))

(defn nav-link [uri title page]
  [:a.navbar-item
   {:href   uri
    :class (when (= page @(rf/subscribe [:common/page-id])) :is-active)}
   title])

(defn navbar []
  (r/with-let [expanded? (r/atom false)]
    [:nav.navbar.is-info>div.container
     [:div.navbar-brand
      [:a.navbar-item {:href "/" :style {:font-weight :bold}} "gadget"]
      [:span.navbar-burger.burger
       {:data-target :nav-menu
        :on-click #(swap! expanded? not)
        :class (when @expanded? :is-active)}
       [:span] [:span] [:span]]]
     [:div#nav-menu.navbar-menu
      {:class (when @expanded? :is-active)}
      [:div.navbar-start
       [nav-link "#/" "Clipboard" :clipboard]
       [nav-link "#/about" "About" :about]]]]))

(defn about-page []
  [:section.section>div.container>div.content
   [:img {:src "/img/warning_clojure.png"
          :on-click #(rf/dispatch [:fetch-hello])}]
   (when-let [hello @(rf/subscribe [:hello])]
     [:h1 (:data  hello) " " (:time hello)])])

(defn clipboard-page []
  [:section.section>div.container>div.content
   [:h2 "Clipboard"]
   [:form {:style {:display "flex" :margin "2rem auto"}}
    [:label "Text to send:"]
    [:input.input {:type "text"
                   :on-change #(rf/dispatch [:set-clipboard-text (-> % .-target .-value)])
                   :on-key-down #(when (= 13 (.-keyCode %))
                                   (rf/dispatch [:send-clipboard-text]))}]
    [:button.button {:type "button"
                     :on-click #(rf/dispatch [:send-clipboard-text])} "Send"]]
   (when-let [toast-data @(rf/subscribe [:toast])]
     (toast (:msg toast-data)))
   (when-let [clipboard @(rf/subscribe [:clipboard])]
     [:div {:on-click #(rf/dispatch [:copy-clipboard])}
      [:h2 "Tap contents or here to copy"]
      [:div#clipboard {:dangerouslySetInnerHTML
                       {:__html (string/replace
                                 clipboard
                                 #"(\r?\n)"
                                 "<br/>")}}]])])

(defn page []
  (if-let [page @(rf/subscribe [:common/page])]
    [:div
     [navbar]
     [page]]))

(defn navigate! [match _]
  (rf/dispatch [:common/navigate match]))

(def router
  (reitit/router
   [["/" {:name :clipboard
          :view #'clipboard-page
          :controllers [{:start (fn [_] (rf/dispatch [:page/init-clipboard]))}]}]
    ["/about" {:name :about
               :view #'about-page}]]))

(defn start-router! []
  (rfe/start!
    router
    navigate!
    {}))

;; -------------------------
;; Initialize app
(defn ^:dev/after-load mount-components []
  (rf/clear-subscription-cache!)
  (rdom/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (start-router!)
  (ajax/load-interceptors!)
  (mount-components))
