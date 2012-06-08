/*
 * Copyright HomerBond005
 * 
 *  Published under CC BY-NC-ND 3.0
 *  http://creativecommons.org/licenses/by-nc-nd/3.0/
 */
package de.HomerBond005.MyEvents;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics.Metrics;
import de.HomerBond005.Permissions.PermissionsChecker;
import de.HomerBond005.Updater.Updater;

public class MyEvents extends JavaPlugin{
	private Map<String,Event> events;
	private Logger log;
	private PermissionsChecker pc;
	private Metrics metrics;
	private Texts T;
	private String local;
	private Updater updater;
	@Override
	public void onEnable(){
		File de = new File(getDataFolder()+File.separator+"de.yml");
		if(!de.exists()){
			try {
				de.createNewFile();
				FileConfiguration deConf = YamlConfiguration.loadConfiguration(de);
				deConf.load(getResource("de.yml"));
				deConf.save(de);
			} catch (IOException e) {
			} catch (InvalidConfigurationException e) {
			}
		}
		File en = new File(getDataFolder()+File.separator+"en.yml");
		if(!en.exists()){
			try {
				en.createNewFile();
				FileConfiguration enConf = YamlConfiguration.loadConfiguration(en);
				enConf.load(getResource("en.yml"));
				enConf.save(en);
			} catch (IOException e) {
			} catch (InvalidConfigurationException e) {
			}
		}
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
		log = getLogger();
		pc = new PermissionsChecker(this, true);
		events = new HashMap<String,Event>();
		if(!getConfig().isSet("Language")){
			getConfig().set("Language", "en");
			this.saveConfig();
		}
		if(!getConfig().isSet("Events")){
			getConfig().set("Events", new HashMap<String,Object>());
			this.saveConfig();
		}
		local = getConfig().getString("Language");
		T = new Texts(YamlConfiguration.loadConfiguration(new File(this.getDataFolder()+File.separator+local+".yml")));
		try{
			Set<String> evs = getConfig().getConfigurationSection("Events").getKeys(false);
			for(String ev : evs){
				ConfigurationSection read = getConfig().getConfigurationSection("Events."+ev);
				long time = read.getLong("time");
				int duration = read.getInt("duration", 10);
				Event e = new Event(read.getString("creator"), ev, time, new Location(getServer().getWorld(read.getString("locWorld")), read.getDouble("locX"), read.getDouble("locY"), read.getDouble("locZ")), read.getString("desc"), duration);
				e.addParticipants(read.getStringList("participants"));
				events.put(ev, e);
			}
		}catch(NullPointerException e){}
		getServer().getPluginManager().registerEvents(new Listener(){
			@SuppressWarnings("unused")
			@EventHandler(priority = EventPriority.HIGH)
			public void onPlayerLogin(PlayerJoinEvent event){
				handlePlayerLoginMsgs(event.getPlayer());
			}
		}, this);
		getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable(){
			@Override
			public void run(){
				checkPlayerNotifications();
			}
		}, 0L, 600L);
		try {
			metrics = new Metrics(this);
			String local;
			if(this.local == "de"){
				local = "German";
			}else if(this.local == "en"){
				local = "English";
			}else{
				local = "Other";
			}
			metrics.addCustomData(new Metrics.Plotter(local) {
				@Override
				public int getValue() {
					return 1;
				}
			});
			metrics.start();
		} catch (IOException e) {
			log.log(Level.INFO, "Error while syncing with Metrics!");
		}
		updater = new Updater(this);
		getServer().getPluginManager().registerEvents(updater, this);
		log.log(Level.INFO, "is enabled!");
	}
	@Override
	public void onDisable(){
		saveToYAML();
		log.log(Level.INFO, "is disabled!");
	}
	private void handlePlayerLoginMsgs(Player player){
		for(Event e : events.values()){
			if(e.isParticipant(player.getName()))
				if(e.isRunning()){
					if(!e.hasTeleported(player.getName())){
						player.sendMessage(ChatColor.DARK_PURPLE+""+T.notification_start+" "+ChatColor.RED+e.getName());
						player.sendMessage(ChatColor.LIGHT_PURPLE+T.tpwith+" /event "+T.teleport+" "+e.getName());
					}
				}
		}
	}
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		Player player;
		if(sender instanceof Player)
			player = (Player)sender;
		else{
			if(command.getName().equalsIgnoreCase("event"))
				log.log(Level.WARNING, "Use this command in-game!");
			return true;
		}
		if(args.length == 0){
			args = new String[]{T.help};
		}
		if(command.getName().equalsIgnoreCase("event")){
			if(args[0].equalsIgnoreCase(T.help)){
				player.sendMessage(ChatColor.YELLOW+this.getName()+": "+T.help);
				player.sendMessage(ChatColor.YELLOW+"-----------------------------------------------------");
				player.sendMessage(ChatColor.YELLOW+"/event ...");
				player.sendMessage(ChatColor.RED+"... "+T.list+ChatColor.GREEN+"    "+T.help_list);
				player.sendMessage(ChatColor.RED+"... "+T.create+ChatColor.GREEN+"    "+T.help_create);
				player.sendMessage(ChatColor.RED+"... "+T.destroy+ChatColor.GREEN+"    "+T.help_destroy);
				player.sendMessage(ChatColor.RED+"... "+T.info+ChatColor.GREEN+"    "+T.help_info);
				player.sendMessage(ChatColor.RED+"... "+T.logon+ChatColor.GREEN+"    "+T.help_logon);
				player.sendMessage(ChatColor.RED+"... "+T.logoff+ChatColor.GREEN+"    "+T.help_logoff);
				player.sendMessage(ChatColor.RED+"... "+T.teleport+ChatColor.GREEN+"    "+T.help_teleport);
				player.sendMessage(ChatColor.YELLOW+T.moreinfos+" /event <"+T.action+">");
			}else if(args[0].equalsIgnoreCase(T.create)){
				if(pc.has(player, "MyEvents.event.create"))
					if(args.length >= 6){
						if(!events.containsKey(args[1])){
							String name = args[1];
							int dayOfMonth,month,year,hourOfDay,minute,duration;
							try{
								dayOfMonth = Integer.parseInt(args[2].split("\\.")[0]);
								month = Integer.parseInt(args[2].split("\\.")[1])-1;
								year = Integer.parseInt(args[2].split("\\.")[2]);
								hourOfDay = Integer.parseInt(args[3].split(":")[0]);
								minute = Integer.parseInt(args[3].split(":")[1]);
								duration = Integer.parseInt(args[4]);
							}catch(ArrayIndexOutOfBoundsException ex){
								player.sendMessage(ChatColor.RED+T.wrongargs+" /event "+T.create+" "+args[1]+" <dd.mm.yyyy hh:mm> <"+T.duration+">"+getLastPartOfArray(args, 5));
								return true;
							}catch(NullPointerException ex){
								player.sendMessage(ChatColor.RED+T.wrongargs+" /event "+T.create+" "+args[1]+" <dd.mm.yyyy hh:mm> <"+T.duration+">"+getLastPartOfArray(args, 5));
								return true;
							}catch(NumberFormatException ex){
								player.sendMessage(ChatColor.RED+T.wrongargs+" /event "+T.create+" "+args[1]+" <dd.mm.yyyy hh:mm> <"+T.duration+">"+getLastPartOfArray(args, 5));
								return true;
							}
							events.put(name, new Event(player.getName(), name, new GregorianCalendar(year, month, dayOfMonth, hourOfDay, minute), player.getLocation(), getLastPartOfArray(args, 5), duration));
							player.sendMessage(ChatColor.GREEN+T.created);
						}else
							player.sendMessage(ChatColor.RED+T.alreadyexists);
					}else
						player.sendMessage(ChatColor.RED+T.moreargs+" /event "+T.create+" <"+T.name+"> <"+T.date+"> <"+T.duration+"> <"+T.desc+">");
				else
					pc.sendNoPermMsg(player);
			}else if(args[0].equalsIgnoreCase(T.destroy)){
				if(pc.has(player, "MyEvents.event.destroy"))
					if(args.length >= 2){
						if(events.containsKey(args[1])){
							events.remove(args[1]);
							player.sendMessage(ChatColor.GREEN+T.destroyed);
						}else
							player.sendMessage(ChatColor.RED+T.doesntexist);
					}else
						player.sendMessage(ChatColor.RED+T.moreargs+" /event "+T.destroy+" <"+T.name+">");
				else
					pc.sendNoPermMsg(player);
			}else if(args[0].equalsIgnoreCase(T.edit)){
				if(pc.has(player, "MyEvents.event.edit"))
					if(args.length >= 2){
						if(args[2].equalsIgnoreCase(T.dateEdit)){
							if(args.length >= 3){
								if(events.containsKey(args[1])){
									int dayOfMonth,month,year,hourOfDay,minute;
									try{
										dayOfMonth = Integer.parseInt(args[3].split("\\.")[0]);
										month = Integer.parseInt(args[3].split("\\.")[1]);
										year = Integer.parseInt(args[3].split("\\.")[2]);
										hourOfDay = Integer.parseInt(args[4].split(":")[0]);
										minute = Integer.parseInt(args[4].split(":")[1]);
									}catch(ArrayIndexOutOfBoundsException ex){
										player.sendMessage(ChatColor.RED+T.wrongargs+" /event "+T.edit+" "+args[1]+" "+T.dateEdit+" <dd.mm.yyyy hh:mm>");
										return true;
									}catch(NullPointerException ex){
										player.sendMessage(ChatColor.RED+T.wrongargs+" /event "+T.edit+" "+args[1]+" "+T.dateEdit+" <dd.mm.yyyy hh:mm>");
										return true;
									}catch(NumberFormatException ex){
										player.sendMessage(ChatColor.RED+T.wrongargs+" /event "+T.edit+" "+args[1]+" "+T.dateEdit+" <dd.mm.yyyy hh:mm>");
										return true;
									}
									events.get(args[1]).setTime(new GregorianCalendar(year, month, dayOfMonth, hourOfDay, minute));
									player.sendMessage(ChatColor.GREEN+T.changed);
								}else
									player.sendMessage(ChatColor.RED+T.doesntexist);
							}else
								player.sendMessage(ChatColor.RED+T.moreargs+" /event "+T.edit+" <"+T.name+"> "+T.dateEdit+" <"+T.value+">");
						}else if(args[2].equalsIgnoreCase(T.desc)){
							if(args.length >= 3)
								if(events.containsKey(args[1])){
									events.get(args[1]).setDesc(getLastPartOfArray(args, 3));
									System.out.println(getLastPartOfArray(args, 3));
									player.sendMessage(ChatColor.GREEN+T.changed);
								}else
									player.sendMessage(ChatColor.RED+T.doesntexist);
							else
								player.sendMessage(ChatColor.RED+T.moreargs+" /event "+T.edit+" <"+T.name+"> "+T.desc+" <"+T.value+">");
						}else if(args[2].equalsIgnoreCase(T.location)){
							if(events.containsKey(args[1])){
								events.get(args[1]).setLocation(player.getLocation());
								player.sendMessage(ChatColor.GREEN+T.locationset);
							}else
								player.sendMessage(ChatColor.RED+T.doesntexist);
						}else if(args[2].equalsIgnoreCase(T.duration)){
							if(args.length >= 3)
								if(events.containsKey(args[1])){
									events.get(args[1]).setDuration(Integer.parseInt(args[3]));
									player.sendMessage(ChatColor.GREEN+T.changed);
								}else
									player.sendMessage(ChatColor.RED+T.doesntexist);
							else
								player.sendMessage(ChatColor.RED+T.moreargs+" /event "+T.edit+" <"+T.name+"> "+T.duration+" <"+T.value+">");
						}
					}else
						player.sendMessage(ChatColor.RED+T.moreargs+" /event "+T.edit+" <"+T.name+"> <"+T.action+"> [<"+T.value+">]");
				else
					pc.sendNoPermMsg(player);
			}else if(args[0].equalsIgnoreCase(T.info)){
				if(pc.has(player, "MyEvents.event.info"))
					if(args.length >= 2){
						if(events.containsKey(args[1])){
							Event e = events.get(args[1]);
							player.sendMessage(ChatColor.YELLOW+T.eventinfo+ChatColor.RED+e.getName()+ChatColor.YELLOW+" "+T.by+" "+ChatColor.RED+e.getCreator());
							player.sendMessage(ChatColor.YELLOW+"-----------------------------------------------------");
							player.sendMessage(ChatColor.YELLOW+T.desc+": "+ChatColor.RED+e.getDesc());
							GregorianCalendar c = (GregorianCalendar) e.getTime().clone();
							player.sendMessage(ChatColor.YELLOW+T.date+": "+ChatColor.RED+c.get(Calendar.DAY_OF_MONTH)+"."+(c.get(Calendar.MONTH)+1)+"."+c.get(Calendar.YEAR)+" "+c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE));
							player.sendMessage(ChatColor.YELLOW+T.duration+": "+ChatColor.RED+e.getDuration()+" "+T.minutes);
							c.setTimeInMillis(c.getTimeInMillis()+e.getDuration()*1000*60);
							player.sendMessage(ChatColor.YELLOW+T.end+": "+ChatColor.RED+c.get(Calendar.DAY_OF_MONTH)+"."+(c.get(Calendar.MONTH)+1)+"."+c.get(Calendar.YEAR)+" "+c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE));player.sendMessage(ChatColor.YELLOW+T.author+": "+ChatColor.RED+e.getCreator());
							Location l = e.getLocation();
							player.sendMessage(ChatColor.YELLOW+T.location+":"+ChatColor.RED+" X="+l.getX()+" Y="+l.getY()+" Z="+l.getZ());
							if(e.isParticipant(player.getName()))
								player.sendMessage(ChatColor.YELLOW+T.status+": "+ChatColor.GREEN+T.participating);
							else
								player.sendMessage(ChatColor.YELLOW+T.status+": "+ChatColor.RED+T.notparticipating);
						}else
							player.sendMessage(ChatColor.RED+T.doesntexist);
					}else
						player.sendMessage(ChatColor.RED+T.moreargs+" /event "+T.info+" <"+T.name+">");
				else
					pc.sendNoPermMsg(player);
			}else if(args[0].equalsIgnoreCase(T.teleport)){
				if(pc.has(player, "MyEvents.event.teleport"))
					if(args.length >= 2){
						if(events.containsKey(args[1])){
							Event e = events.get(args[1]);
							if(e.isRunning())
								if(e.isParticipant(player.getName()))
									if(!e.hasTeleported(player.getName())){
										e.playerTeleported(player.getName());
										player.teleport(e.getLocation());
										player.sendMessage(ChatColor.GREEN+T.teleported);
									}else
										player.sendMessage(ChatColor.RED+T.alreadyteleported);
								else
									player.sendMessage(ChatColor.RED+T.notparticipating);
							else
								player.sendMessage(ChatColor.RED+T.notrunning);
						}else
							player.sendMessage(ChatColor.RED+T.doesntexist);
					}else
						player.sendMessage(ChatColor.RED+T.moreargs+" /event "+T.teleport+" <"+T.name+">");
				else
					pc.sendNoPermMsg(player);
			}else if(args[0].equalsIgnoreCase(T.logon)){
				if(pc.has(player, "MyEvents.event.logon"))
					if(args.length >= 2){
						if(events.containsKey(args[1])){
							Event e = events.get(args[1]);
							if(!e.isRunning()){
								if(!e.isParticipant(player.getName())){
									e.addParticipant(player.getName());
									player.sendMessage(ChatColor.GREEN+T.participating);
								}else
									player.sendMessage(ChatColor.RED+T.alreadyparticipating);
							}else
								player.sendMessage(ChatColor.RED+T.alreadyrunning);
						}else
							player.sendMessage(ChatColor.RED+T.doesntexist);
					}else
						player.sendMessage(ChatColor.RED+T.moreargs+" /event "+T.logon+" <"+T.name+">");
				else
					pc.sendNoPermMsg(player);
			}else if(args[0].equalsIgnoreCase(T.logoff)){
				if(pc.has(player, "MyEvents.event.logoff"))
					if(args.length >= 2){
						if(events.containsKey(args[1])){
							Event e = events.get(args[1]);
							if(e.isParticipant(player.getName())){
								e.removeParticipant(player.getName());
								player.sendMessage(ChatColor.GREEN+T.notparticipatinganymore);
							}else
								player.sendMessage(ChatColor.RED+T.notparticipating);
						}else
							player.sendMessage(ChatColor.RED+T.doesntexist);
					}else
						player.sendMessage(ChatColor.RED+T.moreargs+" /event "+T.logoff+" <"+T.name+">");
				else
					pc.sendNoPermMsg(player);
			}else if(args[0].equalsIgnoreCase(T.list)){
				if(pc.has(player, "MyEvents.event.list")){
					ArrayList<Event> evs = new ArrayList<Event>(events.values());
					Collections.sort(evs, new Comparator<Event>(){
						@Override
						public int compare(Event o1, Event o2){
							if(o1.getTime().getTimeInMillis() < o2.getTime().getTimeInMillis()){
								return 1;
							}else if(o1.getTime().getTimeInMillis() > o2.getTime().getTimeInMillis()){
								return -1;
							}else{
								return 0;
							}
						}
					});
					player.sendMessage(ChatColor.YELLOW+T.listheader);
					int i = 0;
					for(Event e : evs){
						player.sendMessage(ChatColor.YELLOW+""+i+": "+ChatColor.RED+e.getName()+" "+ChatColor.YELLOW+T.by+" "+ChatColor.RED+e.getCreator());
						i++;
					}
					player.sendMessage(ChatColor.YELLOW+T.moreinfos+" /event "+T.info+" <"+T.name+">");
				}else
					pc.sendNoPermMsg(player);
			}else if(args[0].equalsIgnoreCase(T.participants)){
				if(pc.has(player, "MyEvents.event.participants"))
					if(args.length >= 2){
						if(events.containsKey(args[1])){
							player.sendMessage(ChatColor.YELLOW+T.participantslist);
							player.sendMessage(ChatColor.GREEN+events.get(args[1]).getParticipants().toString());
						}else
							player.sendMessage(ChatColor.RED+T.doesntexist);
					}else
						player.sendMessage(ChatColor.RED+T.moreargs+" /event "+T.participants+" <"+T.name+">");
				else
					pc.sendNoPermMsg(player);
			}
		}
		return true;
	}
	private String getLastPartOfArray(String[] arr, int beginIndex){
		String temp = "";
		for(int i = beginIndex; i < arr.length; i++)
			temp += arr[i] + " ";
		return temp;
	}
	private void saveToYAML(){
		getConfig().set("Events", null);
		for(Entry<String, Event> l : events.entrySet()){
			Event e = l.getValue();
			getConfig().set("Events."+e.getName()+".desc", e.getDesc());
			getConfig().set("Events."+e.getName()+".creator", e.getCreator());
			getConfig().set("Events."+e.getName()+".locX", e.getLocation().getX());
			getConfig().set("Events."+e.getName()+".locY", e.getLocation().getY());
			getConfig().set("Events."+e.getName()+".locZ", e.getLocation().getZ());
			getConfig().set("Events."+e.getName()+".locWorld", e.getLocation().getWorld().getName());
			GregorianCalendar c = e.getTime();
			getConfig().set("Events."+e.getName()+".time", c.getTimeInMillis());
			getConfig().set("Events."+e.getName()+".duration", e.getDuration());
			getConfig().set("Events."+e.getName()+".participants", e.getParticipants());
		}
		saveConfig();
	}
	private void checkPlayerNotifications(){
		for(Event e : events.values()){
			if(e.isRunning()){
				notifyPlayers(e);
			}
		}
	}
	private void notifyPlayers(Event e){
		LinkedList<String> players = e.getParticipants();
		for(String player : players){
			OfflinePlayer pl = getServer().getOfflinePlayer(player);
			if(pl.isOnline()&&!e.hasTeleported(player)){
				Player plO = pl.getPlayer();
				plO.sendMessage(ChatColor.DARK_PURPLE+""+T.notification_start+" "+ChatColor.RED+e.getName());
				plO.sendMessage(ChatColor.LIGHT_PURPLE+T.tpwith+" /event "+T.teleport+" "+e.getName());
			}
		}
	}
}
