(ns collection-json.core 
  (:import
    [net.hamnaberg.json.parser CollectionParser]    
    [net.hamnaberg.json Template Property Item Query Collection Link]
    [java.util Map]
  )  
  (:use 
    [clojure.java.io]
    [collection-json.internal]
  ))

(defn parse-collection [input] 
  (beanify (. (CollectionParser.) parse (reader input))))

(defn parse-template [input] 
  (beanify (. (CollectionParser.) parseTemplate (reader input))))

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
        (extract-opt (:template has-template))
    (instance? Item has-template) (. has-template (toTemplate))
    (instance? Collection has-template) (extract-opt (. has-template (getTemplate)))
    ;; whatever this is we cannot extract a template from it.
    :else nil))

(defn props [obj] (beanify (:data obj)))

(defn prop-by-name [obj n] (first (filter (fn [i] (= (:name i) n)) (props obj))))

(defn make-property [n v]
  (cond 
    (nil? v) (Property/template n)
    (seq? v) (Property/arrayObject n none v)
    (map? v) (Property/objectMap n none v)           
  :else (Property/value n none v)))

(defn to-property [input]
  (cond
    (instance? Property input) input
    (map? input)
      (cond
       (and (contains? input :name) (contains? input :value)) 
          (Property/value (:name input) none (:value input))
       (and (contains? input :name) (contains? input :array)) 
          (Property/array (:name input) none (:array input))
       (and (contains? input :name) (contains? input :object)) 
          (Property/object (:name input) none (:object input))
       :else nil)      
    :else
      (let [n (name (key input)) v (val input)]
        (make-property n v))))

(defn make-data [input] (map to-property input))

(defmulti create-link dispatch-on-first-class)
(defmethod create-link Link [link] link)
(defmethod create-link Map [m] (create-link (:href m) (:rel m)))
(defmethod create-link :default [href rel] (Link/create (to-uri href) rel))

(defmulti create-item dispatch-on-first-class)
(defmethod create-item Item [item] item)
(defmethod create-item Map [m] (create-item (:href m) (:data m)))
(defmethod create-item :default [href props links] (Item/create (opt (to-uri href)) (make-data props) (map create-link (listify links))))

(defmulti create-query dispatch-on-first-class)
(defmethod create-query Query [q] q)
(defmethod create-query Map [m] (create-query (:href m) (:rel m) (:data m)))
(defmethod create-query :default [href rel props] (Query/create (to-target href) rel none (make-data props)))

(defn create-template [data]
  (Template/create (make-data data)))

(defmulti create-error dispatch-on-first-class)
(defmethod create-error Map [m] (create-error (:title m) (:code m) (:message m)))
(defmethod create-error :default [title code message]
  (net.hamnaberg.json.Error/create title (opt code) (opt message)))

(defmulti create-collection dispatch-on-first-class)
(defmethod create-collection Collection [c] c)
(defmethod create-collection Map [m]
  (let [href (to-uri (:href m))
        links (listify (:links m))
        items (listify (:items m))
        queries (listify (:queries m))
        template (opt (:template m))
        error (opt (:error m))
       ]
    (create-collection href links items queries template error)))
(defmethod create-collection :default [href links items queries template error]
  (Collection/create (opt href) (map create-link links) (map create-item items) (map create-query queries) (opt template) (opt error)))


(defn expand [query props]
  (. query expand (make-data props)))

(defn write-to [writeable output]
  (. writeable (writeTo (writer output))))

(defn -main [& m]
  (def coll (parse-collection (file (first m))))
  (println coll)  
  (println (template coll))
  ;(println (link-by-rel coll "feed"))
  ;(println (prop-by-name (head-item coll) "full-name"))  
  (println (create-template {:hello "world", :do "fawk"}))
  (println (create-item "foo" {:hello "world", :do 1} nil))
  (println (. (create-query "foo" "alternate" {:q nil}) getData))
  (println (create-collection {:href "hello"}))
  ;(write-to (create-template (cons (prop-by-name (head-item coll) "full-name") nil)) (file "/tmp/template.json"))
  ;(write-to coll (file "/tmp/cj.json")))
)
