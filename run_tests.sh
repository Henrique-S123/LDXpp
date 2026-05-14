#!/bin/sh

make clean
make
make run-tests $1
