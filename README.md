# ion

This library provides ions which are wrappers around Clojure atoms that attempt
to expose an interface identical to atoms with the addition of a
transmogrification function that provides the ability to transmogrify the
values that are `reset!` or `swap!`'d into the ion.

The ions produced by this library are all monatomic.

## Usage

### Require the ion library

In a namespace:

```clojure
(ns your.app
  :require [ion.core :refer [ion]]
```

or in a REPL:

```clojure
(require '[ion.core :refer [ion])
```

### Create an ion

The `ionize` fn is identical to clojure's built in `atom` function except that
it defaults to nil if no value is supplied and it takes one additional option:

The `transmogrifier` option should should be nil or a side-effect free fn of
one argument, which will be passed the value that was is about to be `reset!`
(or `swap!`'d) into the ion and which should return a value which will be
`reset!` (or `swap!`'d) into the ion instead.

#### Create an empty ion with no transmogrifiers

```clojure
(def ion (ionize))
```

Since we didn't supply a transmogrifier this ion will behave identically to
an atom at this point.

#### Create an ion with a transmogrifier

A (ever so slightly) more interesting example would be an ion that increment's
it's value before storing it:

```clojure
(def ion (ionize 2 :transmogrifier inc))

@ion  ;; => 3

(reset! ion 5)

@ion  ;; => 6

(swap! ion dec)

@ion ;; => 6
```

A more real world example might be a `ion` that has a map as a value and which
creates or updates new keys in the map containing denormalized or transformed
projections of the keys that the user is swapping it.

#### Manipulate ions like atoms

```clojure
(def ionize (ionize))

@ion  ;; => nil

(reset! ion 0)

@ion  ;; => 0

(swap! ion (comp inc inc))

@ion  ;; => 2
```

You can also supply `:validator` and `:meta` options at construction time like
with atoms:

When a validator is used with a transmogrifier, the validator applies to the
transmogrified version of the value, not the pre-transmogrified version:

```clojure
(def ion (ionize 0 :transmogrifier inc
                   :validator odd?))

@ion  ;; => 1

(swap! ion identity)  ;; fails validation because the value gets bumped to 2 which is not odd

(swap! ion inc)  ;; => 3  (succeeds because it's inc'd one by swap! and once by transmogrifier)

(def ion (ionize 0 :meta {:foo :bar}))

(meta ion)  ;; => {:foo :bar}
```


#### Check to see if something is an ion

```clojure
(ion? (ionize)) ;; => true
(ion? (atom)) ;; => false
(ion? 5) ;; => false
```

I really wish clojure provided `(atom? x)` or I had time to figure out how to
write it.

## License

Copyright © 2015 John Evans

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
