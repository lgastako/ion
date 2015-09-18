(ns ion.core
  (:import [clojure.lang IDeref IObj])
  (:refer-clojure :exclude [reset! swap!]))

(defprotocol MonatomicIon
  (reset! [this val])
  (swap! [this f & args])
  (underlying-atom [this]))

(deftype Ion [a reset!-transmogrifier swap!-transmogrifier]
  IDeref
  (deref [this] @a)
  MonatomicIon
  (reset! [_ val]
    (letfn [(f [x] (apply (or reset!-transmogrifier identity) x))]
      (apply clojure.core/reset! (f val))))
  (swap! [_ f & args]
    (let [f (apply (-> a swap!-transmogrifier (or identity)) f)]
      (apply clojure.core/swap! a f args)))
  (underlying-atom [this] a)
  IObj
  (meta [_] (meta a)))

(defn ionize
  ([] (ionize nil))
  ([x & opts]
   (let [opts (apply hash-map opts)
         reset!-transmogrifier (:reset!-transmogrifier! opts)
         swap!-transmogrifier (:swap!-transmogrifier! opts)
         opts (flatten (into [] (dissoc opts
                                        :reset!-transmogrifier!
                                        :swap!-transmogrifier!)))]
     (-> (apply atom x opts)
         (Ion. (or (:reset!-transmogrifier opts) identity)
               (or (:swap!-transmogrifier! opts) identity))))))

(defn ion?
  "Return true if x implements "
  [x]
  (instance? Ion x))
