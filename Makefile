TEST_DIR := tests
TESTS := $(shell find $(TEST_DIR) -type f -name '*.xpp')

all:
	javacc -OUTPUT_DIRECTORY=proj/parser/ proj/parser/ParserLDXpp.jj
	javac -d proj/out/ -Xlint:unchecked proj/values/*.java proj/parser/*.java proj/ast/*.java proj/*.java proj/types/*.java
	echo '#!/bin/bash' > x++
	echo 'java -cp proj/out/ proj.Xppint "$$@"' >> x++
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

