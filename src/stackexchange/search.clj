(ns stackexchange.search
  (:require [clj-http.client :as client]))

(defn search [{:keys [params] :as req}]
  {:status 200
   :body {:message "My search"
          :params params}})



