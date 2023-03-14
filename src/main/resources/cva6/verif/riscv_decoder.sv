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

module riscv_decoder (
    input logic [31:0] instr_i,
    output logic [2:0] format_o,
    output logic [6:0] op_o,
    output logic [2:0] funct_3_o,
    output logic [6:0] funct_7_o,
    output logic [4:0] rd_o,
    output logic [4:0] rs1_o,
    output logic [4:0] rs2_o,
    output logic [31:0] imm_o,
);
    
    assign op_o = instr_i[6:0];
    assign funct_3_o = instr_i[14:12];
    assign funct_7_o = instr_i[31:25];
    assign format_o = (
                            op_o == `ADD_OP || 
                            op_o == `SUB_OP || 
                            op_o == `SLL_OP || 
                            op_o == `SLT_OP || 
                            op_o == `SLTU_OP || 
                            op_o == `XOR_OP || 
                            op_o == `SRL_OP || 
                            op_o == `SRA_OP || 
                            op_o == `OR_OP || 
                            op_o == `AND_OP
                        ) ? `TYPE_R : 
                        (
                            (
                                op_o == `SLLI_OP ||
                                op_o == `SRLI_OP ||
                                op_o == `SRAI_OP ||
                                op_o == `JALR_OP ||
                                op_o == `LB_OP ||
                                op_o == `LH_OP ||
                                op_o == `LW_OP ||
                                op_o == `LBU_OP ||
                                op_o == `LHU_OP ||
                                op_o == `ADDI_OP ||
                                op_o == `SLTI_OP ||
                                op_o == `SLTIU_OP ||
                                op_o == `XORI_OP ||
                                op_o == `ORI_OP ||
                                op_o == `ANDI_OP
                            ) ? `TYPE_I :
                            (
                                (
                                    op_o == `SB_OP ||
                                    op_o == `SH_OP ||
                                    op_o == `SW_OP
                                ) ? `TYPE_S :
                                (
                                    (
                                        op_o == `BEQ_OP ||
                                        op_o == `BNE_OP ||
                                        op_o == `BLT_OP ||
                                        op_o == `BGE_OP ||
                                        op_o == `BLTU_OP ||
                                        op_o == `BGEU_OP
                                    ) ? `TYPE_B :
                                    (
                                        op_o == `LUI_OP ||
                                        op_o == `AUIPC_OP
                                    ) ? `TYPE_U :
                                    (
                                        (
                                            op_o == `JAL_OP
                                        ) ? `TYPE_J : `TYPE_ERR
                                    )
                                )
                            )
                        );
    assign rd_o = instr_i[11:7];
    assign rs1_o = instr_i[19:15];
    assign rs2_o = instr_i[24:20];
    assign imm_o =  (
                        (format_o == `TYPE_R) ? 32'b0 :
                        (
                            (format_o == `TYPE_I) ? {20'b0, instr_i[31:20]} :
                            (
                                (format_o == `TYPE_S) ? {20'b0, instr_i[31:25], instr_i[11:7]} :
                                (
                                    (format_o == `TYPE_B) ? {19'b0, instr_i[31], instr_i[7], instr_i[30:25], instr_i[11:6], 1'b0} :
                                    (
                                        (format_o == `TYPE_U) ? {instr_i[31:12], 12'b0} :
                                        (
                                            (format_o == `TYPE_J) ? {12'b0, instr_i[31], instr_i[19:12], instr_i[20], instr_i[30:21], 1'b0} :
                                            32'b0
                                        )
                                    )
                                )
                            )
                        )
                    );
endmodule