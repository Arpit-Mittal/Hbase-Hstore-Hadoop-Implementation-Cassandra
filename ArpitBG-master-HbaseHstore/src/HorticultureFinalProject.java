import java.io.File;
import java.util.*;

import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;



public class HorticultureFinalProject{

	
	static HashMap<String, String> tableHash;
	static HashSet<String> questionTableSet = new HashSet<String>();
	
	static SchemaExtractor[] schemaExtractor= new SchemaExtractor[5];
	public static SchemaExtractor[] getSchemaExtractor() {
		return schemaExtractor;
	}

	public static void setSchemaExtractor(SchemaExtractor[] schemaExtractor) {
		HorticultureFinalProject.schemaExtractor = schemaExtractor;
	}


	static ProceedureExtractor[] proceedureExtractor= new ProceedureExtractor[19];
	public static ProceedureExtractor[] getProceedureExtractor() {
		return proceedureExtractor;
	}

	public static void setProceedureExtractor(
			ProceedureExtractor[] proceedureExtractor) {
		HorticultureFinalProject.proceedureExtractor = proceedureExtractor;
			}


	static WorkloadExtractor[] workloadExtractor= new WorkloadExtractor[19];


	public static WorkloadExtractor[] getWorkloadExtractor() {
		return workloadExtractor;
	}

	public static void setWorkloadExtractor(WorkloadExtractor[] workloadExtractor) {
		HorticultureFinalProject.workloadExtractor = workloadExtractor;
	}
	static int totalworkload1=0;
	public static int totalworkload()
	{
		return totalworkload1;
	}

	public static void setworkload()
	{

		String backup_of_s;

		try {
			DocumentBuilderFactory odbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder odb =  odbf.newDocumentBuilder();
			Document odoc = odb.parse (new File(Env.workloadIn));
			odoc.getDocumentElement ().normalize ();
			// System.out.println ("Root element of the doc is " + odoc.getDocumentElement().getNodeName());
			NodeList LOP = odoc.getElementsByTagName("Action");
			//System.out.println("Total no of Tables : " + totalPersons);
			for(int s=0; s<LOP.getLength() ; s++)
			{

				Node FPN =LOP.item(s);
				if(FPN.getNodeType() == Node.ELEMENT_NODE)
				{
					Element firstPElement = (Element)FPN;

					NodeList oNameList = firstPElement.getElementsByTagName("Name");
					Element firstNameElement = (Element)oNameList.item(0);
					NodeList textNList = firstNameElement.getChildNodes();



					backup_of_s = new String(((Node)textNList.item(0)).getNodeValue().trim().toString());


					//System.out.println("Backupofs"+backup_of_s);
					workloadExtractor[s]=new WorkloadExtractor();

					workloadExtractor[s].setAction(backup_of_s);



					NodeList IDList = firstPElement.getElementsByTagName("frequency");
					Element IDElement = (Element)IDList.item(0);
					NodeList textIDList = IDElement.getChildNodes();


					backup_of_s = new String(((Node)textIDList.item(0)).getNodeValue().trim());

					//System.out.println("Backupofs"+backup_of_s);
					workloadExtractor[s].setFrequency(backup_of_s);
					// totalworkload1=totalworkload1+Integer.parseInt(backup_of_s);


				}    //end of if clause
			}        //end of for loop with variable s
		}catch (SAXParseException err) {
			System.out.println (err.getMessage ());
		}catch (SAXException e) {
			e.printStackTrace ();
		}catch (Throwable t) {
			t.printStackTrace ();
		}



	}

