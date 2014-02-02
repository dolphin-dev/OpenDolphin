/*
 * MasterUpdateDataMapping.java
 *
 * Created on 2003/02/03
 *
 * Last updated on 2003/03/06
 *
 *
 */

package mirrorI.dolphin.server;

/**
 *
 * This class stores master database structure details for MasterUpdateRemote/Local classes <br>
 *
 * @author  Prashanth Kumar, Mirror-I Corp
 *
 */

public final  class MasterUpdateDataMapping {

	public MasterUpdateDataMapping() {
	}
	//List of master tables
	//Note:On Any addition to this array, there is a necessity to add corresponding info in 'primaryKey' array, add table
	//        structure as below and getDbStructure()
	public final static String[] masterTable      		={ "medicine_hierarchy", "medicine", "treatment_hierarchy", "treatment",
																			"tool_material_hierarchy", "tool_material", "disease_hierarchy","disease",
																			"administration", "tbl_admin_comment", "radiology_method"};
	//Master table's primary key
	public final static String[] primaryKey				={ "orderClassCode,hierarchyCode1,hierarchyCode2,hierarchyCode3,name",
	 																	   "code",
	 																	   "orderClassCode,hierarchyCode1,hierarchyCode2,hierarchyCode3,name",
																		   "code",
																		   "hierarchyCode1,hierarchyCode2,hierarchyCode3,name",
																		   "code",
																		   "hierarchyCode1,hierarchyCode2,hierarchyCode3,name",
																		   "code",
																		   "hierarchyCode1,hierarchyCode2,adminName,code,claimClassCode,numberCode,displayName",
																		   "adminComment",
																		   "hierarchyCode1,hierarchyCode2,name"};

	//Individual master table structure - start
	public final static String[] medicine_hierarchy 	={ "orderClassCode", "hierarchyCode1", "hierarchyCode2","hierarchyCode3",
																		   "name"};

	public final static String[] medicine           		= { "code","name","kana","unitCode","unit","costFlag","cost","zaikeiCode",
																			"changeDate","disUseDate","yakkaCode","kohyoBango","keikaSochi","orderClassCode",
																			"hierarchyCode","freqFlag"};

	public final static String[] treatment_hierarchy 	={ "orderClassCode", "hierarchyCode1", "hierarchyCode2","hierarchyCode3",
																		   "name"};

	public final static String[] treatment 				={ "code", "name", "kana", "unitCode", "unit", "costFlag", "cost", "inOutFlag", "oldFlag",
																		   "claimClassCode", "hospitalClinicFlag", "testFlag", "testGroupFlag",
																		   "claimClassCodeInHospital", "codeTableNumber", "changeDate",
																		   "disUseDate","sho","bu", "kubun", "edaban", "kouban", "orderClassCode",
																		   "hierarchyCode", "freqFlag"};

	public final static String[] tool_material_hierarchy={ "hierarchyCode1", "hierarchyCode2","hierarchyCode3",
																		   "name"};

	public final static String[] tool_material 				={ "code", "name", "kana", "unitCode", "unit", "costFlag", "cost",
																		   "hierarchyCode", "freqFlag"};

	public final static String[] disease_hierarchy 		={ "hierarchyCode1", "hierarchyCode2","hierarchyCode3",
																		   "name"};

	public final static String[] disease 					={ "code", "name", "kana", "byoumeiKanri", "saitakuKubun", "byoumeiKoukan",
																		   "icdTen", "collectionDate", "changeDate", "disUseDate", "hierarchyCode",
																		   "freqFlag"};

	public final static String[] administration 			={ "hierarchyCode1", "hierarchyCode2", "adminName", "code","claimClassCode",
																			"numberCode", "displayName"};

	public final static String[] tbl_admin_comment 		={ "adminComment"};

	public final static String[] radiology_method 		={ "hierarchyCode1", "hierarchyCode2","name"};

	public final static String[] fileTransInfo      		={ "FileName", "FilePath", "ChangeType", "FileLength"};

	//Individual master table structure - end

	/**
	 *
	 * getDbStructure(), returns requested master table's structure array<br>
	 *
	 * This method is called from MasterUpdateLocalChangedData.synchronizeLocalDB() &<br>
	 * MasterUpdateRemoteChangedData.getChangedData() &<br>
	 *
	 */
	public static String[] getDbStructure(int masterTableIndex) {

		String [] dbStructure = null;

		//Check corresponding master table, if index of master table is out of range just return null	array
		if ( (masterTableIndex <0) || (masterTableIndex > (masterTable.length-1)) ) {
			dbStructure = null;
			return dbStructure;
		}

		//Check corresponding master table, if index of master table is with in range
		if (masterTable[masterTableIndex].equalsIgnoreCase("medicine_hierarchy")) {
			dbStructure = medicine_hierarchy;
		}
		else if (masterTable[masterTableIndex].equalsIgnoreCase("medicine")) {
			dbStructure = medicine;
		}
		else if (masterTable[masterTableIndex].equalsIgnoreCase("treatment_hierarchy")) {
			dbStructure = treatment_hierarchy;
		}
		else if (masterTable[masterTableIndex].equalsIgnoreCase("treatment")) {
			dbStructure = treatment;
		}
		else if (masterTable[masterTableIndex].equalsIgnoreCase("tool_material_hierarchy")) {
			dbStructure = tool_material_hierarchy;
		}
		else if (masterTable[masterTableIndex].equalsIgnoreCase("tool_material")) {
			dbStructure = tool_material;
		}
		else if (masterTable[masterTableIndex].equalsIgnoreCase("disease_hierarchy")) {
			dbStructure = disease_hierarchy;
		}
		else if (masterTable[masterTableIndex].equalsIgnoreCase("disease")) {
			dbStructure = disease;
		}
		else if (masterTable[masterTableIndex].equalsIgnoreCase("administration")) {
			dbStructure = administration;
		}
		else if (masterTable[masterTableIndex].equalsIgnoreCase("tbl_admin_comment")) {
			dbStructure = tbl_admin_comment;
		}
		else if (masterTable[masterTableIndex].equalsIgnoreCase("radiology_method")) {
			dbStructure = radiology_method;
		}
		//If none of the table matches with requiredMasterTableName
		else{
			dbStructure = null;
		}

		return dbStructure;
	}


