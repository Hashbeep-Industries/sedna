package me.hashbeep.sedna.riscv;

import me.hashbeep.sedna.utils.BitUtils;

@SuppressWarnings({"unused", "RedundantSuppression", "PointlessBitwiseExpression"})
public final class R5 {
    // Supported XLEN values.
    public static final int XLEN_32 = 32;
    public static final int XLEN_64 = 64;

    // Privilege levels.
    public static final int PRIVILEGE_U = 0; // User
    public static final int PRIVILEGE_S = 1; // Supervisor
    public static final int PRIVILEGE_H = 2; // Hypervisor
    public static final int PRIVILEGE_M = 3; // Machine

    // Software interrupts.
    public static final int USIP_SHIFT = 0; // User
    public static final int SSIP_SHIFT = 1; // Supervisor
    public static final int HSIP_SHIFT = 2; // Hypervisor
    public static final int MSIP_SHIFT = 3; // Machine

    // Timer interrupts.
    public static final int UTIP_SHIFT = 4; // User
    public static final int STIP_SHIFT = 5; // Supervisor
    public static final int HTIP_SHIFT = 6; // Hypervisor
    public static final int MTIP_SHIFT = 7; // Machine

    // External interrupts.
    public static final int UEIP_SHIFT = 8; // User
    public static final int SEIP_SHIFT = 9; // Supervisor
    public static final int HEIP_SHIFT = 10; // Hypervisor
    public static final int MEIP_SHIFT = 11; // Machine

    // Interrupt masks for mip/mideleg CSRs.
    public static final int USIP_MASK = 0b1 << USIP_SHIFT;
    public static final int SSIP_MASK = 0b1 << SSIP_SHIFT;
    public static final int HSIP_MASK = 0b1 << HSIP_SHIFT;
    public static final int MSIP_MASK = 0b1 << MSIP_SHIFT;
    public static final int UTIP_MASK = 0b1 << UTIP_SHIFT;
    public static final int STIP_MASK = 0b1 << STIP_SHIFT;
    public static final int HTIP_MASK = 0b1 << HTIP_SHIFT;
    public static final int MTIP_MASK = 0b1 << MTIP_SHIFT;
    public static final int UEIP_MASK = 0b1 << UEIP_SHIFT;
    public static final int SEIP_MASK = 0b1 << SEIP_SHIFT;
    public static final int HEIP_MASK = 0b1 << HEIP_SHIFT;
    public static final int MEIP_MASK = 0b1 << MEIP_SHIFT;

    // Machine status (mstatus[h]) CSR masks and offsets.
    public static final int STATUS_UIE_SHIFT = 0; // U-mode interrupt-enable bit
    public static final int STATUS_SIE_SHIFT = 1; // S-mode interrupt-enable bit
    public static final int STATUS_MIE_SHIFT = 3; // M-mode interrupt-enable bit
    public static final int STATUS_UPIE_SHIFT = 4; // Prior U-mode interrupt-enabled bit.
    public static final int STATUS_SPIE_SHIFT = 5; // Prior S-mode interrupt-enabled bit.
    public static final int STATUS_UBE_SHIFT = 6; // U-mode fetch/store endianness (0 = little, 1 = big).
    public static final int STATUS_MPIE_SHIFT = 7; // Prior M-mode interrupt-enabled bit.
    public static final int STATUS_SPP_SHIFT = 8; // Prior S-mode privilege mode.
    public static final int STATUS_MPP_SHIFT = 11; // Prior M-mode privilege mode.
    public static final int STATUS_FS_SHIFT = 13; // Floating point unit status.
    public static final int STATUS_XS_SHIFT = 15; // User-mode extension status.
    public static final int STATUS_MPRV_SHIFT = 17; // Modify PRiVilege.
    public static final int STATUS_SUM_SHIFT = 18; // Permit Supervisor User Memory access.
    public static final int STATUS_MXR_SHIFT = 19; // Make eXecutable Readable.
    public static final int STATUS_TVM_SHIFT = 20; // Trap Virtual Memory
    public static final int STATUS_TW_SHIFT = 21; // Timeout Wait
    public static final int STATUS_TSR_SHIFT = 22; // Trap SRET
    public static final int STATUS_UXL_SHIFT = 32; // User mode XLEN
    public static final int STATUS_SXL_SHIFT = 34; // Supervisor mode XLEN
    public static final int STATUS_SBE_SHIFT = 36; // Supervisor mode endianness (0 = little, 1 = bit)
    public static final int STATUS_MBE_SHIFT = 37; // Machine mode endianness (0 = little, 1 = bit)
    public static final int STATUS_SD_SHIFT = 63; // State Dirty

