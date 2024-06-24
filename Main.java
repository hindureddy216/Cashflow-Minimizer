import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;

class Bank {
    String name;
    int netAmount;
    Set<String> types;
}

class Pair {
    int index;
    String matchingType;

    public Pair(int index, String matchingType) {
        this.index = index;
        this.matchingType = matchingType;
    }
}

public class Main {
    public static int getMinIndex(Bank[] listOfNetAmounts, int numBanks) {
        int min = Integer.MAX_VALUE, minIndex = -1;
        for (int i = 0; i < numBanks; i++) {
            if (listOfNetAmounts[i].netAmount == 0) continue;

            if (listOfNetAmounts[i].netAmount < min) {
                minIndex = i;
                min = listOfNetAmounts[i].netAmount;
            }
        }
        return minIndex;
    }

    public static int getSimpleMaxIndex(Bank[] listOfNetAmounts, int numBanks) {
        int max = Integer.MIN_VALUE, maxIndex = -1;
        for (int i = 0; i < numBanks; i++) {
            if (listOfNetAmounts[i].netAmount == 0) continue;

            if (listOfNetAmounts[i].netAmount > max) {
                maxIndex = i;
                max = listOfNetAmounts[i].netAmount;
            }
        }
        return maxIndex;
    }

    public static Pair getMaxIndex(Bank[] listOfNetAmounts, int numBanks, int minIndex, Bank[] input, int maxNumTypes) {
        int max = Integer.MIN_VALUE;
        int maxIndex = -1;
        String matchingType = "";

        for (int i = 0; i < numBanks; i++) {
            if (listOfNetAmounts[i].netAmount == 0) continue;

            if (listOfNetAmounts[i].netAmount < 0) continue;

            Set<String> intersection = new HashSet<>(listOfNetAmounts[minIndex].types);
            intersection.retainAll(listOfNetAmounts[i].types);

            if (!intersection.isEmpty() && max < listOfNetAmounts[i].netAmount) {
                max = listOfNetAmounts[i].netAmount;
                maxIndex = i;
                matchingType = intersection.iterator().next();
            }
        }

        return new Pair(maxIndex, matchingType);
    }

    public static void printAns(String[][][] ansGraph, int numBanks, Bank[] input) {
        System.out.println("\nThe transactions for minimum cash flow are as follows : \n\n");
        for (int i = 0; i < numBanks; i++) {
            for (int j = 0; j < numBanks; j++) {
                if (i == j) continue;

                if (ansGraph[i][j][0] != null && ansGraph[j][i][0] != null) {
                    if (Integer.parseInt(ansGraph[i][j][0]) == Integer.parseInt(ansGraph[j][i][0])) {
                        ansGraph[i][j][0] = null;
                        ansGraph[j][i][0] = null;
                    } else if (Integer.parseInt(ansGraph[i][j][0]) > Integer.parseInt(ansGraph[j][i][0])) {
                        int diff = Integer.parseInt(ansGraph[i][j][0]) - Integer.parseInt(ansGraph[j][i][0]);
                        ansGraph[i][j][0] = String.valueOf(diff);
                        ansGraph[j][i][0] = null;

                        System.out.println(input[i].name + " pays Rs " + diff + " to " + input[j].name + " via " + ansGraph[i][j][1]);
                    } else {
                        int diff = Integer.parseInt(ansGraph[j][i][0]) - Integer.parseInt(ansGraph[i][j][0]);
                        ansGraph[j][i][0] = String.valueOf(diff);
                        ansGraph[i][j][0] = null;

                        System.out.println(input[j].name + " pays Rs " + diff + " to " + input[i].name + " via " + ansGraph[j][i][1]);
                    }
                } else if (ansGraph[i][j][0] != null) {
                    System.out.println(input[i].name + " pays Rs " + ansGraph[i][j][0] + " to " + input[j].name + " via " + ansGraph[i][j][1]);
                } else if (ansGraph[j][i][0] != null) {
                    System.out.println(input[j].name + " pays Rs " + ansGraph[j][i][0] + " to " + input[i].name + " via" + ansGraph[j][i][1]);
                }

                ansGraph[i][j][0] = null;
                ansGraph[j][i][0] = null;
            }
        }
        System.out.println("\n");
    }

