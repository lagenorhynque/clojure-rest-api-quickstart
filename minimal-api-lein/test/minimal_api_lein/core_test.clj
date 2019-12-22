(ns minimal-api-lein.core-test
  (:require [clojure.test :as t]
            [minimal-api-lein.core :as sut]
            [minimal-api-lein.test-helper :as helper :refer [with-db-data with-system]]))

(t/deftest test-api
  (with-system [sys (helper/test-system)]
    (with-db-data {"todo1" {"task" "build an API"}
                   "todo2" {"task" "?????"}
                   "todo3" {"task" "profit!"}}
      (t/testing "TODOリストの一覧が取得できる"
        (let [{:keys [status body]} (helper/http-get "/todos")]
          (t/is (= 200 status))
          (t/is (= {:todo1 {:task "build an API"}
                    :todo2 {:task "?????"}
                    :todo3 {:task "profit!"}}
                   body))))
      (t/testing "指定したIDのTODOが取得できる"
        (let [{:keys [status body]} (helper/http-get "/todos/todo3")]
          (t/is (= 200 status))
          (t/is (= {:task "profit!"}
                   body))))
      (t/testing "指定したIDのTODOが削除できる"
        (let [{:keys [status body]} (helper/http-delete "/todos/todo2")]
          (t/is (= 204 status))
          (t/is (nil? body))))
      (t/testing "TODOが追加できる"
        (let [{:keys [status body]} (helper/http-post "/todos" {:task "something new"})]
          (t/is (= 201 status))
          (t/is (= {:task "something new"}
                   body))))
      (t/testing "指定したIDのTODOが更新できる"
        (let [{:keys [status body]} (helper/http-put "/todos/todo3" {:task "something different"})]
          (t/is (= 201 status))
          (t/is (= {:task "something different"}
                   body))))
      (t/testing "更新内容を反映したTODOリストの一覧が取得できる"
        (let [{:keys [status body]} (helper/http-get "/todos")]
          (t/is (= 200 status))
          (t/is (= {:todo1 {:task "build an API"}
                    :todo3 {:task "something different"}
                    :todo4 {:task "something new"}}
                   body))))
      (t/testing "存在しないIDのTODOを取得しようとするとエラー"
        (let [{:keys [status body]} (helper/http-get "/todos/todo10")]
          (t/is (= 404 status))
          (t/is (= {:message "Todo todo10 doesn't exist"}
                   body))))
      (t/testing "存在しないIDのTODOを削除しようとするとエラー"
        (let [{:keys [status body]} (helper/http-delete "/todos/todo10")]
          (t/is (= 404 status))
          (t/is (= {:message "Todo todo10 doesn't exist"}
                   body))))
      (t/testing "存在しないIDのTODOを更新しようとすると作成される"
        (let [{:keys [status body]} (helper/http-put "/todos/todo10" {:task "another one"})]
          (t/is (= 201 status))
          (t/is (= {:task "another one"}
                   body))
          (let [{:keys [status body]} (helper/http-get "/todos")]
            (t/is (= 200 status))
            (t/is (= {:todo1 {:task "build an API"}
                      :todo3 {:task "something different"}
                      :todo4 {:task "something new"}
                      :todo10 {:task "another one"}}
                     body))))))))
