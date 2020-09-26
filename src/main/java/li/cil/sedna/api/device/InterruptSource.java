package li.cil.sedna.api.device;

import li.cil.sedna.api.Interrupt;

/**
 * An interrupt source is a device that can raise and lower interrupts in an {@link InterruptController}.
 */
public interface InterruptSource extends Device {
    /**
     * Returns all interrupts used by this device.
     *
     * @return the interrupts used by this device.
     */
    Iterable<Interrupt> getInterrupts();
}
