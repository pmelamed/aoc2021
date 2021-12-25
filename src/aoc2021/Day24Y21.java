package aoc2021;

import common.AocDay;
import common.Utils;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Day24Y21 implements AocDay<Long, Long> {
    public static final Map<Character, Integer> REGNAMES = Map.of( 'w', 0, 'x', 1, 'y', 2, 'z', 3 );
    private static final int[] DELTA_X = { 10, 13, 12, -12, 11, -13, -9, -12, 14, -9, 15, 11, -16, -2 };
    private static final int[] DELTA_Y = { 5, 9, 4, 4, 10, 14, 14, 12, 14, 14, 5, 10, 8, 15 };
    private final String name;
    private final List<Consumer<ALUState>> instructions;

    private static class ALUState {
        private final long[] regs = new long[4];
        private final long[] input;
        private int inputPtr = 0;

        public ALUState( long[] input ) {
            this.input = input;
        }

        public long readInput() {
            return input[inputPtr++];
        }

        private boolean isValid() {
            return regs[3] == 0;
        }
    }

    public static void main( String[] args ) {
        try {
            executeTasks( "input/Y21D24I.dat", 99919692496939L, 81914111161714L );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Long expected1, Long expected2 ) {
        Utils.executeDay(
                new Day24Y21( fileName ),
                expected1,
                expected2
        );
    }

    public Day24Y21( String file ) {
        this( Utils.readLines( file ), file );
    }

    public Day24Y21( List<String> sample, String name ) {
        this.name = name;
        instructions = sample.stream()
                             .map( Day24Y21::parseInstruction )
                             .collect( Collectors.toList() );
    }

    @Override
    public String sampleName() {
        return name;
    }

    public Long task1() {
        return solve( ( a, b ) -> a > b );
    }

    public Long task2() {
        return solve( ( a, b ) -> a < b );
    }

    private long solve( BiPredicate<Long, Long> replacePredicate ) {
        long number = getExtremeNumber( replacePredicate );
        System.out.printf( "%d - %s", number, executeMonadProgram( numberToInput( number ) ) ? "OK" : "Fail" );
        return number;
    }

    private static long getExtremeNumber( BiPredicate<Long, Long> replacePredicate ) {
        System.out.println();
        Map<Long, Long> result = new TreeMap<>();
        result.put( 0L, 0L );
        for ( int cascade = 0; cascade <= 13; ++cascade ) {
            result = getCascadeResults( cascade, result, replacePredicate );
            System.out.printf( "Cascade %d - entries %d%n", cascade + 1, result.size() );
        }
        return result.get( 0L );
    }

    private static Map<Long, Long> getCascadeResults(
            int cascade,
            Map<Long, Long> prev,
            BiPredicate<Long, Long> replacePredicate
    ) {
        Map<Long, Long> result = new TreeMap<>();
        for ( Map.Entry<Long, Long> entry : prev.entrySet() ) {
            long z = entry.getKey();
            long newNumber = entry.getValue() * 10 + 1;
            for ( int digit = 1; digit <= 9; ++digit, ++newNumber ) {
                long zout = calculateCascade( z, digit, cascade );
                Long curEntry = result.get( zout );
                if ( curEntry == null || replacePredicate.test( newNumber, curEntry ) ) {
                    result.put( zout, newNumber );
                }
            }
        }
        return result;
    }

    private static long calculateCascade( long z, int input, int cascade ) {
        if ( DELTA_X[cascade] > 0 ) {
            z = z * 26 + DELTA_Y[cascade] + input;
        } else {
            boolean xfit = z % 26 + DELTA_X[cascade] == input;
            z /= 26;
            if ( !xfit ) {
                z = z * 26 + DELTA_Y[cascade] + input;
            }
        }
        return z;
    }

    private static long[] numberToInput( long number ) {
        long[] input = new long[14];
        for ( int pos = 13; pos >= 0; --pos ) {
            input[pos] = number % 10;
            number /= 10;
        }
        return input;
    }

    private boolean executeMonadProgram( long[] input ) {
        ALUState alu = new ALUState( input );
        instructions.forEach( instr -> instr.accept( alu ) );
        return alu.isValid();
    }

    private static Consumer<ALUState> parseInstruction( String text ) {
        String[] args = text.split( " " );
        int srcReg = REGNAMES.get( args[1].charAt( 0 ) );
        if ( args.length == 3 ) {
            Integer dstReg = REGNAMES.get( args[2].charAt( 0 ) );
            if ( dstReg != null ) {
                return mapRegInstruction( args[0], srcReg, dstReg );
            } else {
                return mapNumberInstruction( args[0], srcReg, Long.parseLong( args[2] ) );
            }
        } else {
            return alu -> doInput( alu, srcReg );
        }
    }

    private static Consumer<ALUState> mapNumberInstruction( String instr, int reg, long number ) {
        return switch ( instr ) {
            case "add" -> alu -> doAddNumber( alu, reg, number );
            case "mul" -> alu -> doMulNumber( alu, reg, number );
            case "div" -> alu -> doDivNumber( alu, reg, number );
            case "mod" -> alu -> doModNumber( alu, reg, number );
            case "eql" -> alu -> doEqlNumber( alu, reg, number );
            default -> throw new IllegalStateException( "bad instruction " + instr );
        };
    }

    private static Consumer<ALUState> mapRegInstruction( String instr, int src, int dst ) {
        return switch ( instr ) {
            case "add" -> alu -> doAddReg( alu, src, dst );
            case "mul" -> alu -> doMulReg( alu, src, dst );
            case "div" -> alu -> doDivReg( alu, src, dst );
            case "mod" -> alu -> doModReg( alu, src, dst );
            case "eql" -> alu -> doEqlReg( alu, src, dst );
            default -> throw new IllegalStateException( "Bad instruction " + instr );
        };
    }

    private static void doInput( ALUState alu, int reg ) {
        alu.regs[reg] = alu.readInput();
    }

    private static void doAddNumber( ALUState alu, int reg, long number ) {
        alu.regs[reg] += number;
    }

    private static void doAddReg( ALUState alu, int dst, int src ) {
        alu.regs[dst] += alu.regs[src];
    }

    private static void doMulNumber( ALUState alu, int reg, long number ) {
        alu.regs[reg] *= number;
    }

    private static void doMulReg( ALUState alu, int dst, int src ) {
        alu.regs[dst] *= alu.regs[src];
    }

    private static void doDivNumber( ALUState alu, int reg, long number ) {
        alu.regs[reg] /= number;
    }

    private static void doDivReg( ALUState alu, int dst, int src ) {
        alu.regs[dst] /= alu.regs[src];
    }

    private static void doModNumber( ALUState alu, int reg, long number ) {
        alu.regs[reg] %= number;
    }

    private static void doModReg( ALUState alu, int dst, int src ) {
        alu.regs[dst] %= alu.regs[src];
    }

    private static void doEqlNumber( ALUState alu, int reg, long number ) {
        alu.regs[reg] = alu.regs[reg] == number ? 1L : 0L;
    }

    private static void doEqlReg( ALUState alu, int dst, int src ) {
        alu.regs[dst] = alu.regs[src] == alu.regs[dst] ? 1L : 0L;
    }
}
