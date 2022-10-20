(ns gadget.routes.api
  (:require
   [gadget.middleware :refer [wrap-formats]]))

(def api-routes
  ["/api"
   {:middleware [wrap-formats]}
   ["/hello" {:get (fn [_]
                     {:status 200
                      :body {:message "hello"}})}]])
