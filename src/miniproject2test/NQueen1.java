package miniproject2test;
import java.util.ArrayList;

//
public class NQueen1
{
    private static final int START_SIZE = 100;                    // Population size at start.
    private static final int MAX_EraS = 1000;                  // Arbitrary number of test cycles.
    private static final double MATING_PROBABILITY = 0.7;        // Probability of two chromosomes mating. Range: 0.0 < MATING_PROBABILITY < 1.0
    ///////////////////
    private static final double MUTATION_RATE = 0.05;           // Mutation Rate. Range: 0.0 < MUTATION_RATE < 1.0
    private static final int MIN_SELECT = 10;                    // Minimum parents allowed for selection.
    private static final int MAX_SELECT = 50;                    // Maximum parents allowed for selection. Range: MIN_SELECT < MAX_SELECT < START_SIZE
    private static final int OFFSPRING_PER_GENERATION = 20;      // New offspring created per generation. Range: 0 < OFFSPRING_PER_GENERATION < MAX_SELECT.
    private static final int MINIMUM_SHUFFLES = 8;               // For randomizing starting chromosomes
    private static final int MAXIMUM_SHUFFLES = 20;
 
   
    private static int Era = 0;
    private static int Children = 0;
    private static int nextMutation = 0;                         // For scheduling mutations.
    private static int mutations = 0;

    private static ArrayList<Chromosome> population = new ArrayList<Chromosome>();
    
    
    
    //////////////// running the alg
    private static void algorithm()
    {	
    	mutations = 0;
        nextMutation = Rand.getRandomNumber(0, (int)Math.round(1.0 / MUTATION_RATE));
        int PopulationSize = 0;
        Chromosome DNA = null;
        boolean Solution = false;
        initializeChromosomes();
        while(!Solution)
        {
            PopulationSize = population.size();
            for(int i = 0; i < PopulationSize; i++)
            {
                DNA = population.get(i);
                if((DNA.conflicts() == 0)){
                    Solution = true;
                }
            }            
            SetFitness();            
            Selection();            
            mating();
            Reset();
            Era++;
           // current Era
            System.out.println("Era: " + Era);
            
        }
        // solution found
        System.out.println("Finished!");
              
            PopulationSize = population.size();
            for(int i = 0; i < PopulationSize; i++)
            {
                DNA = population.get(i);
                if(DNA.conflicts() == 0){
                    PrintSolution(DNA);
                }
            }
        // statistics 
        System.out.println(DNA.fitness()+ " fitness");
        System.out.println( Era + " Eras.");
        System.out.println( mutations + " Mutations in " + Children + " Children");
        
        return;
    }
    
    //fitness function     
    private static void SetFitness()
    {        
        Chromosome DNA = null;
        double HighScore = 0, LowScore = 0;
        LowScore = population.get(maximum()).conflicts();
        HighScore = LowScore - population.get(minimum()).conflicts();
        for(int i = 0; i < population.size(); i++)
        {
        	// set fitness
            DNA = population.get(i);
            DNA.fitness((LowScore - DNA.conflicts()) * 100.0 / HighScore);
        }
        return;
    }
    
    private static void Selection()
    {
        int q = 0, PopulationSize = population.size(), maximumToSelect = Rand.getRandomNumber(MIN_SELECT, MAX_SELECT);
        double GTotal = 0.0, CTotal = 0.0, Spin = 0.0;
        Chromosome DNA = null;
        Chromosome thatChromo = null;
        boolean Solution = false;

        for(int i = 0; i < PopulationSize; i++)
        {
            DNA = population.get(i);
            GTotal += DNA.fitness();
        }
        GTotal *= 0.01;
        for(int i = 0; i < PopulationSize; i++)
        {
            DNA = population.get(i);
            DNA.selectionProbability(DNA.fitness() / GTotal);
        }

        for(int i = 0; i < maximumToSelect; i++)
        {
            Spin = Rand.getRandomNumber(0, 99);
            q = 0;
            CTotal = 0;
            Solution = false;
            while(!Solution)
            {
                DNA = population.get(q);
                CTotal += DNA.selectionProbability();
                if(CTotal >= Spin){
                    if(q == 0){
                        thatChromo = population.get(q);
                    }else if(q >= PopulationSize - 1){
                        thatChromo = population.get(PopulationSize - 1);
                    }else{
                        thatChromo = population.get(q - 1);
                    }
                    thatChromo.selected(true);
                    Solution = true;
                }else{
                    q++;
                }
            }
        }
        return;
    }
    
///// mating
    private static void mating()
    {
        int getRand = 0, parentA = 0, parentB = 0, newIndex1 = 0, newIndex2 = 0;
        Chromosome Chromosome1 = null;
        Chromosome Chromosome2 = null;

        for(int i = 0; i < OFFSPRING_PER_GENERATION; i++)
        {
            parentA = chooseParent();
            getRand = Rand.getRandomNumber(0, 100);
            if(getRand <= MATING_PROBABILITY * 100){
                parentB = chooseParent();
                Chromosome1 = new Chromosome();
                Chromosome2 = new Chromosome();
                population.add(Chromosome1);
                newIndex1 = population.indexOf(Chromosome1);
                population.add(Chromosome2);
                newIndex2 = population.indexOf(Chromosome2);
                Crossover(parentA, parentB, newIndex1, newIndex2);
                if(Children - 1 == nextMutation){
                    exchangeMutation(newIndex1, 1);
                }else if(Children == nextMutation){
                    exchangeMutation(newIndex2, 1);
                }
                population.get(newIndex1).computeConflicts();
                population.get(newIndex2).computeConflicts();
                Children += 2;
                if(Children % (int)Math.round(1.0 / MUTATION_RATE) == 0){
                    nextMutation = Children + Rand.getRandomNumber(0, (int)Math.round(1.0 / MUTATION_RATE));
                }
            }
        } 
        return;
    }
    
