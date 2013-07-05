
public class Env {
	
	// Design.java //
	static final int num_partitions	= 20;
	// how many servers can contain a partitioned table 
	static final boolean printFreq	= true;
	// print workload frequency
	
	
	// LNS.java //
	public static final boolean memoryEfficient	= false;
	// false to run normal Horticulture, true to run memory efficient version
	public static final double smallPenaltyFactor 	= 1.0E-7;
	// penalty value for a design to replicate one table 
	public static final int MAX_NO_IMPROVE_ROUND 	= 1000;
	// skip this search tree branch if new best design is not found after # of attempts
	
	
	// HorticultureFinalProject //
	public static final String workloadIn	= "workload.xml";
	public static final String procedureIn	= "Procedure.xml";
	public static final String schemaIn		= "BGSchema.xml";
	// input file names
	
}

