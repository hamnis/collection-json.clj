(defproject net.hamnaberg.rest/collection-json-clj "0.1.0-SNAPSHOT"
  :description "Wrapper for collection-json java"
  :url "https://github.com/hamnis/collection-json.clj"
  :license {:name "Apache License v2"
            :url "http://www.apache.org/licenses/LICENSE-2.0"}
  :dependencies [
  			[org.clojure/clojure "1.5.1"]
  			[net.hamnaberg.rest/json-collection "2.3-SNAPSHOT"]
  ]
  :dev-dependencies [
  			[clj-http "0.7.6"]
  ]
  :main collection-json.core
)
