(ns collection-json.internal
  (:import
    [net.hamnaberg.json Target URITarget URITemplateTarget]
    [net.hamnaberg.json.util Optional]))

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

(defn listify [v] (flatten (if (nil? v) () (list v))))

(defn opt [v] (Optional/fromNullable v))

(def none (opt nil))

(defn opt? [v]
  (instance? Optional v))

(defn extract-opt [v] (if (opt? v) (. v (orNull)) nil))

(defn beanify [input]
  (cond
    (map? input) input
    (seq? input) (map bean input)
    :else (bean input)))
