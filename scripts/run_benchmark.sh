#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
mvn -q clean test
java -cp target/classes Benchmark
