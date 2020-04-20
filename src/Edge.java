class Edge
{
   int from;
   int to;
   int cost;
   Edge(int x, int y, int cost)
   {
		this.from = x;
		this.to = y;
		this.cost = cost;
   }

   public int getSrc(){ return from; }
   public int getDest(){ return to; }
   public int getWeight(){ return cost; }

   
}
