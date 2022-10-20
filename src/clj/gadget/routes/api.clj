(ns gadget.routes.api
  (:require
   [gadget.middleware :as middleware]
   [ring.util.http-response :as response]))

(def api-routes
  ["/api"
   {:middleware [middleware/wrap-formats]}
   ["/hello" {:get (fn [_]
                     {:status 200
                      :body {:message "hello"}})}]])
