(ns minimal-api-lein.middleware
  (:require
   [camel-snake-kebab.core :refer [->kebab-case ->snake_case]]
   [camel-snake-kebab.extras :refer [transform-keys]]))

(defn wrap-kebab-case-keys [handler]
  (fn [request]
    (let [response (-> request
                       (update :params (partial transform-keys #(->kebab-case % :separator \_)))
                       handler)]
      (transform-keys #(->snake_case % :separator \-) response))))

(defn wrap-db [handler db]
  (fn [request]
    (handler (assoc request :db db))))
