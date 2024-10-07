package me.hashbeep.sedna.api;

import me.hashbeep.sedna.api.device.InterruptController;

public final class Interrupt {
    public int id;
    public InterruptController controller;

    public Interrupt() {
    }

    public Interrupt(final int id) {
        this.id = id;
    }

    public void set(final int id, final InterruptController controller) {
        this.id = id;
        this.controller = controller;
    }

    public void raiseInterrupt() {
        if (controller != null) {
            controller.raiseInterrupts(1 << id);
        }
    }

    public void lowerInterrupt() {
        if (controller != null) {
            controller.lowerInterrupts(1 << id);
        }
    }

    public boolean isRaised() {
        return controller != null && ((controller.getRaisedInterrupts() & (1 << id)) != 0);
    }
}
