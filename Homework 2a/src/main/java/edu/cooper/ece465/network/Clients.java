package edu.cooper.ece465.network;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

public class Clients {
    private static final Scanner in = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        System.out.println("Enter the name of ports file: ");
        File portsFile = new File(in.next());
        BufferedReader br = new BufferedReader(new FileReader(portsFile));

        String port;
        while ((port = br.readLine()) != null) {
            new Thread(new Client(Integer.parseInt(port))).start();
        }
    }
}
