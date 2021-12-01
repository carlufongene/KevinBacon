import java.awt.desktop.SystemEventListener;
import java.io.*;
import java.util.*;

public class BaconGame {
    /**
     *
     * @param fileName read from file
     * @return return map of actors and movies by ID
     */
    public static Map<String, String> linkMap(String fileName) {
        // Reads the file to build the initial maps
        Map<String, String> linkMap = new HashMap<>();
        BufferedReader input;
        try {
            input = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            System.err.println("Cannot open file.\n" + e.getMessage());
            return linkMap;
        }

        // Read the file
        try {
            // Line by line
            String line;
            int lineNum = 0;
            while ((line = input.readLine()) != null) {
                //System.out.println("read @" + lineNum + "`" + line + "'");
                // Comma separated
                String[] pieces = line.split("\\|");
                if (pieces.length != 2) {
                    System.err.println("bad separation in line " + lineNum + ":" + line);
                } else {
                    // Extract year as an integer, if possible
                    try {
                        String s = pieces[0];
                        String i = pieces[1];
                        // System.out.println("=>" + s);
                        linkMap.put(s, i);
                    } catch (NumberFormatException e) {
                        System.err.println("bad number in line " + lineNum + ":" + line);
                    }
                }
                lineNum++;
            }
        } catch (IOException e) {
            System.err.println("IO error while reading.\n" + e.getMessage());
        }

        // Close the file, if possible
        try {
            input.close();
        } catch (IOException e) {
            System.err.println("Cannot close file.\n" + e.getMessage());
        }

        return linkMap;
    }

    /**
     *
     * @param fileName read from files
     * @return return map of actors and movies
     */

    public static Map<String, Set<String>> actorsMap(String fileName) {
        // reads file to get the actors mao
        Map<String, Set<String>> actorsMap = new HashMap<>();
        BufferedReader input;
        try {
            input = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            System.err.println("Cannot open file.\n" + e.getMessage());
            return actorsMap;
        }

        // Read the file
        try {
            // Line by line
            String line;
            int lineNum = 0;
            while ((line = input.readLine()) != null) {
                //System.out.println("read @" + lineNum + "`" + line + "'");
                String[] pieces = line.split("\\|");
                if (pieces.length != 2) {
                    System.err.println("bad separation in line " + lineNum + ":" + line);
                } else {
                    // Extract the needed elements if possible
                    try {
                        String s = pieces[0];
                        String i = pieces[1];
                        //System.out.println("=>" + s);
                        if (!actorsMap.containsKey(s)) {
                            actorsMap.put(s, new HashSet<String>());

                        }
                        actorsMap.get(s).add(i);

                    } catch (NumberFormatException e) {
                        System.err.println("bad number in line " + lineNum + ":" + line);
                    }
                }
                lineNum++;
            }
        } catch (IOException e) {
            System.err.println("IO error while reading.\n" + e.getMessage());
        }

        // Close the file, if possible
        try {
            input.close();
        } catch (IOException e) {
            System.err.println("Cannot close file.\n" + e.getMessage());
        }

        return actorsMap;
    }

    /**
     *
     * @param g the graph
     * @param source the center
     * @param <V> vertex
     * @param <E> label
     * @return bfs graph
     */

    public static <V, E> Graph<V, E> bfs(Graph<V, E> g, V source) {
        Graph<V, E> veGraph = new AdjacencyMapGraph<>();



        Set<V> visited = new HashSet<V>(); //Set to track which vertices have already been visited
        Queue<V> queue = new LinkedList<V>(); //queue to implement BFS

        queue.add(source); //enqueue start vertex
        visited.add(source); //add start to visited Set
        while (!queue.isEmpty()) { //loop until no more vertices
            V u = queue.remove(); //dequeue
            if (g.hasVertex(u)) {
                veGraph.insertVertex(u);
                for (V v : g.outNeighbors(u)) { //loop over out neighbors
                    if (!visited.contains(v)) { //if neighbor not visited, then neighbor is discovered from this vertex
                        visited.add(v); //add neighbor to visited Set
                        queue.add(v); //enqueue neighbor
                        if (g.hasVertex(v) && u != v) {
                            veGraph.insertVertex(v);
                            if (!veGraph.hasEdge(v, u)) {//save that this vertex was discovered from prior vertex
                                veGraph.insertDirected(v, u, g.getLabel(u, v));
                            }
                        }
                    }
                }
            }
        }
        return veGraph;

    }