	public static void setProceedure()
	{
		String backup_of_s;

		try {
			DocumentBuilderFactory odbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder odb =  odbf.newDocumentBuilder();
			Document odoc = odb.parse (new File(Env.procedureIn));
			odoc.getDocumentElement ().normalize ();
			// System.out.println ("Root element of the doc is " + odoc.getDocumentElement().getNodeName());
			NodeList LOP = odoc.getElementsByTagName("Query");
			//System.out.println("Total no of Tables : " + totalPersons);
			for(int s=0; s<LOP.getLength() ; s++)
			{

				Node FPN =LOP.item(s);
				if(FPN.getNodeType() == Node.ELEMENT_NODE)
				{
					Element firstPElement = (Element)FPN;

					NodeList oNameList = firstPElement.getElementsByTagName("syntax");
					Element firstNameElement = (Element)oNameList.item(0);
					NodeList textNList = firstNameElement.getChildNodes();



					backup_of_s = new String(((Node)textNList.item(0)).getNodeValue().trim().toString());
					//System.out.println(backup_of_s);

					proceedureExtractor[s]=new ProceedureExtractor();
					proceedureExtractor[s].setQuery(backup_of_s);



				}    //end of if clause
			}        //end of for loop with variable s
		}catch (SAXParseException err) {
			System.out.println (err.getMessage ());
		}catch (SAXException e) {
			e.printStackTrace ();
		}catch (Throwable t) {
			t.printStackTrace ();
		}

	}



	public static void setSchema()
	{
		String backup_of_s;

		try {
			DocumentBuilderFactory odbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder odb =  odbf.newDocumentBuilder();
			Document odoc = odb.parse (new File(Env.schemaIn));
			odoc.getDocumentElement ().normalize ();
			// System.out.println ("Root element of the doc is " + odoc.getDocumentElement().getNodeName());
			NodeList LOP = odoc.getElementsByTagName("Table");
			//System.out.println("Total no of Tables : " + totalPersons);
			for(int s=0; s<LOP.getLength() ; s++)
			{

				Node FPN =LOP.item(s);
				if(FPN.getNodeType() == Node.ELEMENT_NODE)
				{
					Element firstPElement = (Element)FPN;

					NodeList oNameList = firstPElement.getElementsByTagName("Name");
					Element firstNameElement = (Element)oNameList.item(0);
					NodeList textNList = firstNameElement.getChildNodes();



					backup_of_s = new String(((Node)textNList.item(0)).getNodeValue().trim().toString());


					schemaExtractor[s]=new SchemaExtractor();
					schemaExtractor[s].setTableName(backup_of_s);



					NodeList IDList = firstPElement.getElementsByTagName("Attributes");
					Element IDElement = (Element)IDList.item(0);
					NodeList textIDList = IDElement.getChildNodes();


					backup_of_s = new String(((Node)textIDList.item(0)).getNodeValue().trim());

					schemaExtractor[s].setAttributeNames(backup_of_s);


				}    //end of if clause
			}        //end of for loop with variable s
		}catch (SAXParseException err) {
			System.out.println (err.getMessage ());
		}catch (SAXException e) {
			e.printStackTrace ();
		}catch (Throwable t) {
			t.printStackTrace ();
		}

	}


	static ArrayList<String> AttributeNames = new ArrayList<String>();


	public static void setAttributeNames(ArrayList<String> attributeNames) {
		AttributeNames = attributeNames;
	}


	static ArrayList<String> TableNames = new ArrayList<String>();


	public static void setTableNames(ArrayList<String> tableNames) {
		TableNames = tableNames;
	}


	static ArrayList<String> QueryNames = new ArrayList<String>();


	public static void setQueryNames(ArrayList<String> queryNames) {
		QueryNames = queryNames;
	}


	static ArrayList<String> ActionNames = new ArrayList<String>();


	public static void setActionNames(ArrayList<String> actionNames) {
		ActionNames = actionNames;
	}


	static ArrayList<String> FrequencyNames = new ArrayList<String>();





	public static ArrayList<String> getFrequencyNames() {
		return FrequencyNames;
	}

	public static void setFrequencyNames(ArrayList<String> frequencyNames) {
		FrequencyNames = frequencyNames;
	}

	/* Zorn added start */
	static ArrayList<Boolean> questionList = new ArrayList<Boolean>();
	public static Boolean[] getQuestionList(Boolean[] bo){
		return questionList.toArray(bo);
	}
	
	public static String[] getAttributeNames(String[] sa){
		return AttributeNames.toArray(sa);
	}

