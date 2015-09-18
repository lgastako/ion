(ns ion.core
  (:import [clojure.lang IDeref IObj])
  (:refer-clojure :exclude [reset! swap!]))

(defprotocol MonatomicIon
  (reset! [this val])
  (swap! [this f & args])
  (underlying-atom [this]))

(deftype Ion [a transmogrify]
  IDeref
  (deref [this] @a)
  MonatomicIon
  (reset! [_ val]
    (apply clojure.core/reset! a (transmogrify val)))
  (swap! [_ f & args]
    (apply clojure.core/swap! a (comp transmogrify f) args))
  (underlying-atom [this] a)
  IObj
  (meta [_] (meta a)))

(defn ionize
  ([] (ionize nil))
  ([x & opts]
   (let [opts (apply hash-map opts)
         transmogrify (or (:transmogrifier opts) identity)
         opts (flatten (into [] (dissoc opts :transmogrifier)))
         a (apply atom (transmogrify x) opts)]
     (Ion. a transmogrify))))

(defn ion?
  "Return true if x implements "
  [x]
  (instance? Ion x))
