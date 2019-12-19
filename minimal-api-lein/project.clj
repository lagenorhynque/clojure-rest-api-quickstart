(defproject minimal-api-lein "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[bidi "2.1.6"]
                 [camel-snake-kebab "0.4.1"]
                 [integrant "0.7.0"]
                 [metosin/ring-http-response "0.9.1"]
                 [org.clojure/clojure "1.10.1"]
                 [ring/ring-core "1.8.0"]
                 [ring/ring-jetty-adapter "1.8.0"]
                 [ring/ring-json "0.5.0"]]
  :main ^:skip-aot minimal-api-lein.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
