import java.util.*;

public class Design {

	// environmental parameter, how many servers can contain a partitioned table 
	static final int num_partitions = Env.num_partitions;
	static final boolean printFreq = Env.printFreq;	// print workload frequency

	static Boolean[] questionMark = {};
	static boolean[] readOnly = {};
	static int[] workloadCnt = {};
	static String[] actionName = {};
	static String[] column = {};
	static String[] queryCommand = {};
	static String[] table = {};
	

	// cost model calculating
	static int distTranscationCount;
	static int partitioncount;
	static int frequencyBG;


	// Don't use these lists directly. Use get() below to prevent modifying list itself.
	LinkedList<Table> partitionList = new LinkedList<Table>();
	public void setPartitionList(LinkedList<Table> partitionList) {
		this.partitionList = partitionList;
	}

	LinkedList<Table> replicationList = new LinkedList<Table>();
	public void setReplicationList(LinkedList<Table> replicationList) {
		this.replicationList = replicationList;
	}
/* ZT
	LinkedList<Table> tableList = new LinkedList<Table>();
	public void setTableList(LinkedList<Table> tableList) {
		this.tableList = tableList;
	}
*/
	LinkedList<Procedure> routAtrrList = new LinkedList<Procedure>();
	public void setRoutAtrrList(LinkedList<Procedure> routAtrrList) {
		this.routAtrrList = routAtrrList;
	}

	private int num_tables;

	public Design(Design d){
		for(Table ta : d.partitionList)
			this.partitionList.add(new Table(ta));

		this.replicationList = new LinkedList<Table>(d.replicationList);
		
//		if(d.partitionList.isEmpty() && d.replicationList.isEmpty())
// ZT			this.tableList = new LinkedList<Table>(d.tableList);
/*		else{
			for(Table ta : d.partitionList)
				this.tableList.add(ta);
			for(Table ta : d.replicationList)
				this.tableList.add(ta);
		}
//		this.tableList = new LinkedList<Table>(d.replicationList);
	*/	
		
		for(Procedure p : d.routAtrrList)
			this.routAtrrList.add(new Procedure(p));
		
		this.num_tables = d.num_tables;
	}

	public Design(){
		super();
	}

	int getNumTables(){
		return num_tables;
	}

	// return lists
	LinkedList<Table> getPartitionList(){
		return partitionList;
	}

	LinkedList<Table> getReplicationList(){
		return replicationList;
	}

	LinkedList<Procedure> getRoutAtrrList(){
		return routAtrrList;
	}

	public static String isArgLengthEqual(){
		return (workloadCnt.length != actionName.length)? "workloadCnt or actionName" :
			(workloadCnt.length != readOnly.length)? "readOnly" :
			(workloadCnt.length != questionMark.length)? "questionMark" :
			(workloadCnt.length != queryCommand.length)? "queryCommand" :
			(workloadCnt.length != column.length)? "column" :
			(workloadCnt.length != table.length)? "table" : null;
	}

	private void partition(){
		int i;
		Table ta = null;
		for(i = 0; i < column.length; i++){
			column[i] = column[i].toLowerCase();
			if(workloadCnt[i] <= 0) continue;
			ta = null;
			for(Table t : partitionList){
				if(!t.name.equalsIgnoreCase(table[i]))
					continue;
				ta = t;
				break;
			}
			if(ta == null){                         
				partitionList.add(new Table(table[i]));
				ta = partitionList.getLast();
			}
			ta.inc(column[i], workloadCnt[i]);
			ta.childrenProcedureName.add(actionName[i].toLowerCase());
			if(!readOnly[i]){
				ta.replication = false;
				ta.modifiedCol.add(column[i]);
				if(queryCommand[i].equalsIgnoreCase("DELETE"))
					ta.hasDelete = true;
			}

			if(LNS.memoryEfficient)
				ta.replication = false;
		}

		// sort Table
		Table[] partitionArray = {};
		partitionArray = partitionList.toArray(partitionArray);
		Arrays.sort(partitionArray);
		partitionList = new LinkedList<Table>();


		for(Table t : partitionArray){

			// get secondary index candidates
			for(i = 0; i < HorticultureFinalProject.schemaExtractor.length; i++){
				if(t.name.equalsIgnoreCase(HorticultureFinalProject.schemaExtractor[i].tableName))
					break;
			}

			if(i >= HorticultureFinalProject.schemaExtractor.length)
				System.err.println("table name not found");

			// modifiedCol is all attributes if it's a DELETE
			if(t.hasDelete)
				t.modifiedCol.addAll(HorticultureFinalProject.schemaExtractor[i].getEveryAttribute());
			
			if(!LNS.memoryEfficient)
				t.setSecondCdt(i);

			partitionList.add(t);
		}
	}

