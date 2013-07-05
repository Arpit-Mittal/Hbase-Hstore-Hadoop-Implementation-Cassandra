import java.util.*;

class Table implements Comparable<Table>{	// compare tables base on temperature (numTxn)
	
	String name = null;
	private String partitionAttr = null;
	private LinkedList<String> secIndex = new LinkedList<String>();
	boolean replication = true;
	boolean hasDelete = false;
	
	private int numTxn;
	
	TreeMap<String, Integer> attr = new TreeMap<String, Integer>();		// counting attr frequency
	LinkedList<String> attrCdt = new LinkedList<String>();		// attribute candidate 
	LinkedList<Integer> attrCdtVal = new LinkedList<Integer>();
	TreeSet<Integer> childrenProcedure = new TreeSet<Integer>();
	TreeSet<String> childrenProcedureName = new TreeSet<String>();
	HashSet<String> modifiedCol = new HashSet<String>();		// what attr is not read-only 
	LinkedList<String> secondCdt = new LinkedList<String>();
	
	public Table(String s){
		name = s;
	}
	
	public Table(Table ta){
		this(ta.name);
		this.partitionAttr = ta.partitionAttr;
		this.secIndex = new LinkedList<String>(ta.secIndex);
		this.replication = ta.replication;
		this.numTxn = ta.numTxn;				// for temperature. Ask Arpit to set this value in schemaExtractor
		this.attrCdt = ta.attrCdt;		// attribute candidate 
		this.attrCdtVal = ta.attrCdtVal;
		this.childrenProcedure = ta.childrenProcedure;
		this.childrenProcedureName = ta.childrenProcedureName;
		this.modifiedCol = ta.modifiedCol;		// what attr is not read-only
	}

	public void setTableSize(double s){
//		tableSize = s; 
	}
	
	public void setNumTxn(int p){
		numTxn = p;
	}
	
	public void incNumTxn(int p){
		numTxn += p;
	}
	
	public void inc(String s, int cnt){
		if(cnt <= 0) return;
		s = s.toLowerCase();
		if(attr.containsKey(s)){
			int val = attr.remove(s);
			attr.put(s, val + cnt);
		}
		else{
			attr.put(s, cnt);
		}
		incNumTxn(cnt);		// TODO cancel this when Arpit finished
	}

	public void computePartAttrSecIndex(){
		Map.Entry<String, Integer> me;
		int max = 0, maxMax = 0;
		int i;
		int val;
		String key;

		while((me = attr.pollFirstEntry())!= null){
			key = me.getKey();
			val = me.getValue();
			for(i = 0; i < attrCdt.size(); i++){
				if(val >= attrCdtVal.get(i))
					break;
			}
			attrCdt.add(i, key);
			attrCdtVal.add(i, val);
		}

		for(i = 0; i < attrCdt.size(); i++){
			key = attrCdt.get(i);
			val = attrCdtVal.get(i);
			
			if(val < max)
				continue;
			if(maxMax <= val){
				if(!modifiedCol.contains(partitionAttr) && partitionAttr != null){	// partitionAttr is read-only, throw partitionAttr to secIndex
					max = maxMax;
					secIndex.add(partitionAttr);
				}
				maxMax = val;
				partitionAttr = key;
			}
			else{ 	// max <= val < maxMax
				if(!modifiedCol.contains(key))
					continue;
				max = val;
				secIndex.add(key);
			}
		}

		attr = null;	// free attr TreeMap
		if(partitionAttr == null)
			partitionAttr = "NIL";
		if(LNS.memoryEfficient)
			secIndex.clear();
		if(secIndex.size() == 0)
			secIndex.add("NIL");
	}

	// Return partition attribute and secondary index.
	public String getPartAttr(){
		if(partitionAttr == null)
			computePartAttrSecIndex();
		if(!replication)
			return partitionAttr;
		return "No partition attribute. Table replicated.";
	}

	// Return partition attribute and secondary index.
	public LinkedList<String> getSecIndex(){
		if(secIndex.size() == 0)
			computePartAttrSecIndex();
		
		if(!replication)
			return secIndex;
		
		LinkedList<String> ret = new LinkedList<String>();
		ret.add("No secondary index. Table replicated.");
		return ret;
	}

	public void fixRelaxPartitionAttr(String attrC) {
		secIndex.clear();
		
		secIndex.addAll(secondCdt);
		secIndex.remove(attrC);
		if(secIndex.size() == 0)
			secIndex.add("NIL");
		
		partitionAttr = attrC;
	}
	
	public boolean equals(String s){
		return (s.equalsIgnoreCase(name));
	}
	
	public String toString(){
		return name;
	}
	
	public int compareTo(Table t){	
		/* 
		 compare tables base on temperature
		 double ret = (double) t.tableSize / (double) t.numTxn - (double) tableSize / (double) numTxn;
		*/		
		
		// compare tables base on numTxn "descending order"
		double ret = (double) t.numTxn - (double) numTxn;
		return (ret > 0)? 1 :
			(ret < 0)? -1 : 0; 
	}

	public void setSecondCdt(int schemaExtractorIndex) {
		Iterator<String> ite;
		
		this.secondCdt.addAll(HorticultureFinalProject.schemaExtractor[schemaExtractorIndex].getEveryAttribute());
		ite = this.secondCdt.iterator();
		while(ite.hasNext()){
			if(this.modifiedCol.contains(ite.next().toLowerCase()))
				ite.remove();
		}
	}

}