	public static String[] getTableNames(String[] sa){
		return TableNames.toArray(sa);
	}

	public static String[] getQueryNames(String[] sa){
		return QueryNames.toArray(sa);
	}

	public static String[] getActionNames(String[] sa){
		return ActionNames.toArray(sa);
	}

	public static boolean[] getReadOnly(boolean[] a){
		boolean[] ret = new boolean[QueryNames.size()];
		int i;
		String tmp;
		for(i = QueryNames.size() - 1; i >= 0; i--){
			tmp = QueryNames.get(i);
			if(tmp.equalsIgnoreCase("UPDATE") || tmp.equalsIgnoreCase("INSERT") || tmp.equalsIgnoreCase("DELETE"))
				ret[i] = false;
			else if(tmp.equalsIgnoreCase("SELECT"))
				ret[i] = true;
			else{
				ret[i] = false;
				System.err.println("Not supported query command: " + tmp);
			}
		}
		return ret;
	}
	/* Zorn added end */  



	public static void printIndexes(String string, char ch,ArrayList<Integer> k) {
		int index = 0;
		while((index = string.indexOf(ch, index)) != -1) {
			index = string.indexOf(ch, index);
			//System.out.println("index"+index);
			k.add(index);
			index++;
		}
	}

	public static void parsingAdd(int workloadExtractorCounter, boolean isQuestion, String QN, String tableDotAttr){
		String split[] = tableDotAttr.split("[.]");
		String th = tableHash.get(split[0]);
		if(isQuestion)
			questionTableSet.add(th);
		parsingAdd(workloadExtractorCounter, isQuestion, QN, th, split[1]);
	}
	
	public static void parsingAdd(int workloadExtractorCounter, boolean isQuestion, String QN, String table, String Attr){
		ActionNames.add(workloadExtractor[workloadExtractorCounter].getAction());
		FrequencyNames.add(workloadExtractor[workloadExtractorCounter].getFrequency());
		questionList.add(isQuestion);
		QueryNames.add(QN);
		TableNames.add(table);
		AttributeNames.add(Attr);
	}
	
	private static void parsingRemove(int index) {
		ActionNames.remove(index);
		FrequencyNames.remove(index);
		questionList.remove(index);
		QueryNames.remove(index);
		TableNames.remove(index);
		AttributeNames.remove(index);
	}
		
	
	private static void clearQuestionMarkAttr(int addCnt) {
		int i;
		for(i = questionList.size() - addCnt; i < questionList.size(); i++){
			if(questionList.get(i))
				continue;
			if(questionTableSet.contains(TableNames.get(i))){
				parsingRemove(i);
				i--;
			}
		}
	}
	
