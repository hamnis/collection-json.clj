(ns collection-json.internal
  (:import
    [net.hamnaberg.json Target URITarget URITemplateTarget]
    [net.hamnaberg.funclite Optional]))

(defn to-uri [href]
  (cond 
    (nil? href) nil
    (instance? java.net.URI href) href
    :else (java.net.URI/create (str href))))

(defn to-target [href]
  (cond
    (instance? Target href) href
    :else 
      (try (URITarget. (to-uri href))
        (catch Exception e (URITemplateTarget. (str href))))))

(defn listify [v] (flatten (if (nil? v) () (list v))))

(defn opt [v] 
  (cond 
    (instance? Optional v) v
    :else (Optional/fromNullable v)))

(defn map-opt [f v]
  (opt (first (map f v))))

(def none (opt nil))

(defn opt? [v]
  (instance? Optional v))

(defn map-values [f m]
  (into {} (for [[k v] m] [k (f v)])))

(defn extract-opt [v] (first v))

(defn beanify [input]
  (cond
    (map? input) input
    (seq? input) (map bean input)
    :else (bean input)))

(defn dispatch-on-first-class [& arglist] (let [args (count arglist)]
  (if (= args 1) (class (first arglist)) nil)))
