package programStructure;

import java.util.ArrayList;

public class ShareResource {

	private String name;
	private int nRead;
	private int nWrite;
	private ArrayList<String> readTasks;
	private ArrayList<String> writeTasks;
	
	public ShareResource(String name) {
		this.name = name;
		this.nRead = 0;
		this.nWrite = 0;
		this.readTasks = new ArrayList<String>();
		this.writeTasks = new ArrayList<String>();
	}
	
	public ShareResource(ShareResource sr) {
		this.name = sr.name;
		this.nRead = sr.nRead;
		this.nWrite = sr.nWrite;
		this.readTasks = new ArrayList<String>(sr.readTasks);
		this.writeTasks = new ArrayList<String>(sr.writeTasks);
		
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getnRead() {
		return this.nRead;
	}
	
	public int getnWrite() {
		return this.nWrite;
	}
	
	public void incRead() {
		this.nRead++;
	}
	
	public void incWrite() {
		this.nWrite++;
	}
	
	public void decRead() {
		this.nRead--;
	}
	
	public void decWrite() {
		this.nWrite--;
	}
	
	public void addReadTask(String name) {
		this.readTasks.add(name);
	}
	
	public void delReadTask(String name) {
		this.readTasks.remove(name);
	}
	
	public void addWriteTask(String name) {
		this.writeTasks.add(name);
	}
	
	public void delWriteTask(String name) {
		this.writeTasks.remove(name);
	}
	
	
	
}
