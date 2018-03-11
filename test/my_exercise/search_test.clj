(ns my-exercise.search-test
  (:require [clojure.test :refer [deftest testing is]]
            [my-exercise.search :as search]))


(deftest format-address
  (testing "address with all fields"
    (is (= (search/format-address
             {:street "20 Jay St"
              :street-2 "Suite 824"
              :city "Brooklyn"
              :state "NY"
              :zip "11201"})
          [:div
           [:div "20 Jay St"]
           [:div "Suite 824"]
           [:div "Brooklyn, NY 11201"]]))))
