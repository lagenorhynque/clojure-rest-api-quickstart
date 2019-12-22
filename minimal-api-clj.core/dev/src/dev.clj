(ns dev
  (:require [clojure.java.io :as io]
            [clojure.repl :refer :all]
            [clojure.spec.alpha :as s]
            [clojure.tools.namespace.repl :refer [refresh]]
            [integrant.core :as ig]
            [integrant.repl :refer [clear halt go init prep reset]]
            [integrant.repl.state :refer [config system]]
            [minimal-api-clj.core]))

(integrant.repl/set-prep! (constantly minimal-api-clj.core/config))
