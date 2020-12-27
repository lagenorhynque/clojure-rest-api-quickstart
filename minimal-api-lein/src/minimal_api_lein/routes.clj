(ns minimal-api-lein.routes
  (:require
   [integrant.core :as ig]
   [minimal-api-lein.handler :as handler]))

(defmethod ig/init-key ::routes [_ _]
  ["/" {"todos" {:get handler/list-todos
                 :post handler/create-todo}
        ["todos/" :todo-id] {:get handler/fetch-todo
                             :delete handler/delete-todo
                             :put handler/update-todo}}])
