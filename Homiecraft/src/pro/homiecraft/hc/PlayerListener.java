package pro.homiecraft.hc;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pro.homiecraft.hc.Homiecraft;

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
                Inventory pi = player.getEnderChest();

                MySql MySql = new pro.homiecraft.hc.MySql(sqlHost, sqlPort, sqlDb, sqlUser, sqlPw);
                Connection c = null;
                c = MySql.open();
                Statement statement = c.createStatement();
                ResultSet res = statement.executeQuery("SELECT * FROM users WHERE minecraft = '" + pName + "';");
                res.next();

                if (res.getRow() == 0){
                    player.sendMessage("You're not registered at HomieCraft... No Reward for you! :D");
                }else{
                    if (res.getInt("reward") == 0){

                        int rewardCount = ItemStackList.size();

                        for(String is : ItemStackList){
                            ItemStack sIS = new ItemStack(Material.getMaterial(is));
                            if(pi.contains(sIS)){
                                if(!(pi.contains(sIS, 64))){
                                    addItem(sIS, is, player.getName(), player.getEnderChest());

                                }else if(!(pi.firstEmpty() == -1)){
                                    addItem(sIS, is, player.getName(), player.getEnderChest());
                                }else{
                                    player.sendMessage("You're registered at HomieCraft. But your EnderChest does not have any slots free");
                                    player.sendMessage("Clear some slots(" + rewardCount + " slots) to retrieve your reward in the next check(3min)!");
                                    break;
                                }
                            }else if(!(pi.firstEmpty() == -1)){
                                addItem(sIS, is, player.getName(), player.getEnderChest());
                            }else{
                                player.sendMessage("You're registered at HomieCraft. But your EnderChest does not have any slots free");
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

    public void addItem(ItemStack sIS, String is, String pName, Inventory pi){
        int amount = itemReward.get(sIS);
        pi.addItem(new ItemStack(Material.getMaterial(is), amount));

        execQuery(pName);
    }

    public void execQuery(String pName){
        try{
            MySql MySql = new pro.homiecraft.hc.MySql(sqlHost, sqlPort, sqlDb, sqlUser, sqlPw);
            Connection c = null;
            c = MySql.open();
            Statement statement = c.createStatement();
            ResultSet res = statement.executeQuery("SELECT * FROM users WHERE minecraft = '" + pName + "';");
            res.next();

            String query = "UPDATE users SET reward='1' WHERE `minecraft` = '" + pName + "';";

            try {
                statement.executeUpdate(query);
            } catch (SQLException ee) {
                // TODO Auto-generated catch block
                ee.printStackTrace();
            }
        }catch (SQLException myR){
            myR.printStackTrace();
        }
    }


    public void sendTheMessage(String pName){
        try{
            MySql MySql = new pro.homiecraft.hc.MySql(sqlHost, sqlPort, sqlDb, sqlUser, sqlPw);
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
