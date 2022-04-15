(ns stackexchange.core
  (:require [donut.system :as ds]
            [ring.adapter.jetty :as jetty]
            [stackexchange.handler :as handler]))


(def system
  {::ds/defs
   {:env {:http-port (or (some-> (System/getenv "PORT")
                                 (Integer/parseInt))
                         8080)
          :pool-size (or (some-> (System/getenv "POOL_SIZE")
                                 (Integer/parseInt))
                         4)}

    :web {:server {:start (fn [{:keys [handler options]} _ _]
                            (jetty/run-jetty handler options))
                   :after-start (fn [{:keys [options]} _ _]
                                  (println (format "Server launched on port - %s" (:port options))))
                   :stop (fn [_ instance _]
                           (.stop instance))
                   :conf {:handler (ds/ref [:web :handler])
                          :options {:join? false
                                    :port (ds/ref [:env :http-port])}}}

          :handler {:start (fn [{:keys [pool-size]} _ _]
                             (fn [req]
                               (handler/app-handler (assoc req :pool-size pool-size))))
                    :conf {:pool-size (ds/ref [:env :pool-size])}}}}})

(defn -main [& _args]
  (ds/signal system :start))

(comment
  (def app
    (ds/signal system :start))

  (ds/signal app :stop)
)
