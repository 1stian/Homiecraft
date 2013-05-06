package pro.homiecraft.hc;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Logger;


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
	pro.homiecraft.hc.MySql MySql = new pro.homiecraft.hc.MySql(sqlHost, sqlPort, sqlDb, sqlUser, sqlPw);
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

    public void email(String email, String pw){
        Properties props = new Properties();
        String from = "admin@homiecraft.pro";
        String host = "localhost";
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", host);
        Session session = Session.getDefaultInstance(props);
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Password for HomieCraft");
            message.setText("Here is your loing password for HomieCraft.pro: " + pw);
            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }
}
