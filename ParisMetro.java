import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

/**
 * Author : Taha Khaldi 
 */

public class ParisMetro {
	private static Metro graph;
	
	/**
	 * Reading the file and producing a graph.  
	 */
	public static void readMetro(String fileName) {
		graph = new Metro();
		
		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			String line = br.readLine();
			int numVertices;
			int numEdges;
			try (Scanner lineScanner = new Scanner(line)) {
				numVertices = lineScanner.nextInt();
				numEdges = lineScanner.nextInt();
			}
			
			// reading the vertex
			for (int i = 0; i < numVertices; i++) {
				line = br.readLine();
				if (line.equals("$")) {
					System.err.println("Unexpected end of vertex data!");
					break;
				}
				try (Scanner lineScanner = new Scanner(line)) {
					int vNumber = lineScanner.nextInt();
					String vName = lineScanner.nextLine().trim();
					graph.addVertex(vNumber, vName);
				}
			}

			// reading the symbol $ signaling the end of the list of stations 		
			line = br.readLine();
			if (!line.equals("$")) {
				System.err.println("File format error!");
			}
			
			// reading the edges		
			for (int i = 0; i < numEdges; i++) {
				line = br.readLine();
				try (Scanner lineScanner = new Scanner(line)) {
					int vertexFrom = lineScanner.nextInt();
					int vertexTo = lineScanner.nextInt();
					int weight = lineScanner.nextInt();
					graph.addEdge(vertexFrom, vertexTo, weight);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * main function
	 * @param args
	 */
	
	public static void main(String[] args) {
		readMetro("metro.txt");
		
		int n1, n2, n3;
		
		if (args.length == 1) {
			System.out.println("Identify all stations belonging to the same line as a specified station:");
			n1 = Integer.parseInt(args[0]);
			graph.printVertices(graph.sameLineVertices(n1));
		} else if (args.length == 2) {
			System.out.println("Find the fastest route between two stations.:");
			n1 = Integer.parseInt(args[0]);
			n2 = Integer.parseInt(args[1]);
			graph.printVerticesWithTime((graph.shortestPath(n1, n2)));
		} else if (args.length == 3) {
			System.out.println("Find the fastest route between two stations when one of the lines is off:");
			n1 = Integer.parseInt(args[0]);
			n2 = Integer.parseInt(args[1]);
			n3 = Integer.parseInt(args[2]);
			graph.printVerticesWithTime((graph.shortestPathExcludedLine(n1, n2, n3)));
		} else {
			System.err.println("Invalid arguments.");
		}
	}

}
