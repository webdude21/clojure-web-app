(defproject clojure-web-app "0.1.0-SNAPSHOT"
  :description "Try out clojure for web development"
  :url "https://github.com/webdude21/clojure-web-app"
  :source-paths ["src/clj"]
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.logging "0.4.0"]
                 [compojure "1.5.1"]
                 [clj-http "3.6.1"]
                 [environ "1.1.0"]
                 [ring/ring-jetty-adapter "1.4.0"]
                 [bk/ring-gzip "0.3.0"]
                 [org.clojure/data.json "0.2.6"]
                 [ring/ring-defaults "0.2.1"]
                 [ring/ring-json "0.4.0"]
                 [org.clojure/clojurescript "1.9.946"]
                 [org.clojure/core.async "0.4.474"]
                 [cljs-http "0.1.21"]
                 [org.omcljs/om "1.0.0-beta1"]]
  :plugins [[lein-ring "0.9.7"]
            [lein-cljsbuild "1.1.7"]
            [lein-figwheel "0.5.14"]
            [lein-environ "1.1.0"]]
  :cljsbuild {:builds [{:id           "dev"
                        :source-paths ["src/cljs"]
                        :figwheel     {:websocket-host "localhost"}
                        :compiler     {:asset-path    "js/out"
                                       :main          "clojure_front_end_app.core"
                                       :optimizations :none
                                       :source-map    true
                                       :output-to     "resources/public/js/app.js"
                                       :output-dir    "resources/public/js/out"}}
                       {:id           "prod"
                        :source-paths ["src"]
                        :compiler     {:output-to     "resources/public/js/app-prod.js"
                                       :optimizations :advanced
                                       :pretty-print  false}}]}
  :figwheel {:css-dirs     ["resources/public/styles"]
             :hawk-options {:watcher :polling}
             :ring-handler clojure-web-app.handler/app
             :server-port  3000}
  :hooks [environ.leiningen.hooks]
  :uberjar-name "clojure-web-app-standalone.jar"
  :ring {:handler clojure-web-app.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.0"]]}})