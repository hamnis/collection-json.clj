(ns collection-json.internal
  (:import
    [net.hamnaberg.json Target URITarget URITemplateTarget]
    [net.hamnaberg.json.util Optional]))

(defn beanify [input]
  (cond
    (map? input) input
    :else (map bean input)))

(defn to-uri [href]
  (cond 
    (nil? href) nil
    (instance? java.net.URI href) href
    :else (java.net.URI/create (str href))))

(defn to-target [dereferenceable]
  (cond
    (instance? Target) dereferenceable
    :else 
      (try (URITarget. (to-uri dereferenceable))
        (catch Exception e (URITemplateTarget. (str dereferenceable))))))

(defn listify [v] (if (nil? v) () (list v)))

(defn opt [v] (Optional/fromNullable v))

(def none (opt nil))

(defn some? [v] 
  (cond 
    (nil? v) false
    (instance? Optional v) (. v isSome)))

(defn none? [v] (not some? v))