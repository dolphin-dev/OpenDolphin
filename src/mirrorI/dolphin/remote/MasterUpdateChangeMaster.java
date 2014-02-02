/*
 * MasterUpdateChangeMaster.java
 *
 * Created on 2003/03/02
 *
 * Last updated on 2003/03/06
 *
 *
 */
 package mirrorI.dolphin.remote;

/**
 *
 * This class stores ChangeMaster table records <br>
 *
 * @author  Prashanth Kumar, Mirror-I Corp
 *
 */
public class MasterUpdateChangeMaster{

	//Table name or file path
	private String recordLocation=null;
	//Primary key  or file name
	private String recordRefNew=null;
	//Old record reference
	private String recordRefOld=null;
	//For DB: DI/DD/DU (Insert/Delete/Update), For File:FI/FD/FU (Copy, Delete,Overwrite)
	private String changetype=null;
	//For DB: D(Data), For File:F(File)
	private String type = null;

	//Creating new ChangeMaster;
	public MasterUpdateChangeMaster() {
	}

	public void setRecordLocation(String recordLocation){
		this.recordLocation=recordLocation;
	}
	public String getRecordLocation(){
		return this.recordLocation;
	}

	public void setRecordRefNew(String recordRefNew){
		this.recordRefNew=recordRefNew;
	}
	public String getRecordRefNew(){
		return this.recordRefNew;
	}

	public void setRecordRefOld(String recordRefOld){
		this.recordRefOld=recordRefOld;
	}
	public String getRecordRefOld(){
		return this.recordRefOld;
	}

	public void setChangetype(String changetype){
		this.changetype=changetype;
	}
	public String getChangetype(){
		return this.changetype;
	}

	public void setType(String type){
		this.type=type;
	}
	public String getType(){
		return this.type;
	}
}

