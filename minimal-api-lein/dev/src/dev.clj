(ns dev
  (:refer-clojure :exclude [test])
  (:require [clojure.java.io :as io]
            [clojure.repl :refer :all]
            [clojure.spec.alpha :as s]
            [clojure.test :as test]
            [clojure.tools.namespace.repl :refer [refresh]]
            [integrant.core :as ig]
            [integrant.repl :refer [clear halt go init prep reset]]
            [integrant.repl.state :refer [config system]]))

(defn read-config []
  (-> (io/resource "config.edn")
      slurp
      ig/read-string
      (doto ig/load-namespaces)))

(integrant.repl/set-prep! (comp ig/prep read-config))

(defn test
  ([] (test/run-all-tests #"minimal-api-lein\..+-test"))
  ([ns-sym] (test/run-tests ns-sym)))
