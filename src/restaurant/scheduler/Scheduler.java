package restaurant.scheduler;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Scheduler {

    private Food[] foods;
    private int nof;//number of foods
    private int wholePeriod;
    private int idleTime;
    private int algorithm;//1 for edf, 2 for rm, 3 for llf
    private int switchCounter;

    public Scheduler() {
        this.idleTime = 0;
        this.switchCounter = 0;
        this.userIn();
        this.wholePeriod = this.lcmCalc();
        switch (this.algorithm) {
            case 1:
                this.edfScheduler();
                break;
            case 2:
                this.rmScheduler();
                break;
            case 3:
                this.llfScheduler();
                break;
        }
    }

    private void userIn() {//get input from user
        Scanner scanner = new Scanner(System.in);
        nof = scanner.nextInt();
        scanner.nextLine();
        String food;
        this.foods = new Food[nof];

        for (int i = 0; i < nof; i++) {
            food = scanner.nextLine();
            String[] pf = food.split(" ");
            if (pf.length != 4) {
                System.err.println("Invalid input!");
                System.exit(1);
            }
            foods[i] = new Food(pf[0], Integer.parseInt(pf[1]), Integer.parseInt(pf[2]), Integer.parseInt(pf[3]));
        }
        System.out.println("Which algorithm you wish to run?\n1-earliest deadline first"
                + "\n2-rate monotonic\n3-least laxity first\n (**JUST ENTER THE NUMBER**)");
        this.algorithm = scanner.nextInt();
    }

    private int lcmCalc() {
        int lcm = foods[0].getPeriod();
        for (int j = 1; j < foods.length; j++) {
            int a = lcm, b = foods[j].getPeriod(), gcd = 1;
            //finds GCD  
            for (int i = 1; i <= a && i <= b; ++i) {
                //divides both the numbers by i, if the remainder is 0 the number is completely divisible by i  
                //Checks that i is present in both or not  
                //returns true if both conditions are true  
                if (a % i == 0 && b % i == 0) //assigns i into gcd  
                {
                    gcd = i;
                }
            }
            //determines lcm of the given number  
            lcm = (a * b) / gcd;
        }
        return lcm;
    }

    private void edfScheduler() {
        PriorityQueue<Food> queue = new PriorityQueue<>((Food o1, Food o2) -> {
            if (o1.getDeadline() == o2.getDeadline()) {
                return 0;
            } else {
                return o1.getDeadline() > o2.getDeadline() ? 1 : -1;
            }
        });
        queue.addAll(Arrays.asList(foods));
        Food currentFood;
        for (int i = 0; i < this.wholePeriod;) {

            currentFood = queue.poll();

            if (currentFood == null) {
                System.out.println(i + " idle");
                idleTime++;
                if (i != 0) {
                    for (Food food : foods) {
                        if (food.getNextEntry() == i) {
                            queue.add(food);
                        }
                    }
                }
                i++;
                continue;
            }
            this.switchCounter++;
            if (currentFood.getCookTime() + i > currentFood.getDeadline()) {
                System.out.println(i + " " + currentFood.getName() + " will miss deadline.");
            }

            int currentTime = i;
            for (; i < currentTime + currentFood.getCookTime();) {
                System.out.println(i + " " + currentFood.getName());
                //cooking is happening
                if (i > currentFood.getDeadline()) {
                    System.out.println(currentFood.getName() + " deadline missed.");
                }
                i++;
                if (i != 0) {
                    for (Food food : foods) {
                        if (food.getNextEntry() == i) {
                            queue.add(food);
                        }
                    }
                }
            }
            for (Food food : queue) {
                if (food.getNextEntry() != i) {
                    if ((food.getNextEntry() > i - currentFood.getCookTime()) && (food.getNextEntry() < i)) {
                        food.makeMeWait(i - food.getNextEntry());
                    } else {
                        food.makeMeWait(currentFood.getCookTime());
                    }
                }
            }
            currentFood.computeNextEntry();
            currentFood.setDeadline(currentFood.getNextEntry() + currentFood.getInitDeadline());
        }
        printStatus();
    }

    private void rmScheduler() {
        PriorityQueue<Food> queue = new PriorityQueue<>((Food o1, Food o2) -> {
            if (o1.getCookTime() == o2.getCookTime()) {
                return 0;
            } else {
                return o1.getCookTime() > o2.getCookTime() ? 1 : -1;
            }
        });
        queue.addAll(Arrays.asList(foods));
        Food currentFood;
        for (int i = 0; i < this.wholePeriod;) {

            currentFood = queue.poll();
            if (currentFood == null) {
                System.out.println(i + " idle");
                idleTime++;
                if (i != 0) {
                    for (Food food : foods) {
                        if (food.getNextEntry() == i) {
                            queue.add(food);
                        }
                    }
                }
                i++;
                continue;
            }
            this.switchCounter++;
            if (currentFood.getCookTime() + i > currentFood.getDeadline()) {
                System.out.println(i + " " + currentFood.getName() + " will miss deadline.");
            }
            int currentTime = i;
            for (; i < currentTime + currentFood.getCookTime();) {
                System.out.println(i + " " + currentFood.getName());
                //cooking is happening
                if (i > currentFood.getDeadline()) {
                    System.out.println(currentFood.getName() + " deadline missed.");
                }
                i++;
                if (i != 0) {
                    for (Food food : foods) {
                        if (food.getNextEntry() == i) {
                            queue.add(food);
                        }
                    }
                }
            }
            for (Food food : queue) {
                if (food.getNextEntry() != i) {
                    if ((food.getNextEntry() > i - currentFood.getCookTime()) && (food.getNextEntry() < i)) {
                        food.makeMeWait(i - food.getNextEntry());
                    } else {
                        food.makeMeWait(currentFood.getCookTime());
                    }
                }
            }
            currentFood.computeNextEntry();
            currentFood.setDeadline(currentFood.getNextEntry() + currentFood.getInitDeadline());
        }
        printStatus();

    }

    private void llfScheduler() {
        Comparator<Food> c = new Comparator<Food>() {
            @Override
            public int compare(Food o1, Food o2) {
                if (o1.getLaxity() == o2.getLaxity()) {
                    return 0;
                } else {
                    return o1.getLaxity() > o2.getLaxity() ? 1 : -1;
                }
            }
        };
        PriorityQueue<Food> queue = new PriorityQueue<>(c);
        queue.addAll(Arrays.asList(foods));
        Food currentFood;
        for (int i = 0; i < this.wholePeriod;) {

            currentFood = queue.poll();

            if (currentFood == null) {
                System.out.println(i + " idle");
                idleTime++;
                if (i != 0) {
                    for (Food food : foods) {
                        if (food.getNextEntry() == i) {
                            queue.add(food);
                        }
                    }
                }
                i++;
                continue;
            }
            this.switchCounter++;
            if (currentFood.getCookTime() + i > currentFood.getDeadline()) {
                System.out.println(i + " " + currentFood.getName() + " will miss deadline.");
            }

            int currentTime = i;
            for (; i < currentTime + currentFood.getCookTime();) {
                System.out.println(i + " " + currentFood.getName());
                //cooking is happening
                if (i > currentFood.getDeadline()) {
                    System.out.println(currentFood.getName() + " deadline missed.");
                }
                i++;
                for(Food food:queue){
                    food.computeLaxity(i);
                }
                Food[] tmp=new Food[queue.size()];
                queue.toArray(tmp);
                queue.removeAll(queue);
                queue.addAll(Arrays.asList(tmp));
                if (i != 0) {
                    for (Food food : foods) {
                        if (food.getNextEntry() == i) {
                            queue.add(food);
                        }
                    }
                }
            }
            for (Food food : queue) {
                if (food.getNextEntry() != i) {
                    if ((food.getNextEntry() > i - currentFood.getCookTime()) && (food.getNextEntry() < i)) {
                        food.makeMeWait(i - food.getNextEntry());
                    } else {
                        food.makeMeWait(currentFood.getCookTime());
                    }
                }
            }
            currentFood.computeNextEntry();
            currentFood.setDeadline(currentFood.getNextEntry() + currentFood.getInitDeadline());
        }
        printStatus();
    }

    private void printStatus() {
        System.out.println("idle time: " + this.idleTime);
        for (Food food : foods) {
            System.out.println(food.getName() + " waiting time =" + food.getWaitingTime());
        }
        System.out.println("Number of switches occured: " + (--this.switchCounter));
    }
}
