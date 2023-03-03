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
    input logic [31:0][31:0] regfile_1_i,
    input logic [31:0][31:0] regfile_2_i,
    input logic [31:0][31:0] mem_addr_1_i,
    input logic [31:0][31:0] mem_addr_2_i,
    input logic [7:0][31:0] mem_data_1_i,
    input logic [7:0][31:0] mem_data_2_i,
    output logic ctr_equiv_o,
);

    struct {
        logic [2:0] format;
        logic [6:0] op;
        logic [2:0] funct_3;
        logic [6:0] funct_7;
        logic [4:0] rd;
        logic [4:0] rs1;
        logic [4:0] rs2;
        logic [31:0] imm;
        logic [31:0] reg_rs1;
        logic [31:0] reg_rs2;
        logic [31:0] mem_rs1;
        logic [31:0] mem_rs2;
        logic [31:0] reg_rd;
        logic [31:0] mem_addr;
        logic [31:0] mem_r_data;
        logic [31:0] mem_w_data;
    } ctr_observation_1;

    struct {
        logic [2:0] format;
        logic [6:0] op;
        logic [2:0] funct_3;
        logic [6:0] funct_7;
        logic [4:0] rd;
        logic [4:0] rs1;
        logic [4:0] rs2;
        logic [31:0] imm;
        logic [31:0] reg_rs1;
        logic [31:0] reg_rs2;
        logic [31:0] mem_rs1;
        logic [31:0] mem_rs2;
        logic [31:0] reg_rd;
        logic [31:0] mem_addr;
        logic [31:0] mem_r_data;
        logic [31:0] mem_w_data;
    } ctr_observation_2;

    
    logic [6:0] op_1;
    logic [2:0] funct_3_1;
    logic [6:0] funct_7_1;
    logic [2:0] format_1;
    logic [4:0] rd_1;
    logic [4:0] rs1_1;
    logic [4:0] rs2_1;
    logic [31:0] imm_1; 
    logic [31:0] reg_rs1_1;
    assign reg_rs1_1 = regfile_1_i[rs1_1];
    logic [31:0] reg_rs2_1;
    assign reg_rs2_1 = regfile_1_i[rs2_1];
    logic [31:0] mem_rs1_1;
    logic [31:0] mem_rs2_1;

    riscv_decoder decoder_1 (
        .instr_i            (instr_1_i),
        .format_o           (format_1),
        .op_o               (op_1),
        .funct_3_o          (funct_3_1),
        .funct_7_o          (funct_7_1),
        .rd_o               (rd_1),
        .rs1_o              (rs1_1),
        .rs2_o              (rs2_1),
        .imm_o              (imm_1),
    );

    logic [6:0] op_2;
    logic [2:0] funct_3_2;
    logic [6:0] funct_7_2;
    logic [2:0] format_2;
    logic [4:0] rd_2;
    logic [4:0] rs1_2;
    logic [4:0] rs2_2;
    logic [31:0] imm_2; 
    logic [31:0] reg_rs1_2;
    assign reg_rs1_2 = regfile_2_i[rs1_2];
    logic [31:0] reg_rs2_2;
    assign reg_rs2_2 = regfile_2_i[rs2_2];
    logic [31:0] mem_rs1_2;
    logic [31:0] mem_rs2_2;

    riscv_decoder decoder_2 (
        .instr_i            (instr_2_i),
        .format_o           (format_2),
        .op_o               (op_2),
        .funct_3_o          (funct_3_2),
        .funct_7_o          (funct_7_2),
        .rd_o               (rd_2),
        .rs1_o              (rs1_2),
        .rs2_o              (rs2_2),
        .imm_o              (imm_2),
    );

    initial ctr_equiv_o <= 1;
    initial ctr_observation_1 <= {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    initial ctr_observation_2 <= {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    integer i;
    logic [31:0] temp;
    always @(negedge clk_i) begin
        mem_rs1_1 = reg_rs1_1;
        for (i = 0; i < 32; i = i + 1) begin
            if ((mem_addr_1_i[i] + 0) == reg_rs1_1) begin
                mem_rs1_1[(0*8)+7:(0*8)] = mem_data_1_i[i];
            end
            if ((mem_addr_1_i[i] + 1) == reg_rs1_1) begin
                mem_rs1_1[(1*8)+7:(1*8)] = mem_data_1_i[i];
            end
            if ((mem_addr_1_i[i] + 2) == reg_rs1_1) begin
                mem_rs1_1[(2*8)+7:(2*8)] = mem_data_1_i[i];
            end
            if ((mem_addr_1_i[i] + 3) == reg_rs1_1) begin
                mem_rs1_1[(3*8)+7:(3*8)] = mem_data_1_i[i];
            end
        end
        mem_rs2_1 = reg_rs2_1;
        for (i = 0; i < 32; i = i + 1) begin
            if ((mem_addr_1_i[i] + 0) == reg_rs2_1) begin
                mem_rs2_1[(0*8)+7:(0*8)] = mem_data_1_i[i];
            end
            if ((mem_addr_1_i[i] + 1) == reg_rs2_1) begin
                mem_rs2_1[(1*8)+7:(1*8)] = mem_data_1_i[i];
            end
            if ((mem_addr_1_i[i] + 2) == reg_rs2_1) begin
                mem_rs2_1[(2*8)+7:(2*8)] = mem_data_1_i[i];
            end
            if ((mem_addr_1_i[i] + 3) == reg_rs2_1) begin
                mem_rs2_1[(3*8)+7:(3*8)] = mem_data_1_i[i];
            end
        end
        mem_rs1_2 = reg_rs1_2;
        for (i = 0; i < 32; i = i + 1) begin
            if ((mem_addr_2_i[i] + 0) == reg_rs1_2) begin
                mem_rs1_2[(0*8)+7:(0*8)] = mem_data_2_i[i];
            end
            if ((mem_addr_2_i[i] + 1) == reg_rs1_2) begin
                mem_rs1_2[(1*8)+7:(1*8)] = mem_data_2_i[i];
            end
            if ((mem_addr_2_i[i] + 2) == reg_rs1_2) begin
                mem_rs1_2[(2*8)+7:(2*8)] = mem_data_2_i[i];
            end
            if ((mem_addr_2_i[i] + 3) == reg_rs1_2) begin
                mem_rs1_2[(3*8)+7:(3*8)] = mem_data_2_i[i];
            end
        end
        mem_rs2_2 = reg_rs2_2;
        for (i = 0; i < 32; i = i + 1) begin
            if ((mem_addr_2_i[i] + 0) == reg_rs2_2) begin
                mem_rs2_2[(0*8)+7:(0*8)] = mem_data_2_i[i];
            end
            if ((mem_addr_2_i[i] + 1) == reg_rs2_2) begin
                mem_rs2_2[(1*8)+7:(1*8)] = mem_data_2_i[i];
            end
            if ((mem_addr_2_i[i] + 2) == reg_rs2_2) begin
                mem_rs2_2[(2*8)+7:(2*8)] = mem_data_2_i[i];
            end
            if ((mem_addr_2_i[i] + 3) == reg_rs2_2) begin
                mem_rs2_2[(3*8)+7:(3*8)] = mem_data_2_i[i];
            end
        end
    //end
    //always @(posedge retire_i) begin
        if (retire_i == 1) begin
            
			/* CONTRACT */
            
            ctr_equiv_o <= ctr_equiv_o && (ctr_observation_1 == ctr_observation_2);
        end
    end

endmodule