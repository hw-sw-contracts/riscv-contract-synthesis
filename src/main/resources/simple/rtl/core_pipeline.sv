//ALU commands
`define ALU_NO_OP       8'h0
`define ALU_ADD_REG     8'h1
`define ALU_ADD_IMM     8'h2
`define ALU_MUL_REG     8'h3
`define ALU_MUL_IMM     8'h4
    
module core_pipeline (
    input logic clock_i,
    input logic [31:0] instr_i,
    output logic [15:0] instr_addr_o,
    output logic retire_o,
    output logic [31:0] retire_instr_o,
    output logic fetch_o,
    output logic [7:0][31:0] regfile_o,
);

    logic [7:0][31:0] regfile;

    logic stall = 0;
    logic retire = 0;
    logic [31:0] retire_instr = 32'b0;


    wire [31:0] instr_fetch = instr_i;
    wire [7:0] op_fetch  = instr_i[31:24];
    wire [7:0] res_fetch = instr_i[23:16];
    wire [7:0] op1_fetch = instr_i[15:8];
    wire [7:0] op2_fetch = instr_i[7:0];

    logic enable_ex = 0;
    logic [31:0] instr_ex = 32'b0;
    logic [7:0] op_ex = `ALU_NO_OP;
    logic [7:0] res_ex = 8'b0;
    logic [31:0] op1_ex = 32'b0;
    logic [31:0] op2_ex = 32'b0;

    logic busy = 0;
    logic [31:0] instr_busy = 32'b0;
    logic [31:0] accumulator = 32'b0;
    logic [31:0] factor_1 = 32'b0;
    logic [31:0] factor_2 = 32'b0;
    logic [32:0] result = 32'b0;

    logic enable_wb = 0;
    logic [31:0] instr_wb = 32'b0;
    logic [7:0] op_wb = `ALU_NO_OP;
    logic [7:0] res_wb = 8'b0;
    logic [31:0] op1_wb = 32'b0;
    logic [31:0] op2_wb = 32'b0;

    assign fetch_o = !stall;
    assign retire_o = retire;
    assign retire_instr_o = retire_instr;
    assign regfile_o = regfile;

    initial begin
        instr_addr_o <= 16'h10;
        retire_o <= 0;
        fetch_o <= 1;
        regfile <= 0;
    end
    
    // fetch
    always @(posedge clock_i) begin
        if (!stall) begin
            instr_ex <= instr_fetch;
            op1_ex <= regfile[instr_fetch[15:8]];
            case(op_fetch)
            `ALU_ADD_REG, `ALU_MUL_REG:
                begin
                    op2_ex <= regfile[instr_fetch[7:0]]; 
                end
            `ALU_ADD_IMM, `ALU_MUL_IMM:
                begin
                    op2_ex <= {24'b0, instr_fetch[7:0]}; 
                end
            endcase
            res_ex <= instr_fetch[23:16];
            op_ex <= instr_fetch[31:24];
        end
        enable_ex <= 1;
    end


    // execute
    always @(posedge clock_i) begin
        if (enable_ex) begin
            // start next instruction if not stalled
            if (!stall) begin
                // we can already pass this information as no wb will occur until this instruction is ready
                instr_wb <= instr_ex;
                op1_wb <= op1_ex;
                op2_wb <= op2_ex;
                op_wb <= op_ex;
                res_wb <= res_ex;
                case(op_ex)
                `ALU_ADD_REG, `ALU_ADD_IMM: 
                    begin
                        result <= op1_ex + op2_ex;
                    end
                `ALU_MUL_REG, `ALU_MUL_IMM: 
                    begin
                        factor_1 = op1_ex;
                        factor_2 = op2_ex;
                        accumulator = 0;
                        busy = 1;
                    end
                endcase
            end
            // proceed with multiplication
            if (busy) begin
                if (factor_2 != 0) begin
                    accumulator = accumulator + factor_1;
                    factor_2 = factor_2 - 1;
                    busy = 1;
                end
                if (factor_2 == 0) begin
                    result <= accumulator;
                    accumulator <= 0;
                    factor_1 <= 0;
                    factor_2 <= 0;
                    busy = 0;
                end
            end

            // check if we need to stall
            if (busy)
            begin
                stall <= 1;
            end
            else
            begin
                stall <= 0;
                enable_wb <= 1;
                instr_addr_o <= instr_addr_o + 16'h4;
            end
        end else begin
            // initial pipeline fill
            instr_addr_o <= instr_addr_o + 16'h4;
        end
    end

    // write-back
    always @(posedge clock_i) begin
        if (enable_wb) begin
            if (!stall)
            begin
                case (res_wb)
                    0: regfile[0] <= result;
                    1: regfile[1] <= result;
                    2: regfile[2] <= result;
                    3: regfile[3] <= result;
                    4: regfile[4] <= result;
                    5: regfile[5] <= result;
                    6: regfile[6] <= result;
                    7: regfile[7] <= result;
                endcase
                //regfile[res] <= result;
                retire <= 1;
                retire_instr <= instr_wb;
            end
            else
            begin
                retire_instr <= 32'b0;
                retire <= 0;
            end
        end
    end
endmodule