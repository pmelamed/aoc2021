package aoc2019;

import common.AocDay;
import common.Utils;

import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

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
        return (long) iteratePhases(
                0,
                0, 4,
                ( max, phases ) -> Math.max( max, computeAmplifiersCascade( phases ) )
        );
    }

    public Long task2() {
        return (long) iteratePhases(
                0,
                5, 9,
                ( max, phases ) -> Math.max( max, computeLoopedCascade( phases ) )
        );
    }

    private int iteratePhases(
            int initial,
            int minPhase,
            int maxPhase,
            BiFunction<Integer, int[], Integer> emulator
    ) {
        int[] phases = new int[5];
        int result = initial;
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

    private int computeAmplifiersCascade( int[] phases ) {
        int[] input = { 0, 0 };
        LinkedList<Integer> output = new LinkedList<>();
        for ( int ampIndex = 0; ampIndex < 5; ++ampIndex ) {
            input[0] = phases[ampIndex];
            IntComputer.fromRam( initialState )
                       .interpret( input, output );
            input[1] = output.get( 0 );
            output.clear();
        }
        return input[1];
    }

    private int computeLoopedCascade( int[] phases ) {
        BlockingQueue<Integer> e2a = new LinkedBlockingQueue<>( 2 );
        BlockingQueue<Integer> a2b = new LinkedBlockingQueue<>( 2 );
        BlockingQueue<Integer> b2c = new LinkedBlockingQueue<>( 2 );
        BlockingQueue<Integer> c2d = new LinkedBlockingQueue<>( 2 );
        BlockingQueue<Integer> d2e = new LinkedBlockingQueue<>( 2 );
        e2a.offer( phases[0] );
        a2b.offer( phases[1] );
        b2c.offer( phases[2] );
        c2d.offer( phases[3] );
        d2e.offer( phases[4] );
        e2a.offer( 0 );
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
            BlockingQueue<Integer> input,
            BlockingQueue<Integer> output
    ) {
        return CompletableFuture.runAsync( () -> IntComputer.fromRam( ram ).interpret(
                queuedSupplier( input ),
                queuedConsumer( output )
        ) );
    }

    private static Supplier<Integer> queuedSupplier( BlockingQueue<Integer> queue ) {
        return () -> {
            try {
                return queue.take();
            } catch ( InterruptedException e ) {
                throw new IllegalStateException( "Interrupted!!!" );
            }
        };
    }

    private static Consumer<Integer> queuedConsumer( BlockingQueue<Integer> queue ) {
        return val -> {
            try {
                queue.put( val );
            } catch ( InterruptedException e ) {
                throw new IllegalStateException( "Interrupted!!!" );
            }
        };
    }
}
