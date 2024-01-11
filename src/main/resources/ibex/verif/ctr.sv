`define TYPE_R 3'd0
`define TYPE_I 3'd1
`define TYPE_S 3'd2
`define TYPE_B 3'd3
`define TYPE_U 3'd4
`define TYPE_J 3'd5
`define TYPE_ERR 3'd6

`define LUI_OP		7'b0110111
`define AUIPC_OP 	7'b0010111
`define JAL_OP		7'b1101111
`define JALR_OP		7'b1100111
`define BEQ_OP		7'b1100011
`define BNE_OP		7'b1100011
`define BLT_OP		7'b1100011
`define BGE_OP		7'b1100011
`define BLTU_OP		7'b1100011
`define BGEU_OP		7'b1100011
`define LB_OP		7'b0000011
`define LH_OP		7'b0000011
`define LW_OP		7'b0000011
`define LBU_OP		7'b0000011
`define LHU_OP		7'b0000011
`define SB_OP		7'b0100011
`define SH_OP		7'b0100011
`define SW_OP		7'b0100011
`define ADDI_OP		7'b0010011
`define SLTI_OP		7'b0010011
`define SLTIU_OP	7'b0010011
`define XORI_OP		7'b0010011
`define ORI_OP		7'b0010011
`define ANDI_OP		7'b0010011
`define SLLI_OP		7'b0010011
`define SRLI_OP		7'b0010011
`define SRAI_OP		7'b0010011
`define ADD_OP		7'b0110011
`define SUB_OP		7'b0110011
`define SLL_OP		7'b0110011
`define SLT_OP		7'b0110011
`define SLTU_OP		7'b0110011
`define XOR_OP		7'b0110011
`define SRL_OP		7'b0110011
`define SRA_OP		7'b0110011
`define OR_OP		7'b0110011
`define AND_OP		7'b0110011

`define JALR_FUNCT_3	3'b000
`define BEQ_FUNCT_3		3'b000
`define BNE_FUNCT_3		3'b001
`define BLT_FUNCT_3		3'b100
`define BGE_FUNCT_3		3'b101
`define BLTU_FUNCT_3	3'b110
`define BGEU_FUNCT_3	3'b111
`define LB_FUNCT_3		3'b000
`define LH_FUNCT_3		3'b001
`define LW_FUNCT_3		3'b010
`define LBU_FUNCT_3		3'b100
`define LHU_FUNCT_3		3'b101
`define SB_FUNCT_3		3'b000
`define SH_FUNCT_3		3'b001
`define SW_FUNCT_3		3'b010
`define ADDI_FUNCT_3	3'b000
`define SLTI_FUNCT_3	3'b010
`define SLTIU_FUNCT_3	3'b011
`define XORI_FUNCT_3	3'b100
`define ORI_FUNCT_3		3'b110
`define ANDI_FUNCT_3	3'b111
`define SLLI_FUNCT_3	3'b001
`define SRLI_FUNCT_3	3'b101
`define SRAI_FUNCT_3	3'b101
`define ADD_FUNCT_3		3'b000
`define SUB_FUNCT_3		3'b000
`define SLL_FUNCT_3		3'b001
`define SLT_FUNCT_3		3'b010
`define SLTU_FUNCT_3	3'b011
`define XOR_FUNCT_3		3'b100
`define SRL_FUNCT_3		3'b101
`define SRA_FUNCT_3		3'b101
`define OR_FUNCT_3		3'b110
`define AND_FUNCT_3		3'b111

