# Hardware-Software Leakage Contract Synthesis Toolchain

[![arXiv](https://img.shields.io/badge/arXiv-2401.09383-b31b1b.svg?style=for-the-badge)](https://arxiv.org/abs/2401.09383)
[![Zenodo](https://img.shields.io/badge/Zenodo-10.5281/zenodo.10491534-blue.svg?style=for-the-badge)](https://doi.org/10.5281/zenodo.10491534)
[![YouTube](https://img.shields.io/badge/YouTube-ff0000.svg?style=for-the-badge&logo=youtube)](https://youtu.be/12GmWUNCiIs)

## Background

Leakage contracts have been presented
in [Hardware-Software Contracts for Secure Speculation](https://doi.org/10.1109/SP40001.2021.00036) and allow to capture
microarchitectural leakages through side channels at the ISA-level. While ideally a processor is designed with a
specific contract in mind, correct leakage contracts rarely exist for existing microarchitectures.

This toolchain allows to generate a leakage contract candidate based on a set of testcases. These test cases are
automatically generated and try to surface common leakages.

Every testcase is composed of two programs which are evaluated in parallel. The simulation shows whether the two
programs are distinguishable by an adversary and using the simulation trace and
the [RISC-V Formal Interface](https://github.com/SymbioticEDA/riscv-formal/blob/master/docs/rvfi.md), possible additions
to the contract, i.e. a set of contract atoms, can be extracted.

Eventually, these results are used to synthesize a contract using [Google OR-Tools](https://github.com/google/or-tools).

## Getting started

To get started, have a look at the main method of the `ContractGen` class.

To start contract generation, use the provided `docker-compose.yml` in the `resources` directory.

The results mentioned in the paper can be found on [Zenodo](https://doi.org/10.5281/zenodo.10491534).

## Adding support for new microarchitectures

Support for a new microarchitecture requires only a few steps:

- Embed two instances of the core in a testbench and ensure that the insturctions can be loaded into memory. Take a look
  at the Ibex core integration for an up-to-date example.
- Implement the adversary model and provide its observations as signals to the adversary module.
- Provide a way to extract the architectural state e.g.
  the [RISC-V Formal Interface](https://github.com/SymbioticEDA/riscv-formal/blob/master/docs/rvfi.md).
- Implement the microarchitecture as a new class in Java and provide the required functionality to compile the
  testbench, simulate a testcase and extract possible observations from a trace.

## Paper

This project was used in the paper "Synthesizing Hardware-Software Leakage Contracts for RISC-V Open-Source Processors"
by Gideon Mohr, Marco Guarnieri and Jan Reineke presented at DATE 2024.
