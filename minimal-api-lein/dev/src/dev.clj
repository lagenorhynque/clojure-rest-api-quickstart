(ns dev
  (:refer-clojure :exclude [test])
  (:require [clojure.java.io :as io]
            [clojure.repl :refer :all]
            [clojure.spec.alpha :as s]
            [clojure.test :as test]
            [clojure.tools.namespace.repl :refer [refresh]]
            [integrant.core :as ig]
            [integrant.repl :refer [clear halt go init prep]]
            [integrant.repl.state :refer [config system]]
            [orchestra.spec.test :as stest]))

(defn read-config []
  (-> (io/resource "config.edn")
      slurp
      ig/read-string
      (doto ig/load-namespaces)))

(defn reset []
  (let [result (integrant.repl/reset)]
    (with-out-str (stest/instrument))
    result))

(integrant.repl/set-prep! (comp ig/prep read-config))

(defn test
  ([] (test/run-all-tests #"minimal-api-lein\..+-test"))
  ([ns-sym] (test/run-tests ns-sym)))

(defn db []
  (:minimal-api-lein.boundary.db.core/db system))

(defn db-run [f & args]
  (apply f (db) args))

(when (io/resource "local.clj")
  (load "local"))
