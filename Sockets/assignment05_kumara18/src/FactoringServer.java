import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Vector;

public class FactoringServer {

	public static final Vector<ClientHandler> handlers = new Vector<>();

	public static final Vector<Job> availableJobs = new Vector<>();
	public static final Hashtable<Job, ClientHandler> pendingJobs = new Hashtable<>();
	public static final Hashtable<BigInteger, BigInteger> solutions = new Hashtable<>();

	private static final Object jobLock = new Object();

	public static boolean isDoneGenerating = false;
	public static int numberOfNumbers = 0;

	public static File logFile = new File("server_log.txt");
	public static File solutionFile = new File("solutions.txt");

	public static PrintWriter logWriter;
	public static PrintWriter solutionWriter;

	public static void main(String[] args) {
		final ServerSocket serversock;
		try {
			serversock = new ServerSocket(Util.PORT);
		} catch (IOException e) {
			e.printStackTrace();
			log("Couldn't establish server socket");
			return;
		}
		try {
			log("Established server on " + InetAddress.getLocalHost().getHostAddress() + " PORT " + Util.PORT);
		} catch (UnknownHostException e2) {
			e2.printStackTrace();
		}
		try {
			logWriter = new PrintWriter(new FileOutputStream(logFile), true);
			solutionWriter = new PrintWriter(new FileOutputStream(solutionFile), true);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			try {
				serversock.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			log("Couldn't write to log or solution file");
			return;
		}

		Thread clientAccepterThread = new Thread() {
			@Override
			public void run() {
				log("Started thread for accepting clients");
				while (!serversock.isClosed()) {
					try {
						Socket sock = serversock.accept();
						ClientHandler handler = new ClientHandler(sock);
						handlers.add(handler);
						handler.start();

						log("Connected to " + sock.getInetAddress() + ", client " + handler.ID);

					} catch (IOException e) {
					}
				}
				log("No longer accepting clients");
			}
		};
		clientAccepterThread.setDaemon(true);
		clientAccepterThread.start();

		generateJobs();

		Scanner scan = new Scanner(System.in);
		System.out.println("Type \"/exit\" to close server and clients");
		while (!scan.nextLine().equals("/exit")) {
		}
		scan.close();

		log("Closing Server with exit command");

		for (ClientHandler handler : handlers) {
			handler.exit();
		}

		try {
			serversock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		logWriter.flush();
		solutionWriter.flush();
		logWriter.close();
		solutionWriter.close();
	}

	public static void foundSolution(BigInteger number, BigInteger solution, ClientHandler handler) {
		solutions.put(number, solution);
		solutionWriter.println(number + " " + solution);

		log("Found solution to " + number + " - solution: " + solution + ", found by client "
				+ (handler == null ? "null" : handler.ID));
	}

	static synchronized void log(String s) {
		System.out.println(s);
		if (logWriter != null) {
			logWriter.println(Util.dateFormat.format(new Date()) + " " + s);
		}
	}

	private static final File numberFile = new File("akarsh.txt");

	public static void generateJobs() {
		// Scanner scan = new Scanner(System.in);
		// String num = scan.nextLine();
		// System.out.println(sqrt(new BigInteger(num)));
		// Job job = new Job(num, scan.nextLine(), scan.nextLine());
		// synchronized (jobLock) {
		// availableJobs.add(job);
		// }

		String[] strings;

		int numCount = 0;
		Scanner scan;
		try {
			scan = new Scanner(numberFile);
			while (scan.hasNextLine()) {
				numCount++;
				scan.nextLine();
			}
			scan.close();
			strings = new String[numCount];

			scan = new Scanner(numberFile);
			for (int i = 0; scan.hasNextLine(); i++) {
				strings[i] = scan.nextLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			isDoneGenerating = true;
			return;
		}
		scan.close();

		log("Generating jobs...");

		for (String s : strings) {
			String[] data = s.split(" ");
			// int numID = Integer.parseInt(data[0]);
			String numberStr = data[1];
			BigInteger number = new BigInteger(numberStr);
			boolean easyFind = false;
			for (int i = 2; i < 7; i++) {
				if (number.mod(BigInteger.valueOf(i)) == BigInteger.ZERO) {
					foundSolution(number, BigInteger.valueOf(i), null);
					easyFind = true;
					break;
				}
			}
			if (easyFind) {
				continue;
			}
			BigInteger sqrt = sqrt(number);
			Job bigJob = new Job(new BigInteger(numberStr), Util.SEVEN, new BigInteger(sqrt.toString()));

			BigInteger range = bigJob.range();

			if (range.compareTo(averageClientHandle) < 0) {
				synchronized (jobLock) {
					availableJobs.add(bigJob);
				}
			} else {
				Job[] jobs = bigJob.splitJob(100);
				for (Job j : jobs) {
					synchronized (jobLock) {
						availableJobs.add(j);
					}
				}
			}
		}
		isDoneGenerating = true;

		log("Done generating jobs, generated " + availableJobs.size() + " jobs");

	}

	public static void disconnected(ClientHandler handler) {
		log("Client " + handler.ID + " disconnected!");
		handlers.remove(handler);
		availableJobs.add(0, handler.currentJob);
	}

	public static final BigInteger averageClientHandle = new BigInteger("100000000000000000000");

	public static Job getJob(ClientHandler handler) {

		boolean run = !isDoneGenerating && availableJobs.isEmpty();
		while (run) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			synchronized (jobLock) {
				run = !isDoneGenerating && availableJobs.isEmpty();
			}
		}
		synchronized (jobLock) {
			if (!availableJobs.isEmpty()) {
				Job job = availableJobs.remove(0);
				pendingJobs.put(job, handler);
				return job;
			} else
				return null;
		}

	}

	public static void finishedJob(ClientHandler jobhandler, Job job, boolean solved, BigInteger solution) {

		log("Client " + jobhandler.ID + " finished job " + job + " with result " + solution);

		if (!solved) {
			// throw away job and free client handler for next job
			pendingJobs.remove(job);
			return;
		}

		foundSolution(job.number, solution, jobhandler);

		synchronized (jobLock) {
			Iterator<Job> it = availableJobs.iterator();
			while (it.hasNext()) {
				Job j = it.next();
				if (job.areSameNumber(j)) {
					it.remove();
				}
			}

			it = pendingJobs.keySet().iterator();
			while (it.hasNext()) {
				Job j = it.next();
				if (job.areSameNumber(j)) {
					ClientHandler handler = pendingJobs.get(j);
					if (jobhandler != handler) {
						handler.cancelJob();
					}
					it.remove();
				}
			}
		}
	}

	public static BigInteger sqrt(BigInteger x) {
		BigInteger div = BigInteger.ZERO.setBit(x.bitLength() / 2);
		BigInteger div2 = div;
		// Loop until we hit the same value twice in a row, or wind
		// up alternating.
		for (;;) {
			BigInteger y = div.add(x.divide(div)).shiftRight(1);
			if (y.equals(div) || y.equals(div2))
				return y;
			div2 = div;
			div = y;
		}
	}
}

class ClientHandler extends Thread {
	private static long idincrement = 0;

	public final long ID = idincrement++;

	public final Socket sock;
	public PrintWriter out;
	public BufferedReader in;

	public Job currentJob;

	public ClientHandler(Socket sock) {
		this.sock = sock;
		try {
			out = new PrintWriter(sock.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		out.println(ID);
		currentJob = null;
		setDaemon(false);
	}

	public void cancelJob() {
		out.println("cancel");
		FactoringServer.log("Client " + ID + " signaled to cancel current job");
	}

	public void exit() {
		out.println("exit");
		FactoringServer.log("Client " + ID + " signaled to exit program");
	}

	@Override
	public void run() {
		try {
			while ((currentJob = FactoringServer.getJob(this)) != null) {
				out.println("job");
				out.println(currentJob.ID);
				out.println(currentJob.number);
				out.println(currentJob.startRange);
				out.println(currentJob.endRange);
				FactoringServer.log("Client " + ID + " received job " + currentJob);

				String solution = in.readLine();
				BigInteger solutionNum = null;
				try {
					solutionNum = new BigInteger(solution);
				} catch (NumberFormatException e) {
				}
				FactoringServer.finishedJob(this, currentJob, !solution.equals("null") && !solution.equals("cancel"),
						solutionNum);

				currentJob = null;
			}
		} catch (IOException e) {
			FactoringServer.disconnected(this);
		}
	}
}
