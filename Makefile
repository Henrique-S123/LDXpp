TEST_DIR ?= proj/tests
TESTS := $(shell find $(TEST_DIR) -type f -name '*.xpp')
DEBUG ?= false
SRC_DIR := proj/src/

ifeq ($(DEBUG),true)
    FLAGS := -Ddebug=true -cp
else
    FLAGS := -cp
endif

all:
	javacc -STATIC=false -OUTPUT_DIRECTORY=$(SRC_DIR)parser/ $(SRC_DIR)parser/ParserLDXpp.jj
	javac -d proj/out/ -Xlint:unchecked \
		$(SRC_DIR)values/*.java $(SRC_DIR)parser/*.java $(SRC_DIR)ast/*.java $(SRC_DIR)*.java $(SRC_DIR)types/*.java \
		$(SRC_DIR)commands/*.java $(SRC_DIR)debug/*.java $(SRC_DIR)defeq/*.java $(SRC_DIR)env/*.java
	echo '#!/bin/bash' > x++
	echo 'java $(FLAGS) proj/out/ proj.src.Xppint "$$@"' >> x++
	chmod +x x++

clean:
	rm -rf proj/out/ x++

run-tests:
	@echo "Running tests recursively with ./x++..."
	@total=0; pass=0; fail=0; \
	for input in $(TESTS); do \
		testname=$$(basename "$$input" .xpp); \
		expected="$${input%.xpp}.out"; \
		output="$$testname.tmp"; \
		total=$$((total + 1)); \
		if [ ! -f "$$expected" ]; then \
			echo "[SKIP] $$input — missing $$expected"; \
			continue; \
		fi; \
		./x++ "$$input" > "$$output"; \
		if diff -q "$$output" "$$expected" > /dev/null; then \
			echo "[PASS] $$input"; \
			pass=$$((pass + 1)); \
		else \
			echo "[FAIL] $$input"; \
			diff "$$output" "$$expected"; \
			fail=$$((fail + 1)); \
		fi; \
		rm -f "$$output"; \
	done; \
	echo "------------------------------"; \
	echo "✅ $$pass passed, ❌ $$fail failed (out of $$total)"; \
	if [ "$$fail" -ne 0 ]; then exit 1; fi

