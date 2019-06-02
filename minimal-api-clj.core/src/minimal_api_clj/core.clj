(ns minimal-api-clj.core
  (:gen-class)
  (:require [bidi.ring :refer [make-handler]]
            [camel-snake-kebab.core :refer [->kebab-case ->snake_case]]
            [camel-snake-kebab.extras :refer [transform-keys]]
            [clojure.string :as str]
            [integrant.core :as ig]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.json :refer [wrap-json-params wrap-json-response]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.util.http-response :as response]))

;;; handlers

(def todos
  (atom {"todo1" {"task" "build an API"}
         "todo2" {"task" "?????"}
         "todo3" {"task" "profit!"}}))

(defn list-todos [_]
  (response/ok @todos))

(defn create-todo [{:keys [params]}]
  (let [id (->> (keys @todos)
                (map #(-> %
                          (str/replace-first "todo" "")
                          Long/parseLong))
                (apply max)
                inc)
        todo-id (str "todo" id)]
    (swap! todos assoc todo-id {"task" (:task params)})
    (response/created (str "/todos/" todo-id) (get @todos todo-id))))

(defn fetch-todo [{:keys [params]}]
  (if-let [todo (get @todos (:todo-id params))]
    (response/ok todo)
    (response/not-found {:message (str "Todo " (:todo-id params) " doesn't exist")})))

(defn delete-todo [{:keys [params]}]
  (if (get @todos (:todo-id params))
    (do (swap! todos dissoc (:todo-id params))
        (response/no-content))
    (response/not-found {:message (str "Todo " (:todo-id params) " doesn't exist")})))

(defn update-todo [{:keys [params]}]
  (let [task {"task" (:task params)}]
    (swap! todos assoc (:todo-id params) task)
    (response/created (str "/todos/" (:todo-id params)) task)))

;;; routes

(defmethod ig/init-key ::routes [_ _]
  ["/" {"todos" {:get list-todos
                 :post create-todo}
        ["todos/" :todo-id] {:get fetch-todo
                             :delete delete-todo
                             :put update-todo}}])

;;; middleware

(defn wrap-kebab-case-keys [handler]
  (fn [request]
    (let [response (-> request
                       (update :params (partial transform-keys #(->kebab-case % :separator \_)))
                       handler)]
      (transform-keys #(->snake_case % :separator \-) response))))

(defmethod ig/init-key ::app [_ {:keys [routes]}]
  (-> (make-handler routes)
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
  {::routes {}
   ::app {:routes (ig/ref ::routes)}
   ::server {:app (ig/ref ::app)
             :options {:port 3000
                       :join? false}}})

;;; main entry point

(defn -main [& args]
  (ig/init config))
