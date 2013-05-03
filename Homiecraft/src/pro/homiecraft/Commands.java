package pro.homiecraft;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {
	String sqlHost = Homiecraft.pluginST.getConfig().getString("HomieCraft.mysql.settings.host");
	String sqlPort = Homiecraft.pluginST.getConfig().getString("HomieCraft.mysql.settings.port");
	String sqlDb = Homiecraft.pluginST.getConfig().getString("HomieCraft.mysql.settings.db");
	String sqlUser = Homiecraft.pluginST.getConfig().getString("HomieCraft.mysql.settings.user");
	String sqlPw = Homiecraft.pluginST.getConfig().getString("HomieCraft.mysql.settings.pw");
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(cmd.getName().equalsIgnoreCase("homiecraft")){
			Player player = (Player) sender;
			String pName = player.getName();			
			
			try {				
				if(!(args.length == 0)){
					if(args[0].equalsIgnoreCase("register")){
						if(args.length == 4){
							pro.homiecraft.MySql MySql = new pro.homiecraft.MySql(sqlHost, sqlPort, sqlDb, sqlUser, sqlPw);
							Statement statement = MySql.open().createStatement();
							ResultSet res = statement.executeQuery("SELECT * FROM users WHERE minecraft = '" + pName + "';");
							res.next();
							
							if(res.getRow() == 0){
									String UserName = args[1];
									String Pw = args[2];
									String EMail = args[3];
									
									String sha1Pw = null;
									
									try {
										  MessageDigest md = MessageDigest.getInstance("SHA-1");
										  md.update(Pw.getBytes());
										         BigInteger hash = new BigInteger(1, md.digest());
										         sha1Pw = hash.toString(16);                 
										         } catch (NoSuchAlgorithmException e) { 
										   e.printStackTrace();
										  }

                                        String query = "INSERT INTO users (minecraft, username, password, email) VALUE('" + player.getName() + "', '" + UserName + "', '" + sha1Pw + "', '" + EMail + "')";

                                    try {
                                        statement.executeUpdate(query);
                                    } catch (SQLException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }

									player.sendMessage("[HomieCraft] You have now registered as: " + UserName + " Password: " + Pw + " Email: " + EMail);
							}else{
								player.sendMessage("[HomieCraft] You have already registered! You can login at http://homiecraft.pro");
							}
						}else{
							player.sendMessage("[HomieCraft] Usage:");
							player.sendMessage("/homiecraft register <UserName> <Password> <email>");
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
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public void insertAsync(final String Minecraft,final String UserName,final String password, final String EMail) throws SQLException{
		Homiecraft.pluginST.getServer().getScheduler().runTaskAsynchronously(Homiecraft.pluginST, new Runnable(){
			
			pro.homiecraft.MySql MySql = new pro.homiecraft.MySql(sqlHost, sqlPort, sqlDb, sqlUser, sqlPw);
			Statement statement = MySql.open().createStatement();
			
			@Override
			public void run() {	
				String query = "INSERT INTO users (minecraft, username, password, email) VALUE('" + Minecraft + "', '" + UserName + "', '" + password + "', '" + EMail + "')";
			
				try {
					statement.executeUpdate(query);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		});
	}
}
