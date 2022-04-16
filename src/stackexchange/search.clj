(ns stackexchange.search
  (:require [clj-http.client :as client]
            [com.climate.claypoole :as cp]))

(defn search-req [tag]
  (try (client/get "https://api.stackexchange.com/2.3/search"
                   {:query-params {:page-size 100
                                   :order "desc"
                                   :sort "creation"
                                   :tagged tag
                                   :site "stackoverflow"}
                    :as :json})
       (catch Exception e
         (throw (ex-info (ex-message e)
                         {:message "Stackexchange search failed"
                          :data {:tag tag}}
                         e)))))

(defn process-response [resp]
  (let [items (-> resp :body :items)
        total-tags (count (mapcat :tags items))
        answered (filter :is_answered items)
        answer-count-sum (->> answered
                              (map :answer_count)
                              (reduce +))]
    {:total-tags total-tags
     :answers answer-count-sum}))

(defn search [{:keys [params pool-size start-time-ms] :as req}]
  (if-let [tags (:tag params)]
    (let [stats (if (coll? tags)
                  (cp/with-shutdown! [pool (cp/threadpool (or pool-size 1))]
                    (let [resps-data (cp/upmap pool (fn [tag]
                                                      [tag (process-response (search-req tag))]) tags)]
                      (into {} resps-data)))
                  {tags (process-response (search-req tags))})
          end-time-ms (System/currentTimeMillis)]
      {:status 200
       :body (assoc stats :spent-ms (- end-time-ms start-time-ms))})
    {:status 200
     :body {:message "No tags in request"}}))