	public static void computePartition(String Query,int counter)	// SELECT, DELETE
	{
		/* zorn start */
		int j;
		ArrayList<Integer> storingDotPosition = new ArrayList<Integer>();
		ArrayList<Integer> storingQuestionMarkPosition = new ArrayList<Integer>();
		ArrayList<Integer> storingspacePosition=new ArrayList<Integer>();

		System.out.println("*******************Proceedure Partition Analyzation*******************************");
		System.out.println("Input Query::-"+Query);
		//Select u.* from User u, Friendship f where f.inviteeId=? and f.ReqId=u.userid and    f.status=confirmed

		String[] splits = Query.split("where");
		System.out.println("splits.size: " + splits.length);

		System.out.println("Line"+splits[0]);
		System.out.println("Line"+splits[1]);

		String[] splits1 = splits[0].split("from");

		printIndexes(splits[1], '.',storingDotPosition);
		printIndexes(splits[1], '?',storingQuestionMarkPosition);
		printIndexes(splits[0], ' ',storingspacePosition);
		
		/* zorn added */
		
		// splits[0] : "SELECT u.* from USER u, PENDFRIENDSHIP f" 
		// splits[1] : " f.InviteeID=? AND u.UserID = f.inviteeID"
		// splits1[0] : "SELECT u.*" 
		// splits1[1] : " USER u, PENDFRIENDSHIP f "
		// splits1[0] from splits1[1] where splits[1]

		// parsing query type	(SELECT, DELETE) 
		String querySplit[] = splits1[0].trim().split("\\s+");
		String queryType = querySplit[0];
		
		// parsing table	(hash map, u = user)
		tableHash = new HashMap<String, String>();
		String tableSplit[] = splits1[1].trim().replace(',', ' ').split("\\s+");
		for(j = 0; j < tableSplit.length; j += 2)
			tableHash.put(tableSplit[j+1], tableSplit[j]);
		if(j != tableSplit.length)
			System.err.println("ERROR parsing table: Check AGAIN this input: " + Query);
		
		// predicate 
		String linkEqual = splits[1].replaceAll("\\s*=\\s*", "=");
		String prediSplit[] = linkEqual.trim().split("\\s+");
		String equalSplit[] = {};
		int addCnt = 0;		// count how many new columns added
		questionTableSet.clear();
		for(j = 0; j < prediSplit.length; j++){
			if(!prediSplit[j].contains("."))
				continue;	// AND, OR, LIMIT etc.
			
			equalSplit = prediSplit[j].split("=");	// at most has 2 split
			if(equalSplit.length <= 1){
				parsingAdd(counter, false, queryType, equalSplit[0]);
				addCnt++;
				continue;
			}
			
			if(equalSplit[0].contains("?")){
				parsingAdd(counter, true, queryType, equalSplit[1]);
				addCnt++;
			}
			else if(equalSplit[1].contains("?")){
				parsingAdd(counter, true, queryType, equalSplit[0]);
				addCnt++;
			}
			else{
				parsingAdd(counter, false, queryType, equalSplit[0]);
				parsingAdd(counter, false, queryType, equalSplit[1]);
				addCnt += 2;
			}
			clearQuestionMarkAttr(addCnt);	// remove columns with the same table name
		}
		
		/* zorn hide
		System.out.println("****************Dot Position Stored Display Logic*************");
		for(int i=0;i<storingDotPosition.size();i++)
		{
			System.out.println("Values"+storingDotPosition.get(i));
		}
		System.out.println("****************Dot Position Stored Display Logic*************");


		System.out.println("****************Question Mark Position Stored Display Logic*************");
		for(int i=0;i<storingQuestionMarkPosition.size();i++)
		{
			System.out.println("Values"+storingQuestionMarkPosition.get(i));
		}
		System.out.println("****************Question Mark Position Stored Display Logic*************");

		System.out.println("****************Printing values between . and ? Putting Attribute Names and Table Names*************");
		String result="";
		String QueryName="";
		int storeIndex=0;

		for(int i=0;i<storingQuestionMarkPosition.size();i++)
		{
			//start=
			result=splits[1].substring(Integer.parseInt(storingDotPosition.get(i).toString())+1,Integer.parseInt(storingQuestionMarkPosition.get(i).toString())-1);
			AttributeNames.add(result);
			System.out.println("Attribute "+AttributeNames.get(i).toString());
			result=splits[1].substring(Integer.parseInt(storingDotPosition.get(i).toString())-1,Integer.parseInt(storingDotPosition.get(i).toString()));
			System.out.println("Result "+result);
			//store=result[0];
			System.out.println("spilits[1]"+splits1[1]);
			printIndexes(splits1[1],result.charAt(0),storingTableposition);
			System.out.println("Table Position Values:-"+storingTableposition.get(i));
			for(int k=Integer.parseInt(storingTableposition.get(i).toString())-2;k>=0;k--)
			{
				if(splits1[1].charAt(k)==' ')
				{
					storeIndex=k+1;
					break;
				}
			}

			QueryName=splits[0].substring(0,Integer.parseInt(storingspacePosition.get(0).toString()));
			QueryNames.add(QueryName);
			System.out.println("Query"+QueryNames.get(i).toString());
			TableNames.add(splits1[1].substring(storeIndex,Integer.parseInt(storingTableposition.get(i).toString())-1));
			System.out.println("Table"+TableNames.get(i).toString());
			ActionNames.add(workloadExtractor[counter].getAction());
			FrequencyNames.add(workloadExtractor[counter].getFrequency());


		}
		for(int i=0;i<AttributeNames.size();i++)
		{
			System.out.println("splits1"+splits1[1]);
			System.out.println("Result:-"+result);
			System.out.println("Attribute Name Values:-"+AttributeNames.get(i));
			System.out.println("Table Name Values:-"+TableNames.get(i));
		}
		for(int i=0;i<QueryNames.size();i++)
		{
			//System.out.println("Result:-"+result);
			System.out.println("Query Name Values:-"+QueryNames.get(i));
		}

		System.out.println("****************Printing values between . and ? Putting Attribute Names and Table Names*************");



		System.out.println("*******************Proceedure Partition Analyzation*******************************");
		*/
	}


