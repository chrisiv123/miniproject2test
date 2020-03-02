package miniproject2test;
import java.util.ArrayList;
import java.util.Random;

public class NQueen1
{
    private static final int START_SIZE = 75;                    // Population size at start.
    private static final int MAX_EPOCHS = 1000;                  // Arbitrary number of test cycles.
    private static final double MATING_PROBABILITY = 0.7;        // Probability of two chromosomes mating. Range: 0.0 < MATING_PROBABILITY < 1.0
    private static final double MUTATION_RATE = 0.05;           // Mutation Rate. Range: 0.0 < MUTATION_RATE < 1.0
    private static final int MIN_SELECT = 10;                    // Minimum parents allowed for selection.
    private static final int MAX_SELECT = 50;                    // Maximum parents allowed for selection. Range: MIN_SELECT < MAX_SELECT < START_SIZE
    private static final int OFFSPRING_PER_GENERATION = 20;      // New offspring created per generation. Range: 0 < OFFSPRING_PER_GENERATION < MAX_SELECT.
    private static final int MINIMUM_SHUFFLES = 8;               // For randomizing starting chromosomes
    private static final int MAXIMUM_SHUFFLES = 20;
    private static final int PBC_MAX = 4;                        // Maximum Position-Based Crossover points. Range: 0 < PBC_MAX < 8 (> 8 isn't good).
    
    private static final int MAX_LENGTH = 8;                    // chess board width.

    private static int epoch = 0;
    private static int childCount = 0;
    private static int nextMutation = 0;                         // For scheduling mutations.
    private static int mutations = 0;

    private static ArrayList<Chromosome> population = new ArrayList<Chromosome>();
    
    
    
    //////////////// runing that alg
    private static void algorithm()
    {
        int popSize = 0;
        Chromosome thisChromo = null;
        boolean done = false;

        initializeChromosomes();
        mutations = 0;
        nextMutation = Rand.getRandomNumber(0, (int)Math.round(1.0 / MUTATION_RATE));
        
        while(!done)
        {
            popSize = population.size();
            for(int i = 0; i < popSize; i++)
            {
                thisChromo = population.get(i);
                if((thisChromo.conflicts() == 0) || epoch == MAX_EPOCHS){
                    done = true;
                }
            }
            
            getFitness();
            
            rouletteSelection();
            
            mating();

            prepNextEpoch();
            
            epoch++;
            // This is here simply to show the runtime status.
            System.out.println("Epoch: " + epoch);
            
        }
        
        System.out.println("done.");
        
        if(epoch != MAX_EPOCHS){
            popSize = population.size();
            for(int i = 0; i < popSize; i++)
            {
                thisChromo = population.get(i);
                if(thisChromo.conflicts() == 0){
                    printbestSolution(thisChromo);
                }
            }
        }
        System.out.println("Completed " + epoch + " epochs.");
        System.out.println("Encountered " + mutations + " mutations in " + childCount + " offspring.");
        return;
    }
    
    //// fitness function 
    
    private static void getFitness()
    {
        // Lowest errors = 100%, Highest errors = 0%
        int popSize = population.size();
        Chromosome thisChromo = null;
        double bestScore = 0;
        double worstScore = 0;

        // The worst score would be the one with the highest energy, best would be lowest.
        worstScore = population.get(maximum()).conflicts();

        // Convert to a weighted percentage.
        bestScore = worstScore - population.get(minimum()).conflicts();

        for(int i = 0; i < popSize; i++)
        {
            thisChromo = population.get(i);
            thisChromo.fitness((worstScore - thisChromo.conflicts()) * 100.0 / bestScore);
        }
        
        return;
    }
    
