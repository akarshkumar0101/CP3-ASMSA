import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class RPSClient {

    private static int PORT = 8901;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    public int move;

    public RPSClient(String serverAddress) throws Exception {
        socket = new Socket(serverAddress, PORT);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

    }

    public void throwRock() {
        move = 0;
        out.println("ROCK");
    }

    public void throwPaper() {
        move = 1;
        out.println("PAPER");
    }

    public void throwScissors() {
        move = 2;
        out.println("SCISSORS");
    }

    public void quitGame(int w, int l, int t){
        System.out.println("You have disconnected.");
        System.out.println("Your final record was " + w + "-" + l + "-" + t + " (W-L-T)");
        out.println("QUIT");
    }

    public void play() throws Exception {
        String response;
        int w = 0, l = 0, t = 0;
        System.out.println("Welcome to rock, paper scissors!\nEach round, you and your opponent will each pick rock, paper, or scissors.\nScissors beat paper, paper beats rock, and rock beats scissors.\nTo select your move, either type the word, or type \"r,\" \"p,\" or \"s.\"\nIf you type more than one option, only the first one will be accepted.\nTo quit, type \"quit.\"");
        try {
            Scanner scan = new Scanner(System.in);
            while (true) {
                System.out.println("Would you like to throw rock, paper, or scissors?");
                String selection = scan.nextLine().toLowerCase();
                boolean r = selection.contains("rock"), p = selection.contains("paper"), s = selection.contains("scissors");
                int ri = selection.indexOf("rock"), pi = selection.indexOf("paper"), si = selection.indexOf("scissors");
                if (selection.length() == 1) {
                    if (selection.equals("r")) throwRock();
                    else if (selection.equals("p")) throwPaper();
                    else if (selection.equals("s")) throwScissors();
                    else if (selection.equals("q")){
                        quitGame(w,l,t);
                        break;
                    }else{
                        System.out.println("Invalid selection. Please try again.");
                        continue;
                    }
                } else if (selection.contains("quit")) {
                    quitGame(w,l,t);
                    break;
                } else if (!r && !p && !s) {
                    System.out.println("Invalid selection. Please try again.");
                    continue;
                } else if (r && p && s) {
                    if (ri < pi && ri < si) throwRock();
                    else if (pi < si) throwPaper();
                    else throwScissors();
                } else if (r) {
                    if (!p && !s) throwRock();
                    else if (p && pi < ri) throwPaper();
                    else if (s && si < ri) throwScissors();
                    else throwRock();
                } else if (p) {
                    if (s && si < pi) throwScissors();
                    else throwPaper();
                } else throwScissors();
                if (move == 0) {
                    System.out.println("You chose rock.");
                } else if (move == 1) {
                    System.out.println("You chose paper.");
                } else if (move == 2) {
                    System.out.println("You chose scissors.");
                }
                response = in.readLine();
                if (response.equals("QUIT")) {
                    System.out.println("Your opponent has disconnected.");
                    System.out.println("Your final record was " + w + "-" + l + "-" + t + " (W-L-T).");
                    break;
                } else if (response.equals("WIN")) {
                    System.out.println("You won!\n");
                    w++;
                } else if (response.equals("LOSE")) {
                    System.out.println("You lost.\n");
                    l++;
                } else {
                    System.out.println("It's a draw.\n");
                    t++;
                }
                System.out.println("Your current record is " + w + "-" + l + "-" + t + " (W-L-T).");
            }

        } finally {
            socket.close();
        }
    }

    public static void main(String[] args) throws Exception {
        try {
            String serverAddress = (args.length == 0) ? "172.16.2.13" : args[0];
            RPSClient client = new RPSClient(serverAddress);
            System.out.println("Waiting for an opponent...");
            String response = client.in.readLine();
            System.out.println("Opponent found!\n");
            client.play();
        }catch(Exception e){
            String serverAddress = (args.length == 0) ? "localhost" : args[0];
            RPSClient client = new RPSClient(serverAddress);
            System.out.println("Waiting for an opponent...");
            String response = client.in.readLine();
            System.out.println("Opponent found!\n");
            client.play();
        }
    }
}