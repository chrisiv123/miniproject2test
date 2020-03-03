package miniproject2test;

import java.util.Random;

public class Rand {
    
    static int getRandomNumber(final int low, final int high)
    {
        return (int)Math.round((high - low) * new Random().nextDouble() + low);
    }
    
    static int getExclusiveRandomNumber(final int high, final int arr)
    {
        boolean done = false;
        int getRand = 0;

        while(!done)
        {
            getRand = new Random().nextInt(high);
            if(getRand != arr){
                done = true;
            }
        }
        return getRand;
    }
    
    static int getRandomNumber(int low, int high, int[] arr)
    {
        boolean done = false;
        int getRand = 0;

        if(high != low){
            while(!done)
            {
                done = true;
                getRand = (int)Math.round((high - low) * new Random().nextDouble() + low);
                for(int i = 0; i < arr.length; i++)
                {
                    if(getRand == arr[i]){
                        done = false;
                    }
                } // i
            }
            return getRand;
        }else{
            return low;
        }
    }

}
