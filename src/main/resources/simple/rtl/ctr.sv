//ALU commands
`define ALU_NO_OP       8'h0
`define ALU_ADD_REG     8'h1
`define ALU_ADD_IMM     8'h2
`define ALU_MUL_REG     8'h3
`define ALU_MUL_IMM     8'h4

module ctr(
    input logic clk_i,
    input logic retire_i,
    input logic [31:0] instr_1_i,
    input logic [31:0] instr_2_i,
    input logic [7:0][31:0] regfile_1_i,
    input logic [7:0][31:0] regfile_2_i,
    output logic ctr_equiv_o,
);

    struct {
        logic [7:0] op;
        logic [7:0] rd;
        logic [7:0] rs1;
        logic [7:0] rs2;
        logic [31:0] reg_rs1;
        logic [31:0] reg_rs2;
    } ctr_observation_1;

    struct {
        logic [7:0] op;
        logic [7:0] rd;
        logic [7:0] rs1;
        logic [7:0] rs2;
        logic [31:0] reg_rs1;
        logic [31:0] reg_rs2;
    } ctr_observation_2;

    wire [7:0] op_1 = instr_1_i[31:24];
    wire [7:0] rd_1 = instr_1_i[23:16];
    wire [7:0] rs1_1 = instr_1_i[15:8];
    wire [7:0] rs2_1 = instr_1_i[7:0];
    wire [31:0] reg_rs1_1 = regfile_1_i[rs1_1];
    wire [31:0] reg_rs2_1 = regfile_1_i[rs2_1];

    wire [7:0] op_2 = instr_2_i[31:24];
    wire [7:0] rd_2 = instr_2_i[23:16];
    wire [7:0] rs1_2 = instr_2_i[15:8];
    wire [7:0] rs2_2 = instr_2_i[7:0];
    wire [31:0] reg_rs1_2 = regfile_2_i[rs1_2];
    wire [31:0] reg_rs2_2 = regfile_2_i[rs2_2];

    initial ctr_equiv_o <= 1;
    initial ctr_observation_1 <= {0, 0, 0, 0, 0, 0};
    initial ctr_observation_2 <= {0, 0, 0, 0, 0, 0};

    always @(negedge clk_i) begin
        if (retire_i == 1) begin
            
			/* CONTRACT */
            
            ctr_equiv_o <= ctr_equiv_o && (ctr_observation_1 == ctr_observation_2);
        end
    end

endmodule