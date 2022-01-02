package aoc2019;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class IntComputer {
    private static final int[] OP_MODE_DIVIDER = { 100, 1_000, 10_000 };
    private final int[] memory;
    private int iptr = 0;
    private final int[] input;
    private int inputPtr = 0;
    private final List<Integer> output = new ArrayList<>();

    IntComputer( String source, String input ) {
        this.memory = Arrays.stream( source.split( "," ) )
                            .mapToInt( Integer::parseInt )
                            .toArray();
        this.input = input == null || input.isBlank()
                ? new int[]{}
                : Arrays.stream( input.split( "," ) )
                        .mapToInt( Integer::parseInt )
                        .toArray();
    }

    private IntComputer( int[] memory, int[] input ) {
        this.memory = Arrays.copyOf( memory, memory.length );
        this.input = Arrays.copyOf( input, input.length );
    }

    public IntComputer( IntComputer peer ) {
        this( peer.memory, peer.input );
        this.iptr = peer.iptr;
        this.inputPtr = peer.inputPtr;
        this.output.addAll( peer.output );
    }

    IntComputer interpret() {
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
                    memory[target] = input[inputPtr++];
                }
                case 4 -> {
                    int value = getOperand( getInstrPart(), instr, 0 );
                    output.add( value );
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

    List<Integer> getOutput() {
        return output;
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

    IntComputer copy() {
        return new IntComputer( memory, input );
    }

    IntComputer copyState() {
        return new IntComputer( this );
    }

    private int getInstrPart() {
        return memory[iptr++];
    }

    private int getOperand( int opValue, int opCode, int opPos ) {
        int mode = opCode / OP_MODE_DIVIDER[opPos] % 10;
        return mode == 0 ? memory[opValue] : opValue;
    }
}
