(ns unihook.core
  (:require
   [tools.config :as cfg]
   [cheshire.core :as json]
   [org.httpkit.server :as http-kit]
   [clojure.string :as str]
   [kinsky.client :as client]))

(def cfg (cfg/*get
          {:port {:default "8888" :parse-fn #(Integer/parseInt %)}
           :bootstrap-server {:required true}}))

(defonce *producer
  (client/producer {:bootstrap.servers (:bootstrap-server cfg)}
                   (client/keyword-serializer)
                   (client/edn-serializer)))

(defn sanitize [x]
  (str/replace x #"[^0-9a-zA-Z]+" "_"))

(defn handle [{body :body :as req}]
  (let [topic (str "unihook" (sanitize (:uri req)))
        msg (cond-> (-> req
                        (dissoc :async-channel)
                        (assoc :ts (java.util.Date.)))
              body (assoc :body (slurp body)))
        msg-key (str (java.util.UUID/randomUUID))]
    (client/send! *producer topic msg-key msg)
    (println (str "Send to " topic " msg " msg-key))
    {:status 200
     :headers {"Content-Type" "text/xml"}
     :body "<Response>Message sent</Response>"}))

(defonce server (atom nil))

(defn stop []
  (when-let [s @server]
    (println "Stoping server")
    (@server)
    (reset! server nil)))

(defn restart []
  (let [{p :port bs :bootstrap-server} cfg]
    (stop)
    (println "Start server on " p " with kafka " bs)
    (reset! server (http-kit/run-server #(handle %) {:port p}))))

(defn -main [& args]
  (restart))