`define SLLI_FUNCT_7	7'b0000000
`define SRLI_FUNCT_7	7'b0000000
`define SRAI_FUNCT_7	7'b0100000
`define ADD_FUNCT_7		7'b0000000
`define SUB_FUNCT_7		7'b0100000
`define SLL_FUNCT_7		7'b0000000
`define SLT_FUNCT_7		7'b0000000
`define SLTU_FUNCT_7	7'b0000000
`define XOR_FUNCT_7		7'b0000000
`define SRL_FUNCT_7		7'b0000000
`define SRA_FUNCT_7		7'b0100000
`define OR_FUNCT_7		7'b0000000
`define AND_FUNCT_7		7'b0000000

`define MUL_OP          7'b0110011
`define MULH_OP         7'b0110011
`define MULHSU_OP       7'b0110011
`define MULHU_OP        7'b0110011
`define DIV_OP          7'b0110011
`define DIVU_OP         7'b0110011
`define REM_OP          7'b0110011
`define REMU_OP         7'b0110011

`define MUL_FUNCT_7     7'b0000001
`define MULH_FUNCT_7    7'b0000001
`define MULHSU_FUNCT_7  7'b0000001
`define MULHU_FUNCT_7   7'b0000001
`define DIV_FUNCT_7     7'b0000001
`define DIVU_FUNCT_7    7'b0000001
`define REM_FUNCT_7     7'b0000001
`define REMU_FUNCT_7    7'b0000001

`define MUL_FUNCT_3     3'b000
`define MULH_FUNCT_3    3'b001
`define MULHSU_FUNCT_3  3'b010
`define MULHU_FUNCT_3   3'b011
`define DIV_FUNCT_3     3'b100
`define DIVU_FUNCT_3    3'b101
`define REM_FUNCT_3     3'b110
`define REMU_FUNCT_3    3'b111

module ctr(
    input logic clk_i,
    input logic retire_i,
    input logic [31:0] instr_1_i,
    input logic [31:0] instr_2_i,
    input logic [4:0] rd_1,
    input logic [4:0] rd_2,
    input logic [4:0] rs1_1,
    input logic [4:0] rs1_2,
    input logic [4:0] rs2_1,
    input logic [4:0] rs2_2,
    input logic [31:0] reg_rs1_1,
    input logic [31:0] reg_rs1_2,
    input logic [31:0] reg_rs2_1,
    input logic [31:0] reg_rs2_2,
    input logic [31:0] reg_rd_1,
    input logic [31:0] reg_rd_2,
    input logic [31:0] mem_addr_1,
    input logic [31:0] mem_addr_2,
    input logic [31:0] mem_r_data_1,
    input logic [31:0] mem_r_data_2,
    input logic [3:0] mem_r_mask_1,
    input logic [3:0] mem_r_mask_2,
    input logic [31:0] mem_w_data_1,
    input logic [31:0] mem_w_data_2,
    input logic [3:0] mem_w_mask_1,
    input logic [3:0] mem_w_mask_2,
    input logic [31:0] new_pc_1,
    input logic [31:0] new_pc_2,
    output logic ctr_equiv_o,
);

    logic [5:0] old_rd_1_1 = 0;
    logic [5:0] old_rd_1_2 = 0;
    logic [5:0] old_rd_1_3 = 0;
    logic [5:0] old_rd_1_4 = 0;
    logic [5:0] old_rd_2_1 = 0;
    logic [5:0] old_rd_2_2 = 0;
    logic [5:0] old_rd_2_3 = 0;
    logic [5:0] old_rd_2_4 = 0;
    always @(negedge clk_i) begin
        if (retire_i == 1) begin
            old_rd_1_1 <= {1'b1, rd_1};
            old_rd_1_2 <= old_rd_1_1;
            old_rd_1_3 <= old_rd_1_2;
            old_rd_1_4 <= old_rd_1_3;
            old_rd_2_1 <= {1'b1, rd_2};
            old_rd_2_2 <= old_rd_2_1;
            old_rd_2_3 <= old_rd_2_2;
            old_rd_2_4 <= old_rd_2_3;
        end
    end

    logic raw_rs1_1_1;
    assign raw_rs1_1_1 = {1'b1, rs1_1} == old_rd_1_1;
    logic raw_rs2_1_1;
    assign raw_rs2_1_1 = {1'b1, rs2_1} == old_rd_1_1;
    logic waw_1_1;
    assign waw_1_1 = {1'b1, rd_1} == old_rd_1_1;

    logic raw_rs1_1_2;
    assign raw_rs1_1_2 = {1'b1, rs1_2} == old_rd_2_1;
    logic raw_rs2_1_2;
    assign raw_rs2_1_2 = {1'b1, rs2_2} == old_rd_2_1;
    logic waw_1_2;
    assign waw_1_2 = {1'b1, rd_2} == old_rd_2_1;

    logic raw_rs1_2_1;
    assign raw_rs1_2_1 = {1'b1, rs1_1} == old_rd_1_2;
    logic raw_rs2_2_1;
    assign raw_rs2_2_1 = {1'b1, rs2_1} == old_rd_1_2;
    logic waw_2_1;
    assign waw_2_1 = {1'b1, rd_1} == old_rd_1_2;

    logic raw_rs1_2_2;
    assign raw_rs1_2_2 = {1'b1, rs1_2} == old_rd_2_2;
    logic raw_rs2_2_2;
    assign raw_rs2_2_2 = {1'b1, rs2_2} == old_rd_2_2;
    logic waw_2_2;
    assign waw_2_2 = {1'b1, rd_2} == old_rd_2_2;

    logic raw_rs1_3_1;
    assign raw_rs1_3_1 = {1'b1, rs1_1} == old_rd_1_3;
    logic raw_rs2_3_1;
    assign raw_rs2_3_1 = {1'b1, rs2_1} == old_rd_1_3;
    logic waw_3_1;
    assign waw_3_1 = {1'b1, rd_1} == old_rd_1_3;

    logic raw_rs1_3_2;
    assign raw_rs1_3_2 = {1'b1, rs1_2} == old_rd_2_3;
    logic raw_rs2_3_2;
    assign raw_rs2_3_2 = {1'b1, rs2_2} == old_rd_2_3;
    logic waw_3_2;
    assign waw_3_2 = {1'b1, rd_2} == old_rd_2_3;

    logic raw_rs1_4_1;
    assign raw_rs1_4_1 = {1'b1, rs1_1} == old_rd_1_4;
    logic raw_rs2_4_1;
    assign raw_rs2_4_1 = {1'b1, rs2_1} == old_rd_1_4;
    logic waw_4_1;
    assign waw_4_1 = {1'b1, rd_1} == old_rd_1_4;

    logic raw_rs1_4_2;
    assign raw_rs1_4_2 = {1'b1, rs1_2} == old_rd_2_4;
    logic raw_rs2_4_2;
    assign raw_rs2_4_2 = {1'b1, rs2_2} == old_rd_2_4;
    logic waw_4_2;
    assign waw_4_2 = {1'b1, rd_2} == old_rd_2_4;

    struct {
        logic [3:0] format;
        logic [7:0] op;
        logic [3:0] funct_3;
        logic [7:0] funct_7;
        logic [5:0] rd;
        logic [5:0] rs1;
        logic [5:0] rs2;
        logic [32:0] imm;
        logic [32:0] reg_rs1;
        logic [32:0] reg_rs2;
        logic [32:0] reg_rd;
        logic [32:0] mem_addr;
        logic [32:0] mem_r_data;
        logic [32:0] mem_w_data;
        logic [2:0] is_branch;
        logic [2:0] branch_taken;
        logic [2:0] is_aligned;
        logic [2:0] is_half_aligned;
        logic [32:0] new_pc;
    } ctr_observation_1;

    struct {
        logic [3:0] format;
        logic [7:0] op;
        logic [3:0] funct_3;
        logic [7:0] funct_7;
        logic [5:0] rd;
        logic [5:0] rs1;
        logic [5:0] rs2;
        logic [32:0] imm;
        logic [32:0] reg_rs1;
        logic [32:0] reg_rs2;
        logic [32:0] reg_rd;
        logic [32:0] mem_addr;
        logic [32:0] mem_r_data;
        logic [32:0] mem_w_data;
        logic [2:0] is_branch;
        logic [2:0] branch_taken;
        logic [2:0] is_aligned;
        logic [2:0] is_half_aligned;
        logic [32:0] new_pc;
    } ctr_observation_2;

    
    logic [6:0] op_1;
    logic [2:0] funct_3_1;
    logic [6:0] funct_7_1;
    logic [2:0] format_1;
    logic [31:0] imm_1;

    logic is_branch_1;
    logic branch_taken_1;
    logic is_aligned_1;
    logic is_half_aligned_1;
    assign is_branch_1 = (op_1 == `JAL_OP) || (op_1 == `JALR_OP) || (op_1 == `BEQ_OP); //all branches have the same opcode
    assign branch_taken_1 =
            (op_1 == `JAL_OP)
        ||  (op_1 == `JALR_OP)
        ||  ((op_1 == `BEQ_OP) && (funct_3_1 == `BEQ_FUNCT_3) && (reg_rs1_1 == reg_rs2_1))
        ||  ((op_1 == `BNE_OP) && (funct_3_1 == `BNE_FUNCT_3) && (reg_rs1_1 != reg_rs2_1))
        ||  ((op_1 == `BLT_OP) && (funct_3_1 == `BLT_FUNCT_3) && ($signed(reg_rs1_1) < $signed(reg_rs2_1)))
        ||  ((op_1 == `BGE_OP) && (funct_3_1 == `BGE_FUNCT_3) && ($signed(reg_rs1_1) >= $signed(reg_rs2_1)))
        ||  ((op_1 == `BLTU_OP) && (funct_3_1 == `BLTU_FUNCT_3) && (reg_rs1_1 < reg_rs2_1))
        ||  ((op_1 == `BGEU_OP) && (funct_3_1 == `BGEU_FUNCT_3) && (reg_rs1_1 >= reg_rs2_1));
    assign is_aligned_1 = mem_addr_1[1:0] == 2'b00;
    assign is_half_aligned_1 = mem_addr_1[1:0] != 2'b11;

    riscv_decoder decoder_1 (
        .instr_i            (instr_1_i),
        .format_o           (format_1),
        .op_o               (op_1),
        .funct_3_o          (funct_3_1),
        .funct_7_o          (funct_7_1),
        .rd_o               (),
        .rs1_o              (),
        .rs2_o              (),
        .imm_o              (imm_1),
    );

    logic [6:0] op_2;
    logic [2:0] funct_3_2;
    logic [6:0] funct_7_2;
    logic [2:0] format_2;
    logic [31:0] imm_2;

    logic is_branch_2;
    logic branch_taken_2;
    logic is_aligned_2;
    logic is_half_aligned_2;
    assign is_branch_2 = (op_2 == `JAL_OP) || (op_2 == `JALR_OP) || (op_2 == `BEQ_OP); //all branches have the same opcode
    assign branch_taken_2 =
        (op_2 == `JAL_OP)
            ||  (op_2 == `JALR_OP)
            ||  ((op_2 == `BEQ_OP) && (funct_3_2 == `BEQ_FUNCT_3) && (reg_rs1_2 == reg_rs2_2))
            ||  ((op_2 == `BNE_OP) && (funct_3_2 == `BNE_FUNCT_3) && (reg_rs1_2 != reg_rs2_2))
            ||  ((op_2 == `BLT_OP) && (funct_3_2 == `BLT_FUNCT_3) && ($signed(reg_rs1_2) < $signed(reg_rs2_2)))
            ||  ((op_2 == `BGE_OP) && (funct_3_2 == `BGE_FUNCT_3) && ($signed(reg_rs1_2) >= $signed(reg_rs2_2)))
            ||  ((op_2 == `BLTU_OP) && (funct_3_2 == `BLTU_FUNCT_3) && (reg_rs1_2 < reg_rs2_2))
            ||  ((op_2 == `BGEU_OP) && (funct_3_2 == `BGEU_FUNCT_3) && (reg_rs1_2 >= reg_rs2_2));
    assign is_aligned_2 = mem_addr_2[1:0] == 2'b00;
    assign is_half_aligned_2 = mem_addr_2[1:0] != 2'b11;

    riscv_decoder decoder_2 (
        .instr_i            (instr_2_i),
        .format_o           (format_2),
        .op_o               (op_2),
        .funct_3_o          (funct_3_2),
        .funct_7_o          (funct_7_2),
        .rd_o               (),
        .rs1_o              (),
        .rs2_o              (),
        .imm_o              (imm_2),
    );

    initial ctr_equiv_o <= 1;
    initial ctr_observation_1 <= {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    initial ctr_observation_2 <= {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    integer i;
    logic [31:0] temp;
    always @(negedge clk_i) begin
        if (retire_i == 1) begin
            
			/* CONTRACT */
            
            ctr_equiv_o <= ctr_equiv_o && (ctr_observation_1 == ctr_observation_2);
        end
    end

endmodule : ctr