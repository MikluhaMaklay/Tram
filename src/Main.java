import jdk.nashorn.internal.runtime.arrays.ArrayLikeIterator;

import java.io.File;
import java.lang.reflect.Array;
import java.rmi.UnexpectedException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        Gene[] population = new Gene[1];

        int numberOfPopulations = 0;

        createFirstPopulation(population);

        boolean isCompleted = false;

        while (!isCompleted) {
            System.out.println("Current population: " + numberOfPopulations);
            numberOfPopulations++;

            selection(population);

            rearrangingByScore(population);

            isCompleted = checkingIfComplete(population);

            if (!isCompleted) createNewPopulation(population);

            System.out.println("Current score = " + population[0].score);
            System.out.println("nextBlocks = " + population[0].nextBlocks);
            System.out.println("futurePositions = " + population[0].futurePositions);
            System.out.println("ddt = " + population[0].ddt);
            System.out.println("numberOfPopulations = " + numberOfPopulations);
            System.out.println();
        }

        result(population, numberOfPopulations);
    }

    public static void result(Gene[] population, int numberOfPopulations){
        System.out.println("The best of the best:");
        System.out.println("score = " + population[0].score);
        System.out.println("nextBlocks = " + population[0].nextBlocks);
        System.out.println("futurePositions = " + population[0].futurePositions);
        System.out.println("ddt = " + population[0].ddt);
        System.out.println("numberOfPopulations = " + numberOfPopulations);
    }
    public static boolean checkingIfComplete(Gene[] population){
        return population[0].score > 0.9;
    }

    public static void createNewPopulation(Gene[] population){
        Gene[] newPopulation = new Gene[population.length];
        int genesToRemain = 2;
        int genesToDelete = 1;
        int genesToCrossover = population.length - genesToDelete - genesToRemain;

        genesToRemain(genesToRemain, genesToDelete, genesToCrossover, population, newPopulation);

        genesToDelete(genesToRemain, genesToDelete, genesToCrossover, population, newPopulation);

        genesToCrossover(genesToRemain, genesToDelete, genesToCrossover, population, newPopulation);

        changePopulationWithNewPopulation(population, newPopulation);
    }

    public static void changePopulationWithNewPopulation(Gene[] population, Gene[] newPopulation){
        for (int i = 0; i < population.length; i++){
            population[i] = newPopulation[i];
        }
    }

    public static void genesToRemain(int genesToRemain, int genesToDelete, int genesToCrossover, Gene[] population, Gene[] newPopulation){
        for (int i = 0; i < population.length; i++){
            newPopulation[i] = population[i];
        }
    }

    public static void genesToDelete(int genesToRemain, int genesToDelete, int genesToCrossover, Gene[] population, Gene[] newPopulation){
        for (int i = genesToRemain + genesToCrossover; i < population.length; i++){
            newPopulation[i] = new Gene();
        }
    }

    public static void genesToCrossover(int genesToRemain, int genesToDelete, int genesToCrossover, Gene[] population, Gene[] newPopulation){
        for (int i = genesToRemain; i < population.length - genesToDelete; i++){
            Gene parent1 = population[new Random().nextInt(genesToCrossover) + genesToRemain];
            Gene parent2;
            do {
                parent2 = population[new Random().nextInt(genesToCrossover) + genesToRemain];
            } while (parent1 == parent2);

            int r = new Random().nextInt(2) + 1;
            if (r == 1) newPopulation[i].nextBlocks = parent1.nextBlocks;
            else newPopulation[i].nextBlocks = parent2.nextBlocks;

            r = new Random().nextInt(2) + 1;
            if (r == 1) newPopulation[i].futurePositions = parent1.futurePositions;
            else newPopulation[i].futurePositions = parent2.futurePositions;

            r = new Random().nextInt(2) + 1;
            if (r == 1) newPopulation[i].ddt = parent1.ddt;
            else newPopulation[i].ddt = parent2.ddt;
        }

        mutation(genesToRemain, genesToCrossover, newPopulation);
    }

    public static void mutation(int genesToRemain, int genesToCrossover, Gene[] newPopulation){
        double mutationRate = 0.05;
        for (int i = genesToRemain; i < genesToRemain + genesToCrossover; i++){
            int mutationChance = new Random().nextInt(100);
            if (mutationChance < mutationRate * 100){
                newPopulation[i].nextBlocks = new Random().nextInt(10);
            }

            mutationChance = new Random().nextInt(100);
            if (mutationChance < mutationRate * 100){
                newPopulation[i].futurePositions = new Random().nextInt(15);
            }

            mutationChance = new Random().nextInt(100);
            if (mutationChance < mutationRate * 100){
                newPopulation[i].ddt = new Random().nextDouble();
            }
        }
    }


    public static void rearrangingByScore(Gene[] population){
        Arrays.sort(population, new Comparator<Gene>() {
            @Override
            public int compare(Gene o1, Gene o2) {
                return Double.compare(o2.score, o1.score);
            }
        });
    }


    public static void createFirstPopulation(Gene[] population){
        for (int i = 0; i < population.length; i++){
            population[i] = new Gene();
        }
    }

    public static void selection(Gene[] population) throws InterruptedException {
        int timeOfOnePopulation = 4 * 1000;
        //TODO: неверно считается score
        int maxSpeed = 17;
        int numberOfRailBlocks = timeOfOnePopulation * maxSpeed;
        Thread[] threads = new Thread[population.length];
        for (int i = 0; i < population.length; i++) {
            int finalI = i;
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    int points = runSimulation(population[finalI].nextBlocks, population[finalI].futurePositions, population[finalI].ddt, timeOfOnePopulation, maxSpeed);
                    population[finalI].score = points * 1.0 / (numberOfRailBlocks * RailBlock.length);

                }
            });
            threads[i].start();

//            gene.score = (int) Math.pow(gene.score, Math.E);
            //TODO: think about percentage
        }
        for (Thread thread: threads) thread.join();
    }


    public static int runSimulation(int nextBlocks, int futurePositions, double ddt, int timeOfOnePopulation, int maxSpeed){
        Frame frame = new Frame(nextBlocks, futurePositions, ddt, timeOfOnePopulation, maxSpeed);
        while (frame.isShowing()){
            frame.repaint();
        }
        return frame.points;
    }

}

