(defproject minimal-api-lein "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[bidi "2.1.6"]
                 [camel-snake-kebab "0.4.1"]
                 [integrant "0.7.0"]
                 [metosin/ring-http-response "0.9.1"]
                 [mysql/mysql-connector-java "8.0.19"]
                 [org.clojure/clojure "1.10.1"]
                 [ring/ring-core "1.8.0"]
                 [ring/ring-jetty-adapter "1.8.0"]
                 [ring/ring-json "0.5.0"]
                 [seancorfield/next.jdbc "1.0.13"]]
  :main ^:skip-aot minimal-api-lein.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :dev {:source-paths ["dev/src"]
                   :resource-paths ["dev/resources"]
                   :dependencies [[clj-http "3.10.0"]
                                  [integrant/repl "0.3.1"]
                                  [orchestra "2019.02.06-1"]
                                  [pjstadig/humane-test-output "0.10.0"]]
                   :plugins [[jonase/eastwood "0.3.6"]
                             [lein-cljfmt "0.6.6"]
                             [lein-cloverage "1.1.2"]
                             [lein-kibit "0.1.8"]]
                   :aliases {"test-coverage" ^{:doc "Execute cloverage."}
                             ["cloverage" "--ns-exclude-regex" "^(:?dev|user)$" "--codecov" "--junit"]
                             "lint" ^{:doc "Execute cljfmt check, eastwood and kibit."}
                             ["do"
                              ["cljfmt" "check"]
                              ["eastwood" "{:source-paths [\"src\"]
                                            :test-paths []}"]
                              ["kibit"]]}
                   :injections [(require 'pjstadig.humane-test-output)
                                (pjstadig.humane-test-output/activate!)]}
             :repl {:repl-options {:init-ns user}}})
