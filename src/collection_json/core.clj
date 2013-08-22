(ns collection-json.core 
  (:import
    [net.hamnaberg.json.parser CollectionParser]
    [net.hamnaberg.json.util Optional]
    [net.hamnaberg.json Template Property Item Query Collection]
  )  
  (:use 
    [clojure.java.io]
    [collection-json.internal]
  ))

(defn parse-collection [input] 
  (. (CollectionParser.) parse (reader input))

(defn parse-template [input] 
  (. (CollectionParser.) parseTemplate (reader input))

(defn by-rel [linkable rel] (beanify (filter (fn [i] (= (. i getRel) rel) ) linkable)))

(defn links-by-rel [linkable rel] (by-rel (:links linkable) rel))

(defn link-by-rel [linkable rel] (first (links-by-rel linkable rel)))

(defn queries-by-rel [coll rel] (by-rel (:queries coll) rel))

(defn query-by-rel [coll rel] (first (queries-by-rel coll rel)))

(defn items [coll] (beanify (:items coll)))

(defn head-item [coll] (first (items coll)))

(defn template [has-template]
  (cond 
    (and (map? has-template) (contains? has-template :template)) 
      (.. (:template has-template) (orNull))
    (instance? Item has-template) (. has-template toTemplate)
    ;; now, assume this is a Collection
    :else (.. has-template .getTemplate (orNull))))

(defn props [obj] (beanify (:data obj)))

(defn prop-by-name [obj n] (first (filter (fn [i] (= (:name i) n)) (props obj))))

(defn make-property [n v]
  (cond 
    (nil? v) (Property/template n)
    (seq? v) (Property/arrayObject n (Optional/none) v)
    (map? v) (Property/objectMap n (Optional/none) v)           
  :else (Property/value n (Optional/none) v)))

(defn to-property [input]
  (cond
    (instance? Property input) input
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
        (make-property n v))))

(defn make-data [input] (map to-property input))

(defn create-item [href props]
  (Item/create (to-uri href) (make-data props)))

(defn create-query [href rel props]
  (Query/create (to-target href) rel (Optional/none) (make-data props)))

(defn create-template [input]
  (Template/create (make-data input)))

(defn create-error [title code message]
  (net.hamnaberg.json.Error/create title code message))

(defn create-collection [m]
  (let [href (opt (to-uri (:href m)))
        links (listify (:links m))
        items (listify (:items m))
        queries (listify (:queries m))
        template (opt (:template m))
        error (opt (:error m))
       ]
    (Collection/create href links items queries template error)))

(defn expand [query props]
  (. query expand (make-data props)))

(defn write-to [writeable output]
  (. (cond 
    (map? writeable) (:underlying writeable)
    :else writeable
  ) writeTo (writer output)))

(defn -main [& m]
  (def coll (parse-collection (file (first m))))
  (println (template coll))
  ;(println (link-by-rel coll "feed"))
  ;(println (prop-by-name (head-item coll) "full-name"))  
  (println (create-template {:hello "world", :do "fawk"}))
  (println (create-item "foo" {:hello "world", :do 1}))
  (println (. (create-query "foo" "alternate" {:q nil}) getData))
  (println (create-collection {:href "hello"}))
  ;(write-to (create-template (cons (prop-by-name (head-item coll) "full-name") nil)) (file "/tmp/template.json"))
  ;(write-to coll (file "/tmp/cj.json")))
)
