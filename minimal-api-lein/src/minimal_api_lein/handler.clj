(ns minimal-api-lein.handler
  (:require [clojure.string :as str]
            [minimal-api-lein.db :refer [todos]]
            [ring.util.http-response :as response]))

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
