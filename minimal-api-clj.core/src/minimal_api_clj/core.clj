(ns minimal-api-clj.core
  (:gen-class)
  (:require [clojure.java.io :as io]
            [integrant.core :as ig]))

(defn -main [& _]
  (-> (io/resource "config.edn")
      slurp
      ig/read-string
      (doto ig/load-namespaces)
      ig/prep
      ig/init))
