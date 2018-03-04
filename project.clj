(defproject clojure-web-app "0.1.0-SNAPSHOT"
  :description "Try out clojure for web development"
  :url "https://github.com/webdude21/clojure-web-app"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.logging "0.4.0"]
                 [compojure "1.5.1"]
                 [clj-http "3.6.1"]
                 [environ "1.1.0"]
                 [ring/ring-jetty-adapter "1.4.0"]
                 [org.clojure/data.json "0.2.6"]
                 [ring/ring-defaults "0.2.1"]
                 [ring/ring-json "0.4.0"]]
  :plugins [[lein-ring "0.9.7"]
            [lein-environ "1.1.0"]]
  :hooks [environ.leiningen.hooks]
  :uberjar-name "clojure-web-app-standalone.jar"
  :ring {:handler clojure-web-app.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.0"]]}})