package org.aa8426.examples.lambdas;

class ThreadedConcurrency {
    private int balance;    

    public ThreadedConcurrency(int initialBalance) {
        this.balance = initialBalance;
    }

    public int goReadTheBalance() {
        return balance;
    }

    public void setNewBalance(int newBalance) {
        balance = newBalance;
    }

    // Simulate withdrawing money
    public void withdraw(int amount) {        
        // This will solve the problem for non-threaded applications, but 
        // threaded applications as the balance could change in the time
        // between when the balance is read during the calculation and setting
        // of the new balance. The only solution is to "lock out" other
        // threads from doing anything (reading or writing) until my work
        // is complete. The atomic classes do this for you, but you MUST
        // use them properly! (see RaceConditionDemo.java)
        //
        // The problem of "concurrent modification" can be very, very
        // difficult to unravel as it isn't 
        // 
        // if (goReadTheBalance() < amount) {
        //    throw new RuntimeException("Can't have that much money");            
        // }        
        System.out.println("Withdrawing: " + amount);
        setNewBalance(goReadTheBalance() - amount);
    }

    // Simulate depositing money
    public void deposit(int amount) {
        System.out.println("Depositing: " + amount);
        setNewBalance(goReadTheBalance() + amount);
    }

    // Print the current balance
    public void checkAndPrintBalance(int day) {
        if (goReadTheBalance() < 0) {
            throw new RuntimeException("OMG YOU STOLE MONEY FROM THE BANK");            
        }
        System.out.println("Balance at end of day "+ day +": " + goReadTheBalance());
    }

    public static void main(String[] args) {
        ThreadedConcurrency account = new ThreadedConcurrency(100);

        int day = 1;
        System.out.println("Balance at start of day "+ day +": " + account.goReadTheBalance());
        while (day < 10) {
            account.withdraw(75); 
            account.deposit(25);  
            account.checkAndPrintBalance(day);
            day++;
        }        
    }
}

