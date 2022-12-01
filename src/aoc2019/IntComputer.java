package aoc2019;

import common.Utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class IntComputer {
    private static final long[] OP_MODE_DIVIDER = { 100L, 1_000L, 10_000L };
    private long[] memory;
    private int iptr = 0;
    private int relativeBase = 0;
    private final int allocationSize;

    private IntComputer( Ram ram ) {
        this.memory = ram.getMemory();
        this.allocationSize = this.memory.length;
    }

    IntComputer reset() {
        this.iptr = 0;
        this.relativeBase = 0;
        return this;
    }

    IntComputer interpret( Supplier<Long> input, Consumer<Long> output ) {
        return interpretUnsafe(
                input::get,
                output::accept
        );
    }

    IntComputer interpretUnsafe( Utils.ThrowingSupplier<Long> input, Utils.ThrowingConsumer<Long> output ) {
        try {
            while ( !Thread.interrupted() ) {
                long instr = getInstrPart();
                switch ( (int) ( instr % 100L ) ) {
                    case 1 -> {
                        long lhs = getOperand( getInstrPart(), instr, 0 );
                        long rhs = getOperand( getInstrPart(), instr, 1 );
                        int target = getOperandAddress( getInstrPart(), instr, 2 );
                        setMemory( target, lhs + rhs );
                    }
                    case 2 -> {
                        long lhs = getOperand( getInstrPart(), instr, 0 );
                        long rhs = getOperand( getInstrPart(), instr, 1 );
                        int target = getOperandAddress( getInstrPart(), instr, 2 );
                        setMemory( target, lhs * rhs );
                    }
                    case 3 -> {
                        int target = getOperandAddress( getInstrPart(), instr, 0 );
                        setMemory( target, input.get() );
                    }
                    case 4 -> {
                        long value = getOperand( getInstrPart(), instr, 0 );
                        output.accept( value );
                    }
                    case 5 -> {
                        long op = getOperand( getInstrPart(), instr, 0 );
                        int target = getIntOperand( getInstrPart(), instr, 1 );
                        if ( op != 0 ) {
                            iptr = target;
                        }
                    }
                    case 6 -> {
                        long op = getOperand( getInstrPart(), instr, 0 );
                        int target = getIntOperand( getInstrPart(), instr, 1 );
                        if ( op == 0 ) {
                            iptr = target;
                        }
                    }
                    case 7 -> {
                        long lhs = getOperand( getInstrPart(), instr, 0 );
                        long rhs = getOperand( getInstrPart(), instr, 1 );
                        int target = getOperandAddress( getInstrPart(), instr, 2 );
                        setMemory( target, lhs < rhs ? 1 : 0 );
                    }
                    case 8 -> {
                        long lhs = getOperand( getInstrPart(), instr, 0 );
                        long rhs = getOperand( getInstrPart(), instr, 1 );
                        int target = getOperandAddress( getInstrPart(), instr, 2 );
                        setMemory( target, lhs == rhs ? 1 : 0 );
                    }
                    case 9 -> relativeBase += getIntOperand( getInstrPart(), instr, 0 );
                    case 99 -> {
                        return this;
                    }
                    default -> throw new IllegalStateException( "Bad opcode = " + instr );
                }
            }
            return this;
        } catch ( InterruptedException e ) {
            return this;
        } catch ( Throwable e ) {
            throw new RuntimeException( e.getMessage(), e );
        }
    }

    CompletableFuture<Void> asyncInterpretUnsafe(
            Utils.ThrowingSupplier<Long> input,
            Utils.ThrowingConsumer<Long> output
    ) {
        return CompletableFuture.runAsync( () -> interpretUnsafe( input, output ) );
    }

    long getMemory( int address ) {
        return address < memory.length ? memory[address] : 0L;
    }

    IntComputer fixMemory( Map<Integer, Long> addrToValue ) {
        for ( var entry : addrToValue.entrySet() ) {
            setMemory( entry.getKey(), entry.getValue() );
        }
        return this;
    }

    static IntComputer fromRam( Ram ram ) {
        return new IntComputer( ram );
    }

    static Supplier<Long> nullInput() {
        return () -> {throw new IllegalStateException( "Trying to read empty input" );};
    }

    static Supplier<Long> singleInput( long input ) {
        return arrayInput( new long[]{ input } );
    }

    static Supplier<Long> arrayInput( long[] input ) {
        return new Supplier<>() {
            int ptr = 0;

            @Override
            public Long get() {
                if ( ptr >= input.length ) {
                    throw new IllegalStateException( "Insufficient input" );
                }
                return input[ptr++];
            }
        };
    }

    static Supplier<Long> channelInput( BlockingQueue<Long> channel ) {
        return () -> {
            try {
                return channel.take();
            } catch ( InterruptedException e ) {
                throw new IllegalStateException( "Execution interrupted while waiting for input" );
            }
        };
    }

    static Consumer<Long> nullOutput() {
        return ( value ) -> {throw new IllegalStateException( "Trying to write to null output" );};
    }

    static Consumer<Long> arrayOutput( long[] output ) {
        return new Consumer<>() {
            int ptr = 0;

            @Override
            public void accept( Long value ) {
                if ( ptr >= output.length ) {
                    throw new IllegalStateException( "Insufficient space for output" );
                }
                output[ptr++] = value;
            }
        };
    }

    static Consumer<Long> listOutput( Collection<? super Long> target ) {
        return target::add;
    }

    private void setMemory( int address, long value ) {
        if ( address >= memory.length ) {
            memory = Arrays.copyOf( memory, ( address + allocationSize ) / allocationSize * allocationSize );
        }
        memory[address] = value;
    }

    private long getInstrPart() {
        return memory[iptr++];
    }

    private int getIntOperand( long opValue, long opCode, int opPos ) {
        long value = getOperand( opValue, opCode, opPos );
        if ( value < Integer.MIN_VALUE || value > Integer.MAX_VALUE ) {
            throw new IllegalStateException( "Bad address " + value );
        }
        return (int) value;
    }

    private long getOperand( long opValue, long opCode, int opPos ) {
        long mode = opCode / OP_MODE_DIVIDER[opPos] % 10;
        return switch ( (int) mode ) {
            case 0 -> getMemory( (int) opValue );
            case 1 -> opValue;
            case 2 -> getMemory( (int) opValue + relativeBase );
            default -> throw new IllegalStateException( "Bad operand mode " + mode );
        };
    }

    private int getOperandAddress( long opValue, long opCode, int opPos ) {
        long mode = opCode / OP_MODE_DIVIDER[opPos] % 10;
        long address = switch ( (int) mode ) {
            case 0 -> opValue;
            case 2 -> opValue + relativeBase;
            default -> throw new IllegalStateException(
                    "Bad lvalue operand mode %d, opCode = %d, opPos = %d".formatted( mode, opCode, opPos )
            );
        };
        if ( address < 0 || address > Integer.MAX_VALUE ) {
            throw new IllegalStateException( "Bad address " + address );
        }
        return (int) address;
    }

    static class Ram {
        private final long[] memory;

        Ram( String source ) {
            this.memory = Arrays.stream( source.split( "," ) )
                                .mapToLong( Long::parseLong )
                                .toArray();
        }

        long[] getMemory() {
            return Arrays.copyOf( memory, memory.length );
        }
    }
}