    private static void rouletteSelection()
    {
        int j = 0;
        int popSize = population.size();
        double genTotal = 0.0;
        double selTotal = 0.0;
        int maximumToSelect = Rand.getRandomNumber(MIN_SELECT, MAX_SELECT);
        double rouletteSpin = 0.0;
        Chromosome thisChromo = null;
        Chromosome thatChromo = null;
        boolean done = false;

        for(int i = 0; i < popSize; i++)
        {
            thisChromo = population.get(i);
            genTotal += thisChromo.fitness();
        }

        genTotal *= 0.01;

        for(int i = 0; i < popSize; i++)
        {
            thisChromo = population.get(i);
            thisChromo.selectionProbability(thisChromo.fitness() / genTotal);
        }

        for(int i = 0; i < maximumToSelect; i++)
        {
            rouletteSpin = Rand.getRandomNumber(0, 99);
            j = 0;
            selTotal = 0;
            done = false;
            while(!done)
            {
                thisChromo = population.get(j);
                selTotal += thisChromo.selectionProbability();
                if(selTotal >= rouletteSpin){
                    if(j == 0){
                        thatChromo = population.get(j);
                    }else if(j >= popSize - 1){
                        thatChromo = population.get(popSize - 1);
                    }else{
                        thatChromo = population.get(j - 1);
                    }
                    thatChromo.selected(true);
                    done = true;
                }else{
                    j++;
                }
            }
        }
        return;
    }
    
///// mating
    private static void mating()
    {
        int getRand = 0;
        int parentA = 0;
        int parentB = 0;
        int newIndex1 = 0;
        int newIndex2 = 0;
        Chromosome newChromo1 = null;
        Chromosome newChromo2 = null;

        for(int i = 0; i < OFFSPRING_PER_GENERATION; i++)
        {
            parentA = chooseParent();
            // Test probability of mating.
            getRand =Rand.getRandomNumber(0, 100);
            if(getRand <= MATING_PROBABILITY * 100){
                parentB = chooseParent(parentA);
                newChromo1 = new Chromosome();
                newChromo2 = new Chromosome();
                population.add(newChromo1);
                newIndex1 = population.indexOf(newChromo1);
                population.add(newChromo2);
                newIndex2 = population.indexOf(newChromo2);
                

                partiallyMappedCrossover(parentA, parentB, newIndex1, newIndex2);


                if(childCount - 1 == nextMutation){
                    exchangeMutation(newIndex1, 1);
                }else if(childCount == nextMutation){
                    exchangeMutation(newIndex2, 1);
                }

                population.get(newIndex1).computeConflicts();
                population.get(newIndex2).computeConflicts();

                childCount += 2;

                // Schedule next mutation.
                if(childCount % (int)Math.round(1.0 / MUTATION_RATE) == 0){
                    nextMutation = childCount + Rand.getRandomNumber(0, (int)Math.round(1.0 / MUTATION_RATE));
                }
            }
        } 
        return;
    }
    
    private static void partiallyMappedCrossover(int chromA, int chromB, int child1, int child2)
    {
        int j = 0;
        int item1 = 0;
        int item2 = 0;
        int pos1 = 0;
        int pos2 = 0;
        Chromosome thisChromo = population.get(chromA);
        Chromosome thatChromo = population.get(chromB);
        Chromosome newChromo1 = population.get(child1);
        Chromosome newChromo2 = population.get(child2);
        
        
        //// make it in the middle
        
        
        int crossPoint1 = 4;
        int crossPoint2 = 5;
        
       /* if(crossPoint2 < crossPoint1){
            j = crossPoint1;
            crossPoint1 = crossPoint2;
            crossPoint2 = j;
        }
        */
        ////// this should be changed

        // Copy Parent genes to offspring.
        for(int i = 0; i < MAX_LENGTH; i++)
        {
            newChromo1.data(i, thisChromo.data(i));
            newChromo2.data(i, thatChromo.data(i));
        }

        for(int i = crossPoint1; i <= crossPoint2; i++)
        {
            // Get the two items to swap.
            item1 = thisChromo.data(i);
            item2 = thatChromo.data(i);

            // Get the items//  positions in the offspring.
            for(j = 0; j < MAX_LENGTH; j++)
            {
                if(newChromo1.data(j) == item1){
                    pos1 = j;
                }else if(newChromo1.data(j) == item2){
                    pos2 = j;
                }
            } // j

            // Swap them.
            if(item1 != item2){
                newChromo1.data(pos1, item2);
                newChromo1.data(pos2, item1);
            }

            // Get the items//  positions in the offspring.
            for(j = 0; j < MAX_LENGTH; j++)
            {
                if(newChromo2.data(j) == item2){
                    pos1 = j;
                }else if(newChromo2.data(j) == item1){
                    pos2 = j;
                }
            } // j

            // Swap them.
            if(item1 != item2){
                newChromo2.data(pos1, item1);
                newChromo2.data(pos2, item2);
            }

        } // i
        return;
    }

