`define NO_OP     32'h0

module instr_mem_1 (
    input logic clk_i,
    input logic enable_i,
    input  logic [15:0]  instr_addr_i,
    output logic [31:0] instr_o,
);
    (* nomem2reg *)
    logic [31:0] mem [31:0];

    assign instr_o = enable_i ? ((instr_addr_i >> 2) <= 31 ? mem[(instr_addr_i >> 2)] : `NO_OP) : `NO_OP;

    /* Instruction Memory 1 - Variables */

    initial begin
        mem = 0;
        
		/* Instruction Memory 1 - Instructions */
		
        instr_o <= 32'h0;
    end
endmodule