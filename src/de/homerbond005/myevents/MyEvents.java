/*
 * Copyright HomerBond005
 * 
 *  Published under CC BY-NC-ND 3.0
 *  http://creativecommons.org/licenses/by-nc-nd/3.0/
 */
package de.homerbond005.myevents;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import de.homerbond005.myevents.Metrics.Graph;
import de.homerbond005.myevents.api.EventCreatedEvent;
import de.homerbond005.myevents.api.EventDestroyedEvent;
import de.homerbond005.myevents.api.EventStartedEvent;
import de.homerbond005.myevents.api.EventStoppedEvent;
import de.homerbond005.myevents.api.PlayerLogOffEventEvent;
import de.homerbond005.myevents.api.PlayerLogOnEventEvent;
import de.homerbond005.myevents.api.PlayerTeleportEventEvent;

public class MyEvents extends JavaPlugin {
	private HashMap<String, EventStorage> events;
	private Logger log;
	private Metrics metrics;
	private Texts T;
	private String local;

	@Override
	public void onEnable() {
		log = getLogger();
		getConfig().addDefault("updateReminderEnabled", true);
		getConfig().options().copyDefaults(true);
		saveConfig();
		reloadConfig();
		events = new HashMap<String, EventStorage>();
		if (!getConfig().isSet("Language")) {
			getConfig().set("Language", "en");
			saveConfig();
		} else if (getResource(getConfig().getString("Language") + ".yml") == null) {
			getConfig().set("Language", "en");
			saveConfig();
		}
		if (!getConfig().isSet("Events")) {
			getConfig().set("Events", new HashMap<String, Object>());
			saveConfig();
		}
		local = getConfig().getString("Language");
		T = new Texts(YamlConfiguration.loadConfiguration(getResource(local + ".yml")));
		try {
			Set<String> evs = getConfig().getConfigurationSection("Events").getKeys(false);
			for (String ev : evs) {
				ConfigurationSection read = getConfig().getConfigurationSection("Events." + ev);
				long time = read.getLong("time");
				int duration = read.getInt("duration", 10);
				EventStorage e = new EventStorage(read.getString("creator"), ev, time, new Location(getServer().getWorld(read.getString("locWorld")), read.getDouble("locX"), read.getDouble("locY"), read.getDouble("locZ")), read.getString("desc"), duration);
				e.addParticipants(read.getStringList("participants"));
				events.put(ev, e);
			}
		} catch (NullPointerException e) {
		}
		getServer().getPluginManager().registerEvents(new Listener() {
			@EventHandler(priority = EventPriority.LOWEST)
			public void onPlayerLogin(PlayerJoinEvent event) {
				handlePlayerLoginMsgs(event.getPlayer());
			}
		}, this);
		getServer().getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
			@Override
			public void run() {
				checkPlayerNotifications();
			}
		}, 0L, 600L);
		try {
			metrics = new Metrics(this);
			String localString;
			if (local.equalsIgnoreCase("de")) {
				localString = "German";
			} else if (local.equalsIgnoreCase("en")) {
				localString = "English";
			} else {
				localString = "Other";
			}
			Graph langgraph = metrics.createGraph("Language of MyEvents");
			langgraph.addPlotter(new Metrics.Plotter(localString) {
				@Override
				public int getValue() {
					return 1;
				}
			});
			metrics.start();
		} catch (IOException e) {
			log.log(Level.INFO, "Error while syncing with Metrics!");
		}
		log.log(Level.INFO, "is enabled!");
	}

	@Override
	public void onDisable() {
		saveToYAML();
		log.log(Level.INFO, "is disabled!");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		Player player;
		if (sender instanceof Player)
			player = (Player) sender;
		else {
			if (command.getName().equalsIgnoreCase("event"))
				log.log(Level.WARNING, "Use this command in-game!");
			return true;
		}
		if (args.length == 0) {
			if (getPlayerRunningEvents(player) == 1)
				args = new String[] { T.teleport,
						getPlayerRunningEvent(player).getName() };
			else
				args = new String[] { T.help };
		}
		if (command.getName().equalsIgnoreCase("event")) {
			if (args[0].equalsIgnoreCase(T.help)) {
				player.sendMessage(ChatColor.GOLD + getName() + ": " + ChatColor.GRAY + T.help);
				player.sendMessage(ChatColor.GRAY + "-----------------------------------------------------");
				player.sendMessage(ChatColor.GOLD + "/event " + T.list + ChatColor.GRAY + "    " + T.help_list);
				player.sendMessage(ChatColor.GOLD + "/event " + T.create + ChatColor.GRAY + "    " + T.help_create);
				player.sendMessage(ChatColor.GOLD + "/event " + T.destroy + ChatColor.GRAY + "    " + T.help_destroy);
				player.sendMessage(ChatColor.GOLD + "/event " + T.info + ChatColor.GRAY + "    " + T.help_info);
				player.sendMessage(ChatColor.GOLD + "/event " + T.logon + ChatColor.GRAY + "    " + T.help_logon);
				player.sendMessage(ChatColor.GOLD + "/event " + T.logoff + ChatColor.GRAY + "    " + T.help_logoff);
				player.sendMessage(ChatColor.GOLD + "/event " + T.teleport + ChatColor.GRAY + "    " + T.help_teleport);
				player.sendMessage(ChatColor.GRAY + T.moreinfos + ChatColor.GOLD + " /event <" + T.action + ">");
			} else if (args[0].equalsIgnoreCase(T.create)) {
				if (player.hasPermission("MyEvents.event.create") || player.hasPermission("MyEvents.admin"))
					if (args.length >= 6) {
						if (!eventExists(args[1])) {
							String name = args[1];
							int dayOfMonth, month, year, hourOfDay, minute, duration;
							try {
								dayOfMonth = Integer.parseInt(args[2].split("\\.")[0]);
								month = Integer.parseInt(args[2].split("\\.")[1]) - 1;
								year = Integer.parseInt(args[2].split("\\.")[2]);
								hourOfDay = Integer.parseInt(args[3].split(":")[0]);
								minute = Integer.parseInt(args[3].split(":")[1]);
								duration = Integer.parseInt(args[4]);
							} catch (ArrayIndexOutOfBoundsException ex) {
								player.sendMessage(ChatColor.RED + T.wrongargs + ChatColor.GRAY + " /event " + T.create + " " + args[1] + " <dd.mm.yyyy hh:mm> <" + T.duration + ">" + getLastPartOfArray(args, 5));
								return true;
							} catch (NullPointerException ex) {
								player.sendMessage(ChatColor.RED + T.wrongargs + ChatColor.GRAY + " /event " + T.create + " " + args[1] + " <dd.mm.yyyy hh:mm> <" + T.duration + ">" + getLastPartOfArray(args, 5));
								return true;
							} catch (NumberFormatException ex) {
								player.sendMessage(ChatColor.RED + T.wrongargs + ChatColor.GRAY + " /event " + T.create + " " + args[1] + " <dd.mm.yyyy hh:mm> <" + T.duration + ">" + getLastPartOfArray(args, 5));
								return true;
							}
							EventStorage created = new EventStorage(player.getName(), name, new GregorianCalendar(year, month, dayOfMonth, hourOfDay, minute), player.getLocation(), getLastPartOfArray(args, 5), duration);
							events.put(name, created);
							getServer().getPluginManager().callEvent(new EventCreatedEvent(created, System.currentTimeMillis()));
							player.sendMessage(ChatColor.GREEN + T.created);
						} else
							player.sendMessage(ChatColor.RED + T.alreadyexists);
					} else
						player.sendMessage(ChatColor.RED + T.moreargs + ChatColor.GRAY + " /event " + T.create + " <" + T.name + "> <" + T.date + "> <" + T.duration + "> <" + T.desc + ">");
				else
					player.sendMessage(ChatColor.RED + "You do not have the required permission!");
			} else if (args[0].equalsIgnoreCase(T.destroy)) {
				if (player.hasPermission("MyEvents.event.destroy") || player.hasPermission("MyEvents.admin"))
					if (args.length >= 2) {
						if (eventExists(args[1])) {
							getServer().getPluginManager().callEvent(new EventDestroyedEvent(events.remove(args[1]), System.currentTimeMillis()));
							player.sendMessage(ChatColor.GREEN + T.destroyed);
						} else
							player.sendMessage(ChatColor.RED + T.doesntexist);
					} else
						player.sendMessage(ChatColor.RED + T.moreargs + ChatColor.GRAY + " /event " + T.destroy + " <" + T.name + ">");
				else
					player.sendMessage(ChatColor.RED + "You do not have the required permission!");
			} else if (args[0].equalsIgnoreCase(T.edit)) {
				if (player.hasPermission("MyEvents.event.edit") || player.hasPermission("MyEvents.admin"))
					if (args.length >= 2) {
						if (args[2].equalsIgnoreCase(T.dateEdit)) {
							if (args.length >= 3) {
								if (eventExists(args[1])) {
									int dayOfMonth, month, year, hourOfDay, minute;
									try {
										dayOfMonth = Integer.parseInt(args[3].split("\\.")[0]);
										month = Integer.parseInt(args[3].split("\\.")[1]);
										year = Integer.parseInt(args[3].split("\\.")[2]);
										hourOfDay = Integer.parseInt(args[4].split(":")[0]);
										minute = Integer.parseInt(args[4].split(":")[1]);
									} catch (ArrayIndexOutOfBoundsException ex) {
										player.sendMessage(ChatColor.RED + T.wrongargs + ChatColor.GRAY + " /event " + T.edit + " " + args[1] + " " + T.dateEdit + " <dd.mm.yyyy hh:mm>");
										return true;
									} catch (NullPointerException ex) {
										player.sendMessage(ChatColor.RED + T.wrongargs + ChatColor.GRAY + " /event " + T.edit + " " + args[1] + " " + T.dateEdit + " <dd.mm.yyyy hh:mm>");
										return true;
									} catch (NumberFormatException ex) {
										player.sendMessage(ChatColor.RED + T.wrongargs + ChatColor.GRAY + " /event " + T.edit + " " + args[1] + " " + T.dateEdit + " <dd.mm.yyyy hh:mm>");
										return true;
									}
									events.get(args[1]).setTime(new GregorianCalendar(year, month, dayOfMonth, hourOfDay, minute));
									player.sendMessage(ChatColor.GREEN + T.changed);
								} else
									player.sendMessage(ChatColor.RED + T.doesntexist);
							} else
								player.sendMessage(ChatColor.RED + T.moreargs + ChatColor.GRAY + " /event " + T.edit + " <" + T.name + "> " + T.dateEdit + " <" + T.value + ">");
						} else if (args[2].equalsIgnoreCase(T.desc)) {
							if (args.length >= 3)
								if (eventExists(args[1])) {
									events.get(args[1]).setDesc(getLastPartOfArray(args, 3));
									System.out.println(getLastPartOfArray(args, 3));
									player.sendMessage(ChatColor.GREEN + T.changed);
								} else
									player.sendMessage(ChatColor.RED + T.doesntexist);
							else
								player.sendMessage(ChatColor.RED + T.moreargs + ChatColor.GRAY + " /event " + T.edit + " <" + T.name + "> " + T.desc + " <" + T.value + ">");
						} else if (args[2].equalsIgnoreCase(T.location)) {
							if (eventExists(args[1])) {
								events.get(args[1]).setLocation(player.getLocation());
								player.sendMessage(ChatColor.GREEN + T.locationset);
							} else
								player.sendMessage(ChatColor.RED + T.doesntexist);
						} else if (args[2].equalsIgnoreCase(T.duration)) {
							if (args.length >= 3)
								if (eventExists(args[1])) {
									events.get(args[1]).setDuration(Integer.parseInt(args[3]));
									player.sendMessage(ChatColor.GREEN + T.changed);
								} else
									player.sendMessage(ChatColor.RED + T.doesntexist);
							else
								player.sendMessage(ChatColor.RED + T.moreargs + ChatColor.GRAY + " /event " + T.edit + " <" + T.name + "> " + T.duration + " <" + T.value + ">");
						}
					} else
						player.sendMessage(ChatColor.RED + T.moreargs + ChatColor.GRAY + " /event " + T.edit + " <" + T.name + "> <" + T.action + "> [<" + T.value + ">]");
				else
					player.sendMessage(ChatColor.RED + "You do not have the required permission!");
			} else if (args[0].equalsIgnoreCase(T.info)) {
				if (player.hasPermission("MyEvents.event.info") || player.hasPermission("MyEvents.use"))
					if (args.length >= 2) {
						if (eventExists(args[1])) {
							EventStorage e = events.get(args[1]);
							player.sendMessage(ChatColor.GRAY + T.eventinfo + ChatColor.GOLD + e.getName() + ChatColor.GRAY + " " + T.by + " " + ChatColor.GOLD + e.getCreator());
							player.sendMessage(ChatColor.GOLD + "-----------------------------------------------------");
							player.sendMessage(ChatColor.GOLD + T.desc + ": " + ChatColor.GRAY + e.getDesc());
							GregorianCalendar c = (GregorianCalendar) e.getTime().clone();
							player.sendMessage(ChatColor.GOLD + T.date + ": " + ChatColor.GRAY + c.get(Calendar.DAY_OF_MONTH) + "." + (c.get(Calendar.MONTH) + 1) + "." + c.get(Calendar.YEAR) + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE));
							player.sendMessage(ChatColor.GOLD + T.duration + ": " + ChatColor.GRAY + e.getDuration() + " " + T.minutes);
							c.setTimeInMillis(c.getTimeInMillis() + e.getDuration() * 1000 * 60);
							player.sendMessage(ChatColor.GOLD + T.end + ": " + ChatColor.GRAY + c.get(Calendar.DAY_OF_MONTH) + "." + (c.get(Calendar.MONTH) + 1) + "." + c.get(Calendar.YEAR) + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE));
							player.sendMessage(ChatColor.GOLD + T.author + ": " + ChatColor.GRAY + e.getCreator());
							Location l = e.getLocation();
							player.sendMessage(ChatColor.GOLD + T.location + ":" + ChatColor.GRAY + " X=" + (int) l.getX() + " Y=" + (int) l.getY() + " Z=" + (int) l.getZ());
							if (e.isParticipant(player.getName()))
								player.sendMessage(ChatColor.GOLD + T.status + ": " + ChatColor.GREEN + T.participating);
							else
								player.sendMessage(ChatColor.GOLD + T.status + ": " + ChatColor.RED + T.notparticipating);
						} else
							player.sendMessage(ChatColor.RED + T.doesntexist);
					} else
						player.sendMessage(ChatColor.RED + T.moreargs + ChatColor.GRAY + " /event " + T.info + " <" + T.name + ">");
				else
					player.sendMessage(ChatColor.RED + "You do not have the required permission!");
			} else if (args[0].equalsIgnoreCase(T.teleport)) {
				if (player.hasPermission("MyEvents.event.teleport") || player.hasPermission("MyEvents.use"))
					if (args.length >= 2) {
						if (eventExists(args[1])) {
							EventStorage e = events.get(args[1]);
							if (e.isRunning())
								if (e.isParticipant(player.getName()))
									if (!e.hasTeleported(player.getName())) {
										PlayerTeleportEventEvent eventSend = new PlayerTeleportEventEvent(e, System.currentTimeMillis(), player);
										getServer().getPluginManager().callEvent(eventSend);
										if (!eventSend.isCancelled()) {
											e.playerTeleported(player.getName());
											player.teleport(e.getLocation());
											player.sendMessage(ChatColor.GREEN + T.teleported);
										}
									} else
										player.sendMessage(ChatColor.RED + T.alreadyteleported);
								else
									player.sendMessage(ChatColor.RED + T.notparticipating);
							else
								player.sendMessage(ChatColor.RED + T.notrunning);
						} else
							player.sendMessage(ChatColor.RED + T.doesntexist);
					} else
						player.sendMessage(ChatColor.RED + T.moreargs + ChatColor.GRAY + " /event " + T.teleport + " <" + T.name + ">");
				else
					player.sendMessage(ChatColor.RED + "You do not have the required permission!");
			} else if (args[0].equalsIgnoreCase(T.logon)) {
				if (player.hasPermission("MyEvents.event.logon") || player.hasPermission("MyEvents.use"))
					if (args.length >= 2) {
						if (eventExists(args[1])) {
							EventStorage e = events.get(args[1]);
							if (!e.isRunning()) {
								if (!e.isParticipant(player.getName())) {
									PlayerLogOnEventEvent eventSend = new PlayerLogOnEventEvent(e, System.currentTimeMillis(), player);
									getServer().getPluginManager().callEvent(eventSend);
									if (!eventSend.isCancelled()) {
										e.addParticipant(player.getName());
										player.sendMessage(ChatColor.GREEN + T.participating);
									}
								} else
									player.sendMessage(ChatColor.RED + T.alreadyparticipating);
							} else
								player.sendMessage(ChatColor.RED + T.alreadyrunning);
						} else
							player.sendMessage(ChatColor.RED + T.doesntexist);
					} else
						player.sendMessage(ChatColor.RED + T.moreargs + ChatColor.GRAY + " /event " + T.logon + " <" + T.name + ">");
				else
					player.sendMessage(ChatColor.RED + "You do not have the required permission!");
			} else if (args[0].equalsIgnoreCase(T.logoff)) {
				if (player.hasPermission("MyEvents.event.logoff") || player.hasPermission("MyEvents.use"))
					if (args.length >= 2) {
						if (eventExists(args[1])) {
							EventStorage e = events.get(args[1]);
							if (e.isParticipant(player.getName())) {
								PlayerLogOffEventEvent eventSend = new PlayerLogOffEventEvent(e, System.currentTimeMillis(), player);
								getServer().getPluginManager().callEvent(eventSend);
								if (!eventSend.isCancelled()) {
									e.removeParticipant(player.getName());
									player.sendMessage(ChatColor.GREEN + T.notparticipatinganymore);
								}
							} else
								player.sendMessage(ChatColor.RED + T.notparticipating);
						} else
							player.sendMessage(ChatColor.RED + T.doesntexist);
					} else
						player.sendMessage(ChatColor.RED + T.moreargs + ChatColor.GRAY + " /event " + T.logoff + " <" + T.name + ">");
				else
					player.sendMessage(ChatColor.RED + "You do not have the required permission!");
			} else if (args[0].equalsIgnoreCase(T.list)) {
				if (player.hasPermission("MyEvents.event.list") || player.hasPermission("MyEvents.use")) {
					ArrayList<EventStorage> evs = new ArrayList<EventStorage>(events.values());
					Collections.sort(evs, new Comparator<EventStorage>() {
						@Override
						public int compare(EventStorage o1, EventStorage o2) {
							if (o1.getTime().getTimeInMillis() < o2.getTime().getTimeInMillis()) {
								return 1;
							} else if (o1.getTime().getTimeInMillis() > o2.getTime().getTimeInMillis()) {
								return -1;
							} else {
								return 0;
							}
						}
					});
					player.sendMessage(ChatColor.GOLD + T.listheader);
					int i = 1;
					for (EventStorage e : evs) {
						player.sendMessage(ChatColor.GRAY + "" + i + ": " + ChatColor.GOLD + e.getName() + " " + ChatColor.GRAY + T.by + " " + ChatColor.GOLD + e.getCreator());
						i++;
					}
					player.sendMessage(ChatColor.GRAY + T.moreinfos + ChatColor.GOLD + " /event " + T.info + " <" + T.name + ">");
				} else
					player.sendMessage(ChatColor.RED + "You do not have the required permission!");
			} else if (args[0].equalsIgnoreCase(T.participants)) {
				if (player.hasPermission("MyEvents.event.participants") || player.hasPermission("MyEvents.use"))
					if (args.length >= 2) {
						if (eventExists(args[1])) {
							player.sendMessage(ChatColor.GRAY + T.participantslist);
							player.sendMessage(ChatColor.GREEN + formatParticipants(events.get(args[1]).getParticipants()));
						} else
							player.sendMessage(ChatColor.RED + T.doesntexist);
					} else
						player.sendMessage(ChatColor.RED + T.moreargs + ChatColor.GRAY + " /event " + T.participants + " <" + T.name + ">");
				else
					player.sendMessage(ChatColor.RED + "You do not have the required permission!");
			}
		}
		return true;
	}

	private String formatParticipants(LinkedList<String> participants) {
		if (participants.size() == 0)
			return ChatColor.GRAY + "-----";
		else {
			String temp = "";
			for (String participant : participants)
				temp += ", " + ChatColor.GOLD + participant + ChatColor.GRAY;
			return temp.substring(2, temp.length());
		}
	}

	private void handlePlayerLoginMsgs(Player player) {
		for (EventStorage e : events.values()) {
			if (e.isParticipant(player.getName()))
				if (!e.isRunning()) {
					if (!e.hasTeleported(player.getName())) {
						player.sendMessage(ChatColor.BOLD + T.notification_start + " " + ChatColor.GOLD + ChatColor.BOLD + e.getName());
						if (getPlayerRunningEvents(player) == 1)
							player.sendMessage(T.tpwith + ChatColor.GOLD + " /event");
						else
							player.sendMessage(T.tpwith + ChatColor.GOLD + " /event " + T.teleport + " " + e.getName());
					}
				}
		}
	}

	private String getLastPartOfArray(String[] arr, int beginIndex) {
		String temp = "";
		for (int i = beginIndex; i < arr.length; i++)
			temp += arr[i] + " ";
		return temp;
	}

	private void checkPlayerNotifications() {
		for (EventStorage e : events.values()) {
			if (e.isRunning()) {
				notifyPlayers(e);
				if (!e.hasEventSent("STARTED")) {
					getServer().getPluginManager().callEvent(new EventStartedEvent(e, System.currentTimeMillis()));
				}
			} else if (e.hasEventSent("STARTED") && !e.hasEventSent("STOPPED")) {
				getServer().getPluginManager().callEvent(new EventStoppedEvent(e, System.currentTimeMillis()));
			}
		}
	}

	private void notifyPlayers(EventStorage e) {
		LinkedList<String> players = e.getParticipants();
		for (String pl : players) {
			OfflinePlayer opl = getServer().getOfflinePlayer(pl);
			if (opl.isOnline() && !e.hasTeleported(pl)) {
				Player player = opl.getPlayer();
				player.sendMessage(ChatColor.BOLD + T.notification_start + " " + ChatColor.GOLD + ChatColor.BOLD + e.getName());
				if (getPlayerRunningEvents(player) == 1)
					player.sendMessage(T.tpwith + ChatColor.GOLD + " /event");
				else
					player.sendMessage(T.tpwith + ChatColor.GOLD + " /event " + T.teleport + " " + e.getName());
			}
		}
	}

	private int getPlayerRunningEvents(Player player) {
		int temp = 0;
		for (EventStorage e : events.values())
			if (e.isParticipant(player.getName()))
				if (e.isRunning())
					temp++;
		return temp;
	}

	private EventStorage getPlayerRunningEvent(Player player) {
		for (EventStorage e : events.values())
			if (e.isParticipant(player.getName()))
				if (e.isRunning())
					return e;
		return null;
	}

	/**
	 * Save the events to the config (Overrides the changes made in config.yml)
	 */
	public void saveToYAML() {
		reloadConfig();
		getConfig().set("Events", null);
		for (Entry<String, EventStorage> l : events.entrySet()) {
			EventStorage e = l.getValue();
			getConfig().set("Events." + e.getName() + ".desc", e.getDesc());
			getConfig().set("Events." + e.getName() + ".creator", e.getCreator());
			getConfig().set("Events." + e.getName() + ".locX", e.getLocation().getX());
			getConfig().set("Events." + e.getName() + ".locY", e.getLocation().getY());
			getConfig().set("Events." + e.getName() + ".locZ", e.getLocation().getZ());
			getConfig().set("Events." + e.getName() + ".locWorld", e.getLocation().getWorld().getName());
			getConfig().set("Events." + e.getName() + ".time", e.getTime().getTimeInMillis());
			getConfig().set("Events." + e.getName() + ".duration", e.getDuration());
			getConfig().set("Events." + e.getName() + ".participants", e.getParticipants());
		}
		saveConfig();
	}

	/**
	 * Get all registered events
	 * 
	 * @return A map with the event name as key and the EventStorage object as
	 *         value
	 */
	public Map<String, EventStorage> getEvents() {
		return events;
	}

	/**
	 * Check if an event exists
	 * 
	 * @param name The name of the event
	 * @return A boolean that indicates if the event exists
	 */
	public boolean eventExists(String name) {
		return events.containsKey(name);
	}

	/**
	 * Get an EventStorage object by it's name
	 * 
	 * @param name The name of the event
	 * @return If the event existsAn EventStorage object , else null
	 */
	public EventStorage getEvent(String name) {
		if (events.containsKey(name))
			return events.get(name);
		return null;
	}

	/**
	 * Register/add event
	 * 
	 * @param event The event that should be registered/added
	 * @return False, if the event already exists, else true
	 */
	public boolean addEvent(EventStorage event) {
		if (events.containsKey(event.getName()))
			return false;
		else {
			events.put(event.getName(), event);
			return true;
		}
	}

	/**
	 * Unregister/remove event
	 * 
	 * @param event The event that should be unregistered/removed
	 * @return True, if the event existed, else false
	 */
	public boolean removeEvent(EventStorage event) {
		if (events.containsKey(event.getName())) {
			events.remove(event);
			return true;
		} else
			return false;
	}
}
