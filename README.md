# ion

This library provides ions which are wrappers around Clojure atoms that attempt
to expose an interface identical to Clojure's atoms with the addition a
transmogrification functions that provide the ability to transmogrify the
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
it defaults to nil if no value is supplied and it takes two additional options:

The `transmogrifier` option should should be nil or a side-effect free fn of
one argument, which will be passed the value that was is about to be `reset!`
(or `swap!`'d) into the ion and which should return a value which will be
`reset!` (or `swap!`'d) into the ion instead.

#### Creating an empty ion with no transmogrifiers

```clojure
(def ion (ionize))
```

Since we didn't supply a transmogrifier this ion is essentially equivalent to
this atom at this point:

```clojure
(def ion (atom nil))
```

### Creating an ion with a transmogrifier

```clojure
(def ion (ionize 2 :tranmogrifier inc))

@ion  ;; => 3

(reset! ion 5)

@ion  ;; => 6

(swap! ion dec)

@ion ;; => 6
```

### Manipulate ions like atoms

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

```clojure
(def ion (ionize
```

### Check to see if something is an ion

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
