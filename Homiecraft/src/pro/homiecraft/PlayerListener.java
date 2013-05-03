package pro.homiecraft;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * User: Stian
 * Date: 22.04.13
 * Time: 13:17
 */
public class PlayerListener implements Runnable {

    static ArrayList<String> rewards = new ArrayList<String>();
    static ArrayList<String> ItemStackList = new ArrayList<String>();
    static ArrayList<String> ifWorld = new ArrayList<String>();
    static HashMap<ItemStack, Integer> itemReward = new HashMap<ItemStack, Integer>();

    public PlayerListener(Homiecraft plugin){
        plugin = plugin;
    }

    String sqlHost = Homiecraft.pluginST.getConfig().getString("HomieCraft.mysql.settings.host");
    String sqlPort = Homiecraft.pluginST.getConfig().getString("HomieCraft.mysql.settings.port");
    String sqlDb = Homiecraft.pluginST.getConfig().getString("HomieCraft.mysql.settings.db");
    String sqlUser = Homiecraft.pluginST.getConfig().getString("HomieCraft.mysql.settings.user");
    String sqlPw = Homiecraft.pluginST.getConfig().getString("HomieCraft.mysql.settings.pw");

    public void run(){
         for (Player player : Homiecraft.pluginST.getServer().getOnlinePlayers()){
            try{
                String pName = player.getName();

                pro.homiecraft.MySql MySql = new pro.homiecraft.MySql(sqlHost, sqlPort, sqlDb, sqlUser, sqlPw);
                Connection c = null;
                c = MySql.open();
                Statement statement = c.createStatement();
                ResultSet res = statement.executeQuery("SELECT * FROM users WHERE minecraft = '" + pName + "';");
                res.next();

                if (res.getRow() == 0){
                    player.sendMessage("You're not registered at HomieCraft... No Reward for you! :D");
                }else{
                    if (res.getInt("reward") == 0){
                        Inventory pi = player.getEnderChest();

                        int rewardCount = rewards.size();

                        for(String is : ItemStackList){
                            ItemStack sIS = new ItemStack(Material.getMaterial(is));
                            if(pi.contains(sIS)){
                                if(!(pi.contains(sIS, 64))){
                                    int amount = itemReward.get(sIS);
                                    pi.addItem(new ItemStack(Material.getMaterial(is), amount));

                                    String query = "UPDATE users SET reward='1' WHERE `minecraft` = '" + pName + "';";
                                    player.sendMessage("pi.contains");
                                    try {
                                        statement.executeUpdate(query);
                                    } catch (SQLException ee) {
                                        // TODO Auto-generated catch block
                                        ee.printStackTrace();
                                    }

                                }else if(!(pi.firstEmpty() == -1)){
                                    int amount = itemReward.get(sIS);
                                    pi.addItem(new ItemStack(Material.getMaterial(is), amount));

                                    String query = "UPDATE users SET reward='1' WHERE `minecraft` = '" + pName + "';";

                                    player.sendMessage("pi.contains else pi.firstempty");
                                    try {
                                        statement.executeUpdate(query);
                                    } catch (SQLException ee) {
                                        // TODO Auto-generated catch block
                                        ee.printStackTrace();
                                    }

                                }else{
                                    player.sendMessage("You're registerd at HomieCraft. But your EnderChest does not have any slots free");
                                    player.sendMessage("Clear some slots(" + rewardCount + " slots) to retrieve your reward in the next check(3min)!");
                                    break;
                                }
                            }else if(!(pi.firstEmpty() == -1)){
                                int amount = itemReward.get(sIS);
                                pi.addItem(new ItemStack(Material.getMaterial(is), amount));

                                String query = "UPDATE users SET reward='1' WHERE `minecraft` = '" + pName + "';";

                                player.sendMessage("pi.firstEmpty");

                                try {
                                    statement.executeUpdate(query);
                                } catch (SQLException ee) {
                                    // TODO Auto-generated catch block
                                    ee.printStackTrace();
                                }
                            }else{
                                player.sendMessage("You're registerd at HomieCraft. But your EnderChest does not have any slots free");
                                player.sendMessage("Clear some slots(" + rewardCount + " slots) to retrieve your reward in the next check(3min)!");
                                break;
                            }
                        }

                        sendTheMessage(player.getName());
                    }
                }
            }catch (SQLException myR){
                 myR.printStackTrace();
             }
        }
    }

    public void sendTheMessage(String pName){
        try{
            pro.homiecraft.MySql MySql = new pro.homiecraft.MySql(sqlHost, sqlPort, sqlDb, sqlUser, sqlPw);
            Connection c = null;
            c = MySql.open();
            Statement statement = c.createStatement();
            ResultSet res = statement.executeQuery("SELECT * FROM users WHERE minecraft = '" + pName + "';");
            res.next();

            if (res.getInt("reward") == 0){

            }else{
                Player player = Homiecraft.pluginST.getServer().getPlayer(pName);

                player.sendMessage("You have been rewarded some items for being registered at HomieCraft!");
                player.sendMessage("Your reward is located in an EnderChest at spawn!");
            }
        }catch (SQLException myR){
            myR.printStackTrace();
        }
    }
}
