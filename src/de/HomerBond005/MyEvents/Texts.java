/*
 * Copyright HomerBond005
 * 
 *  Published under CC BY-NC-ND 3.0
 *  http://creativecommons.org/licenses/by-nc-nd/3.0/
 */
package de.HomerBond005.MyEvents;

import org.bukkit.configuration.ConfigurationSection;

public class Texts{
	public String create = "erstellen",
	destroy = "entfernen",
	edit = "bearbeiten",
	teleport = "tp",
	help = "Hilfe",
	moreargs = "Du brauchst mehr Argumente!",
	name = "Name",
	desc = "Beschreibung",
	date = "Datum und Zeit",
	dateEdit = "Datum",
	location = "Ort",
	action = "Aktion",
	value = "Wert",
	destroyed = "Das Event wurde erfolgreich gelöscht.",
	created = "Das Event wurde erfolgreich erstellt.",
	doesntexist = "Das Event existiert nicht!",
	alreadyexists = "Das Event existiert bereits!",
	changed = "Erfolgreich geändert!",
	locationset = "Der Ort des Events befindet sich nun an deiner Position.",
	info = "info",
	wrongargs = "Falsche Argumente! Benutze:",
	notrunning = "Das Event hat noch nicht begonnen oder ist bereits vorbei!",
	eventinfo = "Informationen zu: ",
	by = "von",
	author = "Autor",
	logon = "anmelden",
	logoff = "abmelden",
	notparticipating = "Du nimmst an diesem Event nicht teil!",
	notparticipatinganymore = "Du nimmst an diesem Event nicht mehr teil.",
	alreadyparticipating = "Du nimmst bereits an diesem Event teil!",
	participating = "Du nimmst an diesem Event teil!",
	status = "Status",
	list = "anzeigen",
	listheader = "Folgende Events existieren:",
	moreinfos = "Für mehr Infos:",
	tpwith = "Teleportieren mit:",
	notification_start = "Das folgende Event hat begonnen:",
	participants = "teilnehmer",
	participantslist = "Folgende Player nehmen an dem Event teil:",
	duration = "Dauer",
	minutes = "Minuten",
	end = "Ende",
	alreadyrunning = "Das Event läuft bereits!",
	teleported = "Du hast dich erfolgreich zum Event teleportiert.",
	alreadyteleported = "Du hast dich bereits teleportiert!",
	help_list = "Alle Events auflisten",
	help_teleport = "Zum Event teleportieren",
	help_create = "Ein neues Event erstellen",
	help_info = "Infos über ein Event erhalten",
	help_edit = "Ein Event bearbeiten",
	help_logon = "Für ein Event anmelden",
	help_logoff = "Von einem Event abmelden",
	help_destroy = "Ein Event entfernen";
	public Texts(ConfigurationSection lang){
		action = lang.getString("action");
		alreadyexists = lang.getString("alreadyexists");
		alreadyparticipating = lang.getString("alreadyparticipating");
		alreadyrunning = lang.getString("alreadyrunning");
		alreadyteleported = lang.getString("alreadyteleported");
		author = lang.getString("author");
		by = lang.getString("by");
		changed = lang.getString("changed");
		create = lang.getString("create");
		created = lang.getString("created");
		date = lang.getString("date");
		dateEdit = lang.getString("dateEdit");
		desc = lang.getString("desc");
		destroy = lang.getString("destroy");
		destroyed = lang.getString("destroyed");
		doesntexist = lang.getString("doesntexist");
		duration = lang.getString("duration");
		edit = lang.getString("edit");
		end = lang.getString("end");
		eventinfo = lang.getString("eventinfo");
		help = lang.getString("help");
		help_create = lang.getString("help_create");
		help_destroy = lang.getString("help_destroy");
		help_edit = lang.getString("help_edit");
		help_info = lang.getString("help_info");
		help_list = lang.getString("help_list");
		help_logoff = lang.getString("help_logoff");
		help_logon = lang.getString("help_logon");
		help_teleport = lang.getString("help_teleport");
		info = lang.getString("info");
		list = lang.getString("list");
		listheader = lang.getString("listheader");
		location = lang.getString("location");
		locationset = lang.getString("locationset");
		logoff = lang.getString("logoff");
		logon = lang.getString("logon");
		minutes = lang.getString("minutes");
		moreargs = lang.getString("moreargs");
		moreinfos = lang.getString("moreinfos");
		name = lang.getString("name");
		notification_start = lang.getString("notification_start");
		notparticipating = lang.getString("notparticipating");
		notparticipatinganymore = lang.getString("notparticipatinganymore");
		notrunning = lang.getString("notrunning");
		participants = lang.getString("participants");
		participantslist = lang.getString("participantslist");
		participating = lang.getString("participating");
		status = lang.getString("status");
		teleport = lang.getString("teleport");
		teleported = lang.getString("teleported");
		tpwith = lang.getString("tpwith");
		value = lang.getString("value");
		wrongargs = lang.getString("wrongargs");
	}
}