    public static void minimizeCashFlow(int numBanks, Bank[] input, Map<String, Integer> indexOf, int numTransactions, int[][] graph, int maxNumTypes) {
        Bank[] listOfNetAmounts = new Bank[numBanks];

        for (int b = 0; b < numBanks; b++) {
            listOfNetAmounts[b] = new Bank();
            listOfNetAmounts[b].name = input[b].name;
            listOfNetAmounts[b].types = input[b].types;

            int amount = 0;
            for (int i = 0; i < numBanks; i++) {
                amount += graph[i][b];
            }

            for (int j = 0; j < numBanks; j++) {
                amount -= graph[b][j];
            }

            listOfNetAmounts[b].netAmount = amount;
        }

        String[][][] ansGraph = new String[numBanks][numBanks][2];

        int numZeroNetAmounts = 0;

        for (int i = 0; i < numBanks; i++) {
            if (listOfNetAmounts[i].netAmount == 0) numZeroNetAmounts++;
        }

        while (numZeroNetAmounts != numBanks) {
            int minIndex = getMinIndex(listOfNetAmounts, numBanks);
            Pair maxAns = getMaxIndex(listOfNetAmounts, numBanks, minIndex, input, maxNumTypes);

            int maxIndex = maxAns.index;

            if (maxIndex == -1) {
                ansGraph[minIndex][0][0] = String.valueOf(Math.abs(listOfNetAmounts[minIndex].netAmount));
                ansGraph[minIndex][0][1] = (String) input[minIndex].types.iterator().next();

                int simpleMaxIndex = getSimpleMaxIndex(listOfNetAmounts, numBanks);
                ansGraph[0][simpleMaxIndex][0] = String.valueOf(Math.abs(listOfNetAmounts[minIndex].netAmount));
                ansGraph[0][simpleMaxIndex][1] = (String) input[simpleMaxIndex].types.iterator().next();

                listOfNetAmounts[simpleMaxIndex].netAmount += listOfNetAmounts[minIndex].netAmount;
                listOfNetAmounts[minIndex].netAmount = 0;

                if (listOfNetAmounts[minIndex].netAmount == 0) numZeroNetAmounts++;
                if (listOfNetAmounts[simpleMaxIndex].netAmount == 0) numZeroNetAmounts++;
            } else {
                int transactionAmount = Math.min(Math.abs(listOfNetAmounts[minIndex].netAmount), listOfNetAmounts[maxIndex].netAmount);

                ansGraph[minIndex][maxIndex][0] = String.valueOf(transactionAmount);
                ansGraph[minIndex][maxIndex][1] = maxAns.matchingType;

                listOfNetAmounts[minIndex].netAmount += transactionAmount;
                listOfNetAmounts[maxIndex].netAmount -= transactionAmount;

                if (listOfNetAmounts[minIndex].netAmount == 0) numZeroNetAmounts++;
                if (listOfNetAmounts[maxIndex].netAmount == 0) numZeroNetAmounts++;
            }
        }

        printAns(ansGraph, numBanks, input);
    }

    public static void main(String[] args) {
        System.out.println("\n\t\t\t\t********************* Welcome to CASH FLOW MINIMIZER SYSTEM ***********************\n\n\n");
        System.out.println("This system minimizes the number of transactions among multiple banks in the different corners of the world that use different modes of payment. There is one world bank (with all payment modes) toact as an intermediary between banks that have no commonmode of payment. \n\n");
        System.out.println("Enter the number of banks participating in the transactions.\n");
        Scanner scanner = new Scanner(System.in);
        int numBanks = scanner.nextInt();

        Bank[] input = new Bank[numBanks];
        Map<String, Integer> indexOf = new HashMap<>();

        System.out.println("Enter the details of the banks and transactions as stated:\n");
        System.out.println("Bank name, number of payment modes it has and the payment modes.\n");
        System.out.println("Bank name and payment modes should not contain spaces\n");

        int maxNumTypes = 0;

        scanner.nextLine(); // Consume the newline character left by nextInt()

        for (int i = 0; i < numBanks; i++) {
            if (i == 0) {
                System.out.print("World Bank: ");
            } else {
                System.out.print("Bank " + i + ": ");
            }
            input[i] = new Bank();
            input[i].name = scanner.next();
            indexOf.put(input[i].name, i);
            int numTypes = scanner.nextInt();

            if (i == 0) maxNumTypes = numTypes;

            input[i].types = new HashSet<>();
            for (int j = 0; j < numTypes; j++) {
                input[i].types.add(scanner.next());
            }
        }

        System.out.println("Enter number of transactions.\n");
        int numTransactions = scanner.nextInt();

        int[][] graph = new int[numBanks][numBanks];

        System.out.println("Enter the details of each transaction as stated");
        System.out.println("Debtor Bank, creditor Bank and amount\n");
        System.out.println("The transactions can be in any order\n");
        for (int i = 0; i < numTransactions; i++) {
            System.out.print(i + " th transaction: ");
            String s1 = scanner.next();
            String s2 = scanner.next();
            int amount = scanner.nextInt();

            graph[indexOf.get(s1)][indexOf.get(s2)] = amount;
        }

        scanner.close(); // Close the Scanner object

        minimizeCashFlow(numBanks, input, indexOf, numTransactions, graph, maxNumTypes);
    }
}