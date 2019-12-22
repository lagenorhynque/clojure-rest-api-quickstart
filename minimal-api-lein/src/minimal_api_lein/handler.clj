(ns minimal-api-lein.handler
  (:require [minimal-api-lein.boundary.db.todo :as db.todo]
            [ring.util.http-response :as response]))

(defn list-todos [{:keys [db]}]
  (response/ok (db.todo/find-todos db)))

(defn create-todo [{:keys [params db]}]
  (let [id (db.todo/create-todo! db {"task" (:task params)})
        todo (db.todo/find-todo-by-id db id)]
    (response/created (str "/todos/" id) todo)))

(defn fetch-todo [{:keys [params db]}]
  (if-let [todo (db.todo/find-todo-by-id db (:todo-id params))]
    (response/ok todo)
    (response/not-found {:message (str "Todo " (:todo-id params) " doesn't exist")})))

(defn delete-todo [{:keys [params db]}]
  (if (db.todo/find-todo-by-id db (:todo-id params))
    (do (db.todo/delete-todo! db (:todo-id params))
        (response/no-content))
    (response/not-found {:message (str "Todo " (:todo-id params) " doesn't exist")})))

(defn update-todo [{:keys [params db]}]
  (let [task {"task" (:task params)}]
    (db.todo/update-todo! db (:todo-id params) task)
    (response/created (str "/todos/" (:todo-id params)) task)))