    private static void exchangeMutation(final int index, final int exchanges)
    {
        int i =0;
        int tempData = 0;
        Chromosome thisChromo = null;
        int gene1 = 0;
        int gene2 = 0;
        boolean done = false;
        
        thisChromo = population.get(index);

        while(!done)
        {
            gene1 = Rand.getRandomNumber(0, MAX_LENGTH - 1);
            gene2 = Rand.getExclusiveRandomNumber(MAX_LENGTH - 1, gene1);

            // Exchange the chosen genes.
            tempData = thisChromo.data(gene1);
            thisChromo.data(gene1, thisChromo.data(gene2));
            thisChromo.data(gene2, tempData);

            if(i == exchanges){
                done = true;
            }
            i++;
        }
        mutations++;
        return;
    }
    //// change to 1 choose parent if possible
    private static int chooseParent()
    {
        // Overloaded function, see also "chooseparent(ByVal parentA As Integer)".
        int parent = 0;
        Chromosome thisChromo = null;
        boolean done = false;

        while(!done)
        {
            // Randomly choose an eligible parent.
            parent = Rand.getRandomNumber(0, population.size() - 1);
            thisChromo = population.get(parent);
            if(thisChromo.selected() == true){
                done = true;
            }
        }

        return parent;
    }
    

    private static int chooseParent(final int parentA)
    {
        // Overloaded function, see also "chooseparent()".
        int parent = 0;
        Chromosome thisChromo = null;
        boolean done = false;

        while(!done)
        {
            // Randomly choose an eligible parent.
            parent = Rand.getRandomNumber(0, population.size() - 1);
            if(parent != parentA){
                thisChromo = population.get(parent);
                if(thisChromo.selected() == true){
                    done = true;
                }
            }
        }

        return parent;
    }
    
    private static void prepNextEpoch()
    {
        int popSize = 0;
        Chromosome thisChromo = null;

        // Reset flags for selected individuals.
        popSize = population.size();
        for(int i = 0; i < popSize; i++)
        {
            thisChromo = population.get(i);
            thisChromo.selected(false);
        }
        return;
    }
    
    private static void printbestSolution(Chromosome bestSolution)
    {
        String board[][] = new String[MAX_LENGTH][MAX_LENGTH];
        
        // Clear the board.
        for(int x = 0; x < MAX_LENGTH; x++)
        {
            for(int y = 0; y < MAX_LENGTH; y++)
            {
                board[x][y] = "";
            }
        }

        for(int x = 0; x < MAX_LENGTH; x++)
        {
            board[x][bestSolution.data(x)] = "Q";
        }

        // Display the board.
        System.out.println("Board:");
        for(int y = 0; y < MAX_LENGTH; y++)
        {
            for(int x = 0; x < MAX_LENGTH; x++)
            {
                if(board[x][y] == "Q"){
                    System.out.print("Q ");
                }else{
                    System.out.print(". ");
                }
            }
            System.out.print("\n");
        }

        return;
    }
    
    /////////// randoms in a new class

    
    private static int minimum()
    {
        // Returns an array index.
        int popSize = 0;
        Chromosome thisChromo = null;
        Chromosome thatChromo = null;
        int winner = 0;
        boolean foundNewWinner = false;
        boolean done = false;

        while(!done)
        {
            foundNewWinner = false;
            popSize = population.size();
            for(int i = 0; i < popSize; i++)
            {
                if(i != winner){             // Avoid self-comparison.
                    thisChromo = population.get(i);
                    thatChromo = population.get(winner);
                    if(thisChromo.conflicts() < thatChromo.conflicts()){
                        winner = i;
                        foundNewWinner = true;
                    }
                }
            }
            if(foundNewWinner == false){
                done = true;
            }
        }
        return winner;
    }
    
    private static int maximum()
    {
        // Returns an array index.
        int popSize = 0;
        Chromosome thisChromo = null;
        Chromosome thatChromo = null;
        int winner = 0;
        boolean foundNewWinner = false;
        boolean done = false;

        while(!done)
        {
            foundNewWinner = false;
            popSize = population.size();
            for(int i = 0; i < popSize; i++)
            {
                if(i != winner){             // Avoid self-comparison.
                    thisChromo = population.get(i);
                    thatChromo = population.get(winner);
                    if(thisChromo.conflicts() > thatChromo.conflicts()){
                        winner = i;
                        foundNewWinner = true;
                    }
                }
            }
            if(foundNewWinner == false){
                done = true;
            }
        }
        return winner;
    }
    
    private static void initializeChromosomes()
    {
        int shuffles = 0;
        Chromosome newChromo = null;
        int chromoIndex = 0;

        for(int i = 0; i < START_SIZE; i++)
        {
            newChromo = new Chromosome();
            population.add(newChromo);
            chromoIndex = population.indexOf(newChromo);

            // Randomly choose the number of shuffles to perform.
            shuffles = Rand.getRandomNumber(MINIMUM_SHUFFLES, MAXIMUM_SHUFFLES);

            exchangeMutation(chromoIndex, shuffles);

            population.get(chromoIndex).computeConflicts();

        }
        return;
    }
    public static void main(String[] args)
    {
       algorithm();
        return;
    }
}
   
  