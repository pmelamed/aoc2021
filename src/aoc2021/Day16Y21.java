package aoc2021;

import common.AocDay;
import common.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class Day16Y21 implements AocDay<Long, Long> {
    enum HexDigits {
        D0( 0, 0, 0, 0 ),
        D1( 0, 0, 0, 1 ),
        D2( 0, 0, 1, 0 ),
        D3( 0, 0, 1, 1 ),
        D4( 0, 1, 0, 0 ),
        D5( 0, 1, 0, 1 ),
        D6( 0, 1, 1, 0 ),
        D7( 0, 1, 1, 1 ),
        D8( 1, 0, 0, 0 ),
        D9( 1, 0, 0, 1 ),
        DA( 1, 0, 1, 0 ),
        DB( 1, 0, 1, 1 ),
        DC( 1, 1, 0, 0 ),
        DD( 1, 1, 0, 1 ),
        DE( 1, 1, 1, 0 ),
        DF( 1, 1, 1, 1 );
        private final byte[] decoded;

        HexDigits( int b3, int b2, int b1, int b0 ) {
            this.decoded = new byte[]{ (byte) b3, (byte) b2, (byte) b1, (byte) b0 };
        }
    }

    private static class BitsSource {
        private final byte[] bits;
        private int ptr = 0;

        private BitsSource( String hex ) {
            bits = new byte[hex.length() * 4];
            int target = 0;
            for ( char digit : hex.toCharArray() ) {
                System.arraycopy( HexDigits.valueOf( "D" + digit ).decoded, 0, bits, target, 4 );
                target += 4;
            }
        }

        private int get( int count ) {
            int buffer = 0;
            for ( int i = 0; i < count; ++i ) {
                buffer = ( buffer << 1 ) + poll();
            }
            return buffer;
        }

        private byte poll() {
            if ( ptr < bits.length ) {
                return bits[ptr++];
            }
            throw new NoSuchElementException( "End of bits lane reached" );
        }

        private int pos() {
            return ptr;
        }

        private int diff( int pos ) {
            return ptr - pos;
        }
    }

    private static class Packet {
        private final int version;
        private final int type;
        private final long literal;
        private final List<Packet> inner;

        public Packet( BitsSource data ) {
            this.version = data.get( 3 );
            this.type = data.get( 3 );
            if ( type == 4 ) {
                inner = List.of();
                literal = parseLiteral( data );
            } else {
                literal = 0L;
                if ( data.poll() == 0 ) {
                    inner = parsePacketsByLengths( data, data.get( 15 ) );
                } else {
                    inner = parsePacketsByCount( data, data.get( 11 ) );
                }
            }
        }

        private List<Packet> parsePacketsByLengths( BitsSource data, int dataLength ) {
            ArrayList<Packet> result = new ArrayList<>();
            int begin = data.pos();
            while ( data.diff( begin ) < dataLength ) {
                result.add( new Packet( data ) );
            }
            return result;
        }

        private List<Packet> parsePacketsByCount( BitsSource data, int packetsCount ) {
            ArrayList<Packet> result = new ArrayList<>();
            for ( int i = 0; i < packetsCount; ++i ) {
                result.add( new Packet( data ) );
            }
            return result;
        }

        private long parseLiteral( BitsSource data ) {
            long result = 0L;
            while ( data.poll() == 1 ) {
                result = ( result << 4 ) + data.get( 4 );
            }
            result = ( result << 4 ) + data.get( 4 );
            return result;
        }

        private int getVersionsSum() {
            return inner.stream().mapToInt( Packet::getVersionsSum ).sum() + version;
        }

        private long calculate() {
            return switch ( type ) {
                case 0 -> inner.stream().mapToLong( Packet::calculate ).sum();
                case 1 -> inner.stream().mapToLong( Packet::calculate ).reduce( 1L, ( a, b ) -> a * b );
                case 2 -> inner.stream().mapToLong( Packet::calculate ).min().orElse( 0L );
                case 3 -> inner.stream().mapToLong( Packet::calculate ).max().orElse( 0L );
                case 4 -> literal;
                case 5 -> inner.get( 0 ).calculate() > inner.get( 1 ).calculate() ? 1 : 0;
                case 6 -> inner.get( 0 ).calculate() < inner.get( 1 ).calculate() ? 1 : 0;
                case 7 -> inner.get( 0 ).calculate() == inner.get( 1 ).calculate() ? 1 : 0;
                default -> throw new IllegalStateException();
            };
        }
    }

    private final String name;
    private final Packet packet;

    public static void main( String[] args ) {
        try {
            executeTasks( "input/Y21D16I.dat", null, null );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Long expected1, Long expected2 ) {
        Utils.executeDay(
                new Day16Y21( fileName ),
                expected1,
                expected2
        );
    }

    public Day16Y21( String file ) {
        this.name = file;
        this.packet = new Packet( new BitsSource( Utils.readLines( name ).get( 0 ) ) );
    }

    @Override
    public String sampleName() {
        return name;
    }

    public Long task1() {
        return (long) packet.getVersionsSum();
    }

    public Long task2() {
        return packet.calculate();
    }
}
