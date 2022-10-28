(ns gadget.components)

(defn toast [message]
  (js/setTimeout #(let [cl (-> js/document
                               (.getElementById "toast")
                               .-classList)]
                    (.add cl "show")
                    (js/setTimeout (fn [] (.remove cl "show"))
                                   1000)) 0)
  [:div#toast.show
   [:div.toast-message message]])
