(ns my-exercise.search
  (:require [hiccup.page :refer [html5]]))

(defn header [_]
  [:head
   [:meta {:charset "UTF-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1.0, maximum-scale=1.0"}]
   [:title "My Elections"]
   [:link {:rel "stylesheet" :href "default.css"}]])

(defn getting-started [_]
  [:div
   [:h1 "My Elections"]
   [:p "The following elections were found in your area:"]
   [:ul
    [:li "Some Election"]
    [:li "Perhaps another election"]]])


(defn handle-post [request]
  (html5
    (header request)
    (getting-started request)))
