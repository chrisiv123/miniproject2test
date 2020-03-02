package miniproject2test;

import java.util.Random;

public class Rand {
    
    static int getRandomNumber(final int low, final int high)
    {
        return (int)Math.round((high - low) * new Random().nextDouble() + low);
    }
    
    static int getExclusiveRandomNumber(final int high, final int except)
    {
        boolean done = false;
        int getRand = 0;

        while(!done)
        {
            getRand = new Random().nextInt(high);
            if(getRand != except){
                done = true;
            }
        }

        return getRand;
    }
    
    static int getRandomNumber(int low, int high, int[] except)
    {
        boolean done = false;
        int getRand = 0;

        if(high != low){
            while(!done)
            {
                done = true;
                getRand = (int)Math.round((high - low) * new Random().nextDouble() + low);
                for(int i = 0; i < except.length; i++) //UBound(except)
                {
                    if(getRand == except[i]){
                        done = false;
                    }
                } // i
            }
            return getRand;
        }else{
            return high; // or low (it doesn't matter).
        }
    }

}
