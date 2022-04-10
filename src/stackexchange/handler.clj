(ns stackexchange.handler
  (:require [reitit.ring :as r]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.params :refer [wrap-params]]
            [stackexchange.search :refer [search]]))


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
      (wrap-params)))


(comment
  (app-handler {:request-method :get
                :uri "/search"
                :query-string "tag=cpp&tag=clojure"})
)

