(ns clojure-mcp.main
  (:gen-class)
  (:require [clojure-mcp.core :as core]
            [clojure-mcp.logging :as logging]
            [clojure-mcp.prompts :as prompts]
            [clojure-mcp.resources :as resources]
            [clojure-mcp.tools :as tools]))

(defn make-resources [nrepl-client-atom _working-dir]
  (resources/make-resources nrepl-client-atom))

(defn make-prompts [nrepl-client-atom _working-dir]
  (prompts/make-prompts nrepl-client-atom))

(defn make-tools [nrepl-client-atom _working-directory]
  (tools/build-all-tools nrepl-client-atom))

(defn start-mcp-server
  "Entry point for MCP server startup."
  [opts]
  (logging/configure-logging!
   {:log-file (get opts :log-file logging/default-log-file)
    :enable-logging? (get opts :enable-logging? false)
    :log-level (get opts :log-level :debug)})
  (core/build-and-start-mcp-server
   (dissoc opts :log-file :log-level :enable-logging?)
   {:make-tools-fn make-tools
    :make-prompts-fn make-prompts
    :make-resources-fn make-resources}))

(defn start
  "Entry point for running from project directory.
   Coerces :project-dir to string to handle CLI function objects like '/'."
  [opts]
  (let [project-dir (some-> (get opts :project-dir) str)
        opts' (cond-> opts
                project-dir (assoc :project-dir project-dir))]
    (start-mcp-server opts')))

(defn- parse-cli-args [args]
  (if (and (= 1 (count args)) (map? (first args)))
    (first args)
    (loop [remaining args
           result {}]
      (if (empty? remaining)
        result
        (let [k (first remaining)
              v (second remaining)
              ;; Ensure key is a keyword regardless of shell/clj-exec-fn parsing
              k-kw (cond
                     (keyword? k) k
                     (symbol? k) (keyword (name k))
                     (string? k) (keyword (clojure.string/replace k #"^:" ""))
                     :else (keyword (str k)))]
          (recur (drop 2 remaining)
                 (assoc result k-kw v)))))))

(defn -main [& args]
  (start (parse-cli-args args)))
