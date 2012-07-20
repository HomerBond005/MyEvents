/*
 * Copyright HomerBond005
 * 
 *  Published under CC BY-NC-ND 3.0
 *  http://creativecommons.org/licenses/by-nc-nd/3.0/
 */
package de.HomerBond005.MyEvents;

import org.bukkit.configuration.ConfigurationSection;

public class Texts{
	public String create,destroy,edit,teleport,help,moreargs,
	name,desc,date,dateEdit = "Datum",location,action,value,destroyed,
	created,doesntexist,alreadyexists,changed,locationset,info,wrongargs
	,notrunning,eventinfo,by,author,logon,logoff,notparticipating,notparticipatinganymore,
	alreadyparticipating,participating,status,list,listheader,moreinfos,tpwith,
	notification_start,participants,participantslist,duration,minutes,end,alreadyrunning,
	teleported,alreadyteleported,help_list,help_teleport,help_create,help_info,help_edit,
	help_logon,help_logoff,help_destroy;
	
	public Texts(ConfigurationSection lang){
		action = fm(lang.getString("action"));
		alreadyexists = fm(lang.getString("alreadyexists"));
		alreadyparticipating = fm(lang.getString("alreadyparticipating"));
		alreadyrunning = fm(lang.getString("alreadyrunning"));
		alreadyteleported = fm(lang.getString("alreadyteleported"));
		author = fm(lang.getString("author"));
		by = fm(lang.getString("by"));
		changed = fm(lang.getString("changed"));
		create = fm(lang.getString("create"));
		created = fm(lang.getString("created"));
		date = fm(lang.getString("date"));
		dateEdit = fm(lang.getString("dateEdit"));
		desc = fm(lang.getString("desc"));
		destroy = fm(lang.getString("destroy"));
		destroyed = fm(lang.getString("destroyed"));
		doesntexist = fm(lang.getString("doesntexist"));
		duration = fm(lang.getString("duration"));
		edit = fm(lang.getString("edit"));
		end = fm(lang.getString("end"));
		eventinfo = fm(lang.getString("eventinfo"));
		help = fm(lang.getString("help"));
		help_create = fm(lang.getString("help_create"));
		help_destroy = fm(lang.getString("help_destroy"));
		help_edit = fm(lang.getString("help_edit"));
		help_info = fm(lang.getString("help_info"));
		help_list = fm(lang.getString("help_list"));
		help_logoff = fm(lang.getString("help_logoff"));
		help_logon = fm(lang.getString("help_logon"));
		help_teleport = fm(lang.getString("help_teleport"));
		info = fm(lang.getString("info"));
		list = fm(lang.getString("list"));
		listheader = fm(lang.getString("listheader"));
		location = fm(lang.getString("location"));
		locationset = fm(lang.getString("locationset"));
		logoff = fm(lang.getString("logoff"));
		logon = fm(lang.getString("logon"));
		minutes = fm(lang.getString("minutes"));
		moreargs = fm(lang.getString("moreargs"));
		moreinfos = fm(lang.getString("moreinfos"));
		name = fm(lang.getString("name"));
		notification_start = fm(lang.getString("notification_start"));
		notparticipating = fm(lang.getString("notparticipating"));
		notparticipatinganymore = fm(lang.getString("notparticipatinganymore"));
		notrunning = fm(lang.getString("notrunning"));
		participants = fm(lang.getString("participants"));
		participantslist = fm(lang.getString("participantslist"));
		participating = fm(lang.getString("participating"));
		status = fm(lang.getString("status"));
		teleport = fm(lang.getString("teleport"));
		teleported = fm(lang.getString("teleported"));
		tpwith = fm(lang.getString("tpwith"));
		value = fm(lang.getString("value"));
		wrongargs = fm(lang.getString("wrongargs"));
	}
	
	private String fm(String msg){
		return msg.replaceAll("(&([a-f0-9]))", "\u00A7$2");
	}
}