    public static final long STATUS_UIE_MASK = 1L << STATUS_UIE_SHIFT;
    public static final long STATUS_SIE_MASK = 1L << STATUS_SIE_SHIFT;
    public static final long STATUS_MIE_MASK = 1L << STATUS_MIE_SHIFT;
    public static final long STATUS_UPIE_MASK = 1L << STATUS_UPIE_SHIFT;
    public static final long STATUS_SPIE_MASK = 1L << STATUS_SPIE_SHIFT;
    public static final long STATUS_UBE_MASK = 1L << STATUS_UBE_SHIFT;
    public static final long STATUS_MPIE_MASK = 1L << STATUS_MPIE_SHIFT;
    public static final long STATUS_SPP_MASK = 1L << STATUS_SPP_SHIFT;
    public static final long STATUS_MPP_MASK = 0b11L << STATUS_MPP_SHIFT;
    public static final long STATUS_FS_MASK = 0b11L << STATUS_FS_SHIFT;
    public static final long STATUS_XS_MASK = 0b11L << STATUS_XS_SHIFT;
    public static final long STATUS_MPRV_MASK = 1L << STATUS_MPRV_SHIFT;
    public static final long STATUS_SUM_MASK = 1L << STATUS_SUM_SHIFT;
    public static final long STATUS_MXR_MASK = 1L << STATUS_MXR_SHIFT;
    public static final long STATUS_TVM_MASK = 1L << STATUS_TVM_SHIFT;
    public static final long STATUS_TW_MASK = 1L << STATUS_TW_SHIFT;
    public static final long STATUS_TSR_MASK = 1L << STATUS_TSR_SHIFT;
    public static final long STATUS_UXL_MASK = 0b11L << STATUS_UXL_SHIFT;
    public static final long STATUS_SXL_MASK = 0b11L << STATUS_SXL_SHIFT;
    public static final long STATUS_SBE_MASK = 1L << STATUS_SBE_SHIFT;
    public static final long STATUS_MBE_MASK = 1L << STATUS_MBE_SHIFT;
    public static final long STATUS_SD_MASK = 1L << STATUS_SD_SHIFT;

    // Exception codes used mep/medeleg CSRs.
    public static final int EXCEPTION_MISALIGNED_FETCH = 0;
    public static final int EXCEPTION_FAULT_FETCH = 1;
    public static final int EXCEPTION_ILLEGAL_INSTRUCTION = 2;
    public static final int EXCEPTION_BREAKPOINT = 3;
    public static final int EXCEPTION_MISALIGNED_LOAD = 4;
    public static final int EXCEPTION_FAULT_LOAD = 5;
    public static final int EXCEPTION_MISALIGNED_STORE = 6;
    public static final int EXCEPTION_FAULT_STORE = 7;
    public static final int EXCEPTION_USER_ECALL = 8;
    public static final int EXCEPTION_SUPERVISOR_ECALL = 9;
    public static final int EXCEPTION_HYPERVISOR_ECALL = 10;
    public static final int EXCEPTION_MACHINE_ECALL = 11;
    public static final int EXCEPTION_FETCH_PAGE_FAULT = 12;
    public static final int EXCEPTION_LOAD_PAGE_FAULT = 13;
    public static final int EXCEPTION_STORE_PAGE_FAULT = 15;

    // Supported counters in [m|s]counteren CSRs.
    public static final int MCOUNTERN_CY = 1 << 0;
    public static final int MCOUNTERN_TM = 1 << 1;
    public static final int MCOUNTERN_IR = 1 << 2;
    public static final int MCOUNTERN_HPM3 = 1 << 3; // Contiguous HPM counters up to HPM31 after this.

    // SATP CSR masks.
    public static final long SATP_PPN_MASK32 = BitUtils.maskFromRange(0, 21);
    public static final long SATP_ASID_MASK32 = BitUtils.maskFromRange(22, 30);
    public static final long SATP_MODE_MASK32 = BitUtils.maskFromRange(31, 31);
    public static final long SATP_PPN_MASK64 = BitUtils.maskFromRange(0, 43);
    public static final long SATP_ASID_MASK64 = BitUtils.maskFromRange(44, 59);
    public static final long SATP_MODE_MASK64 = BitUtils.maskFromRange(60, 63);

    // SATP modes.
    public static final long SATP_MODE_NONE = 0L << 60;
    public static final long SATP_MODE_SV32 = 1L << 31;
    public static final long SATP_MODE_SV39 = 8L << 60;
    public static final long SATP_MODE_SV48 = 9L << 60;
    public static final long SATP_MODE_SV57 = 10L << 60;
    public static final long SATP_MODE_SV64 = 11L << 60;

    // Page sizes are 4KiB (V2p73).
    public static final int PAGE_ADDRESS_SHIFT = 12; // 1<<12 == 4096; SATP << 12 == root PTE address
    public static final int PAGE_ADDRESS_MASK = (1 << PAGE_ADDRESS_SHIFT) - 1;

