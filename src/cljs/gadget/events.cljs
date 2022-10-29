(ns gadget.events
  (:require
   [re-frame.core :as rf]
   [ajax.core :as ajax]
   [ajax.edn :refer [edn-response-format]]
   [reitit.frontend.easy :as rfe]
   [reitit.frontend.controllers :as rfc]))

;;dispatchers

(rf/reg-event-db
  :common/navigate
  (fn [db [_ match]]
    (let [old-match (:common/route db)
          new-match (assoc match :controllers
                                 (rfc/apply-controllers (:controllers old-match) match))]
      (assoc db :common/route new-match))))

(rf/reg-fx
  :common/navigate-fx!
  (fn [[k & [params query]]]
    (rfe/push-state k params query)))

(rf/reg-event-fx
  :common/navigate!
  (fn [_ [_ url-key params query]]
    {:common/navigate-fx! [url-key params query]}))

(rf/reg-event-db
 :update-hello
 (fn [db [_ data]]
   (println "update-hello" data)
   (let [time (-> db :hello :time (or 0))]
     (assoc db :hello {:data (:message data)
                       :time (inc time)}))))

(rf/reg-event-fx
 :fetch-hello
 (fn [_ _]
   {:http-xhrio {:method :get
                 :uri "/api/hello"
                 :response-format (edn-response-format)
                 :on-success [:update-hello]}}))

(rf/reg-event-db
 :api-error
 (fn [db [_ error]]
   {:dispatch [:toast-message (str "API error: " error)]}))

(rf/reg-event-db
 :common/set-error
 (fn [db [_ error]]
   (assoc db :common/error error)))

(rf/reg-event-db
 :toast-message
 (fn [db [_ message]]
   (let [id (-> db :toast-message :id (or 0))]
     (assoc db :toast-message {:msg message
                               :id (inc id)}))))

;;subscriptions

(rf/reg-sub
 :common/route
 (fn [db _]
   (-> db :common/route)))

(rf/reg-sub
  :common/page-id
  :<- [:common/route]
  (fn [route _]
    (-> route :data :name)))

(rf/reg-sub
  :common/page
  :<- [:common/route]
  (fn [route _]
    (-> route :data :view)))

(rf/reg-sub
 :hello
 (fn [db _]
   (:hello db)))

(rf/reg-sub
 :clipboard
 (fn [db _]
   (:clipboard db)))

(rf/reg-sub
 :toast
 (fn [db _]
   (:toast-message db)))

(rf/reg-sub
 :common/error
 (fn [db _]
   (:common/error db)))
