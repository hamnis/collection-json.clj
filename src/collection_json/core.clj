(ns collection-json.core 
  (:import
    [net.hamnaberg.json.parser CollectionParser]
    [net.hamnaberg.json.util Optional]
    [net.hamnaberg.json Template Property Item Query]
  )  
  (:use 
    [clojure.java.io]
    [collection-json.internal]
  ))

(defn parse-collection [input] 
  (let [b (. (CollectionParser.) parse (reader input))]
    (assoc (bean b) :underlying b)))

(defn parse-template [input] 
  (let [b (. (CollectionParser.) parseTemplate (reader input))]
    (assoc (bean b) :underlying b)))

(defn by-rel [linkable rel] (beanify (filter (fn [i] (= (. i getRel) rel) ) linkable)))

(defn links-by-rel [linkable rel] (by-rel (:links linkable) rel))

(defn link-by-rel [linkable rel] (first (links-by-rel linkable rel)))

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
       (and (contains? input :name) (contains? input :value)) 
          (Property/value (:name input) (Optional/none) (:value input))
       (and (contains? input :name) (contains? input :array)) 
          (Property/array (:name input) (Optional/none) (:array input))
       (and (contains? input :name) (contains? input :object)) 
          (Property/object (:name input) (Optional/none) (:object input))
       :else nil)      
    :else
      (let [n (name (key input)) v (val input)]
         (cond 
           (nil? v) (Property/template n)
           (seq? v) (Property/arrayObject n (Optional/none) v)
           (map? v) (Property/objectMap n (Optional/none) v)           
        :else (Property/value n (Optional/none) v)
      ))))

(defn make-data [input] (map to-property input))

(defn create-item [href props]
  (Item/create (to-uri href) (make-data props)))

(defn create-query [href rel props]
  (Query/create (to-target href) rel (Optional/none) (make-data props)))

(defn expand [query data]
  (. query expand data))

(defn create-template [input]
  (Template/create (make-data input)))

(defn create-error [title code message]
  (net.hamnaberg.json.Error/create title code message))

(defn write-to [writeable output]
  (. (cond 
    (map? writeable) (:underlying writeable)
    :else writeable
  ) writeTo (writer output)))

(defn -main [& m]
  ;(def coll (parse-collection (file (first m))))
  ;(println (link-by-rel coll "feed"))
  ;(println (prop-by-name (head-item coll) "full-name"))  
  (println (create-template {:hello "world", :do "fawk"}))
  (println (create-item "foo" {:hello "world", :do 1}))
  (println (. (create-query "foo" "alternate" {:q nil}) getData))
  ;(write-to (create-template (cons (prop-by-name (head-item coll) "full-name") nil)) (file "/tmp/template.json"))
  ;(write-to coll (file "/tmp/cj.json")))
)
