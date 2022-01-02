package aoc2019;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Map;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class IntComputer {
    private static final int[] OP_MODE_DIVIDER = { 100, 1_000, 10_000 };
    private final int[] memory;
    private int iptr = 0;

    private IntComputer( Ram ram ) {
        this.memory = ram.getMemory();
    }

    IntComputer interpret() {
        return interpret( (Supplier<Integer>) null, null );
    }

    IntComputer interpret( int[] input, Queue<Integer> output ) {
        ArrayDeque<Integer> inputs = new ArrayDeque<>( Arrays.stream( input ).boxed().toList() );
        interpret(
                inputs::remove,
                output::offer
        );
        return this;
    }

    IntComputer interpret( Supplier<Integer> input, Consumer<Integer> output ) {
        while ( true ) {
            int instr = getInstrPart();
            switch ( instr % 100 ) {
                case 1 -> {
                    int lhs = getOperand( getInstrPart(), instr, 0 );
                    int rhs = getOperand( getInstrPart(), instr, 1 );
                    int target = getInstrPart();
                    memory[target] = lhs + rhs;
                }
                case 2 -> {
                    int lhs = getOperand( getInstrPart(), instr, 0 );
                    int rhs = getOperand( getInstrPart(), instr, 1 );
                    int target = getInstrPart();
                    memory[target] = lhs * rhs;
                }
                case 3 -> {
                    int target = getInstrPart();
                    memory[target] = input.get();
                }
                case 4 -> {
                    int value = getOperand( getInstrPart(), instr, 0 );
                    output.accept( value );
                }
                case 5 -> {
                    int op = getOperand( getInstrPart(), instr, 0 );
                    int target = getOperand( getInstrPart(), instr, 1 );
                    if ( op != 0 ) {
                        iptr = target;
                    }
                }
                case 6 -> {
                    int op = getOperand( getInstrPart(), instr, 0 );
                    int target = getOperand( getInstrPart(), instr, 1 );
                    if ( op == 0 ) {
                        iptr = target;
                    }
                }
                case 7 -> {
                    int lhs = getOperand( getInstrPart(), instr, 0 );
                    int rhs = getOperand( getInstrPart(), instr, 1 );
                    int target = getInstrPart();
                    memory[target] = lhs < rhs ? 1 : 0;
                }
                case 8 -> {
                    int lhs = getOperand( getInstrPart(), instr, 0 );
                    int rhs = getOperand( getInstrPart(), instr, 1 );
                    int target = getInstrPart();
                    memory[target] = lhs == rhs ? 1 : 0;
                }
                case 99 -> {
                    return this;
                }
                default -> throw new IllegalStateException( "Bad opcode= " + instr );
            }
        }
    }

    int getMemory( int addr ) {
        return memory[addr];
    }

    IntComputer fixMemory( Map<Integer, Integer> addrToValue ) {
        for ( var entry : addrToValue.entrySet() ) {
            memory[entry.getKey()] = entry.getValue();
        }
        return this;
    }

    static IntComputer fromRam( Ram ram ) {
        return new IntComputer( ram );
    }

    private int getInstrPart() {
        return memory[iptr++];
    }

    private int getOperand( int opValue, int opCode, int opPos ) {
        int mode = opCode / OP_MODE_DIVIDER[opPos] % 10;
        return mode == 0 ? memory[opValue] : opValue;
    }

    static class Ram {
        private final int[] memory;

        Ram( String source ) {
            this.memory = Arrays.stream( source.split( "," ) )
                                .mapToInt( Integer::parseInt )
                                .toArray();
        }

        int[] getMemory() {
            return Arrays.copyOf( memory, memory.length );
        }
    }
}
