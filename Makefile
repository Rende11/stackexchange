.PHONY: test

test:
		clojure -M:test

repl:
		clj -M:cider

run:
		clj -M -m stackexchange.core