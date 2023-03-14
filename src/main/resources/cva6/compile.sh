cd "$1" || exit
export LR_VERIF_OUT_DIR=$2
rm -r "$LR_VERIF_OUT_DIR"
mkdir -p "$LR_VERIF_OUT_DIR"


#-------------------------------------------------------------------------
# use sv2v to convert all SystemVerilog files to Verilog
#-------------------------------------------------------------------------
export directories=( \
	"core/core/alu.sv" \
	"core/core/amo_buffer.sv" \
	"core/core/ariane_regfile_ff.sv" \
	"core/core/ariane.sv" \
	"core/corev_apu/tb/axi_adapter.sv" \
	"core/core/axi_shim.sv" \
	"core/core/frontend/bht.sv" \
	"core/core/branch_unit.sv" \
	"core/core/frontend/btb.sv" \
	"core/core/cache_subsystem/cache_ctrl.sv" \
	"core/core/commit_stage.sv" \
	"core/core/compressed_decoder.sv" \
	"core/core/controller.sv" \
	"core/vendor/pulp-platform/fpnew/src/fpu_div_sqrt_mvp/hdl/control_mvp.sv" \
	"core/common/submodules/common_cells/src/counter.sv" \
	"core/core/csr_buffer.sv" \
	"core/core/csr_regfile.sv" \
	"core/core/cache_subsystem/cva6_icache_axi_wrapper.sv" \
	"core/core/cache_subsystem/cva6_icache.sv" \
	"core/core/mmu_sv32/cva6_mmu_sv32.sv" \
	"core/core/mmu_sv32/cva6_ptw_sv32.sv" \
	"core/core/cva6.sv" \
	"core/core/mmu_sv32/cva6_tlb_sv32.sv" \
	"core/core/cvxif_example/cvxif_example_coprocessor.sv" \
	"core/core/cvxif_fu.sv" \
	"core/core/decoder.sv" \
	"core/vendor/pulp-platform/fpnew/src/fpu_div_sqrt_mvp/hdl/div_sqrt_top_mvp.sv" \
	"core/core/dromajo_ram.sv" \
	"core/common/submodules/common_cells/src/exp_backoff.sv" \
	"core/core/ex_stage.sv" \
	"core/common/submodules/common_cells/src/deprecated/fifo_v2.sv" \
	"core/common/submodules/common_cells/src/fifo_v3.sv" \
	"core/vendor/pulp-platform/fpnew/src/fpnew_cast_multi.sv" \
	"core/vendor/pulp-platform/fpnew/src/fpnew_classifier.sv" \
	"core/vendor/pulp-platform/fpnew/src/fpnew_divsqrt_multi.sv" \
	"core/vendor/pulp-platform/fpnew/src/fpnew_fma_multi.sv" \
	"core/vendor/pulp-platform/fpnew/src/fpnew_fma.sv" \
	"core/vendor/pulp-platform/fpnew/src/fpnew_noncomp.sv" \
	"core/vendor/pulp-platform/fpnew/src/fpnew_opgroup_block.sv" \
	"core/vendor/pulp-platform/fpnew/src/fpnew_opgroup_fmt_slice.sv" \
	"core/vendor/pulp-platform/fpnew/src/fpnew_opgroup_multifmt_slice.sv" \
	"core/vendor/pulp-platform/fpnew/src/fpnew_rounding.sv" \
	"core/vendor/pulp-platform/fpnew/src/fpnew_top.sv" \
	"core/core/fpu_wrap.sv" \
	"core/core/frontend/frontend.sv" \
	"core/core/id_stage.sv" \
	"core/core/cvxif_example/instr_decoder.sv" \
	"core/core/frontend/instr_queue.sv" \
	"core/core/instr_realign.sv" \
	"core/core/frontend/instr_scan.sv" \
	"core/core/issue_read_operands.sv" \
	"core/core/issue_stage.sv" \
	"core/vendor/pulp-platform/fpnew/src/fpu_div_sqrt_mvp/hdl/iteration_div_sqrt_mvp.sv" \
	"core/common/submodules/common_cells/src/lfsr_8bit.sv" \
	"core/common/submodules/common_cells/src/lfsr.sv" \
	"core/core/load_store_unit.sv" \
	"core/core/load_unit.sv" \
	"core/core/lsu_bypass.sv" \
	"core/common/submodules/common_cells/src/lzc.sv" \
	"core/core/cache_subsystem/miss_handler.sv" \
	"core/core/mmu_sv39/mmu.sv" \
	"core/core/multiplier.sv" \
	"core/core/mult.sv" \
	"core/vendor/pulp-platform/fpnew/src/fpu_div_sqrt_mvp/hdl/norm_div_sqrt_mvp.sv" \
	"core/vendor/pulp-platform/fpnew/src/fpu_div_sqrt_mvp/hdl/nrbd_nrsc_mvp.sv" \
	"core/core/perf_counters.sv" \
	"core/core/pmp/src/pmp_entry.sv" \
	"core/core/pmp/src/pmp.sv" \
	"core/common/submodules/common_cells/src/popcount.sv" \
	"core/vendor/pulp-platform/fpnew/src/fpu_div_sqrt_mvp/hdl/preprocess_mvp.sv" \
	"core/core/mmu_sv39/ptw.sv" \
	"core/core/frontend/ras.sv" \
	"core/core/re_name.sv" \
	"core/common/submodules/common_cells/src/deprecated/rrarbiter.sv" \
	"core/vendor/pulp-platform/fpnew/src/common_cells/src/rr_arb_tree.sv" \
	"core/core/scoreboard.sv" \
	"core/core/serdiv.sv" \
	"core/common/submodules/common_cells/src/shift_reg.sv" \
	"core/common/local/util/sram.sv" \
	"core/core/cache_subsystem/std_cache_subsystem.sv" \
	"core/core/cache_subsystem/std_nbdcache.sv" \
	"core/core/store_buffer.sv" \
	"core/core/store_unit.sv" \
	"core/common/submodules/common_cells/src/stream_arbiter_flushable.sv" \
	"core/common/submodules/common_cells/src/stream_arbiter.sv" \
	"core/common/submodules/common_cells/src/stream_demux.sv" \
	"core/common/submodules/common_cells/src/stream_mux.sv" \
	"core/vendor/pulp-platform/fpga-support/rtl/SyncDpRam.sv" \
	"core/core/cache_subsystem/tag_cmp.sv" \
	"core/vendor/pulp-platform/tech_cells_generic/src/rtl/tc_sram.sv" \
	"core/common/local/util/tc_sram_wrapper.sv" \
	"core/core/mmu_sv39/tlb.sv" \
	"core/common/submodules/common_cells/src/unread.sv" \
	"core/core/cache_subsystem/wt_axi_adapter.sv" \
	"core/core/cache_subsystem/wt_cache_subsystem.sv" \
	"core/core/cache_subsystem/wt_dcache_ctrl.sv" \
	"core/core/cache_subsystem/wt_dcache_mem.sv" \
	"core/core/cache_subsystem/wt_dcache_missunit.sv" \
	"core/core/cache_subsystem/wt_dcache.sv" \
	"core/core/cache_subsystem/wt_dcache_wbuffer.sv" \
	"core/core/cache_subsystem/wt_l15_adapter.sv" \
      "verif/*.sv" \
    );

