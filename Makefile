.EXPORT_ALL_VARIABLES:
include .env

.PHONY: test

test:
		clojure -M:test

repl:
		clj -M:cider

run-clj:
		clj -M:run

run-jar:
		java -jar ./target/stackexchange.jar

build:
		rm -rf ./classes
		mkdir ./classes
		clj -e "(compile 'stackexchange.core)"
		clj -M:uberjar --main-class stackexchange.core --target ./target/stackexchange.jar
