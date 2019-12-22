(ns minimal-api-lein.server
  (:require [bidi.ring :refer [make-handler]]
            [integrant.core :as ig]
            [minimal-api-lein.middleware :refer [wrap-kebab-case-keys]]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.json :refer [wrap-json-params wrap-json-response]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.params :refer [wrap-params]]))

(defmethod ig/init-key ::app [_ {:keys [routes]}]
  (-> (make-handler routes)
      wrap-kebab-case-keys
      wrap-keyword-params
      wrap-json-params
      wrap-json-response
      wrap-params))

(defmethod ig/init-key ::server [_ {:keys [app options]}]
  (jetty/run-jetty app options))

(defmethod ig/halt-key! ::server [_ server]
  (.stop server))
