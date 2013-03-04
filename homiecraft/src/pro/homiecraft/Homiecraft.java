package pro.homiecraft;

import java.sql.Connection;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

public class Homiecraft extends JavaPlugin{
	public static Homiecraft pluginST;
	public Homiecraft plugin;
	public Logger log = Logger.getLogger("Minecraft");
	
	//MySql
	String sqlHost = null;
	String sqlPort = null;
	String sqlDb = null;
	String sqlUser = null;
	String sqlPw= null;
	pro.homiecraft.MySql MySql = new pro.homiecraft.MySql(sqlHost, sqlPort, sqlDb, sqlUser, sqlPw);
	public Connection c = null;
	
	public void onDisable(){
		
	}
	
	public void onEnable(){
		pluginST = this;
		
		getCommands();
		loadConfiguration();
		setupMySql();
	}
	
	public void getCommands(){
		this.getCommand("homiecraft").setExecutor(new Commands());
	}
	
	public void loadConfiguration() {
		if(!getDataFolder().exists()){
			getDataFolder().mkdir();
		}		
		
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
		this.reloadConfig();
	}
	
	public void setupMySql(){
		sqlHost = this.getConfig().getString("HomieCraft.mysql.settings.host");
		sqlPort = this.getConfig().getString("HomieCraft.mysql.settings.port");
		sqlDb = this.getConfig().getString("HomieCraft.mysql.settings.db");
		sqlUser = this.getConfig().getString("HomieCraft.mysql.settings.user");
		sqlPw = this.getConfig().getString("HomieCraft.mysql.settings.pw");
		//c = MySql.open();
	}
}
