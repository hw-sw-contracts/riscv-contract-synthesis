cd "$1" || exit

export OUT_DIR=$2
rm -rf "$OUT_DIR"
mkdir -p "$OUT_DIR"

cp -r core "$OUT_DIR"
cp -r include "$OUT_DIR"
cp -r verif "$OUT_DIR"
cp cva6.patch "$OUT_DIR"
cp riscv.patch "$OUT_DIR"
cp cva6.ys "$OUT_DIR"
cd "$OUT_DIR"/core
patch -p1 < ../cva6.patch

mkdir vendor/pulp-platform/common_cells/src/common_cells/
ln -s vendor/pulp-platform/common_cells/include/common_cells/registers.svh vendor/pulp-platform/common_cells/src/common_cells/registers.svh
ln -s vendor/pulp-platform/common_cells/include/common_cells/assertions.svh vendor/pulp-platform/common_cells/src/common_cells/assertions.svh
mkdir core/common_cells/
ln -s vendor/pulp-platform/common_cells/include/common_cells/registers.svh core/common_cells/registers.svh
ln -s vendor/pulp-platform/common_cells/include/common_cells/assertions.svh core/common_cells/assertions.svh
sv2v -v vendor/pulp-platform/tech_cells_generic/src/rtl/tc_sram.sv > tc_sram.v
# sv2v -v core/include/ariane_dm_pkg.sv core/include/riscv_pkg.sv core/include/cv32a6_imac_sv32_config_pkg.sv core/include/ariane_pkg.sv vendor/pulp-platform/common_cells/src/fifo_v3.sv > fifo_v3.v

yosys ../cva6.ys
