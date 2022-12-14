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

(def is-mac?
  (= "Mac OS X" (System/getProperty "os.name" "unknown")))

(def clipboard (atom ""))

(def api-routes
  ["/api"
   {:middleware [wrap-formats]}
   ["/hello" {:get (fn [_]
                     {:status 200
                      :body {:message "hello"}})}]
   ["/clipboard" {:get (fn [_]
                         (when is-mac?
                           (reset! clipboard (clipboard-get)))
                         {:status 200
                          :body {:text @clipboard}})
                  :post (fn [req]
                          (let [text (get-in req [:params :text])]
                            (when is-mac?
                              (clipboard-set text))
                            (reset! clipboard text)
                            {:status 200
                             :body {:text text}}))}]])
