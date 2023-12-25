// https://adventofcode.com/2023/day/16
package aoc2023;

import common.AocDay;
import common.Utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Day16Y23 implements AocDay<Long, Long> {
    private static final int DIR_NORTH = 0;
    private static final int DIR_EAST = 1;
    private static final int DIR_SOUTH = 2;
    private static final int DIR_WEST = 3;

    private static final int[] DX = { 0, 1, 0, -1 };
    private static final int[] DY = { -1, 0, 1, 0 };

    private static final int[][] POINT_TRANSLATE = { { DIR_NORTH }, { DIR_EAST }, { DIR_SOUTH }, { DIR_WEST } };
    private static final int[][] SLASH_TRANSLATE = { { DIR_EAST }, { DIR_NORTH }, { DIR_WEST }, { DIR_SOUTH } };
    private static final int[][] BSLASH_TRANSLATE = { { DIR_WEST }, { DIR_SOUTH }, { DIR_EAST }, { DIR_NORTH } };
    private static final int[][] VSPLITTER_TRANSLATE =
            { { DIR_NORTH }, { DIR_NORTH, DIR_SOUTH }, { DIR_SOUTH }, { DIR_NORTH, DIR_SOUTH } };
    private static final int[][] HSPLITTER_TRANSLATE =
            { { DIR_EAST, DIR_WEST }, { DIR_EAST }, { DIR_EAST, DIR_WEST }, { DIR_WEST } };

    private static final Map<Character, int[][]> TILE_TRANSLATORS = Map.of(
            '.', POINT_TRANSLATE,
            '/', SLASH_TRANSLATE,
            '\\', BSLASH_TRANSLATE,
            '|', VSPLITTER_TRANSLATE,
            '-', HSPLITTER_TRANSLATE
    );

    private final String filename;
    private final char[][] field;
    private final int width;

    public static void main( String[] args ) {
        try {
            Utils.executeSampleDay( new Day16Y23( "input/2023/Y23D16S1.dat" ), 46L, 51L );
            Utils.executeDay( new Day16Y23( "input/2023/Y23D16I.dat" ), 7434L, 8183L );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public Day16Y23( String file ) {
        this.filename = file;
        field = Utils.lines( filename )
                     .map( String::toCharArray )
                     .toArray( char[][]::new );
        width = field.length;
    }

    @Override
    public String sampleName() {
        return filename;
    }

    public Long task1() {
        return getBeamEnergy( new Beam( 0, 0, DIR_EAST ) );
    }

    public Long task2() {
        ArrayList<Beam> beams = new ArrayList<>();
        for ( int tile = 0; tile < width; tile++ ) {
            beams.add( new Beam( tile, 0, DIR_SOUTH ) );
            beams.add( new Beam( tile, width - 1, DIR_NORTH ) );
            beams.add( new Beam( 0, tile, DIR_EAST ) );
            beams.add( new Beam( width - 1, tile, DIR_WEST ) );
        }
        return beams.stream()
                    .mapToLong( this::getBeamEnergy )
                    .max()
                    .orElseThrow();
    }

    private long getBeamEnergy( Beam initialBeam ) {
        List<Beam> activeBeams = new LinkedList<>();
        byte[][] beamMarks = new byte[width][width];
        activeBeams.add( initialBeam );
        while ( !activeBeams.isEmpty() ) {
            Beam beam = activeBeams.get( 0 );
            if ( beam.x < 0 || beam.y < 0
                    || beam.x >= width || beam.y >= width
                    || markTile( beamMarks, beam.x, beam.y, beam.dir ) ) {
                activeBeams.remove( 0 );
                continue;
            }
            int[] translate = TILE_TRANSLATORS.get( field[beam.y][beam.x] )[beam.dir];
            if ( translate.length > 1 ) {
                Beam newBeam = new Beam( beam.x, beam.y, translate[1] );
                newBeam.x += DX[newBeam.dir];
                newBeam.y += DY[newBeam.dir];
                activeBeams.add( newBeam );
            }
            beam.dir = translate[0];
            beam.x += DX[beam.dir];
            beam.y += DY[beam.dir];
        }
        long result = 0;
        for ( byte[] row : beamMarks ) {
            for ( byte tile : row ) {
                if ( tile != 0 ) {
                    result++;
                }
            }
        }
        return result;
    }

    private boolean markTile( byte[][] beamMarks, int x, int y, int dir ) {
        byte mask = (byte) ( 1 << dir );
        if ( ( beamMarks[y][x] & mask ) != 0 ) {
            return true;
        }
        beamMarks[y][x] |= mask;
        return false;
    }

    private static class Beam {
        private int x;
        private int y;
        private int dir;

        public Beam( int x, int y, int dir ) {
            this.x = x;
            this.y = y;
            this.dir = dir;
        }
    }
}
