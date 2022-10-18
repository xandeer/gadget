(ns gadget.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[gadget started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[gadget has shut down successfully]=-"))
   :middleware identity})
