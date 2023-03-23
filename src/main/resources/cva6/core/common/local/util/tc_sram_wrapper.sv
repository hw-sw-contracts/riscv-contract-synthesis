// Attention: This file has been changed to be compatible with the contractgen project. 


// Copyright 2022 Thales DIS design services SAS
//
// Licensed under the Solderpad Hardware Licence, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// SPDX-License-Identifier: Apache-2.0 WITH SHL-2.0
// You may obtain a copy of the License at https://solderpad.org/licenses/
//
// Original Author: Jean-Roch COULON (jean-roch.coulon@thalesgroup.com)

module tc_sram_wrapper #(
  parameter int unsigned NumWords     = 32'd1024, // Number of Words in data array
  parameter int unsigned DataWidth    = 32'd128,  // Data signal width
  parameter int unsigned ByteWidth    = 32'd8,    // Width of a data byte
  parameter int unsigned NumPorts     = 32'd2,    // Number of read and write ports
  parameter int unsigned Latency      = 32'd1,    // Latency when the read data is available
  parameter              SimInit      = "none",   // Simulation initialization
  parameter bit          PrintSimCfg  = 1'b0,     // Print configuration
  // DEPENDENT PARAMETERS, DO NOT OVERWRITE!
  parameter int unsigned AddrWidth = (NumWords > 32'd1) ? $clog2(NumWords) : 32'd1,
  parameter int unsigned BeWidth   = (DataWidth + ByteWidth - 32'd1) / ByteWidth, // ceil_div
  parameter int unsigned addr_t    = AddrWidth-1,
  parameter int unsigned data_t    = DataWidth-1,
  parameter int unsigned be_t      = BeWidth-1
) (
  input  logic                 clk_i,      // Clock
  input  logic                 rst_ni,     // Asynchronous reset active low
  // input ports
  input  logic  [NumPorts-1:0] req_i,      // request
  input  logic  [NumPorts-1:0] we_i,       // write enable
  input  addr_t_t [NumPorts-1:0] addr_i,     // request address
  input  data_t_t [NumPorts-1:0] wdata_i,    // write data
  input  be_t_t   [NumPorts-1:0] be_i,       // write byte enable
  // output ports
  output logic [data_t:0] [NumPorts-1:0] rdata_o     // read data
);


typedef logic [addr_t:0] addr_t_t;
typedef logic [data_t:0] data_t_t;
typedef logic [be_t:0] be_t_t;

// synthesis translate_off

  tc_sram #(
    .NumWords(NumWords),
    .DataWidth(DataWidth),
    .ByteWidth(ByteWidth),
    .NumPorts(NumPorts),
    .Latency(Latency),
    .SimInit(SimInit),
    .PrintSimCfg(PrintSimCfg)
  ) i_tc_sram (
      .clk_i    ( clk_i   ),
      .rst_ni   ( rst_ni  ),
      .req_i    ( req_i   ),
      .we_i     ( we_i    ),
      .be_i     ( be_i    ),
      .wdata_i  ( wdata_i ),
      .addr_i   ( addr_i  ),
      .rdata_o  ( rdata_o )
    );

// synthesis translate_on

endmodule
