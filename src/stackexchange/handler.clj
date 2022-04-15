(ns stackexchange.handler
  (:require [reitit.ring :as r]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.params :refer [wrap-params]]
            [stackexchange.search :refer [search]]))


(defn wrap-start-time-ms
  [handler]
  (fn [req]
    (handler (assoc req :start-time-ms (System/currentTimeMillis)))))


(def handler
  (r/ring-handler
   (r/router
    ["/search" {:get search}])
   (r/routes
    (r/create-default-handler
     {:not-found (constantly {:status 404 :body "Not found"})}))))


(def app-handler
  (-> handler
      (wrap-json-response {:pretty true})
      (wrap-keyword-params)
      (wrap-params)
      (wrap-start-time-ms)))


(comment
  (time
   (app-handler {:request-method :get
                 :uri "/search"
                 :query-string "tag=cpp&tag=clojure&tag=javascript&tag=php&tag=java&tag=purescript&tag=ruby"
                 :pool-size 1})
   )
  ;; => "Elapsed time: 8169.954815 msecs"

  (time
   (app-handler {:request-method :get
                 :uri "/search"
                 :query-string "tag=cpp&tag=clojure&tag=javascript&tag=php&tag=java&tag=purescript&tag=ruby"
                 :pool-size 2})
   )
  ;; => "Elapsed time: 3160.608194 msecs"

  (time
   (app-handler {:request-method :get
                 :uri "/search"
                 :query-string "tag=cpp&tag=clojure&tag=javascript&tag=php&tag=java&tag=purescript&tag=ruby"
                 :pool-size 4})
   )
  ;; => "Elapsed time: 1399.67507 msecs"

  (time
   (app-handler {:request-method :get
                 :uri "/search"
                 :query-string "tag=cpp&tag=clojure&tag=javascript&tag=php&tag=java&tag=purescript&tag=ruby"
                 :pool-size 10})
   )
  ;; => "Elapsed time: 685.870774 msecs"

)
