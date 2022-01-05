package aoc2019;

import common.AocDay;
import common.Utils;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Day14Y19 implements AocDay<Long, Long> {


    public static final String FUEL = "FUEL";
    public static final String ORE = "ORE";
    public static final long ORE_LIMIT = 1_000_000_000_000L;

    private static record Material( String name, int quantity ) {
    }

    private static record Reaction( Material result, Material[] sources ) {
    }

    private final String name;
    private final Map<String, Reaction> reactions;

    public static void main( String[] args ) {
        try {
            executeTasks( "input/2019/d14s01.dat", 31L, null );
            executeTasks( "input/2019/d14s02.dat", 165L, null );
            executeTasks( "input/2019/d14s03.dat", 13312L, 82892753L );
            executeTasks( "input/2019/d14s04.dat", 180697L, 5586022L );
            executeTasks( "input/2019/d14s05.dat", 2210736L, 460664L );
            executeTasks( "input/2019/d14i.dat", null, null );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Long expected1, Long expected2 ) {
        Utils.executeDay(
                new Day14Y19( fileName ),
                expected1,
                expected2
        );
    }

    public Day14Y19( String file ) {
        this.name = file;
        this.reactions = Utils.lines( file )
                              .map( Day14Y19::parseLine )
                              .collect( Collectors.toMap( r -> r.result.name, r -> r ) );
    }

    private static Reaction parseLine( String line ) {
        Material[] materials = Arrays.stream( line.split( ", | => " ) )
                                     .map( Day14Y19::parseMaterial )
                                     .toArray( Material[]::new );
        return new Reaction(
                materials[materials.length - 1],
                Arrays.copyOf( materials, materials.length - 1 )
        );
    }

    private static Material parseMaterial( String str ) {
        String[] parts = str.split( " " );
        return new Material( parts[1], Integer.parseInt( parts[0] ) );
    }

    @Override
    public String sampleName() {
        return name;
    }

    public Long task1() throws InterruptedException {
        ProductionState productionState = new ProductionState();
        productionState.produce( FUEL, 1 );
        return productionState.oreConsumed;
    }

    public Long task2() throws InterruptedException {
        ProductionState productionState = new ProductionState();
        for ( long step = 100_000_000_000L; step > 0; step /= 10L ) {
            productionState = loopProduction( step, productionState );
        }
        return productionState.fuelProduced;
    }

    private ProductionState loopProduction( long step, ProductionState initial ) {
        ProductionState state = initial.copy();
        ProductionState prev;
        do {
            prev = state.copy();
            state.produce( FUEL, step );
        } while ( state.oreConsumed < ORE_LIMIT );
        return prev;
    }

    private class ProductionState implements Cloneable {
        private long oreConsumed;
        private long fuelProduced;
        private final Map<String, Long> store = new TreeMap<>();

        private ProductionState() {
        }

        private void consume( String material, long quantity ) {
            if ( ORE.equals( material ) ) {
                oreConsumed += quantity;
                return;
            }
            long inStore = store.getOrDefault( material, 0L );
            if ( quantity == inStore ) {
                store.remove( material );
            } else if ( quantity < inStore ) {
                store.put( material, inStore - quantity );
            } else {
                store.remove( material );
                produce( material, quantity - inStore );
            }
        }

        private void produce( String material, long quantity ) {
            Reaction reaction = reactions.get( material );
            long multiply = ( quantity + reaction.result.quantity - 1 ) / reaction.result.quantity;
            for ( Material source : reaction.sources ) {
                consume( source.name, source.quantity * multiply );
            }
            if ( FUEL.equals( material ) ) {
                fuelProduced += quantity;
            } else {
                store.put( material, multiply * reaction.result.quantity - quantity );
            }
        }

        private ProductionState copy() {
            try {
                return (ProductionState) this.clone();
            } catch ( CloneNotSupportedException e ) {
                throw new RuntimeException( e );
            }
        }
    }
}
