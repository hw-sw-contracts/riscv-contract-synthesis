#!/usr/bin/env bash

cd /home/yosys/project
mvn package
java -cp target/RISCV-Verilog-1.0-SNAPSHOT.jar src/main/java/contractgen/ContractGen.java
