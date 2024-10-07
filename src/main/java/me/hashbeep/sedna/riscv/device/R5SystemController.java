package me.hashbeep.sedna.riscv.device;

import me.hashbeep.sedna.device.syscon.AbstractSystemController;
import me.hashbeep.sedna.riscv.exception.R5SystemPowerOffException;
import me.hashbeep.sedna.riscv.exception.R5SystemResetException;

public class R5SystemController extends AbstractSystemController {
    @Override
    protected void handleReset() {
        throw new R5SystemResetException();
    }

    @Override
    protected void handlePowerOff() {
        throw new R5SystemPowerOffException();
    }
}