	public static void  actuallogic()
	{
		setSchema();


		for(int x=0;x<(schemaExtractor.length);x++)
		{
			System.out.println("Table : " + schemaExtractor[x].getTableName()); 
			System.out.println("Attribute : " + schemaExtractor[x].getAttributeNames());
			LinkedList<String> everyAttribute = new LinkedList<String>();

			String[] splits = schemaExtractor[x].getAttributeNames().split(",");
			for(int i=0;i<splits.length;i++)
			{
				everyAttribute.add(splits[i]);
			}
			schemaExtractor[x].setEveryAttribute(everyAttribute);
			System.out.println("Final Results");
			System.out.println("Every Attribute"+schemaExtractor[x].getEveryAttribute());

		}

		setProceedure();

		for(int y=0;y<(proceedureExtractor.length);y++)
		{
			System.out.println("Queries : " + proceedureExtractor[y].getQuery()); 
		}

		setworkload();

		for(int z=0;z<(workloadExtractor.length);z++)
		{
			System.out.println("Action : " + workloadExtractor[z].getAction()); 
			System.out.println("Frequency : " + workloadExtractor[z].getFrequency()); 

		}
		for(int t=0;t<proceedureExtractor.length;t++)
		{

			String[] splits = proceedureExtractor[t].getQuery().split(" "); 
			String[] splits1111 = proceedureExtractor[t].getQuery().split(" ");
			System.out.println("Arpit 11111111111Splits"+splits[0]);
			if(splits[0].contentEquals("INSERT"))
			{
				String result=null;
				System.out.println("DEKDKLKDEKEDKKDKCKKDKEKFLLEDLDLDE Insert Query not Working");
				ArrayList<Integer> storingBracket1Position = new ArrayList<Integer>();
				ArrayList<Integer> storingBracket2Position = new ArrayList<Integer>();

				printIndexes(proceedureExtractor[t].getQuery().toString(),'(',storingBracket1Position);
				System.out.println("First Barcket Index:-"+storingBracket1Position.get(0).toString());

				printIndexes(proceedureExtractor[t].getQuery().toString(), ')',storingBracket2Position);
				System.out.println("Second Barcket Index:-"+storingBracket2Position.get(0).toString());
				result= proceedureExtractor[t].getQuery().substring(Integer.parseInt(storingBracket1Position.get(0).toString())+1 ,Integer.parseInt(storingBracket2Position.get(0).toString()) );
				System.out.println("Result between 2 Brackets"+result);
				System.out.println("Final Atrributes of Insert Statement");
				String[] splits1 = result.split(",");
				System.out.println("splits.size: " + splits1.length);
				for(int i=0;i<splits1.length;i++)
				{
					/* zorn hide
					AttributeNames.add(splits1[i]);
					QueryNames.add("Insert");
					TableNames.add(splits1111[2]);
					System.out.println("number"+splits1[i]);
					ActionNames.add(workloadExtractor[t].getAction());
					FrequencyNames.add(workloadExtractor[t].getFrequency());
					*/
					parsingAdd(t, true, "Insert", splits1111[2], splits1[i]);
				}

				System.out.println("Table Name corresponding to Insert Statement");
				System.out.println("Query  Name corresponding to Insert Statement");


			}
			else if(splits[0].contentEquals("UPDATE"))
			{

				////////////////////////////
				ArrayList<Integer> storingDotPosition = new ArrayList<Integer>();
				ArrayList<Integer> storingQuestionMarkPosition = new ArrayList<Integer>();
			


				String[] split1 = proceedureExtractor[t].getQuery().toString().split("where");
				System.out.println("splits.size: " + split1.length);

				System.out.println("Line"+split1[0]);
				System.out.println("Line"+split1[1]);

				//tring[] splits1 = splits[0].split("from");

				printIndexes(split1[1], '.',storingDotPosition);
				printIndexes(split1[1], '?',storingQuestionMarkPosition);
				//printIndexes(splits[0], ' ',storingspacePosition);




				System.out.println("****************Dot Position Stored Display Logic*************");
				for(int i=0;i<storingDotPosition.size();i++)
				{
					System.out.println("Values"+storingDotPosition.get(i));
				}
				System.out.println("****************Dot Position Stored Display Logic*************");


				System.out.println("****************Question Mark Position Stored Display Logic*************");
				for(int i=0;i<storingQuestionMarkPosition.size();i++)
				{
					System.out.println("Values"+storingQuestionMarkPosition.get(i));
				}
				System.out.println("****************Question Mark Position Stored Display Logic*************");

				System.out.println("****************Printing values between . and ? Putting Attribute Names and Table Names*************");
				String result="";
			
				System.out.println("storingQuestionMarkPosition"+storingQuestionMarkPosition.size());

				for(int i=0;i<storingQuestionMarkPosition.size();i++)
				{
					/* zorn hides
					//start=
					result=split1[1].substring(Integer.parseInt(storingDotPosition.get(i).toString())+1,Integer.parseInt(storingQuestionMarkPosition.get(i).toString())-1);
					AttributeNames.add(result);
					String[] splits11111 = proceedureExtractor[t].getQuery().split(" ");
					QueryNames.add("UPDATE");
					TableNames.add(splits11111[1]);
					//AttributeNames.add("arpit");
					ActionNames.add(workloadExtractor[t].getAction());
					FrequencyNames.add(workloadExtractor[t].getFrequency());
					*/
					result=split1[1].substring(Integer.parseInt(storingDotPosition.get(i).toString())+1,Integer.parseInt(storingQuestionMarkPosition.get(i).toString())-1);
					String[] splits11111 = proceedureExtractor[t].getQuery().split(" ");
					parsingAdd(t, true, "UPDATE", splits11111[1], result);


				}






				////////////////////////////
				System.out.println("UPDATE STATEMENT PENDING");

			}
			else
			{
				computePartition(proceedureExtractor[t].getQuery(),t);
			}
		}

		System.out.println("Final Output");

		for(int i=0;i<AttributeNames.size();i++)
		{

			//System.out.println("splits1"+splits1[1]);
			//System.out.println("Result:-"+result);
			System.out.println("Records"+i);
			System.out.println("Attribute Name Values:-"+AttributeNames.get(i));
			System.out.println("Table Name Values:-"+TableNames.get(i));
			System.out.println("Query Name Values:-"+QueryNames.get(i));
			System.out.println("Action Name Values:-"+ActionNames.get(i));
			System.out.println("Frequency Name Values:-"+FrequencyNames.get(i));
			//System.out.println("Table Name Values:-"+TableNames.get(i));
		}

		for(int i=0;i<TableNames.size();i++)
		{
			//System.out.println("splits1"+splits1[1]);
			//System.out.println("Result:-"+result);
			//System.out.println("Attribute Name Values:-"+AttributeNames.get(i));
			//System.out.println("Table Name Values:-"+TableNames.get(i));
		}
		for(int i=0;i<QueryNames.size();i++)
		{
			//System.out.println("Result:-"+result);

		}
		System.out.println("Final Output");


	}


	public static void main (String argv []){
		actuallogic();
		return;
	}
}