	private void replication(){
		Iterator<Table> ite;
		Table t;       
		boolean found;

		for(SchemaExtractor se : HorticultureFinalProject.schemaExtractor){
			found = false;
			for(Table ta : partitionList){
				if(se.getTableName().equalsIgnoreCase(ta.name)){
					found = true;
					break;                           
				}
			}

			// TODO still need to give one descent routing attribute

			if(!found){
				Table ta = new Table(se.getTableName());
				if(LNS.memoryEfficient){
					ta.replication = false;
					partitionList.add(ta);
				}
				else
					replicationList.add(ta);
			}
		}

		ite = partitionList.descendingIterator();
		while(ite.hasNext()){
			t = ite.next();
			if(!t.replication)
				continue;
			replicationList.addFirst(t);
			ite.remove();
		}

		num_tables = partitionList.size() + replicationList.size();
/* ZT
		for(Table ta: partitionList)
			tableList.add(ta);
		for(Table ta: replicationList)
			tableList.add(ta);
			*/
	}

	private void routingAttr(){
		int i;
		Procedure pr = null;
		for(i = 0; i < column.length; i++){
			if(!questionMark[i])
				continue;
			
			pr = null;
			for(Procedure p : routAtrrList){
				if(!p.name.equals(actionName[i]))
					continue;
				pr = p;
				break;
			}
			if(pr == null){                         
				routAtrrList.add(new Procedure(actionName[i], this));
				pr = routAtrrList.getLast();
			}
			pr.addAttr(column[i], table[i]);
			pr.setFreq(workloadCnt[i]);
		}

		// sort routingAttr
		Procedure[] routingArray = {};
		routingArray = routAtrrList.toArray(routingArray);
		Arrays.sort(routingArray);
		routAtrrList = new LinkedList<Procedure>();
		for(Procedure t : routingArray)
			routAtrrList.add(t);

		// routAtrrList set, set childrenProcedure for every table
		for(Table ta: partitionList){
			for(String s: ta.childrenProcedureName){
				for(i = routAtrrList.size() - 1; i >= 0; i--){
					if(s.equalsIgnoreCase(routAtrrList.get(i).name))
						ta.childrenProcedure.add(i);
				}
			}
		}
		for(Table ta: replicationList){
			for(String s: ta.childrenProcedureName){
				for(i = routAtrrList.size() - 1; i >= 0; i--){
					if(s.equalsIgnoreCase(routAtrrList.get(i).name))
						ta.childrenProcedure.add(i);
				}
			}
		}
	}

	private void printlog(String s, boolean printBest){
		System.out.println("\n  = = = " + s + " = = =\n");
		printlog(printBest);
	}

