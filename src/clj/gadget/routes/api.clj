(ns gadget.routes.api
  (:refer-clojure)
  (:import (java.awt Toolkit)
           (java.awt.datatransfer DataFlavor Transferable StringSelection))
  (:require
   [gadget.middleware :refer [wrap-formats]]
   [clojure.tools.logging :as log]))

(defn get-clipboard
  []
  (-> (Toolkit/getDefaultToolkit)
      (.getSystemClipboard)))

(defn clipboard-get
  []
  (when-let [^Transferable clip-text (some-> (get-clipboard)
                                             (.getContents nil))]
    (when (.isDataFlavorSupported clip-text DataFlavor/stringFlavor)
      (.getTransferData clip-text DataFlavor/stringFlavor))))

(defn clipboard-set
  [s]
  (let [sel (StringSelection. s)]
    (some-> (get-clipboard)
            (.setContents sel sel))))

(def api-routes
  ["/api"
   {:middleware [wrap-formats]}
   ["/hello" {:get (fn [_]
                     {:status 200
                      :body {:message "hello"}})}]
   ["/clipboard" {:get (fn [_]
                         {:status 200
                          :body {:text (clipboard-get)}})
                  :post (fn [req]
                          (let [text (get-in req [:params :text])]
                            (log/info "params: " text)
                            (clipboard-set text)
                            {:status 200
                             :body {:text (clipboard-get)}}))}]])
