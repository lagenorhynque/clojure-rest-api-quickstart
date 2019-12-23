(ns minimal-api-clj.test-helper
  (:require  [clj-http.client :as client]
             [integrant.core :as ig]
             [minimal-api-clj.core :as core]))

(defn test-config []
  (assoc-in core/config [::core/server :options :port] 3001))

(defn test-system []
  (ig/prep (test-config)))

(def test-url-prefix "http://localhost:3001")

;;; macros for testing context

(defmacro with-system [[bound-sym binding-expr] & body]
  `(let [~bound-sym (ig/init ~binding-expr)]
     (try
       ~@body
       (finally (ig/halt! ~bound-sym)))))

(defmacro with-db-data [db-data-map & body]
  `(let [old-val# @core/todos]
     (try
       (reset! core/todos ~db-data-map)
       ~@body
       (finally (reset! core/todos old-val#)))))

;;; HTTP client

(defn http-get [path]
  (client/get (str test-url-prefix path)
              {:accept :json
               :as :json
               :coerce :always
               :throw-exceptions? false}))

(defn http-post [path body]
  (client/post (str test-url-prefix path)
               {:form-params body
                :content-type :json
                :accept :json
                :as :json
                :coerce :always
                :throw-exceptions? false}))

(defn http-put [path body]
  (client/put (str test-url-prefix path)
              {:form-params body
               :content-type :json
               :accept :json
               :as :json
               :coerce :always
               :throw-exceptions? false}))

(defn http-delete [path]
  (client/delete (str test-url-prefix path)
                 {:accept :json
                  :as :json
                  :coerce :always
                  :throw-exceptions? false}))