    // Page table entry masks. See V2p73.
    public static final int PTE_DATA_BITS = 10; // Number of PTE data bits.
    public static final int PTE_V_MASK = 0b1 << 0; // Valid flag.
    public static final int PTE_R_MASK = 0b1 << 1; // Allow read access.
    public static final int PTE_W_MASK = 0b1 << 2; // Allow write access.
    public static final int PTE_X_MASK = 0b1 << 3; // Allow code execution (instruction fetch).
    public static final int PTE_U_MASK = 0b1 << 4; // Allow access to user mode only.
    public static final int PTE_G_MASK = 0b1 << 5; // Global mapping.
    public static final int PTE_A_MASK = 0b1 << 6; // Accessed flag (read, written or fetched).
    public static final int PTE_D_MASK = 0b1 << 7; // Dirty flag (written).
    public static final int PTE_RSW_MASK = 0b11 << 8; // Reserved for supervisor software.

    // Config for SV39/48/57 configuration.
    public static final int SV32_LEVELS = 2;
    public static final int SV39_LEVELS = 3;
    public static final int SV48_LEVELS = 4;
    public static final int SV57_LEVELS = 5;

    // Floating point extension CSR.
    public static final int FCSR_FFLAGS_NX_MASK = 0b1 << 0; // Inexact.
    public static final int FCSR_FFLAGS_UF_MASK = 0b1 << 1; // Underflow.
    public static final int FCSR_FFLAGS_OF_MASK = 0b1 << 2; // Overflow.
    public static final int FCSR_FFLAGS_DZ_MASK = 0b1 << 3; // Division by zero.
    public static final int FCSR_FFLAGS_NV_MASK = 0b1 << 4; // Invalid operation.
    public static final int FCSR_FRM_SHIFT = 5;
    public static final int FCSR_FFLAGS_MASK = 0b11111;
    public static final int FCSR_FRM_MASK = 0b111 << FCSR_FRM_SHIFT;

    // Floating point rounding modes.
    public static final int FCSR_FRM_RNE = 0b000; // Round to nearest, ties to even.
    public static final int FCSR_FRM_RTZ = 0b001; // Round towards zero.
    public static final int FCSR_FRM_RDN = 0b010; // Round down (towards negative infinity).
    public static final int FCSR_FRM_RUP = 0b011; // Round up (towards positive infinity).
    public static final int FCSR_FRM_RMM = 0b100; // Round to nearest, ties to max magnitude.
    public static final int FCSR_FRM_DYN = 0b111; // Use rm field of instruction to determine rounding mode.

    // States for FS field in mstatus.
    public static final int FS_OFF = 0; // All off.
    public static final int FS_INITIAL = 1; // None dirty or clean, some on.
    public static final int FS_CLEAN = 2; // None dirty, some clean.
    public static final int FS_DIRTY = 3; // Some dirty.

    // Upper bit mask for 32bit float values in 64bit registers.
    public static final long NAN_BOXING_MASK = 0xFFFFFFFFL << 32;

    public static final String CANONICAL_ISA_ORDER = "IEMAFDQLCBJTPVNSUHKORWXYZG";

    /**
     * Computes flags for the machine ISA CSR given a list of extension letters.
     *
     * @param extensions the list of extensions to build a mask from.
     * @return the mask representing the list of extensions.
     */
    public static int isa(final char... extensions) {
        int result = 0;
        for (final char ch : extensions) {
            final char extension = Character.toUpperCase(ch);
            if (extension < 'A' || extension > 'Z') {
                throw new IllegalArgumentException("Not a valid extension letter: " + extension);
            }

            result |= 1 << (extension - 'A');
        }
        return result;
    }

    /**
     * Gets the value for the MXL field in the misa CSR for a given XLEN.
     *
     * @param xlen the XLEN to get the MXL value for. Must be 32, 64 or 128.
     * @return the MXL for the specified XLEN.
     */
    public static long mxl(final int xlen) {
        return switch (xlen) {
            case 32 -> 0b01;
            case 64 -> 0b10;
            case 128 -> 0b11;
            default -> throw new IllegalArgumentException();
        };
    }

    public static int xlen(final long mxl) {
        return switch ((int) mxl) {
            case 0b01 -> 32;
            case 0b10 -> 64;
            case 0b11 -> 128;
            default -> 0;
        };
    }

    public static int mxlShift(final int xlen) {
        return xlen - 2;
    }

    public static long mxlMask(final int xlen) {
        return 0b11L << mxlShift(xlen);
    }

    public static long interrupt(final int xlen) {
        // Highest bit means it's an interrupt/asynchronous exception, otherwise a regular exception.
        return 1L << (xlen - 1);
    }

    /**
     * Gets the mask for extracting the status dirty flag.
     * <p>
     * The position of this bit depends on the current {@code XLEN}.
     *
     * @param xlen the current {@code XLEN}.
     * @return the mask for the status dirty bit.
     */
    public static long getStatusStateDirtyMask(final int xlen) {
        return 1L << (xlen - 1);
    }
}
