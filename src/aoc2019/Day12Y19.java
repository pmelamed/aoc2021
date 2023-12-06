package aoc2019;

import common.AocDay;
import common.Utils;

import java.util.Arrays;

public class Day12Y19 implements AocDay<Long, Long> {
    private static record Point3D( int x, int y, int z ) {
        public Point3D( String encoded ) {
            this( Utils.matchGroups( encoded, "\\-?[0-9]+" )
                        .mapToInt( Integer::parseInt )
                        .toArray() );
        }

        private Point3D( int[] coords ) {
            this( coords[0], coords[1], coords[2] );
        }
    }

    private final String name;
    private final Point3D[] moons;
    private final int steps;

    public static void main( String[] args ) {
        try {
            executeTasks( "input/2019/d12s01.dat", 10, 179L, null );
            executeTasks( "input/2019/d12s02.dat", 100, 1940L, 4686774924L );
            executeTasks( "input/2019/d12i.dat", 1000, 7138L, 572087463375796L );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, int steps, Long expected1, Long expected2 ) {
        Utils.executeDay(
                new Day12Y19( fileName, steps ),
                expected1,
                expected2
        );
    }

    public Day12Y19( String file, int steps ) {
        this.name = file;
        this.steps = steps;
        moons = Utils.lines( file ).map( Point3D::new ).toArray( Point3D[]::new );

    }

    @Override
    public String sampleName() {
        return name;
    }

    public Long task1() throws InterruptedException {
        int[] x = Arrays.stream( moons ).mapToInt( Point3D::x ).toArray();
        int[] y = Arrays.stream( moons ).mapToInt( Point3D::y ).toArray();
        int[] z = Arrays.stream( moons ).mapToInt( Point3D::z ).toArray();
        int[] vx = new int[4];
        int[] vy = new int[4];
        int[] vz = new int[4];
        for ( int step = 0; step < steps; ++step ) {
            simulateStep( x, vx );
            simulateStep( y, vy );
            simulateStep( z, vz );
        }
        long energy = 0;
        for ( int index = 0; index < 4; index++ ) {
            energy += (long) ( Math.abs( x[index] ) + Math.abs( y[index] ) + Math.abs( z[index] ) )
                    * ( Math.abs( vx[index] ) + Math.abs( vy[index] ) + Math.abs( vz[index] ) );
        }
        return energy;
    }

    public Long task2() throws InterruptedException {
        long stepsX = simulateUntilSame( Arrays.stream( moons ).mapToInt( Point3D::x ).toArray() );
        long stepsY = simulateUntilSame( Arrays.stream( moons ).mapToInt( Point3D::y ).toArray() );
        long stepsZ = simulateUntilSame( Arrays.stream( moons ).mapToInt( Point3D::z ).toArray() );
        return Utils.mvp( stepsX, stepsY, stepsZ );
    }

    private long simulateUntilSame( int[] initial ) {
        int[] vel = new int[4];
        int[] pos = Arrays.copyOf( initial, 4 );
        long step = 1;
        do {
            simulateStep( pos, vel );
            step++;
        } while ( !Arrays.equals( initial, pos ) );
        return step;
    }

    private void simulateStep( int[] pos, int[] vel ) {
        for ( int from = 0; from < 3; from++ ) {
            for ( int to = from + 1; to < 4; to++ ) {
                int delta = Integer.compare( pos[from], pos[to] );
                vel[from] -= delta;
                vel[to] += delta;
            }
        }
        for ( int index = 0; index < 4; ++index ) {
            pos[index] += vel[index];
        }
    }
}
