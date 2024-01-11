package cva6_axi_pkg;
  // TODO how to import automatically from CVA6Cfg
  localparam CVA6ConfigXlen = 32;
  localparam CVA6ConfigAxiIdWidth = 4;
  localparam CVA6ConfigAxiAddrWidth = 64;
  localparam CVA6ConfigAxiDataWidth = 64;
  localparam CVA6ConfigFetchUserEn = 0;
  localparam CVA6ConfigFetchUserWidth = CVA6ConfigXlen;
  localparam CVA6ConfigDataUserEn = 0;
  localparam CVA6ConfigDataUserWidth = CVA6ConfigXlen;
  localparam CVA6ConfigAxiUserWidth = CVA6ConfigXlen;
  
  // AXI types
  typedef struct packed {
        logic [CVA6ConfigAxiIdWidth-1:0]       id;
        logic [CVA6ConfigAxiAddrWidth-1:0]     addr;
        axi_pkg::len_t                       len;
        axi_pkg::size_t                      size;
        axi_pkg::burst_t                     burst;
        logic                                lock;
        axi_pkg::cache_t                     cache;
        axi_pkg::prot_t                      prot;
        axi_pkg::qos_t                       qos;
        axi_pkg::region_t                    region;
        logic [CVA6ConfigAxiUserWidth-1:0]     user;
  } axi_ar_chan_t;
  typedef struct packed {
        logic [CVA6ConfigAxiIdWidth-1:0]       id;
        logic [CVA6ConfigAxiAddrWidth-1:0]     addr;
        axi_pkg::len_t                       len;
        axi_pkg::size_t                      size;
        axi_pkg::burst_t                     burst;
        logic                                lock;
        axi_pkg::cache_t                     cache;
        axi_pkg::prot_t                      prot;
        axi_pkg::qos_t                       qos;
        axi_pkg::region_t                    region;
        axi_pkg::atop_t                      atop;
        logic [CVA6ConfigAxiUserWidth-1:0]     user;
  } axi_aw_chan_t;
  typedef struct packed {
        logic [CVA6ConfigAxiDataWidth-1:0]     data;
        logic [(CVA6ConfigAxiDataWidth/8)-1:0] strb;
        logic                                last;
        logic [CVA6ConfigAxiUserWidth-1:0]     user;
  } axi_w_chan_t;
  typedef struct packed {
        logic [CVA6ConfigAxiIdWidth-1:0]       id;
        axi_pkg::resp_t                      resp;
        logic [CVA6ConfigAxiUserWidth-1:0]     user;
  } b_chan_t;
  typedef struct packed {
        logic [CVA6ConfigAxiIdWidth-1:0]       id;
        logic [CVA6ConfigAxiDataWidth-1:0]     data;
        axi_pkg::resp_t                      resp;
        logic                                last;
        logic [CVA6ConfigAxiUserWidth-1:0]     user;
  } r_chan_t;
  typedef struct packed {
        axi_aw_chan_t                aw;
        logic                        aw_valid;
        axi_w_chan_t                 w;
        logic                        w_valid;
        logic                        b_ready;
        axi_ar_chan_t                ar;
        logic                        ar_valid;
        logic                        r_ready;
  } noc_req_t;
  typedef struct packed {
        logic                        aw_ready;
        logic                        ar_ready;
        logic                        w_ready;
        logic                        b_valid;
        b_chan_t                     b;
        logic                        r_valid;
        r_chan_t                     r;
  } noc_resp_t;
  
endpackage
