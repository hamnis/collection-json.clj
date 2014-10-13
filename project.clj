(defproject net.hamnaberg.rest/collection-json-clj "0.2.0-SNAPSHOT"
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
    [org.clojure/clojure "1.6.0"]
    [cheshire "5.3.1"]
    [uritemplate-clj "1.0.0"]
  ]
  :pom-addition [:developers [:developer
                              [:name "Erlend Hamnaberg"]
                              [:email "erlend@hamnaberg.net"]]]
  :main collection-json.core
)
