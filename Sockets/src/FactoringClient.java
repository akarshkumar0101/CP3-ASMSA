import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;

public class FactoringClient {
	private static long clientID;

	private static String serverIP = "localhost";

	private static final int NUMBER_OF_SEARCH_THREADS = 8;
	private static final Thread[] searchThreads = new Thread[NUMBER_OF_SEARCH_THREADS];

	public static File logFile;
	public static PrintWriter logWriter;

	public static Socket sock;
	public static PrintWriter out;
	public static BufferedReader in;

	public static void main(String[] args) {
		System.out.println("Type in IP address of the server");
		Scanner scan = new Scanner(System.in);
		serverIP = scan.nextLine();
		scan.close();
		try {
			sock = new Socket(serverIP, Util.PORT);
			out = new PrintWriter(sock.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		} catch (Exception e) {
			try {
				PrintWriter errorWriter = new PrintWriter(new File("error.txt"));
				errorWriter.println(
						"Unable to connect to server or unable to establish input/output stream with socket...");
				e.printStackTrace(errorWriter);
				errorWriter.close();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			return;
		}

		try {
			clientID = Long.parseLong(in.readLine());
			logFile = new File("client" + clientID + "_log.txt");
			logWriter = new PrintWriter(new FileOutputStream(logFile), true);

			log("Successfully connected to server " + serverIP + " and received client ID: " + clientID);

			while (true) {
				String input = "";
				input = in.readLine();

				if (input.equals("cancel")) {
					log("[Server]: cancel");
					cancelJob();
				} else if (input.equals("job")) {
					long jobid = Long.parseLong(in.readLine());
					BigInteger number = new BigInteger(in.readLine());
					BigInteger startRange = new BigInteger(in.readLine());
					BigInteger endRange = new BigInteger(in.readLine());
					Job job = new Job(jobid, number, startRange, endRange);

					log("[Server]: job " + job);

					startJob(job);
				} else if (input.equals("exit")) {
					log("[Server]: exit");
					break;
				}
			}

		} catch (IOException e) {
			log("Connection with server was lost...");
		}

		log("Client exited");

		logWriter.flush();
		logWriter.close();
	}

	private static synchronized void log(String s) {
		System.out.println(s);
		if (logWriter != null) {
			logWriter.println(Util.dateFormat.format(new Date()) + " " + s);
		}
	}

	private static int numThreadsFinished = 0;

	private static volatile boolean solutionFound;

	public static synchronized void setNumThreadsFinished(int val) {
		numThreadsFinished = val;
	}

	public static synchronized int getNumThreadsFinished() {
		return numThreadsFinished;
	}

	public static synchronized void setSolutionFound(boolean val) {
		solutionFound = val;
	}

	public static synchronized boolean getSolutionFound() {
		return solutionFound;
	}

	public static void startJob(Job job) {

		Job[] jobs = job.splitJob(NUMBER_OF_SEARCH_THREADS);

		String sections = "";
		for (Job j : jobs) {
			sections += "(" + j.startRange + "-" + j.endRange + "), ";
		}
		log("Split job into " + sections);

		setNumThreadsFinished(0);
		setSolutionFound(false);
		for (int i = 0; i < NUMBER_OF_SEARCH_THREADS; i++) {
			final int threadnum = i;
			Job j = jobs[i];
			Thread thread = new Thread() {
				@Override
				public void run() {
					int threadID = threadnum;
					BigInteger solution = performJob(j);
					setNumThreadsFinished(getNumThreadsFinished() + 1);
					// log("Thread " + threadID + " finished searching");
					if (solution != null && !getSolutionFound()) {
						setSolutionFound(true);
						log("Solution found on Thread " + threadID + ", solution: " + solution);
						out.println(solution);
						log("Solution sent to server, solution: " + solution);
					} else if (getNumThreadsFinished() == NUMBER_OF_SEARCH_THREADS && !getSolutionFound()) {
						out.println("null");
						log("No solution found");
					}
				}
			};
			thread.setDaemon(true);
			thread.start();
			searchThreads[i] = thread;
		}
		log("Started job " + job + " with " + NUMBER_OF_SEARCH_THREADS + " scanning threads");
	}

	@SuppressWarnings("deprecation")
	public static void cancelJob() {
		log("Canceling current job and stopping searching threads");
		out.println("cancel");

		try {
			boolean killself = false;
			for (Thread thread : searchThreads) {
				if (Thread.currentThread() == thread) {
					killself = true;
					continue;
				}
				thread.stop();
			}
			if (killself) {
				Thread.currentThread().stop();
			}
		} catch (NullPointerException e) {
		}
	}

	public static BigInteger performJob(Job job) {
		return findFactor(job.number, job.startRange, job.endRange);
	}

	// start range must be in groups of 6, ie 1, 7, 13, 19, etc.
	public static BigInteger findFactor(BigInteger number, BigInteger startRange, BigInteger endRange) {
		BigInteger rangelength = endRange.subtract(startRange);
		BigInteger iterations = rangelength.divide(Util.SIX);

		for (BigInteger i = BigInteger.ZERO; i.compareTo(iterations) < 0; i = i.add(BigInteger.ONE)) {
			BigInteger a = i.multiply(Util.SIX).add(startRange);
			BigInteger b = a.add(Util.FOUR);
			if (number.mod(a).equals(BigInteger.ZERO))
				return a;
			if (number.mod(b).equals(BigInteger.ZERO))
				return b;
		}
		return null;
	}

}
