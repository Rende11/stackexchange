(ns stackexchange.search
  (:require [clj-http.client :as client]))

(defn stackexchange-search [tag]
  (let [resp (try (client/get "https://api.stackexchange.com/2.3/search"
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
                                    e))))
        items (-> resp :body :items)
        answered (filter :is_answered items)
        answer-count-sum (->> answered
                              (map :answer_count)
                              (apply +))]
    {:total (count answered)
     :answered answer-count-sum}))



(defn search [{:keys [params] :as req}]
  (let [stats (reduce (fn [acc tag]
                        (assoc acc tag (stackexchange-search tag)))
                      {}
                      (:tag params))]
    {:status 200
     :body stats}))
