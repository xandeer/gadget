(ns gadget.pages.clipboard
  (:require
   [re-frame.core :as rf]
   [gadget.components :refer [toast]]
   [clojure.string :as string]
   [ajax.core :as ajax]
   [ajax.edn :refer [edn-response-format]]))

(defn clipboard-page []
  [:section.section>div.container>div.content
   [:h2 "Clipboard"]
   [:form {:style {:display "flex" :margin "2rem auto"}}
    [:label "Text to send:"]
    [:input.input {:type "text"
                   :on-change #(rf/dispatch [:set-clipboard-text (-> % .-target .-value)])
                   :on-key-down #(when (= 13 (.-keyCode %))
                                   (.preventDefault %)
                                   (rf/dispatch [:send-clipboard-text]))}]
    [:button.button {:type "button"
                     :on-click #(rf/dispatch [:send-clipboard-text])} "Send"]]
   (when-let [toast-data @(rf/subscribe [:toast])]
     (toast (:msg toast-data)))
   (when-let [clipboard @(rf/subscribe [:clipboard])]
     [:div
      [:h2 {:on-click #(rf/dispatch [:fetch-clipboard])} "Tap here to refetch, tap contents to copy"]
      [:div#clipboard {:on-click #(rf/dispatch [:copy-clipboard])
                       :dangerouslySetInnerHTML
                       {:__html (string/replace
                                 clipboard
                                 #"(\r?\n)"
                                 "<br/>")}}]])])

(rf/reg-event-fx
 :page/init-clipboard
 (fn [_ _]
   {:dispatch [:fetch-clipboard]}))

;;; fetch from remote
(rf/reg-event-fx
 :fetch-clipboard
 (fn [_ _]
   {:http-xhrio {:method :get
                 :uri "/api/clipboard"
                 :response-format (edn-response-format)
                 :on-success [:update-clipboard]}}))

(rf/reg-event-db
 :update-clipboard
 (fn [db [_ data]]
   (assoc db :clipboard (:text data))))

;;; send to remote
(rf/reg-event-fx
 :set-clipboard-text
 (fn [_ [_ text]]
   {:dispatch [:update-send-text {:text text}]}))

(rf/reg-event-db
 :update-send-text
 (fn [db [_ data]]
   (assoc db :send-text data)))

(rf/reg-event-fx
 :send-clipboard-text
 (fn [{:keys [db]} [_ _]]
   (when-let [text (:send-text db)]
     {:http-xhrio {:method :post
                   :uri "/api/clipboard"
                   :params text
                   :response-format (edn-response-format)
                   :format (ajax/json-request-format)
                   :on-success [:update-clipboard]
                   :on-failure [:api-error {:error "send-clipboard"}]}})))

;;; try to copy
(rf/reg-event-fx
 :copy-clipboard
 (fn [_ _]
   (-> js/window
       .getSelection
       (.selectAllChildren
        (.getElementById js/document "clipboard")))
   (when (.queryCommandEnabled js/document "copy")
     (-> js/document
         (.execCommand "copy"))
     (-> js/window
         .getSelection
         (.removeAllRanges))
     {:dispatch [:toast-message "Copied to clipboard!"]})))
