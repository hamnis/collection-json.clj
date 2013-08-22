(ns collection-json.core 
  (:import
    [net.hamnaberg.json.parser CollectionParser]
    [net.hamnaberg.json.util Optional]
    [net.hamnaberg.json Template Property]
  ) 
  (:use 
    [clojure.java.io]
  )
)

(defn beanify [input]
  (cond
    (map? input) input
    :else (map (fn [a] (bean a)) input)
  )
)

(defn parseCollection [input] 
  (let [b (. (new CollectionParser) parse (reader input))]
    (assoc (bean b) :underlying b)
  )
)

(defn parseTemplate [input] 
  (let [b (. (new CollectionParser) parseTemplate (reader input))]
    (assoc (bean b) :underlying b)
  )  
)

(defn byRel [withRels rel]
  (beanify
    (filter (fn [i] (= (. i getRel) rel) ) withRels)
  )
)

(defn linksByRel [collOrItem rel]
  (byRel (:links collOrItem) rel)
)

(defn linkByRel [collOrItem rel]
  (first (linksByRel collOrItem rel))
)

(defn queriesByRel [coll rel]
  (byRel (:queries coll) rel)
)

(defn queryByRel [coll rel]
  (first (queryByRel coll rel))
)

(defn items [coll]
  (beanify (:items coll))  
)

(defn headItem [coll] 
  (first (items coll))
)

(defn props [obj]
  (beanify (:data obj))
)

(defn propByName [obj n]
  (first (filter (fn [i] (= (:name i) n)) (props obj)))
)

(defn toProperty [input]
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
      ))
  )
)

(defn makeData [input]
  (map (fn [e] (toProperty e)) input)
)

(defn create-template [input]
  (Template/create (makeData input))
)

(defn write-to [templateOrCollection output]
  (. (cond 
    (map? templateOrCollection) (:underlying templateOrCollection)
    :else templateOrCollection
  ) writeTo (writer output))
)

(defn -main [& m]
  (def coll (parseCollection (file (first m))))
  (println (linkByRel coll "feed"))
  (println (propByName (headItem coll) "full-name"))  
  (println (create-template {:hello "world", :do "fawk"}))
  (write-to (create-template (cons (propByName (headItem coll) "full-name") nil)) (file "/tmp/template.json"))
  (write-to coll (file "/tmp/cj.json"))
)