package me.hashbeep.sedna.utils;

import java.math.BigInteger;

public final class BitUtils {
    /**
     * Extract a field encoded in an integer value and shifts it to the desired output position.
     * The length is determined by the destination low and high bit indices.
     *
     * @param value       the value that contains the field.
     * @param srcBitFrom  the lowest bit of the field in the source value.
     * @param srcBitUntil the highest bit of the field in the source value.
     * @param destBit     the lowest bit of the field in the destination (returned) value.
     * @return the extracted field at the bit location specified in <code>destBitFrom</code> and <code>destBitUntil</code>.
     */
    public static int getField(final int value, final int srcBitFrom, final int srcBitUntil, final int destBit) {
        // For bit-shifts Java always only uses the lowest five bits for the right-hand operand,
        // so we can't be clever and shift by a negative amount; need to branch here.
        // NB: This method is optimized for bytecode size to make sure it gets inlined.
        return (destBit >= srcBitFrom
            ? value << (destBit - srcBitFrom)
            : value >>> (srcBitFrom - destBit))
            & ((1 << (srcBitUntil - srcBitFrom + 1)) - 1) << destBit;
    }

    /**
     * Sign-extends a value of the specified bit width stored in the specified integer value.
     *
     * @param value the integer value holding the less-than-32-bit wide value.
     * @param width the bit width of the value to be sign-extended.
     * @return the sign-extended integer value representing the specified value.
     */
    public static int extendSign(final int value, final int width) {
        return (value << (32 - width)) >> (32 - width);
    }

    /**
     * Generates a mask for a bit range defined by its lowest and highest bits, both inclusive.
     *
     * @param bitFrom  the least significant bit in the mask.
     * @param bitUntil the most significant bit in the mask.
     * @return the mask defined by the given bit indices.
     */
    public static long maskFromRange(final int bitFrom, final int bitUntil) {
        return ((1L << (bitUntil - bitFrom + 1)) - 1) << bitFrom;
    }

    public static BigInteger unsignedLongToBigInteger(final long value) {
        if (value >= 0L) {
            return BigInteger.valueOf(value);
        }

        final int upper = (int) (value >>> 32);
        final int lower = (int) value;

        return BigInteger.valueOf(Integer.toUnsignedLong(upper)).shiftLeft(32).
            or(BigInteger.valueOf(Integer.toUnsignedLong(lower)));
    }
}
