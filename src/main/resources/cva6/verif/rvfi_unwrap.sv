import rvfi_pkg::*;

module rvfi_unwrap (
    input rvfi_instr_t rvfi_instr_i,
    output logic [ariane_pkg::NRET-1:0]                 valid_o,
    output logic [ariane_pkg::NRET*64-1:0]              order_o,
    output logic [ariane_pkg::NRET*ariane_pkg::ILEN-1:0] insn_o,
    output logic [ariane_pkg::NRET-1:0]                 trap_o,
    output logic [ariane_pkg::NRET*riscv::XLEN-1:0]     cause_o,
    output logic [ariane_pkg::NRET-1:0]                 halt_o,
    output logic [ariane_pkg::NRET-1:0]                 intr_o,
    output logic [ariane_pkg::NRET*2-1:0]               mode_o,
    output logic [ariane_pkg::NRET*2-1:0]               ixl_o,
    output logic [ariane_pkg::NRET*5-1:0]               rs1_addr_o,
    output logic [ariane_pkg::NRET*5-1:0]               rs2_addr_o,
    output logic [ariane_pkg::NRET*riscv::XLEN-1:0]     rs1_rdata_o,
    output logic [ariane_pkg::NRET*riscv::XLEN-1:0]     rs2_rdata_o,
    output logic [ariane_pkg::NRET*5-1:0]               rd_addr_o,
    output logic [ariane_pkg::NRET*riscv::XLEN-1:0]     rd_wdata_o,

    output logic [ariane_pkg::NRET*riscv::XLEN-1:0]     pc_rdata_o,
    output logic [ariane_pkg::NRET*riscv::XLEN-1:0]     pc_wdata_o,

    output logic [ariane_pkg::NRET*riscv::XLEN-1:0]     mem_addr_o,
    output logic [ariane_pkg::NRET*(riscv::XLEN/8)-1:0] mem_rmask_o,
    output logic [ariane_pkg::NRET*(riscv::XLEN/8)-1:0] mem_wmask_o,
    output logic [ariane_pkg::NRET*riscv::XLEN-1:0]     mem_rdata_o,
    output logic [ariane_pkg::NRET*riscv::XLEN-1:0]     mem_wdata_o
);

    assign valid_o = rvfi_instr_i.valid;
    assign order_o = rvfi_instr_i.order;
    assign insn_o = rvfi_instr_i.insn;
    assign trap_o = rvfi_instr_i.trap;
    assign cause_o = rvfi_instr_i.cause;
    assign halt_o = rvfi_instr_i.halt;
    assign intr_o = rvfi_instr_i.intr;
    assign mode_o = rvfi_instr_i.mode;
    assign ixl_o = rvfi_instr_i.ixl;
    assign rs1_addr_o = rvfi_instr_i.rs1_addr;
    assign rs2_addr_o = rvfi_instr_i.rs2_addr;
    assign rs1_rdata_o = rvfi_instr_i.rs1_rdata;
    assign rs2_rdata_o = rvfi_instr_i.rs2_rdata;
    assign rd_addr_o = rvfi_instr_i.rd_addr;
    assign rd_wdata_o = rvfi_instr_i.rd_wdata;

    assign pc_rdata_o = rvfi_instr_i.pc_rdata;
    assign pc_wdata_o = rvfi_instr_i.pc_wdata;
    
    assign mem_addr_o = rvfi_instr_i.mem_addr;
    assign mem_rmask_o = rvfi_instr_i.mem_rmask;
    assign mem_wmask_o = rvfi_instr_i.mem_wmask;
    assign mem_rdata_o = rvfi_instr_i.mem_rdata;
    assign mem_wdata_o = rvfi_instr_i.mem_wdata;

endmodule