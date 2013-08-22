(ns collection-json.internal
  (:import
    [net.hamnaberg.json Target URITarget URITemplateTarget]))

(defn beanify [input]
  (cond
    (map? input) input
    :else (map bean input)))

(defn to-uri [href]
  (cond 
    (instance? java.net.URI href) href
    :else (java.net.URI/create (str href))))

(defn to-target [dereferenceable]
  (cond
    (instance? Target) dereferenceable
    :else 
      (try (URITarget. (to-uri dereferenceable))
        (catch Exception e (URITemplateTarget. (str dereferenceable))))))