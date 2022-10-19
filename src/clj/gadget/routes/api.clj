(ns gadget.routes.api
  (:require
   [gadget.middleware :as middleware]
   [ring.util.http-response :as response]))

(defn api-routes []
  [""
   {:middleware [middleware/wrap-formats]}
   ["/api/hello" {:get (fn [_]
                         (-> (response/ok "hello")
                             (response/header "Content-Type" "text/plain; charset=utf-8")))}]])
