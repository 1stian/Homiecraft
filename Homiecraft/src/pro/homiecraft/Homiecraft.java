package pro.homiecraft;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
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

    @Override
	public void onEnable(){
		pluginST = this;
        plugin = this;
		
		getCommands();
		loadConfiguration();
		setupMySql();
        loadingAllItNeeds();

        Long startDelay = this.getConfig().getLong("HomieCraft.settings.start-delay");
        Long repeatAfter = this.getConfig().getLong("HomieCraft.settings.repeat");

        getServer().getScheduler().scheduleSyncRepeatingTask(this.plugin, new PlayerListener(this), startDelay, repeatAfter);

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

    public void loadingAllItNeeds(){
        ArrayList<String> rewards = (ArrayList<String>) this.getConfig().getList("HomieCraft.reward.items");

        for(String items : rewards){
            PlayerListener.rewards.add(items);
        }

        ArrayList<String> ifWorlds = (ArrayList<String>) this.getConfig().getList("HomieCraft.reward.Worlds-Not-To-Give-Rewards-In");

        for (String cWorld : ifWorlds){
            PlayerListener.ifWorld.add(cWorld);
        }

        for (String items : rewards){
            String[] split = items.split(",");
            String item = split[0].trim();
            String amount = split[1].trim();

            ItemStack is = new ItemStack(Material.getMaterial(item));

            PlayerListener.itemReward.put(is, Integer.parseInt(amount));
            PlayerListener.ItemStackList.add(item);
        }
    }
}
