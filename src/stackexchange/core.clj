(ns stackexchange.core
  (:require [donut.system :as ds]
            [ring.adapter.jetty :as jetty]
            [stackexchange.handler :as handler]))


(def system
  {::ds/defs
   {:env {:http-port 4321}
    :web {:server {:start (fn [{:keys [handler options]} _ _]
                            (jetty/run-jetty handler options))
                   :after-start (fn [{:keys [options]} _ _]
                                  (println (format "Server launched on port - %s" (:port options))))
                   :stop (fn [_ instance _]
                           (.stop instance))
                   :conf {:handler (ds/ref [:web :handler])
                          :options {:join? false
                                    :port (ds/ref [:env :http-port])}}}
          
          :handler #'handler/app-handler}}})

(defn -main [& _args]
  (ds/signal system :start))

(comment
  (def app
    (ds/signal system :start))
  
  (ds/signal app :stop)

  ;; Logger
  ;; Router
  ;; Use envs

  )