	private void printlog(boolean printBest){
		String s;

		if(LNS.memoryEfficient){
			System.out.println(" == DISCARD ==");
			for(Table tl: partitionList){
				if(tl.getPartAttr().equalsIgnoreCase("NIL"))
					System.out.println(tl.name);			
			}
		}

		System.out.println(" == PARTITION & SECONDARY INDEX ==");
		for(Table tl: partitionList){
			if(tl.getPartAttr().equalsIgnoreCase("NIL") && LNS.memoryEfficient)
				continue;
			System.out.printf("%s\t|%s|\t> secondary index: %s\n", tl.name, tl.getPartAttr(), tl.getSecIndex());
		}

		System.out.println(" == REPLICATE ==");
		for(Table tl: replicationList)
			System.out.printf("%s\n", tl);

		System.out.println(" == ROUTING ATTRIBUTE ==");
		for(Procedure p: routAtrrList){
			if(p.frequency == 0)
				continue;
			s = p.getRoutAtrr();
			for(Table tl: replicationList){
				if(s.toLowerCase().endsWith(tl.name.toLowerCase())){
					s += " [Replicated] ";
				}
			}
			if(printFreq)
				System.out.printf("%s\t> %s\t# %d\n", s, p.name, p.frequency);
			else
				System.out.printf("%s\t> %s\n", s, p.name);
		}

		if(printBest){
			System.out.println("Best Cost : "+LNS.bestCost);
			System.out.println("Total Number of Procedures = "+LNS.bestFrequencyBG);
			System.out.println("Total Distributed Transaction Count = "+LNS.bestDistTranscationCount);
			System.out.println("Total Number of Partitions = "+num_partitions);
			System.out.println("Total Partition Count = "+LNS.bestPartitioncount);
		}
		/*
		///////Arpit Code Extracting Attribute Names
		Design design= new Design();
		design.setPartitionList(partitionList);
		design.setReplicationList(replicationList);
		design.setRoutAtrrList(routAtrrList);




		attributeExtractionHorticultureFinalProject(design);

		///////Arpit Code Extracting Attribute Nmaes
		*/
	}
	ArrayList<String> frequency;
	int transactioncount=0;
	float coordinationcost=0;
	public float attributeExtractionHorticultureFinalProject(Design design)
	{

		///////////////////////Cost Model Part//////////////////////
		System.out.println("Cost Model Part For BG ie Calculating Coordination Cost");
		System.out.println(" == PARTITION & SECONDARY INDEX ==");
		for(Table tl: design.getPartitionList()){
			System.out.printf("%s\tby %s,\tsecondary index: %s\n", tl.name, tl.getPartAttr(), tl.getSecIndex());
		}
		System.out.println(" == REPLICATION ==");
		for(Table tl: design.getReplicationList()){
			System.out.printf("%s\n", tl);
		}
		System.out.println(" == ROUTING ATTRIBUTE ==");
		for(Procedure p: design.getRoutAtrrList()){
			System.out.printf("%s:\t%s\n", p.name, p.getRoutAtrr());
		}

		//**************Extracting Attribute Names from Horticulture Final Project*******************************


		column = HorticultureFinalProject.getAttributeNames(column);
		System.out.println(column.length);
		frequency=HorticultureFinalProject.getFrequencyNames();
		table = HorticultureFinalProject.getTableNames(table);
		distTranscationCount=0;
		partitioncount=0;
		System.out.println("\n Schema Extractor Logic");
		SchemaExtractor[] schemaExtractor= new SchemaExtractor[5];
		schemaExtractor=HorticultureFinalProject.getSchemaExtractor();
		for(int x=0;x<(schemaExtractor.length);x++)
		{
			System.out.println("Table : " + schemaExtractor[x].getTableName());
			System.out.println("Attribute : " + schemaExtractor[x].getAttributeNames());
		}
		System.out.println("\n Schema Extractor Logic");


		System.out.println("\n ****************UNIT TESTING**********************************************");

		System.out.println("Cost Model Part For BG ie Calculating Coordination Cost");
		System.out.println(" == PARTITION & SECONDARY INDEX ==");
		for(Table tl: design.getPartitionList()){
			System.out.printf("%s\tby %s,\tsecondary index: %s\n", tl.name, tl.getPartAttr(), tl.getSecIndex());
		}
		System.out.println(" == REPLICATION ==");
		for(Table tl: design.getReplicationList()){
			System.out.printf("%s\n", tl);
		}
		System.out.println(" == ROUTING ATTRIBUTE ==");
		for(Procedure p: design.getRoutAtrrList()){
			System.out.printf("%s:\t%s\n", p.name, p.getRoutAtrr());
		}

		// int distTranscationCount=0;

		int actionChecking=0;
		frequencyBG=0;

		for(int i=0;i<actionName.length;i++)
		{
			if(i==0)
			{
				frequencyBG=Integer.parseInt(frequency.get(i))+frequencyBG;
			}

			if(i!=0)
			{
				if(!(actionName[i-1].equalsIgnoreCase(actionName[i])))
					frequencyBG=Integer.parseInt(frequency.get(i))+frequencyBG;
			}
		}   

		for(int i=0;i<column.length;i++)
		{



			System.out.println("\nRecords start Unit Testing");


			int replication1=0,secondaryindex1=0,partition1=0,routing1=0;





			if(Integer.parseInt(frequency.get(i))!=0)
			{
				// Partition Index
				for(Table tl: design.getPartitionList()){
					//System.out.println("Table Name: "+tl.name+" "+table[i]);
					// System.out.println("part attr: "+tl.getPartAttr()+" "+column[i]);
					if( ( (tl.name).toLowerCase().replace(" ","").equalsIgnoreCase(table[i].toLowerCase().replace(" ","")) )&&((tl.getPartAttr().toLowerCase().replace(" ","").equalsIgnoreCase(column[i].replace(" ",""))))   ){
						partition1=1;

					}
				}


				// Secondary Index

				for(Table tl: design.getPartitionList()){
					System.out.println("Secondary index Table Name: "+tl.name+ " " +table[i]);
					System.out.println("Secondary index Index Name: "+tl.getSecIndex()+ " "+column[i]);


					for(int count=0;count<tl.getSecIndex().size();count++)
					{

						if( ( (tl.name).toLowerCase().replace(" ","").equalsIgnoreCase(table[i].toLowerCase().replace(" ","")) )&&((tl.getSecIndex().get(count).toString().toLowerCase().replace(" ","").equalsIgnoreCase(column[i].replace(" ",""))))   ){
							secondaryindex1=1;

						}
					}
				}




				// Replication Attribute

				for(Table tl: design.getReplicationList()){
					String s1 = (tl.name).toLowerCase().replace(" ",""), s2 = table[i].toLowerCase().replace(" ","");
					boolean cmp =s1.equalsIgnoreCase(s2);


					//System.out.println("Table Name "+tl.name + " s1 = " + s1 + " s2 = " + s2 + "\t" + cmp) ;
					if( ( cmp )){
						replication1 = 1;

					}



				}



				// Routing Attribute

				for(Procedure p: design.getRoutAtrrList()){


					if(p.name.equalsIgnoreCase(actionName[i]))
					{
						//System.out.println("Action Name: "+p.name);
						//System.out.println("routing attr: "+p.getRoutAtrr());

						String s=p.getRoutAtrr();
						String[] split1 = s.split(" ");
						if(split1.length>2)
						{
							System.out.println("Split Name: "+split1[2]+" "+table[i]);
							System.out.println("Split Name: "+split1[1]);
							System.out.println("Split Name: "+split1[0]+" "+column[i]);
							if( ( (split1[2]).toLowerCase().replace(" ","").equalsIgnoreCase(table[i].toLowerCase().replace(" ","")) )&&((split1[0].toLowerCase().replace(" ","").equalsIgnoreCase(column[i].replace(" ",""))))   ){
								routing1 = 1;

							}  
						}
					}
				}
				//}

				if(Integer.parseInt(frequency.get(i))!=0)
				{

					System.out.println("Table name"+table[i]);
					System.out.println("Attribute Name+"+column[i]);
					System.out.println("\nAction Names+"+actionName[i]);
					System.out.println("Partitioning Attribute"+partition1);
					System.out.println("SecondaryIndex+"+secondaryindex1);
					System.out.println("Routing Attribute"+routing1);
					System.out.println("Replication Attribute"+replication1);
					System.out.println("Records End Unit Testing");
				}



		}

		if(readOnly[i]){

			if((partition1==0)&&(secondaryindex1==0)&&(routing1==0)&&(replication1==0))
			{

				//frequencyBG+=(Integer.parseInt(frequency.get(i)));         
				distTranscationCount=distTranscationCount+(Integer.parseInt(frequency.get(i)));
				partitioncount=partitioncount+num_partitions*Integer.parseInt(frequency.get(i));

			}

			if((routing1==0)&&(partition1==1))
			{
				distTranscationCount=distTranscationCount+(Integer.parseInt(frequency.get(i)));
				partitioncount=partitioncount+num_partitions*Integer.parseInt(frequency.get(i));
			}
		}
		else
		{
			if(!(actionName[i-1].equalsIgnoreCase(actionName[i])))
			{
				actionChecking=0; 
			}


			if((replication1==1)||(secondaryindex1==1))
			{



				if(actionChecking==0)
				{
					distTranscationCount=distTranscationCount+(Integer.parseInt(frequency.get(i)));
					partitioncount=partitioncount+num_partitions*Integer.parseInt(frequency.get(i));

				}


				actionChecking=1;



			}
			if((partition1==1)&&(secondaryindex1==0)&&(routing1==0)&&(replication1==0))
			{

				if(actionChecking==0)
				{
					distTranscationCount=distTranscationCount+(Integer.parseInt(frequency.get(i)));
					partitioncount=partitioncount+num_partitions*Integer.parseInt(frequency.get(i));
					actionChecking=1;
				}

				actionChecking=1;



			}


		}


	}
	//int num_partitions=20;//User Entered the number of Servers Assume it is 20
	System.out.println("Final Results");
	System.out.println("Total Number of Proceedures = "+frequencyBG);
	System.out.println("Total Distributed Transaction Count = "+distTranscationCount);
	System.out.println("Total Number of Partitions = "+num_partitions);
	System.out.println("Total Partition Count = "+partitioncount);
	System.out.println("Final Results");





	float distgdfgfh=distTranscationCount;
	float p=partitioncount;


	System.out.println(frequency.size());
	float k=(1+(distgdfgfh/frequencyBG));
	System.out.println("K value"+k);
	coordinationcost=(p/((frequencyBG)*num_partitions));
	coordinationcost=coordinationcost*k;
	System.out.println("Coordination cost = "+coordinationcost);



	//**************Extracting Attrribute Names from Horticulture Final Project******************************


	///////////////////////Cost Model Part//////////////////////
	return coordinationcost;
}

private void init(){
	partition();    // is not finished without calling replication()
	replication();
	routingAttr();       
}


static void passArgumets(){
	String[] nil = null;

	// passing Arpit xml file
	HorticultureFinalProject.main(nil);

	// Get entries from HorticultureFinalProject
	questionMark = HorticultureFinalProject.getQuestionList(questionMark);
	column = HorticultureFinalProject.getAttributeNames(column);
	table = HorticultureFinalProject.getTableNames(table);
	queryCommand = HorticultureFinalProject.getQueryNames(queryCommand);
	readOnly = HorticultureFinalProject.getReadOnly(readOnly);
	actionName = HorticultureFinalProject.getActionNames(actionName);
	workloadCnt = new int[actionName.length];

	int i;
	String s;

	for(i = actionName.length - 1; i >= 0; i--){
		for(WorkloadExtractor wle : HorticultureFinalProject.workloadExtractor){
			s = wle.getAction();
			if(actionName[i].equalsIgnoreCase(s)){
				workloadCnt[i] = Integer.parseInt(wle.getFrequency());
				break;
			}
		}
		column[i] = column[i].trim();
	}

	/* DEBUG
	   for(i=0;i<readOnly.length;i++)           
	   System.out.println(actionName[i] +'\t'+ column[i]+'\t'+table[i]+'\t'+queryCommand[i]+'\t'+readOnly[i]);
	   */

	if(isArgLengthEqual() != null){
		System.err.println("arrays length is inconsistent: " + isArgLengthEqual());
		System.exit(1);
	}
}

public static void main(String[] args){

	passArgumets();

	Design best = new Design();
	best.init();
	best.printlog("Initial Design", false);

	best = LNS.relaxThenSearch(best);
	best.printlog("Final Design", true);
}
}

