package pro.homiecraft;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {
	
	public static String getHash(String str) {
	    MessageDigest digest = null;
	    byte[] input = null;

	    try {
	      digest = MessageDigest.getInstance("SHA-1");
	      digest.reset();
	      input = digest.digest(str.getBytes("UTF-8"));

	    } catch (NoSuchAlgorithmException e1) {
	      e1.printStackTrace();
	    } catch (UnsupportedEncodingException e) {
	      e.printStackTrace();
	    }
	    return convertToHex(input);
	  }
	  
	  public static String getHash(byte[] data) {
	    MessageDigest digest = null;
	    byte[] input = null;

	    try {
	      digest = MessageDigest.getInstance("SHA-1");
	      digest.reset();
	      input = digest.digest(data);

	    } catch (NoSuchAlgorithmException e1) {
	      e1.printStackTrace();
	    }
	    return convertToHex(input);
	  }
	  
	    private static String convertToHex(byte[] data) { 
	        StringBuffer buf = new StringBuffer();
	        for (int i = 0; i < data.length; i++) { 
	            int halfbyte = (data[i] >>> 4) & 0x0F;
	            int two_halfs = 0;
	            do { 
	                if ((0 <= halfbyte) && (halfbyte <= 9)) 
	                    buf.append((char) ('0' + halfbyte));
	                else 
	                    buf.append((char) ('a' + (halfbyte - 10)));
	                halfbyte = data[i] & 0x0F;
	            } while(two_halfs++ < 1);
	        } 
	        return buf.toString();
	    } 

	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(cmd.getName().equalsIgnoreCase("homiecraft")){
			Player player = (Player) sender;
			String pName = player.getName();
			
			Connection c = null;
			
			
			try {
				String sqlHost = Homiecraft.pluginST.getConfig().getString("HomieCraft.mysql.settings.host");
				String sqlPort = Homiecraft.pluginST.getConfig().getString("HomieCraft.mysql.settings.port");
				String sqlDb = Homiecraft.pluginST.getConfig().getString("HomieCraft.mysql.settings.db");
				String sqlUser = Homiecraft.pluginST.getConfig().getString("HomieCraft.mysql.settings.user");
				String sqlPw = Homiecraft.pluginST.getConfig().getString("HomieCraft.mysql.settings.pw");
				
				if(!(args.length == 0)){
					if(args[0].equalsIgnoreCase("register")){
						pro.homiecraft.MySql MySql = new pro.homiecraft.MySql(sqlHost, sqlPort, sqlDb, sqlUser, sqlPw);
						Statement statement = MySql.open().createStatement();
						ResultSet res = statement.executeQuery("SELECT * FROM users WHERE minecraft = '" + pName + "';");
						res.next();
						
						if(res.getRow() == 0){
							if (!(args[1].length() == 0) && !(args[2].length() == 0) && !(args[3].length() == 0)){
								String UserName = args[1];
								String Pw = args[2];
								String EMail = args[3];
										
								//String sha1Pw = getHash(Pw);
										
								String query = "INSERT INTO users (minecraft, username, password, email) VALUE('" + pName + "', '" + UserName + "', '" + Pw + "', '" + EMail + "')";
										
								statement.executeUpdate(query);
								player.sendMessage("[HomieCraft] You have now registered as: " + UserName + " Password: " + Pw + " Email: " + EMail);
							}else{
								player.sendMessage("[HomieCraft] Usage:");
								player.sendMessage("/homiecraft register <UserName> <Password> <email>");
							}
						}else{
							player.sendMessage("[HomieCraft] You have allready registered! You can login at http://homiecraft.pro");
						}
					}else{
						player.sendMessage("[HomieCraft] Usage:");
						player.sendMessage("/homiecraft register <UserName> <Password> <email>");
					}
				}else{
					player.sendMessage("[HomieCraft] Usage:");
					player.sendMessage("/homiecraft register <UserName> <Password> <email>");
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
}
