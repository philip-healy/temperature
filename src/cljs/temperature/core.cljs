(ns temperature.core
  (:require [clojure.string :as str]
            [reagent.core :as reagent]
            [re-frame.core :as rf]))


;; Business logic
(defn celsius->fahrenheit [celsius-str]
  (let [celsius-float (js/Number celsius-str)]
    (if (or (str/blank? celsius-str)
            (js/isNaN celsius-float))
      ""
      (-> celsius-float (* 9) (/ 5.0) (+ 32) str))))

(defn fahrenheit->celsius [fahrenheit-str]
  (let [fahrenheit-float (js/Number fahrenheit-str)]
    (if (or (str/blank? fahrenheit-str)
            (js/isNaN fahrenheit-float))
      ""
      (-> fahrenheit-float (- 32) (* 5) (/ 9.0)))))


;; Events
(rf/reg-event-db
  :initialize
  (fn [_ _]
    {:celsius ""
     :fahrenheit ""}))

(rf/reg-event-db
  :celsius-change
  (fn [db [_ new-celsius-value]]
    (merge db {:celsius new-celsius-value
               :fahrenheit (celsius->fahrenheit new-celsius-value)})))

(rf/reg-event-db
  :fahrenheit-change
  (fn [db [_ new-fahrenheit-value]]
    (merge db {:celsius (fahrenheit->celsius new-fahrenheit-value)
               :fahrenheit new-fahrenheit-value})))


;; Subscriptions
(rf/reg-sub
  :celsius
  (fn [db _]
    (:celsius db)))

(rf/reg-sub
  :fahrenheit
  (fn [db _]
    (:fahrenheit db)))


;; UI Component
(defn ui []
  [:div.temperature-component
    [:div.field.input-group
      [:input.form-control
        {:name "celsius"
         :value @(rf/subscribe [:celsius])
         :on-change #(rf/dispatch [:celsius-change (-> % .-target .-value)])}]
      [:span.label.input-group-addon.bg-primary "℃"]]
    [:div.equals "="]
    [:div.field.input-group
      [:input.form-control
        {:name "fahrenheit"
         :value @(rf/subscribe [:fahrenheit])
         :on-change #(rf/dispatch [:fahrenheit-change (-> % .-target .-value)])}]
      [:span.label.input-group-addon.bg-primary "℉"]]])


;; Entry point
(defn ^:export init
  []
  (rf/dispatch-sync [:initialize])
  (reagent/render [ui] (js/document.getElementById "app")))
