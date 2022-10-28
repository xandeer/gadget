(ns gadget.components)

(defn toast [message]
  (-> js/window
      (.setTimeout #(let [el (-> js/document
                                 (.getElementById "toast"))]
                      (-> el
                          .-classList
                          (.add "show"))
                      (.setTimeout js/window (fn [] (-> el
                                                        (.-classList)
                                                        (.remove "show")))
                                   1000)) 0))
  [:div#toast.show
   [:div.toast-message message]])
