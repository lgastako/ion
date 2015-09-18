# ion

This library provides ions which are wrappers around Clojure atoms that attempt
to expose an interface identical to Clojure's atoms with the addition of two
transmogrification functions that provide the ability to transmogrify the
values that are `reset!` into the ion and transmogrify the value that is passed
to the function applied by `swap!`.

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

The `:reset!-transmogrifier` option should should be nil or a side-effect free
fn of one argument, which will be passed the value that was passed `reset!` to
reset and which should return a value which will be `reset!` into the ion instead.

The `:swap!-transmogrifier` option should be nil or be a side-effect free fn of
one argument that will be passed the value of to be passed to `swap!` and
return a new value which will be passed to `swap!` instead.

You can pass either option, both options or neither option to the `ionize`
function to create an ion.

#### Create an empty ion

```clojure
(def i1 (ionize))
```

### Manipulate ions like atoms

```clojure
@i1 ;; => nil
(reset! i1 0)
@i1 ;; => 0
(swap! i1 (comp inc inc))
@i1 ;; => 2
```

### Change transmogrifiers at runtime.

You can change either transmogrifier at runtime:

```clojure
;; Require `reset!` value to be an even integer (or something that defines an even? protocol, etc).
(set-reset!-transmogrifier! i1 even?)
;; Increment the value before storing it (making it odd...)
(set-swap!-transmogrifier! i1 )
;; i1 is now
;; Let any old value be `reset!`
(set-reset!-transmogrifier! i1 identity)
```

The transmogrifiers are stored as metadata on the underlying atom.  I'm looking
for better ideas while keeping the code approximately as simple or better.

### Check to see if something is an ion

```clojure
(ion? (ionize)) ;; => true
(ion? (atom)) ;; => false
(ion? 5) ;; => false
```

I really which clojure provided `(atom? x)` or I had time to figure out how to
write it.

## License

Copyright © 2015 John Evans

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