# Print array values in  lines
for file in ${directories[*]}; do
  module=$(basename -s .sv "$file")
  if [[ "$module" == *_pkg ]]; then
    continue
  fi
  if [[ "$module" == *_intf ]]; then
    continue
  fi
  sv2v -v \
    --define=SYNTHESIS \
    --define=CONTRACT \
    --define=VERILATOR \
    --define=RVFI_TRACE \
    --define=RVFI_MEM \
	./core/core/include/ariane_axi_pkg.sv \
	./core/core/include/ariane_dm_pkg.sv \
	./core/core/include/ariane_pkg.sv \
	./core/core/include/ariane_rvfi_pkg.sv \
	./core/vendor/pulp-platform/axi/src/axi_pkg.sv \
        ./core/common/submodules/common_cells/src/cf_math_pkg.sv \
	./core/core/include/cv32a6_imac_sv32_config_pkg.sv \
	./core/core/cvxif_example/include/cvxif_instr_pkg.sv \
	./core/core/include/cvxif_pkg.sv \
	./core/vendor/pulp-platform/fpnew/src/fpu_div_sqrt_mvp/hdl/defs_div_sqrt_mvp_pkg.sv \
	./core/vendor/pulp-platform/fpnew/src/fpnew_pkg.sv \
	./core/vendor/pulp-platform/fpnew/src/common_cells/include/common_cells/registers.svh \
	./core/core/include/riscv_pkg.sv \
	./core/core/include/std_cache_pkg.sv \
	./core/core/include/wt_cache_pkg.sv \
    "$file" \
    > "$LR_VERIF_OUT_DIR"/"${module}".v
done

# Insert content from formal.prop into generated top.v as sv2v would remove it
sed -i '/endmodule/i MARKER' "$LR_VERIF_OUT_DIR"/top.v
sed -i -e '/MARKER/e cat verif\/vcd.prop' -e '/MARKER/d' "$LR_VERIF_OUT_DIR"/top.v

# Read initial memory content from files
# shellcheck disable=SC2016
sed -i '/\/\/ Trace: verif\/mem.sv:45:9/i $readmemh({"init_", $sformatf("%0d", ID), ".dat"}, instr_mem, 0, 31);' "$LR_VERIF_OUT_DIR"/mem.v
# shellcheck disable=SC2016
sed -i '/\/\/ Trace: verif\/mem.sv:45:9/i $readmemh({"memory_", $sformatf("%0d", ID), ".dat"}, instr_mem, 32, (128 - 1));' "$LR_VERIF_OUT_DIR"/mem.v


# shellcheck disable=SC2016
sed -i '/\/\/ Trace: verif\/control.sv:22:9/i $readmemh({"count.dat"}, counters, 0, 0);' "$LR_VERIF_OUT_DIR"/control.v

cd "$LR_VERIF_OUT_DIR"/ || exit

# shellcheck disable=SC2035
iverilog -o ariane *.v
