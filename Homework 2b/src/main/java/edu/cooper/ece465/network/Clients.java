package edu.cooper.ece465.network;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

public class Clients {
    private static final Scanner in = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        System.out.println("Enter the name of IPs file: ");
        File portsFile = new File(in.next());
        BufferedReader br = new BufferedReader(new FileReader(portsFile));

        String ip;
        while ((ip = br.readLine()) != null) {
            new Thread(new Client(ip.substring(0, ip.indexOf(':')),
                    Integer.parseInt(ip.substring(ip.indexOf(':') + 1)))).start();
            Thread.sleep(2000);
        }
    }
}