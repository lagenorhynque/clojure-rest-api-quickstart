(ns minimal-api-clj.core
  (:gen-class)
  (:require [camel-snake-kebab.core :refer [->kebab-case ->snake_case]]
            [camel-snake-kebab.extras :refer [transform-keys]]
            [integrant.core :as ig]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.json :refer [wrap-json-params wrap-json-response]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.util.response :as response]))

;;; handlers

(defn hello-world [request]
  ;; for debug
  (clojure.pprint/pprint (:params request))
  (response/response {:message "Hello, World!"
                      :params (:params request)}))

;;; middleware

(defn wrap-kebab-case-keys [handler]
  (fn [request]
    (let [response (-> request
                       (update :params (partial transform-keys #(->kebab-case % :separator \_)))
                       handler)]
      (transform-keys #(->snake_case % :separator \-) response))))

(defmethod ig/init-key ::app [_ _]
  (-> hello-world
      wrap-kebab-case-keys
      wrap-keyword-params
      wrap-json-params
      wrap-json-response
      wrap-params))

;;; API server

(defmethod ig/init-key ::server [_ {:keys [app options]}]
  (jetty/run-jetty app options))

(defmethod ig/halt-key! ::server [_ server]
  (.stop server))

;;; system configuration

(def config
  {::app {}
   ::server {:app (ig/ref ::app)
             :options {:port 3000
                       :join? false}}})

;;; main entry point

(defn -main [& args]
  (ig/init config))
