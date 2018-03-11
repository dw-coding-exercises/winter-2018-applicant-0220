(ns my-exercise.search
  (:require
    [clojure.string :as string]
    [clojure.walk :refer [keywordize-keys]]
    [hiccup.page :refer [html5]]
    [clj-http.client :as client]))

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

;;;;;;;;;;;;;;;;
;; Sketchy Code
;;
;; This part's a bit rough, but shows the outline of what's coming next.
;; Derive OCD's from address, combine them into a request URL, and call the API.
;;;;;;;;;;;;;;;;

;; TODO: test
(defn address->ocds
  "Given an address, return a collection of OCD's"
  [address]
  (cond-> []
    (seq (:state address))
    (conj (->state-ocd address))

    (and
      (seq (:state address))
      (seq (:city address)))
    (conj (->place-ocd address))))

(def api-base-url "https://api.turbovote.org/elections/upcoming?district-divisions=")

;; TODO: building the request URL might be better as its own function, separate from the request
(defn request-elections-for-ocds
  [ocds]
  (let [request-url (str api-base-url (string/join "," ocds))]
    (client/get request-url)))

;; TODO: handle case of no OCD's derived
(defn request-elections-for-address
  [address]
  (let [ocds (address->ocds address)]
    (request-elections-for-ocds ocds)))

;;;;;;;;;;;;;;;;
;; /Sketchy Code
;;;;;;;;;;;;;;;;

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
