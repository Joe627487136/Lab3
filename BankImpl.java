package Lab3;

import java.util.Arrays;

public class BankImpl {
	private int numberOfCustomers;	// the number of customers
	private int numberOfResources;	// the number of resources

	private int[] available; 	// the available amount of each resource
	public int[][] maximum; 	// the maximum demand of each customer
	private int[][] allocation;	// the amount currently allocated
	private int[][] need;		// the remaining needs of each customer
	
	public BankImpl (int[] resources, int numberOfCustomers) {
        this.numberOfResources = resources.length;
        this.numberOfCustomers = numberOfCustomers;
        this.available = new int[numberOfResources];
        for(int i = 0; i < this.numberOfResources; i++){
            this.available[i] = resources[i];
        }
        this.maximum = new int[numberOfCustomers][numberOfResources];
        this.allocation = new int[numberOfCustomers][numberOfResources];
        this.need = new int[numberOfCustomers][numberOfResources];
	}
	
	public int getNumberOfCustomers() {
        return this.numberOfCustomers;
	}

	public void addCustomer(int customerNumber, int[] maximumDemand) {
        for(int i = 0; i < this.numberOfResources; i++){
            this.maximum[customerNumber][i] = maximumDemand[i];
            this.need[customerNumber][i] = maximumDemand[i];
        }
	}

	public void getState() {
		System.out.println("\navailable:");
		printArray1(available);
		System.out.println("max:");
		printArray2(maximum);
		System.out.println("allocation:");
		printArray2(allocation);
		System.out.println("need:");
		printArray2(need);
	}

    public synchronized boolean requestResources(int customerNumber, int[] request) {
        System.out.print("\nCustomerNumber " + customerNumber + " is expecting to do: [" );
        for(int i = 0; i < this.numberOfResources-1; i++) {
            System.out.print(request[i] + " ");
        }
        System.out.print(request[this.numberOfResources-1] + "]\n");
        // check if request larger than need
        for(int i = 0; i < this.numberOfResources; i++){
            if(request[i] > this.need[customerNumber][i]){
                System.out.print("Request from customer "+customerNumber+" exceeds the need for resource "+i+"!\n");
                return false;
            }
        }
        // check if request larger than available
        for(int i = 0; i < this.numberOfResources; i++){
            if(request[i] > this.available[i]){
                System.out.print("Request from customer "+customerNumber+" is more than avaliable for resource "+i+"!\n");
                return false;
            }
        }
        if (!this.checkSafe(customerNumber, request)){
            System.out.print("Unsafe request! Rejecct!\n");
            return false;
        }
        // if it is safe, allocate the resources to customer customerNumber
        System.out.print("Request granted.\n");
        for(int i = 0; i < this.numberOfResources; i++){
            this.available[i] -= request[i];
            this.allocation[customerNumber][i] += request[i];
            this.need[customerNumber][i] -= request[i];
        }
        return true;
    }

	public synchronized void releaseResources(int customerNumber, int[] release) {
		for (int i=0; i<numberOfResources;i++){
			available[i]+=release[i];
			allocation[customerNumber][i] -= release[i];
			need[customerNumber][i] = maximum[customerNumber][i] + allocation[customerNumber][i];
		}

	}

	private synchronized boolean checkSafe(int customerNumber, int[] request) {
        int[] temp_avail = new int[numberOfResources];
        int[][] temp_need = new int[numberOfCustomers][numberOfResources];
        int[][] temp_allocation = new int[numberOfCustomers][numberOfResources];
        int[] work = new int[numberOfCustomers];
        boolean[] finish = new boolean[numberOfCustomers];
        boolean possible = true;

        for(int j = 0; j < this.numberOfResources; j++){
            temp_avail[j] = this.available[j] - request[j];
            work[j] = temp_avail[j];
            for(int i = 0; i < numberOfCustomers; i++){
                if (i == customerNumber){
                    temp_need[customerNumber][j] = this.need[customerNumber][j] - request[j];
                    temp_allocation[customerNumber][j] = this.allocation[customerNumber][j] + request[j];
                }
                else{
                    temp_need[i][j] = this.need[i][j];
                    temp_allocation[i][j] = this.allocation[i][j];
                }
            }
        }

        for (int i = 0; i < this.numberOfCustomers; i++){
            finish[i] = false;
        }

        while(possible){
            possible = false;
            for (int i = 0; i < this.numberOfCustomers; i++){
                boolean feasible = true;
                for(int j = 0; j < this.numberOfResources; j++){
                    if(temp_need[i][j] > work[j]){
                        feasible = false;
                    }
                }
                if(!finish[i] && feasible){
                    possible = true;
                    for(int j = 0; j < this.numberOfResources; j++){
                        work[j] += temp_allocation[i][j];
                    }
                    finish[i] = true;
                }
            }
        }
        boolean safe = true;
        for (int i = 0; i < this.numberOfCustomers; i++){
            if(!finish[i]){
                safe = false;
            }
        }
        return safe;
    }
	private static void printArray1(int[] anArray) {
		System.out.println(Arrays.toString(anArray));
	}
	public static void printArray2(int[][] anArray) {
		System.out.println(Arrays.deepToString(anArray));
	}
}