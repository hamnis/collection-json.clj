(ns collection-json.core 
  (:require 
    [clojure.java.io :as io]
    [cheshire.core :as cheshire]))

(defn- not-nil? [n] (not (nil? n)))

(defn- is-uri-template? [s] (.contains s "{"))

(defn- parse-json [input] (if (string? input) (cheshire/parse-string input true) (cheshire/parse-stream (io/reader input) true)))

(defn parse-collection [input]   
  (:collection (parse-json input)))

(defn parse-template [input] 
  (:template (parse-json input)))

(defn- by-rel [linkable rel] (filter (fn [i] (= (:rel i) rel) ) linkable))

(defn links-by-rel [linkable rel] (by-rel (:links linkable) rel))

(defn link-by-rel [linkable rel] (first (links-by-rel linkable rel)))

(defn queries-by-rel [coll rel] (by-rel (:queries coll) rel))

(defn query-by-rel [coll rel] (first (queries-by-rel coll rel)))

(defn head-item [coll] (first (:items coll)))

(defn- extract-value [prop]
  (cond 
    (not-nil? (:object prop)) (:object prop)
    (not-nil? (:array prop)) (:array prop)
    :else (:value prop)))

(defn- prop-as-map [prop]
  (let [n (:name prop) v (extract-value prop)] {n v}))

(defn data-as-map [obj] 
  (let [data (:data obj)] 
    (reduce merge (map prop-as-map))))

(defn prop-by-name [obj n] ((data-as-map obj) n))

(defn make-property [n v]
  (cond 
    (nil? v) {:name n}
    (seq? v) {:name n, :array v}
    (map? v) {:name n, :object v}           
    :else {:name n, :value v}))

(defn to-property [input]
  (let [n (name (key input)) v (val input)] (make-property n v)))

(defn make-data [input] (map to-property input))

(defn create-link [href rel] {:href href, :rel rel})

(defn create-item [href props links] {:href href, :data (make-data props), :links links})

(defn create-query [href rel props]
  (let [ base {:rel rel, :data (make-data props)} ] 
    (merge base (if is-uri-template? {:href href, :encoding "uri-template"} {:href href}))))

(defn create-template [data] 
  {:template (make-data data)})

(defn create-error [title code message]
  {:title title, :code code, :message message})

(defn create-collection [href links items queries template error]
  {:collection {:href href, :links links, :items items, :queries queries, :template template, :error error}})

(defn create-collection-map [m]
  (let [href (:href m)
        links (:links m)
        items (:items m)
        queries (:queries m)
        template (:template m)
        error (:error m)
       ]
    (create-collection href links items queries template error)))

(defn expand [query props] nil)
  ;;(if (nil? (:encoding "uri-template") (expand-href :href props) )
  ;;nil)

(defn- expand-href [href props] nil)
