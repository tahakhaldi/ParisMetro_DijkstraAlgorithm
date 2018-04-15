import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Author : Taha Khaldi 
 */

public class Metro {

	private static class Vertex implements Comparable<Vertex> {
		private int number;
		private String name;
		private int minDistance = Integer.MAX_VALUE / 2;
		private Vertex previous;

		public Vertex(int number, String name) {
			this.number = number;
			this.name = name;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + number;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Vertex other = (Vertex) obj;
			if (number != other.number)
				return false;
			return true;
		}

		@Override
		public String toString() {
			return String.format("%04d %s", number, name);
		}

		@Override
		public int compareTo(Vertex o) {
			return Double.compare(minDistance, o.minDistance);
		}
		
	}

	private Map<Vertex, Map<Vertex, Integer>> adjacencyList;

	public Metro() {
		this.adjacencyList = new HashMap<>();
	}

	/**
	 * Adding a vertex to the graph
	 */
	public void addVertex(int vNumber, String vName) {
		Vertex v = new Vertex(vNumber, vName);
		adjacencyList.put(v, new HashMap<Vertex, Integer>());
	}

	/**
	 * Adding an edge to the graph
	 */
	public void addEdge(Integer vNumFrom, Integer vNumTo, Integer weight) {
		Vertex vertexFrom = getVertexByNumber(vNumFrom);
		Vertex vertexTo = getVertexByNumber(vNumTo);
		adjacencyList.get(vertexFrom).put(vertexTo, weight);
	}

	/**
	 * Getting the Vertex object by the id (number) of the vertex 
	 */
	private Vertex getVertexByNumber(Integer vNum) {
		for (Vertex v : adjacencyList.keySet()) {
			if (v.number == vNum)
				return v;
		}
		return null;
	}
	
	/**
	 * Creating the list of vertices containing a specified vertex
	 */
	public List<Vertex> sameLineVertices(Integer vertexNumber) {
	LinkedList<Vertex> resultList = new LinkedList<>();
		Vertex v = getVertexByNumber(vertexNumber);
		
		if (v != null) {
			Vertex[] directNeighbours = new Vertex[3];
			int counter = 0;
			for (Vertex vertex : getNeighbors(v)) {
				if (getWeight(v, vertex) != -1) {
					directNeighbours[counter++] = vertex;
				}
			}
			
			resultList.add(v);
			if (directNeighbours[0] != null)
				addAllNext(resultList, directNeighbours[0]);
			if (directNeighbours[1] != null)
				addAllPrev(resultList, directNeighbours[1]);
		}
		
		return resultList;
	}
	
	/**
	 * Adding to the list of results all the vertices that follow
	 */
	private void addAllNext(LinkedList<Vertex> resultList, Vertex startVertex) {
		resultList.add(startVertex);
		for (Vertex vertex : getNeighbors(startVertex)) {
			if (getWeight(startVertex, vertex) != -1 && !resultList.contains(vertex)) {
				addAllNext(resultList, vertex);
			}
		}
	}

	/**
	 * Adding to the list of results all the vertices that precede.
	 */
	private void addAllPrev(LinkedList<Vertex> resultList, Vertex startVertex) {
		resultList.add(0, startVertex);
		for (Vertex vertex : getNeighbors(startVertex)) {
			if (getWeight(startVertex, vertex) != -1 && !resultList.contains(vertex)) {
				addAllPrev(resultList, vertex);
			}
		}
	}

	/**
	 * Returns a set containing all the neighboring vertices.
	 */
	public Set<Vertex> getNeighbors(Vertex v) {
		return adjacencyList.get(v).keySet();
	}

	/**
	 * Returns the weight of the edge between v1 and v2.
	 */
	public Integer getWeight(Vertex v1, Vertex v2) {
		return adjacencyList.get(v1).get(v2);
	}

	/**
	 * Calculate the shortest path between two vertices
	 */
	public List<Vertex> shortestPath(int n1, int n2) {
		Vertex vertexFrom = getVertexByNumber(n1);
		Vertex vertexTo = getVertexByNumber(n2);

		vertexFrom.minDistance = 0;
		PriorityQueue<Vertex> vertexQueue = new PriorityQueue<Vertex>();
		vertexQueue.add(vertexFrom);
		
		while (!vertexQueue.isEmpty()) {
			Vertex u = vertexQueue.poll();

			// Visit each edge coming out of u
			for (Vertex e : getNeighbors(u)) {
				int weight = getWeight(u, e);
				if (weight == -1)
					weight = 90;
				int distanceThroughU = u.minDistance + weight;
				if (distanceThroughU < e.minDistance) {
					vertexQueue.remove(e);
					e.minDistance = distanceThroughU;
					e.previous = u;
					vertexQueue.add(e);
				}
			}
		}
		
		List<Vertex> path = new ArrayList<Vertex>();
		for (Vertex vertex = vertexTo; vertex != null; vertex = vertex.previous)
			path.add(vertex);
		Collections.reverse(path);
		
		return path;
	}
	
	/**
	 * Calculate the shortest path with the off-line
	 */
	public List<Vertex> shortestPathExcludedLine(int n1, int n2, int n3) {
		Vertex vertexFrom = getVertexByNumber(n1);
		Vertex vertexTo = getVertexByNumber(n2);
		
		List<Vertex> path = new ArrayList<Vertex>();

		List<Vertex> sameLineVertices = sameLineVertices(n3);
		if (sameLineVertices.contains(vertexFrom) || sameLineVertices.contains(vertexTo))
			return path;

		vertexFrom.minDistance = 0;
		PriorityQueue<Vertex> vertexQueue = new PriorityQueue<Vertex>();
		vertexQueue.add(vertexFrom);
		
		while (!vertexQueue.isEmpty()) {
			Vertex u = vertexQueue.poll();

			// Visit each edge coming out of u
			for (Vertex e : getNeighbors(u)) {
				int weight = getWeight(u, e);
				if (weight == -1)
					weight = 90;
				if (sameLineVertices.contains(e))
					weight = Integer.MAX_VALUE / 3;
				int distanceThroughU = u.minDistance + weight;
				if (distanceThroughU < e.minDistance) {
					vertexQueue.remove(e);
					e.minDistance = distanceThroughU;
					e.previous = u;
					vertexQueue.add(e);
				}
			}
		}
		
		for (Vertex vertex = vertexTo; vertex != null; vertex = vertex.previous)
			path.add(vertex);
		Collections.reverse(path);
		
		return path;
	}
	
	/**
	 * Print the number and name of the vertices.
	 */
	public void printVertices(List<Vertex> vertices) {
		for (Vertex vertex : vertices) {
			System.out.println(vertex);
		}
	}

	/**
	 * Print the name, number, and accumulated weight of the vertices.
	 */
	public void printVerticesWithTime(List<Vertex> vertices) {
		if (vertices.isEmpty())
			System.out.println("No path.");
		else {
			for (Vertex vertex : vertices) {
				System.out.println(vertex + " (" + vertex.minDistance + ")");
			}
		}
	}

}
