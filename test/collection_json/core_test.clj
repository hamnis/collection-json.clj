(ns collection-json.core-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [collection-json.core :refer :all]))

(def not-nil? (complement nil?))

(deftest parse-coll
  (testing "parse collection"
    (testing "from string"
      (let [ c (parse-collection (slurp "resources/item.json"))]
      (is (not-nil? c)))
    (testing "from file"
      (let [ c (parse-collection (io/file "resources/item.json"))]
      (is (not-nil? c))))
    )))

(deftest check-item-coll
  (let [ c (parse-collection (io/reader "resources/item.json"))]
    (testing "items"    
      (is (= 1 (count (items c))))
      (is (not-nil? (head-item c)))
    )
    (testing "links in collection"
      (is (= 3 (count (:links c))))
      (is (not-nil? (link-by-rel c "feed")))
      )
    (testing "links in item"
      (is (= 2 (count (:links (head-item c)))))
      (is (not-nil? (link-by-rel (head-item c) "blog")))
      )))
