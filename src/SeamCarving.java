import java.io.*;
import java.util.*;
public class SeamCarving
{

   public static int[][] readpgm(String fn)
	 {
        try {
            InputStream f = ClassLoader.getSystemClassLoader().getResourceAsStream(fn);
            BufferedReader d = new BufferedReader(new InputStreamReader(f));
            String magic = d.readLine();
            String line = d.readLine();
		   while (line.startsWith("#")) {
			  line = d.readLine();
		   }
		   Scanner s = new Scanner(line);
		   int width = s.nextInt();
		   int height = s.nextInt();
		   line = d.readLine();
		   s = new Scanner(line);
		   int maxVal = s.nextInt();
		   int[][] im = new int[height][width];
		   s = new Scanner(d);
		   int count = 0;
		   while (count < height*width) {
			  im[count / width][count % width] = s.nextInt();
			  count++;
		   }
		   return im;
        }

        catch(Throwable t) {
            t.printStackTrace(System.err) ;
            return null;
        }
    }

    public static void writepgm(int[][] image, String filename) throws IOException {
      File f = new File(filename);
      if(f.createNewFile()) {
          System.out.println("Fichier crée");
          FileWriter fw = new FileWriter(f);
          fw.write("P2\n");
          fw.write(image.length + " " + image[0].length+"\n");
          fw.write("255\n");
          for (int[] ints : image) {
              for (int j = 0; j < ints.length; j++) {
                  fw.write(ints[j] + " ");
              }
              fw.write("\n");
          }
          fw.close();
      }
    }

    public static int[][] interest(int[][] image){
       int[][] tab = new int[image.length][image[0].length];
       for(int i = 0 ; i<image.length; i++){
           for(int j = 0; j<image[i].length;j++){
               if(j==0){
                   tab[i][j]=Math.abs(image[i][j]-image[i][j+1]);
               }
               else if(j==image[i].length-1){
                    tab[i][j]=Math.abs(image[i][j]-image[i][j-1]);
               }
               else{
                    int moy = (image[i][j+1] + image[i][j-1])/2 ;
                    tab[i][j]=Math.abs(image[i][j]-moy);
               }
           }
       }
       return tab;
    }

    public static Graph tograph(int[][] itr) {
       int numSommet = 1;
       int numLigne = 0;
       GraphArrayList sortie = new GraphArrayList(itr.length*itr[0].length+2);
        for (int[] ints : itr) {
            for (int j = 0; j < ints.length; j++) {
                if (numLigne == 0) { //Premiere ligne
                    // Sommet de départ
                    sortie.addEdge(new Edge(0, numSommet, 0));
                    if (j == 0) { // Premiere ligne, premiere colonne
                        sortie.addEdge(new Edge(numSommet, numSommet + ints.length, ints[j]));
                        sortie.addEdge(new Edge(numSommet, numSommet + ints.length + 1, ints[j]));
                    } else if (j == ints.length - 1) //Premiere ligne, derniere colonne
                    {
                        sortie.addEdge(new Edge(numSommet, numSommet + ints.length, ints[j]));
                        sortie.addEdge(new Edge(numSommet, numSommet + ints.length - 1, ints[j]));
                    } else // Premiere ligne mais ni premiere ni derniere colonne
                    {
                        sortie.addEdge(new Edge(numSommet, numSommet + ints.length, ints[j]));
                        sortie.addEdge(new Edge(numSommet, numSommet + ints.length - 1, ints[j]));
                        sortie.addEdge(new Edge(numSommet, numSommet + ints.length + 1, ints[j]));
                    }
                } else if (numLigne == itr.length - 1) { //Derniere ligne, sommet d'arrivée
                    sortie.addEdge(new Edge(numSommet, sortie.vertices() - 1, ints[j]));
                } else // Ni sur la premiere, ni sur la dernière ligne
                {
                    if (j == 0) // Premiere colonne
                    {
                        sortie.addEdge(new Edge(numSommet, numSommet + ints.length, ints[j]));
                        sortie.addEdge(new Edge(numSommet, numSommet + ints.length + 1, ints[j]));
                    } else if (j == ints.length - 1)    //Derniere colonne
                    {
                        sortie.addEdge(new Edge(numSommet, numSommet + ints.length, ints[j]));
                        sortie.addEdge(new Edge(numSommet, numSommet + ints.length - 1, ints[j]));
                    } else // Ni premiere, ni derniere colonne
                    {
                        sortie.addEdge(new Edge(numSommet, numSommet + ints.length, ints[j]));
                        sortie.addEdge(new Edge(numSommet, numSommet + ints.length - 1, ints[j]));
                        sortie.addEdge(new Edge(numSommet, numSommet + ints.length + 1, ints[j]));
                    }
                }
                numSommet++;
            }
            numLigne++;
        }
        return sortie;
    }

    public static int[] Bellman_Ford(Graph g, int s, int t){
       //Initialisation
       GraphArrayList graph = (GraphArrayList) g;
       int V = g.vertices();
       int[] parent = new int[V];
       int[] d = new int[V];
       for(int i = 0 ; i < V ; i++) {
           d[i] = Integer.MAX_VALUE;
           parent[i]= -1;
       }
       d[s] = 0;

       //Relachement des arcs
       for(int i = 0 ; i < V-1 ; i++){
           for(Edge e : graph.edges()) {
               int u = e.getSrc();
               int v = e.getDest();
               int weight = e.getWeight();
               if((d[v] > d[u] + weight) && (d[u] != Integer.MAX_VALUE)){
                   d[v]=d[u]+weight;
                   parent[v]=u;
               }
           }
       }

       //Vérification de circuit négatif
        for(Edge e : graph.edges()){
            int u = e.getSrc();
            int v = e.getDest();
            int weight = e.getWeight();
            if (d[u] != Integer.MAX_VALUE && d[u] + weight < d[v]) {
                System.out.println("Le graphe contient un circuit négatif");
                return parent;
            }
        }
        System.out.println("Poids entre le sommet " + s + " et " + t + " = "+d[t]);
        printTabPere(parent);
        return parent;
    }

    static private void printTabPere(int[] parent) {
        System.out.print("Père :\t\t");
        for (int value : parent) {
            System.out.print(value + "|\t");
        }
        System.out.print("\nSommet : \t");
        for(int j = 0 ; j < parent.length; j++){
            System.out.print(j+"|\t");
        }
    }

    static public void deleteOnePixel(){
       int[][] original = readpgm("ressources/vampire.pgm");
       int[][] originalInterest = interest(original);
       Graph g = tograph(originalInterest);
       int[] tabPere = Bellman_Ford(g, 0, g.vertices()-1);
       int i = g.vertices()-1;
       ArrayList<Integer> liste = new ArrayList<>();
       while(tabPere[i]!=0){
           liste.add(tabPere[i]);
           i=tabPere[i];
       }
       System.out.println("\n"+liste);
    }
}
