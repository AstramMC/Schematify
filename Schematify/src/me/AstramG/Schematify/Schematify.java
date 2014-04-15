package me.AstramG.Schematify;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.data.DataException;

public class Schematify extends JavaPlugin {
	
	WorldEditPlugin wep;
	
	@Override
	public void onEnable() {
		Plugin p = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
		if (p != null) {
			if (p instanceof WorldEditPlugin) {
				wep = (WorldEditPlugin) p;
			}
		} else {
			System.out.println(ChatColor.RED + "WorldEdit plugin not found!");
			Bukkit.getPluginManager().disablePlugin(this);
		}
		System.out.println(ChatColor.GREEN + "Everything is up and running...");
	}
	
	@SuppressWarnings("deprecation")
	private void loadArea(World world, File file, Vector vector) throws DataException, IOException, MaxChangedBlocksException{
	    EditSession es = new EditSession(new BukkitWorld(world), 999999999);
	    CuboidClipboard cc = CuboidClipboard.loadSchematic(file);
	    cc.paste(es, vector, false);
	}
	
	public File download(String urls) {
		URL url; //represents the location of the file we want to dl.
        URLConnection con;  // represents a connection to the url we want to dl.
        DataInputStream dis;  // input stream that will read data from the file.
        FileOutputStream fos; //used to write data from inut stream to file.
        byte[] fileData;  //byte aray used to hold data from downloaded file.
        try {
        	if (urls.contains("planetminecraft")) {
        		url = new URL(urls + "/download/schematic/");
        	} else {
        		url = new URL(urls);
        	}
            con = url.openConnection(); // open the url connection.
            dis = new DataInputStream(con.getInputStream()); // get a data stream from the url connection.
            fileData = new byte[con.getContentLength()]; // determine how many byes the file size is and make array big enough to hold the data
            for (int x = 0; x < fileData.length; x++) { // fill byte array with bytes from the data input stream
                fileData[x] = dis.readByte();
            }
            dis.close(); // close the data input stream
            fos = new FileOutputStream(new File("tempschematic.schematic"));  //create an object representing the file we want to save
            fos.write(fileData);  // write out the file we want to save.
            fos.close(); // close the output stream writer
            return new File("tempschematic.schematic");
        }
        catch(MalformedURLException m) {
            System.out.println(m);
        }
        catch(IOException io) {
            System.out.println(io);
        }
        return null;
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player))
			return true;
		Player player = (Player) sender;
		if (label.equalsIgnoreCase("schem")) {
			if (!(player.hasPermission("schematify.use"))) return true;
			if (args.length == 1) {
			    File file = download(args[0]);
			    org.bukkit.util.Vector v = player.getLocation().toVector();
			    try {
					loadArea(player.getWorld(), file, new Vector((double) v.getBlockX(), (double) v.getBlockY(), (double) v.getBlockZ()));
				} catch (MaxChangedBlocksException e) {
					player.sendMessage(e.getMessage());
				} catch (DataException e) {
					player.sendMessage(ChatColor.RED + "Error downloading schematic: " + e.getMessage());
				} catch (IOException e) {
					player.sendMessage(ChatColor.RED + "Error downloading schematic: " + e.getMessage());
				}
			}
		}
		return true;
	}
	
}
