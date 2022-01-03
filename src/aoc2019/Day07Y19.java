package aoc2019;

import common.AocDay;
import common.Utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static aoc2019.IntComputer.arrayInput;
import static aoc2019.IntComputer.arrayOutput;

public class Day07Y19 implements AocDay<Long, Long> {

    private final String name;
    private final IntComputer.Ram initialState;

    public static void main( String[] args ) {
        try {
            executeTasks( "input/2019/d07i.dat", 914828L, 17956613L );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Long expected1, Long expected2 ) {
        Utils.executeDay(
                new Day07Y19( fileName ),
                expected1,
                expected2
        );
    }

    public Day07Y19( String file ) {
        this.name = file;
        initialState = new IntComputer.Ram( Utils.readLines( file ).get( 0 ) );
    }

    @Override
    public String sampleName() {
        return name;
    }

    public Long task1() {
        return iteratePhases(
                0, 4,
                ( max, phases ) -> Math.max( max, computeAmplifiersCascade( phases ) )
        );
    }

    public Long task2() {
        return iteratePhases(
                5, 9,
                ( max, phases ) -> Math.max( max, computeLoopedCascade( phases ) )
        );
    }

    private long iteratePhases(
            int minPhase,
            int maxPhase,
            BiFunction<Long, int[], Long> emulator
    ) {
        int[] phases = new int[5];
        long result = 0L;
        for ( phases[0] = minPhase; phases[0] <= maxPhase; ++phases[0] ) {
            for ( phases[1] = minPhase; phases[1] <= maxPhase; ++phases[1] ) {
                if ( phases[1] == phases[0] ) {
                    continue;
                }
                for ( phases[2] = minPhase; phases[2] <= maxPhase; ++phases[2] ) {
                    if ( phases[2] == phases[0] || phases[2] == phases[1] ) {
                        continue;
                    }
                    for ( phases[3] = minPhase; phases[3] <= maxPhase; ++phases[3] ) {
                        if ( phases[3] == phases[0] || phases[3] == phases[1] || phases[3] == phases[2] ) {
                            continue;
                        }
                        for ( phases[4] = minPhase; phases[4] <= maxPhase; ++phases[4] ) {
                            if ( phases[4] == phases[0] || phases[4] == phases[1]
                                    || phases[4] == phases[2] || phases[4] == phases[3] ) {
                                continue;
                            }
                            result = emulator.apply( result, phases );
                        }
                    }
                }
            }
        }
        return result;
    }

    private long computeAmplifiersCascade( int[] phases ) {
        long[] input = { 0, 0 };
        long[] output = new long[1];
        for ( int ampIndex = 0; ampIndex < 5; ++ampIndex ) {
            input[0] = phases[ampIndex];
            IntComputer.fromRam( initialState )
                       .interpret(
                               arrayInput( input ),
                               arrayOutput( output )
                       );
            input[1] = output[0];
        }
        return input[1];
    }

    private long computeLoopedCascade( int[] phases ) {
        BlockingQueue<Long> e2a = new LinkedBlockingQueue<>( 2 );
        BlockingQueue<Long> a2b = new LinkedBlockingQueue<>( 2 );
        BlockingQueue<Long> b2c = new LinkedBlockingQueue<>( 2 );
        BlockingQueue<Long> c2d = new LinkedBlockingQueue<>( 2 );
        BlockingQueue<Long> d2e = new LinkedBlockingQueue<>( 2 );
        e2a.offer( (long) phases[0] );
        a2b.offer( (long) phases[1] );
        b2c.offer( (long) phases[2] );
        c2d.offer( (long) phases[3] );
        d2e.offer( (long) phases[4] );
        e2a.offer( 0L );
        CompletableFuture<Void> threadA = runCpu( initialState, e2a, a2b );
        CompletableFuture<Void> threadB = runCpu( initialState, a2b, b2c );
        CompletableFuture<Void> threadC = runCpu( initialState, b2c, c2d );
        CompletableFuture<Void> threadD = runCpu( initialState, c2d, d2e );
        CompletableFuture<Void> threadE = runCpu( initialState, d2e, e2a );
        try {
            CompletableFuture.allOf( threadA, threadB, threadC, threadD, threadE )
                             .get( 5L, TimeUnit.SECONDS );
        } catch ( InterruptedException | ExecutionException e ) {
            throw new IllegalStateException( "Execution problem", e );
        } catch ( TimeoutException e ) {
            throw new IllegalStateException( "Execution timed out" );
        }
        return e2a.remove();
    }

    private CompletableFuture<Void> runCpu(
            IntComputer.Ram ram,
            BlockingQueue<Long> input,
            BlockingQueue<Long> output
    ) {
        return CompletableFuture.runAsync( () -> IntComputer.fromRam( ram ).interpret(
                queuedSupplier( input ),
                queuedConsumer( output )
        ) );
    }

    private static Supplier<Long> queuedSupplier( BlockingQueue<Long> queue ) {
        return () -> {
            try {
                return queue.take();
            } catch ( InterruptedException e ) {
                throw new IllegalStateException( "Interrupted!!!" );
            }
        };
    }

    private static Consumer<Long> queuedConsumer( BlockingQueue<Long> queue ) {
        return val -> {
            try {
                queue.put( val );
            } catch ( InterruptedException e ) {
                throw new IllegalStateException( "Interrupted!!!" );
            }
        };
    }
}
