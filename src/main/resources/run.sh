#!/usr/bin/env bash

cd /home/yosys/project
mvn package
java -Xmx8192m -cp target/RISCV-Verilog-1.0-SNAPSHOT.jar src/main/java/contractgen/ContractGen.java
