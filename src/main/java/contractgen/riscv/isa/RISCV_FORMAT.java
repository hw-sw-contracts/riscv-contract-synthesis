package contractgen.riscv.isa;

/**
 * The format of a RISC-V instruction.
 */
public enum RISCV_FORMAT {
    /**
     * A R-Type instruction.
     */
    RTYPE,
    /**
     * A I-Type instruction.
     */
    ITYPE,
    /**
     * A S-Type instruction.
     */
    STYPE,
    /**
     * A B-Type instruction.
     */
    BTYPE,
    /**
     * A U-Type instruction.
     */
    UTYPE,
    /**
     * A J-Type instruction.
     */
    JTYPE
}
