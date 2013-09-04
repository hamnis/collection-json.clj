(ns collection-json.internal
  (:import
    [net.hamnaberg.json Target URITarget URITemplateTarget]
    [net.hamnaberg.json.util Optional Function]))

(deftype internal-fn [f]
  Function 
  (apply [this input] (f input)))

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
  (.map v (internal-fn. f)))

(def none (opt nil))

(defn opt? [v]
  (instance? Optional v))

(defn extract-opt [v] (if (opt? v) (. v (orNull)) nil))

(defn beanify [input]
  (cond
    (map? input) input
    (seq? input) (map bean input)
    :else (bean input)))

(defn dispatch-on-first-class [& arglist] (let [args (count arglist)]
  (cond 
    (= args 1) (class (first arglist))
    :default nil)))