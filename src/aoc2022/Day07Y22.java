package aoc2022;

import common.AocDay;
import common.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class Day07Y22 implements AocDay.DayLong {

    private static final String DIR_PREFIX = "dir ";

    private static final String CD_PREFIX = "$ cd ";

    private static final String LS_COMMAND = "$ ls";

    public static final long SIZE_TOTAL = 70_000_000L;

    public static final long SIZE_DESIRED = 30_000_000L;

    private final String fileName;
    private final List<Dir> dirs = new ArrayList<>();
    private final Dir rootDir = new Dir( null );

    public static void main( String[] args ) {
        try {
            executeTasks( "input/2022/Y22D07I.DAT", 1501149L, 10096985L );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String name, Long expected1, Long expected2 ) {
        Utils.executeDay( new Day07Y22( name ), expected1, expected2 );
    }

    public Day07Y22( String fileName ) {
        this.fileName = fileName;
    }

    @Override
    public String sampleName() {
        return fileName;
    }

    public Long task1() {
        Dir currentDir = rootDir;
        for ( String line : Utils.readLines( fileName ) ) {
            if ( line.startsWith( DIR_PREFIX ) ) {
                dirs.add( currentDir.dir( line.trim().substring( DIR_PREFIX.length() ) ) );
            } else if ( line.startsWith( CD_PREFIX ) ) {
                String dirName = line.substring( CD_PREFIX.length() );
                currentDir = switch ( dirName ) {
                    case "/" -> rootDir;
                    case ".." -> currentDir.getParent();
                    default -> currentDir.dir( dirName );
                };
            } else if ( !line.equals( LS_COMMAND ) ) {
                currentDir.file( Long.parseLong( line.substring( 0, line.indexOf( ' ' ) ) ) );
            }
        }
        return dirs.stream()
                   .mapToLong( Dir::getSize )
                   .filter( size -> size <= 100_000L )
                   .sum();
    }


    public Long task2() {
        final long toFree = SIZE_DESIRED - ( SIZE_TOTAL - rootDir.getSize() );
        return dirs.stream()
                   .mapToLong( Dir::getSize )
                   .filter( size -> size >= toFree )
                   .min()
                   .orElse( 0L );
    }

    private static final class Dir {
        private final Dir parent;

        private final TreeMap<String, Dir> children = new TreeMap<>();

        private long size = 0;

        private Dir( Dir parent ) {
            this.parent = parent;
        }

        private void file( long size ) {
            this.size += size;
            if ( parent != null ) {
                parent.file( size );
            }
        }

        private Dir dir( String name ) {
            return children.computeIfAbsent( name, n -> new Dir( this ) );
        }

        private long getSize() {
            return size;
        }

        public Dir getParent() {
            return parent;
        }
    }
}

