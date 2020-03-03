package miniproject2test;


class Chromosome
{
    private static final int BOARD = 8;
    private int BData[] = new int[BOARD];
    private int CConflicts = 0;
    private double CFitness = 0.0;
    private boolean CSelected = false;
    private double CProbability = 0.0;
    
    // set conflicts
    public void conflicts(int value)
    {
        this.CConflicts = value;
        return;
    }
    // get conflicts
    public int conflicts()
    {
        return this.CConflicts;
    }

    public double selectionProbability()
    {
        return CProbability;
    }
    
    public void selectionProbability(final double SelProb)
    {
        CProbability = SelProb;
        return;
    }

    public boolean selected()
    {
        return CSelected;
    }
    
    public void selected(final boolean sValue)
    {
        CSelected = sValue;
        return;
    }
    // get fitness
    public double fitness()
    {
        return CFitness;
    }
    // set fitness
    public void fitness(final double score)
    {
        CFitness = score;
        return;
    }

    public int data(final int index)
    {
        return BData[index];
    }
    
    public void data(final int index, final int value)
    {
        BData[index] = value;
        return;
    }

    public Chromosome()
    {
        for(int i = 0; i < BOARD; i++)
        {
            this.BData[i] = i;
        }
        return;
    }
    
    public void computeConflicts()
    {
        int x = 0;
        int y = 0;
        int tempx = 0;
        int tempy = 0;
        String board[][] = new String[BOARD][BOARD];
        int conflicts = 0;
        int dx[] = new int[] {-1, 1, -1, 1};
        int dy[] = new int[] {-1, 1, 1, -1};
        boolean done = false;

        for(int i = 0; i < BOARD; i++)
        {
            for(int j = 0; j < BOARD; j++)
            {
                board[i][j] = "";
            }
        }
        for(int i = 0; i < BOARD; i++)
        {
            board[i][this.BData[i]] = "Q";
        }

        for(int i = 0; i < BOARD; i++)
        {
            x = i;
            y = this.BData[i];
            for(int j = 0; j <= 3; j++)
            {
                tempx = x;
                tempy = y;
                done = false;
                while(!done)
                {
                    tempx += dx[j];
                    tempy += dy[j];
                    if((tempx < 0 || tempx >= BOARD) || (tempy < 0 || tempy >= BOARD)){
                        done = true;
                    }else{
                        if(board[tempx][tempy].compareToIgnoreCase("Q") == 0){
                            conflicts++;
                        }
                    }
                }
            }
        }

        this.CConflicts = conflicts;
    }


}