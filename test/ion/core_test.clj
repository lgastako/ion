(ns ion.core-test
  (:refer-clojure :exclude [reset! swap!])
  (:require [clojure.test :refer :all]
            [ion.core :refer :all]))

(deftest creating-ions
  (testing "no args constructor"
    (is (ion? (ionize))))
  (testing "providing just a value"
    (is (ion? (ionize 5)))
    (is (ion? (ionize {:foo :bar})))
    (is (ion? (ionize nil))))
  (testing "providing a value and options"
    (is (ion? (ionize 2 :validator even?)))
    (is (ion? (ionize 5 :validator odd?)))))

(deftest works-like-atom
  (testing ":validator when valid"
    (is (ion? (ionize 2 :validator even?)))
    (is (ion? (ionize 3 :validator odd?))))
  (testing ":validator when invalid"
    (is (thrown? Exception (ionize 2 odd?)))
    (is (thrown? Exception (ionize 3 even?))))
  (testing ":meta"
    (is (= :bar (:foo (meta (ionize nil :meta {:foo :bar}))))))
  (testing "deref"
    (is (= 0 (deref (ionize 0))))
    (is (= 1 (deref (ionize 1 :meta {:foo :bar}))))
    (is (= 2 (deref (ionize 2 :validator even?))))
    (is (= 0 @(ionize 0)))
    (is (= @(ionize {:foo :bar})
           (deref (ionize {:foo :bar})))))
  (testing "resetable?"
    (is (= :bar (reset! (ionize :foo))))
    (is (= 2 (reset! (ionize 2 :validator even?))))))

(run-tests)

