(ns stackexchange.handler
  (:require [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.json :refer [wrap-json-response]]))

(defn handler [req]
  {:status 200
   :body {:message "My handler"}})


(def app-handler
  (-> handler
      (wrap-json-response {:pretty true})
      (wrap-keyword-params)
      (wrap-params)))

