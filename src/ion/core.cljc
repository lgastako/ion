(ns ion.core
  (:import (clojure.lang IAtom
                         IDeref
                         IObj)))

(defprotocol MonatomicIon
  (underlying-atom [this]))

(deftype Ion [a transmogrify]
  IDeref
  (deref [this] @a)
  IAtom
  (compareAndSet [_ old new]
    (compare-and-set! a old new))
  (reset [_ val]
    (reset! a (transmogrify val)))
  (swap [_ f]
    (swap! a (comp transmogrify f)))
  (swap [_ f x]
    (swap! a (comp transmogrify f) x))
  (swap [_ f x y]
    (swap! a (comp transmogrify f) x y))
  (swap [_ f x y args]
    (apply swap! a (comp transmogrify f) x y args))
  MonatomicIon
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
