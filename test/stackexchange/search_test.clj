(ns stackexchange.search-test
  (:require [stackexchange.search :as sut]
            [clojure.test :refer [deftest is]]))

(def clojure-resp
  {:status 200,
   :body
   {:items
    [{:tags ["clojure"],
      :answer_count 0,
      :is_answered false}
     {:tags ["function" "clojure"],
      :answer_count 3,
      :is_answered true}
     {:tags ["function" "random" "clojure"],
      :answer_count 1,
      :is_answered true}]}})


(deftest process-response
  (is (= {:total-tags 6 :answers 4}
         (sut/process-response clojure-resp))))
