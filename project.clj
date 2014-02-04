(defproject net.hamnaberg.rest/collection-json-clj "0.1.0-SNAPSHOT"
  :description "Wrapper for collection-json java"
  :url "https://github.com/hamnis/collection-json.clj"
  :license {
    :name "Apache License v2"
    :url "http://www.apache.org/licenses/LICENSE-2.0"
  }
  :scm {
    :url "https://github.com/hamnis/collection-json.clj"
    :developerConnection "git@github.com:hamnis/collection-json.clj.git"
    :connection "git@github.com:hamnis/collection-json.clj.git"
  }
  :dependencies [
    [org.clojure/clojure "1.5.1"]
    [net.hamnaberg.rest/json-collection "3.0.0"]
    [com.damnhandy/handy-uri-templates "2.0.1"]
    [com.google.guava/guava "15.0"] ; This is stupid.
  ]
  :pom-addition [:developers [:developer
                              [:name "Erlend Hamnaberg"]
                              [:email "erlend@hamnaberg.net"]]]
  :main collection-json.core
)
