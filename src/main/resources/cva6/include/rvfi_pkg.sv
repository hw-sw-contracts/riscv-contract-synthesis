// Copyright 2020 Thales DIS design services SAS
//
// Licensed under the Solderpad Hardware Licence, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// SPDX-License-Identifier: Apache-2.0 WITH SHL-2.0
// You may obtain a copy of the License at https://solderpad.org/licenses/
//
// Original Author: Jean-Roch COULON - Thales

package rvfi_pkg;

typedef struct packed {
    logic [ariane_pkg::NRET-1:0]                  valid;
    logic [ariane_pkg::NRET*64-1:0]               order;
    logic [ariane_pkg::NRET*ariane_pkg::ILEN-1:0] insn;
    logic [ariane_pkg::NRET-1:0]                  trap;
    logic [ariane_pkg::NRET*riscv::XLEN-1:0]      cause;
    logic [ariane_pkg::NRET-1:0]                  halt;
    logic [ariane_pkg::NRET-1:0]                  intr;
    logic [ariane_pkg::NRET*2-1:0]                mode;
    logic [ariane_pkg::NRET*2-1:0]                ixl;
    logic [ariane_pkg::NRET*5-1:0]                rs1_addr;
    logic [ariane_pkg::NRET*5-1:0]                rs2_addr;
    logic [ariane_pkg::NRET*riscv::XLEN-1:0]      rs1_rdata;
    logic [ariane_pkg::NRET*riscv::XLEN-1:0]      rs2_rdata;
    logic [ariane_pkg::NRET*5-1:0]                rd_addr;
    logic [ariane_pkg::NRET*riscv::XLEN-1:0]      rd_wdata;
    logic [ariane_pkg::NRET*riscv::XLEN-1:0]      pc_rdata;
    logic [ariane_pkg::NRET*riscv::XLEN-1:0]      pc_wdata;
    logic [ariane_pkg::NRET*riscv::VLEN-1:0]      mem_addr;
    logic [ariane_pkg::NRET*riscv::PLEN-1:0]      mem_paddr;
    logic [ariane_pkg::NRET*(riscv::XLEN/8)-1:0]  mem_rmask;
    logic [ariane_pkg::NRET*(riscv::XLEN/8)-1:0]  mem_wmask;
    logic [ariane_pkg::NRET*riscv::XLEN-1:0]      mem_rdata;
    logic [ariane_pkg::NRET*riscv::XLEN-1:0]      mem_wdata;
  } rvfi_instr_t;

endpackage
