package li.cil.sedna.vm.elf;

public enum SectionHeaderFlags {
    SHF_WRITE(0x1),
    SHF_ALLOC(0x2),
    SHF_EXECINSTR(0x4),
    SHF_MERGE(0x10),
    SHF_STRINGS(0x20),
    SHF_INFO_LINK(0x40),
    SHF_LINK_ORDER(0x80),
    SHF_OS_NONCONFORMING(0x100),
    SHF_GROUP(0x200),
    SHF_TLS(0x400),
    SHF_MASKOS(0x0ff00000),
    SHF_MASKPROC(0xf0000000),
    SHF_ORDERED(0x4000000),
    SHF_EXCLUDE(0x8000000),

    ;

    public final int value;

    SectionHeaderFlags(final int value) {
        this.value = value;
    }
}