    /**
     *
     * @param tree initial graph (bfs)
     * @param v Label
     * @param <V> Vertex
     * @param <E> Label
     * @return return path of shortest path
     */

    public static <V, E> List<V> getPath(Graph<V, E> tree, V v) {
        //check that DFS or BFS have already been run from start
        if (!tree.hasVertex(v)) {
            System.out.println("\tNo path found");
            return new ArrayList<V>();
        }
        if (tree.outDegree(v) == 0) { // If it is the same as current, you cannot do.
            throw new IllegalArgumentException("Cannot do");
        }
        //start from end vertex and work backward to start vertex
        ArrayList<V> path = new ArrayList<V>(); //this will hold the path from start to end vertex
        V current = v; //start at end vertex
        //loop from end vertex back to start vertex
        while (tree.outDegree(current) != 0) {
            path.add(0, current); //add this vertex to front of arraylist path
            for (V vertex : tree.outNeighbors(current)) {
                current = vertex; //get vertex that discovered this vertex

            }
        }
        path.add(0,current); //add the last vertex to the front to keep track of it.
        return path;


    }

    public static <V, E> Set<V> missingVertices(Graph<V, E> graph, Graph<V, E> subgraph) {

        // Using sets, finds the vertices not included in the bfs graph.
        Set<V> missingVerts = new HashSet<>();
        Set<V> sameVerts = new HashSet<>();

        for (V vertex : graph.vertices()) {
            if (subgraph.hasVertex(vertex)) {
                sameVerts.add(vertex);

            }

        }
        for (V item : graph.vertices()) {
            if (!sameVerts.contains(item)) {
                missingVerts.add(item);

            }
        }


        return missingVerts;

    }
    public static <V,E> double averageSep(Graph<V,E> tree, V root, double separation){ // finds the average
        // separation of those in the bfs tree
        return totalSeparation(tree,root,0)/tree.numVertices();
    }

    public static <V, E> double totalSeparation(Graph<V, E> tree, V root, double separation){ // finds the total separation
        // to find the average separation
        double total = separation;
        for (V vertex : tree.inNeighbors(root)) {
            total += totalSeparation(tree,vertex,separation +1);
        }
        return total;
    }
    public static Graph<String,Set<String>> mainGraphTest(String actor, String movies, String movieActors) { //Create the main graphs using the three files
        Map<String, String> actors = linkMap(actor);
        Map<String, String> b = linkMap(movies);
        Map<String, Set<String>> j = actorsMap(movieActors);
        Graph<String, Set<String>> graph = new AdjacencyMapGraph<>();
        for (String id : actors.keySet()) {
            graph.insertVertex(actors.get(id));
        }
        // Links each person in the map with their matching Id and finds the appropriate edges to insert
        for (String id : actors.keySet()) {
            for (String movieID : j.keySet()) {
                if (j.get(movieID).contains(id)) {
                    for (String link : j.get(movieID)) {
                        if (!id.equals(link)) {
                            String actorID = actors.get(id);
                            String otherActorID = actors.get(link);
                            if (!graph.hasEdge(actors.get(id), actors.get(link))) {
                                graph.insertUndirected(actorID, otherActorID, new HashSet<>());
                            }
                            graph.getLabel(actorID, otherActorID).add(b.get(movieID));

                        }

                    }
                }
            }

        }
        return graph;
    }

