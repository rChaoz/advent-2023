@echo off

cd input
mkdir %1
cd %1
type nul > "example1.txt"
type nul > "example2.txt"
type nul > "input.txt"
