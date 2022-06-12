package restaurant.scheduler;

public class Food {
    private final String name;
    private int deadline;
    private final int cookTime;
    private final int period;
    private int laxity;
    private int nextEntry;
    private int waitingTime; 
    private final int initDeadline;
    
    public Food(String name, int cookTime, int deadline, int period){
        this.cookTime=cookTime;
        this.initDeadline=deadline;
        this.period=period;
        this.name=name;
        this.deadline=this.initDeadline;
        this.laxity=this.deadline-this.cookTime;
        this.nextEntry=0;
        waitingTime=0;
    }
    
    public void makeMeWait(int n){
        this.waitingTime+=n;
    }
    
    /**
     * @return the deadline
     */
    public int getDeadline() {
        return deadline;
    }

    public void computeLaxity(int i){
        this.laxity=this.deadline-(i+this.cookTime);
    }

    /**
     * @return the cookTime
     */
    public int getCookTime() {
        return cookTime;
    }

    

    /**
     * @return the period
     */
    public int getPeriod() {
        return period;
    }

    

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }


    /**
     * @return the nextEntry
     */
    public int getNextEntry() {
        return nextEntry;
    }

    public final void computeNextEntry() {
        this.nextEntry += this.period;
    }

    /**
     * @param deadline the deadline to set
     */
    public void setDeadline(int deadline) {
        this.deadline = deadline;
    }

    /**
     * @return the waitingTime
     */
    public int getWaitingTime() {
        return waitingTime;
    }

    /**
     * @return the initDeadline
     */
    public int getInitDeadline() {
        return initDeadline;
    }

    /**
     * @return the laxity
     */
    public int getLaxity() {
        return laxity;
    }

    
}