    public static Graph<String,Set<String>> mainGraph() { //Create the main graphs using the three files
        Map<String, String> actors = linkMap("inputs/actors.txt");
        Map<String, String> b = linkMap("inputs/movies.txt");
        Map<String, Set<String>> j = actorsMap("inputs/movie-actors.txt");
        Graph<String, Set<String>> graph = new AdjacencyMapGraph<>();
        for (String id : actors.keySet()) {
            graph.insertVertex(actors.get(id));
        }
        // Links each person in the map with their matching Id and finds the appropriate edges to insert
        for (String id : actors.keySet()) {
            for (String movieID : j.keySet()) {
                if (j.get(movieID).contains(id)) {
                    for (String link : j.get(movieID)) {
                        if (!id.equals(link)) {
                            String actorID = actors.get(id);
                            String otherActorID = actors.get(link);
                            if (!graph.hasEdge(actors.get(id), actors.get(link))) {
                                graph.insertUndirected(actorID, otherActorID, new HashSet<>());
                            }
                            graph.getLabel(actorID, otherActorID).add(b.get(movieID));

                        }

                    }
                }
            }

        }
        return graph;
    }

    public static void getAvg(Graph<String,Set<String>> graph,int k, String name) {
        // Uses a priority queue to order the averages lowest to highest
        Map<String,Double> integerMap = new HashMap<>();
        for (String vertex : graph.vertices()) {
            Graph<String, Set<String>> graph1 = bfs(graph,vertex);
            if (graph1.hasVertex(name)) {
                integerMap.put(vertex, averageSep(graph1, vertex,0));
            }
        }


        PriorityQueue<String> pq = new PriorityQueue<String>(new Comparator<String>() {
            // Orders from lowest to highest
            @Override
            public int compare(String o1, String o2) {
                return integerMap.get(o2).compareTo(integerMap.get(o1));

            }

        });
        for (String entry : integerMap.keySet()) { // gets top k options
            pq.add(entry);
            if (pq.size() > k) pq.poll();
        }

        List<String> result = new ArrayList<>();
        while (!pq.isEmpty()) result.add(pq.poll());

        System.out.println(result); // return the top list
    }