    private static void Crossover(int chromA, int chromB, int child1, int child2)
    {
        int j = 0, item1 = 0, item2 = 0, pos1 = 0, pos2 = 0;
        Chromosome DNA = population.get(chromA);
        Chromosome thatChromo = population.get(chromB);
        Chromosome Chromosome1 = population.get(child1);
        Chromosome Chromosome2 = population.get(child2);
        int crossPoint1 = 4;
        int crossPoint2 = 5;
        for(int i = 0; i < 8; i++)
        {
            Chromosome1.data(i, DNA.data(i));
            Chromosome2.data(i, thatChromo.data(i));
        }
        for(int i = crossPoint1; i <= crossPoint2; i++)
        {
            item1 = DNA.data(i);
            item2 = thatChromo.data(i);
            for(j = 0; j < 8; j++)
            {
                if(Chromosome1.data(j) == item1){
                    pos1 = j;
                }else if(Chromosome1.data(j) == item2){
                    pos2 = j;
                }
            } 
            if(item1 != item2){
                Chromosome1.data(pos1, item2);
                Chromosome1.data(pos2, item1);
            }
            for(j = 0; j < 8; j++)
            {
                if(Chromosome2.data(j) == item2){
                    pos1 = j;
                }else if(Chromosome2.data(j) == item1){
                    pos2 = j;
                }
            }
            if(item1 != item2){
                Chromosome2.data(pos1, item1);
                Chromosome2.data(pos2, item2);
            }
        }
        return;
    }

    private static void exchangeMutation(final int index, final int exchanges)
    {
        int i =0 , tempData = 0, gene1 = 0, gene2 = 0;
        Chromosome DNA = null;
        boolean Solution = false;        
        DNA = population.get(index);
        while(!Solution)
        {
            gene1 = Rand.getRandomNumber(0, 8 - 1);
            gene2 = Rand.getExclusiveRandomNumber(8 - 1, gene1);
            tempData = DNA.data(gene1);
            DNA.data(gene1, DNA.data(gene2));
            DNA.data(gene2, tempData);
            if(i == exchanges){
                Solution = true;
            }
            i++;
        }
        mutations++;
        return;
    }
    private static int chooseParent()
    {
        int parent = 0;
        Chromosome DNA = null;
        boolean Solution = false;
        while(!Solution)
        {
            parent = Rand.getRandomNumber(0, population.size() - 1);
            DNA = population.get(parent);
            if(DNA.selected() == true){
                Solution = true;
            }
        }
        return parent;
    }

    private static void Reset()
    {
        int PopulationSize = 0;
        Chromosome DNA = null;
        PopulationSize = population.size();
        for(int i = 0; i < PopulationSize; i++)
        {
            DNA = population.get(i);
            DNA.selected(false);
        }
        return;
    }
    
    private static void PrintSolution(Chromosome bestSolution)
    {
        String board[][] = new String[8][8];

        for(int x = 0; x < 8; x++)
        {
            for(int y = 0; y < 8; y++)
            {
                board[x][y] = "N";
            }
        }

        for(int x = 0; x < 8; x++)
        {
            board[x][bestSolution.data(x)] = "Q";
        }

        // Display the board.
        System.out.println("Board:");
        for(int y = 0; y < 8; y++)
        {
            for(int x = 0; x < 8; x++)
            {
                if(board[x][y] == "Q"){
                    System.out.print("|Q|");
                }else{
                    System.out.print("[ ]");
                }
            }
            System.out.print("\n");
        }
        return;
    }

    private static int minimum()
    {
        // Returns an array index.
        int PopulationSize = 0;
        Chromosome DNA = null;
        Chromosome thatChromo = null;
        int winner = 0;
        boolean foundNewWinner = false;
        boolean Solution = false;
        while(!Solution)
        {
            foundNewWinner = false;
            PopulationSize = population.size();
            for(int i = 0; i < PopulationSize; i++)
            {
                if(i != winner){             // Avoid self-comparison.
                    DNA = population.get(i);
                    thatChromo = population.get(winner);
                    if(DNA.conflicts() < thatChromo.conflicts()){
                        winner = i;
                        foundNewWinner = true;
                    }
                }
            }
            if(foundNewWinner == false){
                Solution = true;
            }
        }
        return winner;
    }
    
    private static int maximum()
    {
        // Returns an array index.
        int PopulationSize = 0;
        Chromosome DNA = null;
        Chromosome thatChromo = null;
        int winner = 0;
        boolean foundNewWinner = false;
        boolean Solution = false;
        while(!Solution)
        {
            foundNewWinner = false;
            PopulationSize = population.size();
            for(int i = 0; i < PopulationSize; i++)
            {
                if(i != winner){             // Avoid self-comparison.
                    DNA = population.get(i);
                    thatChromo = population.get(winner);
                    if(DNA.conflicts() > thatChromo.conflicts()){
                        winner = i;
                        foundNewWinner = true;
                    }
                }
            }
            if(foundNewWinner == false){
                Solution = true;
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
   
  