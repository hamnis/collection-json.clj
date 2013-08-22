(ns collection-json.core 
  (:import
    [net.hamnaberg.json.parser CollectionParser]
    [net.hamnaberg.json.util Optional]
    [net.hamnaberg.json Template Property]
  ) 
  (:use 
    [clojure.java.io]
  ))

(defn beanify [input]
  (cond
    (map? input) input
    :else (map bean input)))

(defn parse-collection [input] 
  (let [b (. (new CollectionParser) parse (reader input))]
    (assoc (bean b) :underlying b)))

(defn parse-template [input] 
  (let [b (. (new CollectionParser) parseTemplate (reader input))]
    (assoc (bean b) :underlying b)))

(defn by-rel [withRels rel] (beanify (filter (fn [i] (= (. i getRel) rel) ) withRels)))

(defn links-by-rel [collOrItem rel] (by-rel (:links collOrItem) rel))

(defn link-by-rel [collOrItem rel] (first (links-by-rel collOrItem rel)))

(defn queries-by-rel [coll rel] (by-rel (:queries coll) rel))

(defn query-by-rel [coll rel] (first (queries-by-rel coll rel)))

(defn items [coll] (beanify (:items coll)))

(defn head-item [coll] (first (items coll)))

(defn props [obj] (beanify (:data obj)))

(defn prop-by-name [obj n] (first (filter (fn [i] (= (:name i) n)) (props obj))))

(defn to-property [input]
  (cond
    (map? input)
      (cond
       (and (contains? input :name) (contains? input :value)) (Property/value (:name input) (Optional/none) (:value input))
       (and (contains? input :name) (contains? input :array)) (Property/array (:name input) (Optional/none) (:array input))
       (and (contains? input :name) (contains? input :object)) (Property/object (:name input) (Optional/none) (:object input))
       :else nil
      )      
    :else
      (let [n (name (key input)) v (val input)]
         (cond 
           (seq? v) (Property/arrayObject n (Optional/none) v)
           (map? v) (Property/objectMap n (Optional/none) v)
        :else (Property/value n (Optional/none) v)
      ))))

(defn make-data [input]
  (map to-property input))

(defn create-template [input]
  (Template/create (make-data input)))

(defn write-to [templateOrCollection output]
  (. (cond 
    (map? templateOrCollection) (:underlying templateOrCollection)
    :else templateOrCollection
  ) writeTo (writer output)))

(defn -main [& m]
  (def coll (parse-collection (file (first m))))
  (println (link-by-rel coll "feed"))
  (println (prop-by-name (head-item coll) "full-name"))  
  (println (create-template {:hello "world", :do "fawk"}))
  (write-to (create-template (cons (prop-by-name (head-item coll) "full-name") nil)) (file "/tmp/template.json"))
  (write-to coll (file "/tmp/cj.json")))