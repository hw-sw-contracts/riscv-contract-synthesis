//ALU commands
`define ALU_NO_OP       8'h0
`define ALU_ADD_REG     8'h1
`define ALU_ADD_IMM     8'h2
`define ALU_MUL_REG     8'h3
`define ALU_MUL_IMM     8'h4
    
module core (
    input logic clock_i,
    input logic [31:0] instr_i,
    output logic [15:0] instr_addr_o,
    output logic retire_o,
    output logic [31:0] retire_instr_o,
    output logic fetch_o,
    output logic [7:0][31:0] regfile_o,
);

    wire [7:0] op2 = instr_i[7:0];
    wire [7:0] op1 = instr_i[15:8];
    wire [7:0] res = instr_i[23:16];
    wire [7:0] op = instr_i[31:24];

    logic [7:0][31:0] regfile;
    logic busy;

    logic [31:0] accumulator = 0;
    logic [31:0] factor_1 = 0;
    logic [31:0] factor_2 = 0;

    logic [31:0] retire_instr;

    initial begin
        instr_addr_o <= 16'h10;
        retire_o <= 0;
        fetch_o <= 1;
        busy <= 0;
        regfile[0] <= 0;
        regfile[1] <= 0;
        regfile[2] <= 0;
        regfile[3] <= 0;
        regfile[4] <= 0;
        regfile[5] <= 0;
        regfile[6] <= 0;
        regfile[7] <= 0;
    end

    assign retire_o = !busy;
    assign retire_instr = busy ? 32'b0 : instr_i;
    assign fetch_o = !busy;
    assign regfile_o = regfile;
    assign retire_instr_o = retire_instr;

    always @(posedge clock_i) begin
        case(op)
        `ALU_NO_OP:
        begin
            instr_addr_o <= instr_addr_o + 16'h4;
        end
        `ALU_ADD_REG: 
        begin
            case (res)
                0: regfile[0] <= regfile[op1] + regfile[op2];
                1: regfile[1] <= regfile[op1] + regfile[op2];
                2: regfile[2] <= regfile[op1] + regfile[op2];
                3: regfile[3] <= regfile[op1] + regfile[op2];
                4: regfile[4] <= regfile[op1] + regfile[op2];
                5: regfile[5] <= regfile[op1] + regfile[op2];
                6: regfile[6] <= regfile[op1] + regfile[op2];
                7: regfile[7] <= regfile[op1] + regfile[op2];
            endcase
            //regfile[res] <= regfile[op1] + regfile[op2];
            instr_addr_o <= instr_addr_o + 16'h4;
        end
        `ALU_ADD_IMM: 
        begin
            case (res)
                0: regfile[0] <= regfile[op1] + op2;
                1: regfile[1] <= regfile[op1] + op2;
                2: regfile[2] <= regfile[op1] + op2;
                3: regfile[3] <= regfile[op1] + op2;
                4: regfile[4] <= regfile[op1] + op2;
                5: regfile[5] <= regfile[op1] + op2;
                6: regfile[6] <= regfile[op1] + op2;
                7: regfile[7] <= regfile[op1] + op2;
            endcase
            //regfile[res] <= regfile[op1] + op2;
            instr_addr_o <= instr_addr_o + 16'h4;
        end
        `ALU_MUL_REG: 
        begin
        //    case (res)
        //        0: regfile[0] <= regfile[op1] * regfile[op2];
        //        1: regfile[1] <= regfile[op1] * regfile[op2];
        //        2: regfile[2] <= regfile[op1] * regfile[op2];
        //        3: regfile[3] <= regfile[op1] * regfile[op2];
        //        4: regfile[4] <= regfile[op1] * regfile[op2];
        //        5: regfile[5] <= regfile[op1] * regfile[op2];
        //        6: regfile[6] <= regfile[op1] * regfile[op2];
        //        7: regfile[7] <= regfile[op1] * regfile[op2];
        //    endcase
        //    regfile[res] <= regfile[op1] * regfile[op2];
        //    instr_addr_o <= instr_addr_o + 16'h4;
        //    fetch_o <= 1;

            if (!busy) begin
                factor_1 = regfile[op1];
                factor_2 = regfile[op2];
                accumulator = 0;
                busy = 1;
            end
            if (factor_2 != 0) begin
                accumulator = accumulator + factor_1;
                factor_2 = factor_2 - 1;
                busy = 1;
            end
            if (factor_2 == 0) begin
                case (res)
                    0: regfile[0] <= accumulator;
                    1: regfile[1] <= accumulator;
                    2: regfile[2] <= accumulator;
                    3: regfile[3] <= accumulator;
                    4: regfile[4] <= accumulator;
                    5: regfile[5] <= accumulator;
                    6: regfile[6] <= accumulator;
                    7: regfile[7] <= accumulator;
                endcase
                //regfile[res] <= accumulator;
                factor_1 <= 0;
                factor_2 <= 0;
                accumulator <= 0;
                busy = 0;
                instr_addr_o <= instr_addr_o + 16'h4;
            end
        end
        `ALU_MUL_IMM: 
        begin
        //    case (res)
        //        0: regfile[0] <= regfile[op1] * op2;
        //        1: regfile[1] <= regfile[op1] * op2;
        //        2: regfile[2] <= regfile[op1] * op2;
        //        3: regfile[3] <= regfile[op1] * op2;
        //        4: regfile[4] <= regfile[op1] * op2;
        //        5: regfile[5] <= regfile[op1] * op2;
        //        6: regfile[6] <= regfile[op1] * op2;
        //        7: regfile[7] <= regfile[op1] * op2;
        //    endcase
        //    //regfile[res] <= regfile[op1] * op2;
        //    instr_addr_o <= instr_addr_o + 16'h4;
        //    fetch_o <= 1;
        
            if (!busy) begin
                factor_1 = regfile[op1];
                factor_2 = op2;
                accumulator = 0;
                busy = 1;
            end
            if (factor_2 != 0) begin
                accumulator = accumulator + factor_1;
                factor_2 = factor_2 - 1;
            end
            if (factor_2 == 0) begin
                case (res)
                    0: regfile[0] <= accumulator;
                    1: regfile[1] <= accumulator;
                    2: regfile[2] <= accumulator;
                    3: regfile[3] <= accumulator;
                    4: regfile[4] <= accumulator;
                    5: regfile[5] <= accumulator;
                    6: regfile[6] <= accumulator;
                    7: regfile[7] <= accumulator;
                endcase
                //regfile[res] <= accumulator;
                factor_1 <= 0;
                factor_2 <= 0;
                accumulator <= 0;
                busy = 0;
                instr_addr_o <= instr_addr_o + 16'h4;
            end   
        end 
        endcase
    end
endmodule