    public static void getDeg(Graph<String,Set<String>> graph, int k, String name) {

        // gets the top degree options using priority queue
        Map<String,Double> integerMap = new HashMap<>();
        for (String vertex : graph.vertices()) {
            Graph<String, Set<String>> graph1 = bfs(graph,vertex);
            if (graph1.hasVertex(name)) {
                graph1.inDegree(vertex);
                integerMap.put(vertex, (double) graph1.inDegree(vertex));
            }
        }


        PriorityQueue<String> pq = new PriorityQueue<String>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return integerMap.get(o1).compareTo(integerMap.get(o2));
            }

        });
        for (String entry : integerMap.keySet()) {
            pq.add(entry);
            if (pq.size() > k) pq.poll();
        }

        List<String> result = new ArrayList<>();
        while (!pq.isEmpty()) result.add(pq.poll());

        System.out.println(result);
    }


    public static void testPlay() { //test cases, find paths that are not connected, find boundaries/cycles,
        // find missing vertices, find top suggestions based on degree and length
        System.out.println("finding missing vertex:  ");
        System.out.println(missingVertices(mainGraphTest("inputs/actorsTest.txt",
                "inputs/moviesTest.txt","inputs/movie-actorsTest.txt"),
                bfs(mainGraphTest("inputs/actorsTest.txt","inputs/moviesTest.txt",
                        "inputs/movie-actorsTest.txt"),"Kevin Bacon")));
        Stack<String> path = new Stack<>();
        List<String> list = getPath(bfs(mainGraphTest("inputs/actorsTest.txt",
                "inputs/moviesTest.txt","inputs/movie-actorsTest.txt"), "Kevin Bacon"),
                "Dartmouth (Earl thereof)");
        path.addAll(list);
        if (!path.isEmpty()) {
            System.out.println("Bacon number is: " + (path.size() - 1));
        }
        for (int i = list.size() - 1; i > 0; i--) {
            System.out.println(list.get(i) + " was in " + mainGraphTest("inputs/actorsTest.txt",
                    "inputs/moviesTest.txt","inputs/movie-actorsTest.txt").getLabel(list.get(i),
                    list.get(i - 1)).toString() + " with" + " " + list.get(i - 1));
        }
        if (path.isEmpty()){
            System.out.println("Bacon number is infinity");
        }
        Stack<String> path1 = new Stack<>();
        List<String> list1 = getPath(bfs(mainGraphTest("inputs/actorsTest.txt",
                "inputs/moviesTest.txt","inputs/movie-actorsTest.txt"), "Kevin Bacon"), "Nobody");
        path1.addAll(list1);
        if (!path.isEmpty()) {
            System.out.println("Bacon number is: " + (path.size() - 1));
        }
        for (int i = list1.size() - 1; i > 0; i--) {
            System.out.println(list1.get(i) + " was in " + mainGraphTest("inputs/actorsTest.txt",
                    "inputs/moviesTest.txt","inputs/movie-actorsTest.txt").getLabel(list.get(i),
                    list.get(i - 1)).toString() + " with" + " " + list.get(i - 1));
        }
        if (path1.isEmpty()){
            System.out.println("Bacon number is infinity");
        }

        System.out.println("Search for a better Bacon by path length");
        getAvg(mainGraphTest("inputs/actorsTest.txt","inputs/moviesTest.txt",
                "inputs/movie-actorsTest.txt"), 3, "Kevin Bacon");

        System.out.println("Search for a better Bacon by Deg");
        getDeg(mainGraphTest("inputs/actorsTest.txt","inputs/moviesTest.txt",
                "inputs/movie-actorsTest.txt"),3,"Kevin Bacon");


    }
    public static void testPlay1() { //test cases, find paths that are not connected, find boundaries/cycles,
        // find missing vertices, find top suggestions based on degree and length
        System.out.println("finding missing vertex:  ");
        System.out.println(missingVertices(mainGraphTest("inputs/actors.txt",
                "inputs/movies.txt","inputs/movie-actors.txt"),
                bfs(mainGraphTest("inputs/actors.txt","inputs/movies.txt",
                        "inputs/movie-actors.txt"),"Kevin Bacon")));
        Stack<String> path = new Stack<>();
        List<String> list = getPath(bfs(mainGraphTest("inputs/actors.txt",
                "inputs/movies.txt","inputs/movie-actors.txt"), "Kevin Bacon"), "Don Rickles");
        path.addAll(list);
        if (!path.isEmpty()) {
            System.out.println("Bacon number is: " + (path.size() - 1));
        }
        for (int i = list.size() - 1; i > 0; i--) {
            System.out.println(list.get(i) + " was in " + mainGraphTest("inputs/actors.txt",
                    "inputs/movies.txt","inputs/movie-actors.txt").getLabel(list.get(i),
                    list.get(i - 1)).toString() + " with" + " " + list.get(i - 1));
        }
        if (path.isEmpty()){
            System.out.println("Bacon number is infinity");
        }
        Stack<String> path1 = new Stack<>();
        List<String> list1 = getPath(bfs(mainGraphTest(
                "inputs/actors.txt","inputs/movies.txt","inputs/movie-actors.txt"),
                "Kevin Bacon"), "Nobody");
        path1.addAll(list1);
        if (!path.isEmpty()) {
            System.out.println("Bacon number is: " + (path.size() - 1));
        }
        for (int i = list1.size() - 1; i > 0; i--) {
            System.out.println(list1.get(i) + " was in " + mainGraphTest(
                    "inputs/actorsTest.txt","inputs/moviesTest.txt",
                    "inputs/movie-actorsTest.txt").getLabel(list.get(i), list.get(i - 1)).toString() +
                    " with" + " " + list.get(i - 1));
        }
        if (path1.isEmpty()){
            System.out.println("Bacon number is infinity");
        }

        System.out.println("Search for a better Bacon by path length");
        getAvg(mainGraphTest("inputs/actors.txt","inputs/movies.txt",
                "inputs/movie-actors.txt"), 3, "Kevin Bacon");

        System.out.println("Search for a better Bacon by Deg");
        getDeg(mainGraphTest("inputs/actors.txt","inputs/movies.txt",
                "inputs/movie-actors.txt"),3,"Kevin Bacon");


    }

    public static void playGame(String firstName,Graph<String,Set<String>> main) {
        String current;
        boolean playing = true;
        System.out.println( // Gaming interface, suggest best Bacon by avg separation and Degree, finds path within list
                """
                        c <#>:List the best Kevin Bacon substitutes from worst to best to k people based on path length
                        d <low> <high>: list actors sorted by degree, with degree between low and high
                        p <name>: find path from <name> to current center of the universe
                        s <low> <high>: Number of actors who have a path
                        a <n#>: Average path length of all connected vertices u <name>: make <name> the center of the universe
                        m <list> missing verticies
                        l <#> find the degree of the current
                        q: quit game""");
        while (playing) {
            Scanner in = new Scanner(System.in);

            current = firstName;
            System.out.println("<The " + current + " Game>  ");

            System.out.println(current + " is now the center of the acting universe, connected to " +
                    (bfs(mainGraph(),current).numVertices()-1)
                    + " / " + mainGraph().numVertices() + " with average seperation " +
                    averageSep(bfs(mainGraph(),current),current, 0));


            String character = in.next();
            if (character.equals("c")) {
                System.out.println("Choose top k"); // Suggest new Bacon by path length with top k suggestions
                int nextInt = in.nextInt();
                getAvg(mainGraph(), nextInt, current);
            } else if (character.equals("d")) { // Suggest new Bacon by degree with top k suggestions
                System.out.println("Choose top k");
                int nextInt = in.nextInt();
                getDeg(mainGraph(), nextInt,current);
            } else if (character.equals("p")) { //Find a path from someone in the tree, if not in tree no path found.
                System.out.println("Who do you want path from?: ");
                in.nextLine();
                String nextString = in.nextLine();

                if (mainGraph().hasVertex(nextString)) {
                    Stack<String> path = new Stack<>();
                    List<String> list = getPath(bfs(mainGraph(), current), nextString);
                    path.addAll(list);
                    if (!path.isEmpty()) {
                        System.out.println("Bacon number is: " + (path.size() - 1));
                    }
                    for (int i = list.size() - 1; i > 0; i--) {
                        System.out.println(list.get(i) + " was in " + mainGraph().getLabel(list.get(i),
                                list.get(i - 1)).toString() + " with" + " " + list.get(i - 1));
                    }
                    if (path.isEmpty()){
                        System.out.println("Bacon number is infinity");
                    }

                } else {
                    System.err.println("Not a valid vertex");
                    System.out.println("Choose these: " + mainGraph().vertices());

                }


            } else if (character.equals("s")) { //Find actors in the tree with a path to center
                in.nextLine();
                System.out.println("Number of actors who have a path:  ");
                System.out.println((bfs(mainGraph(), current).numVertices() - 1));

            } else if (character.equals("u")) { //change the center and start new Bacon game, unless not in graph
                System.out.println("Who do you want at the center of the Universe?: ");
                in.nextLine();
                String newCurrent = in.nextLine();
                if (mainGraph().hasVertex(newCurrent)) {
                    current = newCurrent;
                    System.out.println(current);
                    playGame(current,main);
                }
                else if (!mainGraph().hasVertex(newCurrent)) {
                    throw new IllegalArgumentException("Can't do that one");
                }
            }
            else if (character.equals("m")) { // finds vertex that is not connected to center
                System.out.println("finding missing vertex:  ");
                System.out.println(missingVertices(mainGraph(),bfs(mainGraph(),current)));

            }else if (character.equals("a")) { // find average separation of current
                System.out.println(averageSep(bfs(mainGraph(),current),current,0));

            }else if (character.equals("l")) { // find the degree of the current
                System.out.println(bfs(mainGraph(),current).inDegree(current));
            }
            else if (character.equals("q")) {
                in.close();
                playing = false;
            }
        }

    }




    public static void main(String[]args) {
        System.out.println(mainGraphTest("inputs/actors.txt","inputs/movies.txt",
                "inputs/movie-actors.txt")); // create real graph
        System.out.println(mainGraphTest("inputs/actorsTest.txt","inputs/moviesTest.txt",
                "inputs/movie-actorsTest.txt")); // create real graph

        testPlay();
        testPlay1();
        //playGame("Kevin Bacon", mainGraph());



    }
}