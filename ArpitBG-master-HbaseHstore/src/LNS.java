import java.util.*;

public class LNS {

	// environmental parameters
	public static final boolean memoryEfficient = Env.memoryEfficient;
	private static final double smallPenaltyFactor = Env.smallPenaltyFactor;
	static final int MAX_NO_IMPROVE_ROUND = Env.MAX_NO_IMPROVE_ROUND;
	
	static int noImproveRnd = 0;		// global counter for noImproveRnd;

	// currently best design
	private static Design best;			
	static double bestCost;
	static int bestDistTranscationCount;
	static int bestPartitioncount;
	static int bestFrequencyBG;
	
	
	// current Design
	private static Design current;
	private static TreeSet<Integer> relaxedProcedureIndex = new TreeSet<Integer>();
	
	// principles Design
	static Design principle;	// contains parsing info;
	static int num_tables;
	static int partitionTableSize;
	
	static int relaxRound = 0;
	static boolean[][] flip;	// corresponding to principle table's name
	static boolean[] flipBase;
	static int combination = 1;

	static int inscribeOrder = 0;
	static void inscribe(){
		int i;
		for(i = 0; i < partitionTableSize; i++)
			flip[inscribeOrder][i] = flipBase[i];
		inscribeOrder++;
	}
	
	static void forward(int digit, int start){
		if(start >= partitionTableSize || digit <= 0)
			return;

		flipBase[start] = true;
		if(digit == 1)
			inscribe();
		forward(digit - 1, start + 1);
		flipBase[start] = false;
		forward(digit, start + 1);
	}
	
	public static boolean relaxTable(){
		int i;
		for(i = 0; i < partitionTableSize; i++)
			combination *= 2;
		
		flipBase = new boolean[num_tables];
		flip = new boolean[combination - 1][partitionTableSize];
		for(i = 1; i <= partitionTableSize; i++)
			forward(i, 0);
		return true;
	}
	
	static boolean addRelaxProc(Table t){
		return relaxedProcedureIndex.addAll(t.childrenProcedure);
	}
	
	public static void setRelaxTable(int index){
		relaxedProcedureIndex = new TreeSet<Integer>();  
		current = new Design(principle);
		
		LinkedList<Table> dupPart = new LinkedList<Table>(principle.partitionList); 
		Table tmp;
		int i;

		for(i = 0; i < partitionTableSize; i++){
			tmp = dupPart.get(i);
			if(flip[index][i]){
				addRelaxProc(tmp);
			}
		}

		// clearRouteAtrr() for relaxed table;
		for(Integer ind: relaxedProcedureIndex)
			current.routAtrrList.get(ind).clearRouteAtrr();
		
		return;
	}
	
	public static boolean searchProcedure(TreeSet<Integer> routingUnknown){
		if(noImproveRnd++ > MAX_NO_IMPROVE_ROUND)
			return false;
		
		if(routingUnknown.size() <= 0)
			return compareCost(current);
		
		TreeSet<Integer> stepRoutingUnknown = new TreeSet<Integer>(routingUnknown);
		int index = stepRoutingUnknown.pollFirst();
		boolean ret = false;
		Procedure pr = current.routAtrrList.get(index);
		for(String s : pr.attrCdt){
			pr.setRoutAtrr(s);
			if(estimateCost(current) >= bestCost)
				continue;
			if(searchProcedure(stepRoutingUnknown))
				ret = true;
		}
		return ret;
	}
	
	public static boolean fixRelaxTable(int tableIndex, int localPartSize){	
		boolean ret = false;
		if(tableIndex >= localPartSize)
			return searchProcedure(relaxedProcedureIndex);
		
		if(!flipBase[tableIndex])				
			return fixRelaxTable(tableIndex + 1, localPartSize);
		/* DEBUG
		if(current.tableList.get(tableIndex).replication
				|| (current.tableList.get(tableIndex).attrCdt.size() == 0))
			System.err.println("Something Wrong with fixRelaxTable()");
		*/
		Table tmp = current.partitionList.get(tableIndex);
		// use all attrCdt to set routing attribute
		for(String attrC: tmp.attrCdt){	
			tmp.fixRelaxPartitionAttr(attrC);
			if(fixRelaxTable(tableIndex + 1, localPartSize))
				ret = true;
		}
		
		int i;
		// replicate originally partitioned table
		tmp.replication = true;
//zt		current.tableList.remove(tableIndex);
		current.partitionList.remove(tableIndex);
		current.replicationList.addFirst(tmp);
		
		for(i = tableIndex; i < localPartSize - 1; i++)
			flipBase[i] = flipBase[i+1];
		
		if(fixRelaxTable(tableIndex , localPartSize - 1))
			ret = true;
		
		for(i = tableIndex; i < localPartSize - 1; i++)
			flipBase[i+1] = flipBase[i];
		flipBase[tableIndex] = true;
		
		current.replicationList.removeFirst();
		current.partitionList.add(tableIndex, tmp);
//zt		current.tableList.add(tableIndex, tmp);
		tmp.replication = false;
		
		return ret;
	}
	
	static boolean search(int round){
		int i;
		
		flipBase = new boolean[partitionTableSize];
		for(i = 0; i < partitionTableSize; i++)
			flipBase[i] = flip[round][i];
		
		return fixRelaxTable(0, partitionTableSize);
	}
	
	public static Design relaxThenSearch(Design d) {
		int i;
		
		setPrinciple(d);
		setBest(d, estimateCost(d));
		
		relaxTable();
		for(i = 0; i < combination - 1; i++){
			setRelaxTable(i);
			
			/* DEBUG
			noImproveRnd = 0;
			while(noImproveRnd < noImproveRndMax){
				if(search(i))
					noImproveRnd = 0;
				else
					noImproveRnd++;
			}
			*/
			search(i);
		}
		return best;
	}
	
	static void setPrinciple(Design d){
		principle = new Design(d);
		num_tables = d.getNumTables();
		partitionTableSize = d.partitionList.size();
	}
	
	static double smallPenalty(Design d){
		int ret = d.getReplicationList().size();
		return ((double) ret) * smallPenaltyFactor;
	}
	
	static double cost = 0.0;
	public static double estimateCost(Design d){
		/* DEBUG */
		//cost--;
		//cost = 0;
		
		cost = (double) d.attributeExtractionHorticultureFinalProject(d);
		return (memoryEfficient)? cost + smallPenalty(d) : cost;

		/*	DEBUG test cost model
		if(cost >= -100)
			cost = -10;
		return cost++;
		 */
	}
	
	static void setBest(Design de, double dou){
		best = new Design(de);
		bestCost = dou;
		noImproveRnd = 0;
		
		bestDistTranscationCount = Design.distTranscationCount;
		bestPartitioncount = Design.partitioncount;
		bestFrequencyBG = Design.frequencyBG;
		
		/* DEBUG
		int i, j;
		for(i = best.replicationList.size() - 1; i >= 0; i--){
			for(j = i - 1; j >= 0; j--){
				if(best.replicationList.get(i).name.equalsIgnoreCase(best.replicationList.get(j).name))
					bestCost = dou;
			}
		}
		*/
	}

	static boolean compareCost(Design de){
		double newCost = estimateCost(de);
		if(newCost >= bestCost)
			return false;
		setBest(de, newCost);
		return true;
	}
}
