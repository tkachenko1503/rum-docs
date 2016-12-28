(ns rum-docs.app
    (:require [rum.core :as rum]
              [cljs.tools.reader :refer [read-string]]
              [cljs.js :refer [empty-state eval js-eval]]
              [cljs.env :refer [*compiler*]]
              [cljs.pprint :refer [pprint]]))

; Eval
(defn eval-str [s]
  (eval (empty-state)
        (read-string s)
        {:eval       js-eval
         :source-map true
         :context    :expr}
        (fn [result] result)))

(defn gen-and-eval-code
  [text]
  (-> text
      eval-str
      :value))


; State
(def editor (atom {:text "(+ 1 2)"}))
(def editor-text (rum/cursor-in editor [:text]))

(defn swapEditorValue
  [event]
  (reset! editor-text (.. event -target -value)))


; Components
(rum/defc editor [userInput]
  [:div
   [:textarea {:cols 50
               :rows 20
               :on-change swapEditorValue
               :value userInput}]])

(rum/defc preview < rum/reactive []
  (let [text (rum/react editor-text)
        result (gen-and-eval-code text)]
    [:div result]))

(rum/defc app []
  [:div
    (editor @editor-text)
    (preview)])


; Start
(defn init []
  (rum/mount (app) (. js/document (getElementById "container"))))
