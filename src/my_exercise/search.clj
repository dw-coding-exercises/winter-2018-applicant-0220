(ns my-exercise.search
  (:require
    [clojure.string :as string]
    [clojure.walk :refer [keywordize-keys]]
    [hiccup.page :refer [html5]]))

(defn header [_]
  [:head
   [:meta {:charset "UTF-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1.0, maximum-scale=1.0"}]
   [:title "My Elections"]
   [:link {:rel "stylesheet" :href "default.css"}]])

(defn request->address
  "Take a ring request from a /search POST and pull out address fields
  as keywords"
  [request]
  (-> request
    :form-params
    (select-keys ["street" "street-2" "city" "state" "zip"])
    keywordize-keys))

(defn ->state-ocd
  [{state :state}]
  {:pre [(and (string? state) (< 0 (.length state)))]}
  (str "ocd-division/country:us/state:" (string/lower-case state)))

(defn ->place-ocd
  [address]
  (let [state-ocd (->state-ocd address)
        city (-> (:city address)
               string/lower-case
               (string/replace #" " "_"))]
    (str state-ocd "/place:" city)))

;; TODO: Handle missing fields, add CSS
(defn format-address
  "Given an address, format it as hiccup HTML"
  [{:keys [street street-2 city state zip]}]
  [:div
   [:div street]
   [:div street-2]
   [:div (str city ", " state " " zip)]])

(defn explanation [request]
  (let [address (request->address request)]
    [:div
     [:h1 "My Elections"]
     [:p "The following upcoming elections were found near"]
     (format-address address)]))

(defn election-results [_]
  [:div
   [:ul
    [:li "Some Election"]
    [:li "Perhaps another election"]]])

(defn handle-post [request]
  (html5
    (header request)
    (explanation request)
    (election-results request)))
