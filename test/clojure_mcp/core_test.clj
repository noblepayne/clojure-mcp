(ns clojure-mcp.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [clojure-mcp.core :as core]))

(deftest coerce-options-test
  (testing "coerce-options handles symbol project-dir"
    (let [result (core/coerce-options {:project-dir '"/path/to/project"})]
      (is (= {:project-dir "/path/to/project"} result))))

  (testing "coerce-options handles symbol config-file"
    (let [result (core/coerce-options {:config-file '"/path/to/config.edn"})]
      (is (= {:config-file "/path/to/config.edn"} result))))

  (testing "coerce-options passes through string values unchanged"
    (let [result (core/coerce-options {:project-dir "/path/to/project"
                                       :config-file "/path/to/config.edn"})]
      (is (= {:project-dir "/path/to/project"
              :config-file "/path/to/config.edn"} result)))))

(deftest validate-options-test
  (testing "validates allowed-directories as vector of strings"
    (let [result (core/validate-options {:allowed-directories ["/"]})]
      (is (= {:allowed-directories ["/"]} result)))

    (let [result (core/validate-options {:allowed-directories ["/" "/home"]})]
      (is (= {:allowed-directories ["/" "/home"]} result)))

    (let [result (core/validate-options {})]
      (is (= {} result))))

  (testing "validates config-file path exists"
    (let [result (core/validate-options {:config-file "deps.edn"})]
      (is (= {:config-file "deps.edn"} result))))

  (testing "validates project-dir path exists and is directory"
    (let [result (core/validate-options {:project-dir "src"})]
      (is (= {:project-dir "src"} result)))))
