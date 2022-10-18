(ns gadget.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [gadget.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[gadget started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[gadget has shut down successfully]=-"))
   :middleware wrap-dev})