	public static boolean primarKeyExist(String masterTableName) {
		boolean primarKeyExist=false;

		try{
			if (masterTableName.equalsIgnoreCase("medicine_hierarchy") ||
				masterTableName.equalsIgnoreCase("treatment_hierarchy") ||
				masterTableName.equalsIgnoreCase("tool_material_hierarchy")||
				masterTableName.equalsIgnoreCase("disease_hierarchy") ||
				masterTableName.equalsIgnoreCase("administration") ||
				masterTableName.equalsIgnoreCase("tbl_admin_comment") ||
				masterTableName.equalsIgnoreCase("radiology_method") ) {

				primarKeyExist = false;
			}
			else if (masterTableName.equalsIgnoreCase("medicine") ||
					   masterTableName.equalsIgnoreCase("treatment") ||
					   masterTableName.equalsIgnoreCase("tool_material") ||
					   masterTableName.equalsIgnoreCase("disease")  ) {
				primarKeyExist = true;
			}
			//If none of the table matches with requiredMasterTableName
			else{
				primarKeyExist = false;
			}
		}
		catch(Exception e){
			System.out.println("Exception while searching for primary key");
		}
		return primarKeyExist;
	}

	/**
	 *
	 * getPrimaryKeyConditionStmt(), checks whether primary key is a combined key<br>
	 * if so, then make SQL condition statement and return<br>
	 *
	 * This method is called from MasterUpdateRemote/Local classes<br>
	 *
	 * Note:Supports only for text based primary key (as it add " ' "  for value)
	 *
	 */
	public static String getPrimaryKeyConditionStmt(String primaryKey, String primaryValue) {
		StringBuffer primaryKeyConditionBuffer=null;
		String primaryKeyConditionStmt=null;
		int primaryKeyCheck=0;

		if ( (primaryKey != null) && (primaryValue != null) ){
			try {
				primaryKeyCheck =primaryKey.indexOf(",");
				//If primary key is single key
				if( primaryKeyCheck < 1){
					//check for NULL
					if(primaryValue.equals("NULL")){
						primaryKeyConditionStmt = primaryKey + " is NULL";
					}
					else{
						primaryKeyConditionStmt = primaryKey + "='"+ primaryValue +"'";
					}
				}
				//If primary key is a combined key
				else if (primaryKeyCheck > 0){
					int combinedPrimaryColumnLength=0;
					int combinedPrimaryValueLength=0;
					primaryKeyConditionBuffer = new StringBuffer();
					String primaryValueTemp=null;

					//parse till last item( , )
					while( (primaryKeyCheck >0) ){
						//Get Column name and add into buffer
						primaryKeyConditionBuffer.append(primaryKey.substring(0,primaryKeyCheck));
						combinedPrimaryColumnLength = primaryKey.length();
						primaryKey=primaryKey.substring( (primaryKeyCheck+1),combinedPrimaryColumnLength);

						//Get value and add into buffer
						primaryKeyCheck=0;
						primaryKeyCheck =primaryValue.indexOf(",");

						primaryValueTemp=primaryValue.substring(0,primaryKeyCheck);
						if(primaryValueTemp.equals("NULL") ){
							primaryKeyConditionBuffer.append(" is NULL and ");
						}
						else{
							primaryKeyConditionBuffer.append("='"+primaryValueTemp +"' and ");
						}
						combinedPrimaryValueLength = primaryValue.length();
						primaryValue=primaryValue.substring( (primaryKeyCheck+1),combinedPrimaryValueLength);

						//recalculate the index
						primaryKeyCheck=primaryKey.indexOf(",");
					}
					//add last column name and value

					if( primaryValue.equals("NULL") ){
						primaryKeyConditionBuffer.append(primaryKey + " is NULL");
					}
					else {
						primaryKeyConditionBuffer.append(primaryKey + "='" +primaryValue +"'");
					}

				primaryKeyConditionStmt=primaryKeyConditionBuffer.toString();
				}
			}
			catch (Exception e){
				primaryKeyConditionStmt=null;
			}
		}
		return primaryKeyConditionStmt;
	}

}//Class end

