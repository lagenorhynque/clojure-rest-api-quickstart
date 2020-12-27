(ns minimal-api-lein.boundary.db.todo
  (:require
   [clojure.spec.alpha :as s]
   [minimal-api-lein.boundary.db.core :as db]))

(s/def ::id string?)
(s/def ::todo (s/map-of string? string?))
(s/def ::todos (s/map-of ::id ::todo))

(s/fdef find-todos
  :args (s/cat :db ::db/db)
  :ret ::todos)

(s/fdef find-todo-by-id
  :args (s/cat :db ::db/db
               :id ::id)
  :ret (s/nilable ::todo))

(s/fdef create-todo!
  :args (s/cat :db ::db/db
               :todo ::todo)
  :ret ::id)

(s/fdef update-todo!
  :args (s/cat :db ::db/db
               :id ::id
               :todo ::todo)
  :ret any?)

(s/fdef delete-todo!
  :args (s/cat :db ::db/db
               :id ::id)
  :ret any?)

(defprotocol Todo
  (find-todos [db])
  (find-todo-by-id [db id])
  (create-todo! [db todo])
  (update-todo! [db id todo])
  (delete-todo! [db id]))

(extend-protocol Todo
  minimal_api_lein.boundary.db.core.Boundary
  (find-todos [db]
    (db/select-all db :todo))
  (find-todo-by-id [db id]
    (db/selet-by-key db :todo id))
  (create-todo! [db todo]
    (db/insert! db :todo "todo" todo))
  (update-todo! [db id todo]
    (db/update-by-key! db :todo id todo))
  (delete-todo! [db id]
    (db/delete-by-key! db :todo id)))
