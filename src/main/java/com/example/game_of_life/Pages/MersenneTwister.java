package com.example.game_of_life.Pages;

import java.io.*;

public strictfp class MersenneTwister extends java.util.Random implements Serializable, Cloneable
{
    // Serialization
    private static final long serialVersionUID = -4035832775130174188L;  // locked as of Version 15

    // Period parameters
    private static final int N = 624;
    private static final int M = 397;
    private static final int MATRIX_A = 0x9908b0df;   //    private static final * constant vector a
    private static final int UPPER_MASK = 0x80000000; // most significant w-r bits
    private static final int LOWER_MASK = 0x7fffffff; // least significant r bits

    // Tempering parameters
    private static final int TEMPERING_MASK_B = 0x9d2c5680;
    private static final int TEMPERING_MASK_C = 0xefc60000;

    private int mt[]; // the array for the state vector
    private int mti; // mti==N+1 means mt[N] is not initialized
    private int mag01[];

    // a good initial seed (of int size, though stored in a long)
    //private static final long GOOD_SEED = 4357;

    /* implemented here because there's a bug in Random's implementation
       of the Gaussian code (divide by zero, and log(0), ugh!), yet its
       gaussian variables are private so we can't access them here.  :-( */

    private double __nextNextGaussian;
    private boolean __haveNextNextGaussian;

    /* We're overriding all internal data, to my knowledge, so this should be okay */
    public Object clone()
    {
        try
        {
            MersenneTwister f = (MersenneTwister)(super.clone());
            f.mt = (int[])(mt.clone());
            f.mag01 = (int[])(mag01.clone());
            return f;
        }
        catch (CloneNotSupportedException e) { throw new InternalError(); } // should never happen
    }

    /** Returns true if the MersenneTwister's current internal state is equal to another MersenneTwister.
     This is roughly the same as equals(other), except that it compares based on value but does not
     guarantee the contract of immutability (obviously random number generators are immutable).
     Note that this does NOT check to see if the internal gaussian storage is the same
     for both.  You can guarantee that the internal gaussian storage is the same (and so the
     nextGaussian() methods will return the same values) by calling clearGaussian() on both
     objects. */
    public synchronized boolean stateEquals(MersenneTwister other)
    {
        if (other == this) return true;
        if (other == null)return false;
        synchronized(other)
        {
            if (mti != other.mti) return false;
            for(int x=0;x<mag01.length;x++)
                if (mag01[x] != other.mag01[x]) return false;
            for(int x=0;x<mt.length;x++)
                if (mt[x] != other.mt[x]) return false;
            return true;
        }
    }

    /** Reads the entire state of the MersenneTwister RNG from the stream */
    public synchronized void readState(DataInputStream stream) throws IOException
    {
        int len = mt.length;
        for(int x=0;x<len;x++) mt[x] = stream.readInt();

        len = mag01.length;
        for(int x=0;x<len;x++) mag01[x] = stream.readInt();

        mti = stream.readInt();
        __nextNextGaussian = stream.readDouble();
        __haveNextNextGaussian = stream.readBoolean();
    }

    /** Writes the entire state of the MersenneTwister RNG to the stream */
    public synchronized void writeState(DataOutputStream stream) throws IOException
    {
        int len = mt.length;
        for(int x=0;x<len;x++) stream.writeInt(mt[x]);

        len = mag01.length;
        for(int x=0;x<len;x++) stream.writeInt(mag01[x]);

        stream.writeInt(mti);
        stream.writeDouble(__nextNextGaussian);
        stream.writeBoolean(__haveNextNextGaussian);
    }


    /**
     * Constructor using the default seed.
     */
    public MersenneTwister()
    {
        this(System.currentTimeMillis());
    }

    /**
     * Constructor using a given seed.  Though you pass this seed in
     * as a long, it's best to make sure it's actually an integer.
     */
    public MersenneTwister(long seed)
    {
        super(seed);    /* just in case */
        setSeed(seed);
    }

    /**
     * Constructor using an array of integers as seed.
     * Your array must have a non-zero length.  Only the first 624 integers
     * in the array are used; if the array is shorter than this then
     * integers are repeatedly used in a wrap-around fashion.
     */
    public MersenneTwister(int[] array)
    {
        super(System.currentTimeMillis());    /* pick something at random just in case */
        setSeed(array);
    }

    /**
     * Initalize the pseudo random number generator.  Don't
     * pass in a long that's bigger than an int (Mersenne Twister
     * only uses the first 32 bits for its seed).
     */

    synchronized public void setSeed(long seed)
    {
        // it's always good style to call super
        super.setSeed(seed);

        // Due to a bug in java.util.Random clear up to 1.2, we're
        // doing our own Gaussian variable.
        __haveNextNextGaussian = false;

        mt = new int[N];

        mag01 = new int[2];
        mag01[0] = 0x0;
        mag01[1] = MATRIX_A;

        mt[0]= (int)(seed & 0xffffffff);
        mt[0] = (int) seed;
        for (mti=1; mti<N; mti++)
        {
            mt[mti] =
                    (1812433253 * (mt[mti-1] ^ (mt[mti-1] >>> 30)) + mti);
            /* See Knuth TAOCP Vol2. 3rd Ed. P.106 for multiplier. */
            /* In the previous versions, MSBs of the seed affect   */
            /* only MSBs of the array mt[].                        */
            /* 2002/01/09 modified by Makoto Matsumoto             */
            // mt[mti] &= 0xffffffff;
            /* for >32 bit machines */
        }
    }


    /**
     * Sets the seed of the MersenneTwister using an array of integers.
     * Your array must have a non-zero length.  Only the first 624 integers
     * in the array are used; if the array is shorter than this then
     * integers are repeatedly used in a wrap-around fashion.
     */

    synchronized public void setSeed(int[] array)
    {
        if (array.length == 0)
            throw new IllegalArgumentException("Array length must be greater than zero");
        int i, j, k;
        setSeed(19650218);
        i=1; j=0;
        k = (N>array.length ? N : array.length);
        for (; k!=0; k--)
        {
            mt[i] = (mt[i] ^ ((mt[i-1] ^ (mt[i-1] >>> 30)) * 1664525)) + array[j] + j; /* non linear */
            // mt[i] &= 0xffffffff; /* for WORDSIZE > 32 machines */
            i++;
            j++;
            if (i>=N) { mt[0] = mt[N-1]; i=1; }
            if (j>=array.length) j=0;
        }
        for (k=N-1; k!=0; k--)
        {
            mt[i] = (mt[i] ^ ((mt[i-1] ^ (mt[i-1] >>> 30)) * 1566083941)) - i; /* non linear */
            // mt[i] &= 0xffffffff; /* for WORDSIZE > 32 machines */
            i++;
            if (i>=N)
            {
                mt[0] = mt[N-1]; i=1;
            }
        }
        mt[0] = 0x80000000; /* MSB is 1; assuring non-zero initial array */
    }



    /**
     * Returns an integer with <i>bits</i> bits filled with a random number.
     */
    synchronized protected int next(int bits)
    {
        int y;

        if (mti >= N)   // generate N words at one time
        {
            int kk;
            final int[] mt = this.mt; // locals are slightly faster
            final int[] mag01 = this.mag01; // locals are slightly faster

            for (kk = 0; kk < N - M; kk++)
            {
                y = (mt[kk] & UPPER_MASK) | (mt[kk+1] & LOWER_MASK);
                mt[kk] = mt[kk+M] ^ (y >>> 1) ^ mag01[y & 0x1];
            }
            for (; kk < N-1; kk++)
            {
                y = (mt[kk] & UPPER_MASK) | (mt[kk+1] & LOWER_MASK);
                mt[kk] = mt[kk+(M-N)] ^ (y >>> 1) ^ mag01[y & 0x1];
            }
            y = (mt[N-1] & UPPER_MASK) | (mt[0] & LOWER_MASK);
            mt[N-1] = mt[M-1] ^ (y >>> 1) ^ mag01[y & 0x1];

            mti = 0;
        }

        y = mt[mti++];
        y ^= y >>> 11;                          // TEMPERING_SHIFT_U(y)
        y ^= (y << 7) & TEMPERING_MASK_B;       // TEMPERING_SHIFT_S(y)
        y ^= (y << 15) & TEMPERING_MASK_C;      // TEMPERING_SHIFT_T(y)
        y ^= (y >>> 18);                        // TEMPERING_SHIFT_L(y)

        return y >>> (32 - bits);    // hope that's right!
    }

    /* If you've got a truly old version of Java, you can omit these
       two next methods. */

    private synchronized void writeObject(ObjectOutputStream out)
            throws IOException
    {
        // just so we're synchronized.
        out.defaultWriteObject();
    }

    private void readObject (ObjectInputStream in)   // readObject never needs to be Synchronized
            throws IOException, ClassNotFoundException
    {
        in.defaultReadObject();
    }

    /** This method is missing from jdk 1.0.x and below.  JDK 1.1
     includes this for us, but what the heck.*/
    public boolean nextBoolean() {return next(1) != 0;}

    /** This generates a coin flip with a probability <tt>probability</tt>
     of returning true, else returning false. <tt>probability</tt> must
     be between 0.0 and 1.0, inclusive.  Not as precise a random real
     event as nextBoolean(double), but twice as fast. To explicitly
     use this, remember you may need to cast to float first. */

    public boolean nextBoolean (float probability)
    {
        if (probability < 0.0f || probability > 1.0f)
            throw new IllegalArgumentException ("probability must be between 0.0 and 1.0 inclusive.");
        if (probability==0.0f) return false;            // fix half-open issues
        else if (probability==1.0f) return true;        // fix half-open issues
        return nextFloat() < probability;
    }

    /** This generates a coin flip with a probability <tt>probability</tt>
     of returning true, else returning false. <tt>probability</tt> must
     be between 0.0 and 1.0, inclusive. */

    public boolean nextBoolean (double probability)
    {
        if (probability < 0.0 || probability > 1.0)
            throw new IllegalArgumentException ("probability must be between 0.0 and 1.0 inclusive.");
        if (probability==0.0) return false;             // fix half-open issues
        else if (probability==1.0) return true; // fix half-open issues
        return nextDouble() < probability;
    }

    /** This method is missing from JDK 1.1 and below.  JDK 1.2
     includes this for us, but what the heck. */

    public int nextInt(int n)
    {
        if (n<=0)
            throw new IllegalArgumentException("n must be positive, got: " + n);

        if ((n & -n) == n)
            return (int)((n * (long)next(31)) >> 31);

        int bits, val;
        do
        {
            bits = next(31);
            val = bits % n;
        }
        while(bits - val + (n-1) < 0);
        return val;
    }

    /** This method is for completness' sake.
     Returns a long drawn uniformly from 0 to n-1.  Suffice it to say,
     n must be greater than 0, or an IllegalArgumentException is raised. */

    public long nextLong(long n)
    {
        if (n<=0)
            throw new IllegalArgumentException("n must be positive, got: " + n);

        long bits, val;
        do
        {
            bits = (nextLong() >>> 1);
            val = bits % n;
        }
        while(bits - val + (n-1) < 0);
        return val;
    }


    /** A bug fix for versions of JDK 1.1 and below.  JDK 1.2 fixes
     this for us, but what the heck. */
    public double nextDouble()
    {
        return (((long)next(26) << 27) + next(27))
                / (double)(1L << 53);
    }

    /** Returns a double in the range from 0.0 to 1.0, possibly inclusive of 0.0 and 1.0 themselves.  Thus:

     <table border=0>
     <tr><th>Expression</th><th>Interval</th></tr>
     <tr><td>nextDouble(false, false)</td><td>(0.0, 1.0)</td></tr>
     <tr><td>nextDouble(true, false)</td><td>[0.0, 1.0)</td></tr>
     <tr><td>nextDouble(false, true)</td><td>(0.0, 1.0]</td></tr>
     <tr><td>nextDouble(true, true)</td><td>[0.0, 1.0]</td></tr>
     <caption>Table of intervals</caption>
     </table>

     <p>This version preserves all possible random values in the double range.
     */
    public double nextDouble(boolean includeZero, boolean includeOne)
    {
        double d = 0.0;
        do
        {
            d = nextDouble();                           // grab a value, initially from half-open [0.0, 1.0)
            if (includeOne && nextBoolean()) d += 1.0;  // if includeOne, with 1/2 probability, push to [1.0, 2.0)
        }
        while ( (d > 1.0) ||                            // everything above 1.0 is always invalid
                (!includeZero && d == 0.0));            // if we're not including zero, 0.0 is invalid
        return d;
    }

    /** A bug fix for versions of JDK 1.1 and below.  JDK 1.2 fixes
     this for us, but what the heck. */

    public float nextFloat()
    {
        return next(24) / ((float)(1 << 24));
    }



    /** Returns a float in the range from 0.0f to 1.0f, possibly inclusive of 0.0f and 1.0f themselves.  Thus:

     <table border=0>
     <tr><th>Expression</th><th>Interval</th></tr>
     <tr><td>nextFloat(false, false)</td><td>(0.0f, 1.0f)</td></tr>
     <tr><td>nextFloat(true, false)</td><td>[0.0f, 1.0f)</td></tr>
     <tr><td>nextFloat(false, true)</td><td>(0.0f, 1.0f]</td></tr>
     <tr><td>nextFloat(true, true)</td><td>[0.0f, 1.0f]</td></tr>
     <caption>Table of intervals</caption>
     </table>

     <p>This version preserves all possible random values in the float range.
     */
    public float nextFloat(boolean includeZero, boolean includeOne)
    {
        float d = 0.0f;
        do
        {
            d = nextFloat();                            // grab a value, initially from half-open [0.0f, 1.0f)
            if (includeOne && nextBoolean()) d += 1.0f; // if includeOne, with 1/2 probability, push to [1.0f, 2.0f)
        }
        while ( (d > 1.0f) ||                           // everything above 1.0f is always invalid
                (!includeZero && d == 0.0f));           // if we're not including zero, 0.0f is invalid
        return d;
    }



    /** A bug fix for all versions of the JDK.  The JDK appears to
     use all four bytes in an integer as independent byte values!
     Totally wrong. I've submitted a bug report. */

    public void nextBytes(byte[] bytes)
    {
        for (int x=0;x<bytes.length;x++) bytes[x] = (byte)next(8);
    }

    /** For completeness' sake, though it's not in java.util.Random.  */

    public char nextChar()
    {
        // chars are 16-bit UniCode values
        return (char)(next(16));
    }

    /** For completeness' sake, though it's not in java.util.Random. */

    public short nextShort()
    {
        return (short)(next(16));
    }

    /** For completeness' sake, though it's not in java.util.Random.  */

    public byte nextByte()
    {
        return (byte)(next(8));
    }

    /**
     Clears the internal gaussian variable from the RNG.  You only need to do this
     in the rare case that you need to guarantee that two RNGs have identical internal
     state.  Otherwise, disregard this method. See stateEquals(other).
     */
    public synchronized void clearGaussian() { __haveNextNextGaussian = false; }

    /** A bug fix for all JDK code including 1.2.  nextGaussian can theoretically
     ask for the log of 0 and divide it by 0! See Java bug
     <a href="http://developer.java.sun.com/developer/bugParade/bugs/4254501.html">
     http://developer.java.sun.com/developer/bugParade/bugs/4254501.html</a>
     */

    synchronized public double nextGaussian()
    {
        if (__haveNextNextGaussian)
        {
            __haveNextNextGaussian = false;
            return __nextNextGaussian;
        }
        else
        {
            double v1, v2, s;
            do
            {
                v1 = 2 * nextDouble() - 1; // between -1.0 and 1.0
                v2 = 2 * nextDouble() - 1; // between -1.0 and 1.0
                s = v1 * v1 + v2 * v2;
            } while (s >= 1 || s==0 );
            double multiplier = StrictMath.sqrt(-2 * StrictMath.log(s)/s);
            __nextNextGaussian = v2 * multiplier;
            __haveNextNextGaussian = true;
            return v1 * multiplier;
        }
    }

}