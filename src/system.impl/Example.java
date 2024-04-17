package system.impl;

import java.util.stream.DoubleStream;

public class Example {

    public static double add(double... operands) {
        return DoubleStream.of(operands)
                .sum();
    }

}