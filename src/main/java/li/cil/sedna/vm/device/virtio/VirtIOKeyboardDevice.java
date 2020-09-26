package li.cil.sedna.vm.device.virtio;

import li.cil.sedna.api.vm.MemoryMap;
import li.cil.sedna.vm.evdev.EvdevEvents;
import li.cil.sedna.vm.evdev.EvdevKeys;

import java.nio.ByteBuffer;
import java.util.BitSet;

public final class VirtIOKeyboardDevice extends AbstractVirtIOInputDevice {
    private static final String NAME = "virtio_keyboard";

    public VirtIOKeyboardDevice(final MemoryMap memoryMap) {
        super(memoryMap);
    }

    public void sendKeyEvent(final int keycode, final boolean isDown) {
        putEvent(EvdevEvents.EV_KEY, keycode, isDown ? 1 : 0);
        putSyn();
    }

    @Override
    protected int generateConfigUnion(final int select, final int subsel, final ByteBuffer config) {
        switch (select) {
            case VIRTIO_INPUT_CFG_SELECT_ID_NAME: {
                final char[] chars = NAME.toCharArray();
                for (final char ch : chars) {
                    config.put((byte) ch);
                }
                break;
            }
            case VIRTIO_INPUT_CFG_SELECT_EV_BITS: {
                switch (subsel) {
                    case EvdevEvents.EV_KEY: {
                        final BitSet bitmap = new BitSet();
                        for (final int keycode : EvdevKeys.ALL_KEYS) {
                            bitmap.set(keycode);
                        }
                        final byte[] maskBytes = bitmap.toByteArray();
                        config.put(maskBytes);
                        break;
                    }
                    case EvdevEvents.EV_REP: {
                        config.put((byte) 0);
                        break;
                    }
                }
                break;
            }
        }

        return config.position();
    }
}
