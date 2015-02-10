package com.jjtech.newap;

import com.jjoe64.graphview.series.DataPoint;

public class db {
	final static int CATEGORY_1		= 0;
	final static int CATEGORY_2		= 1;
	final static int CATEGORY_3		= 2;
	final static int CATEGORY_4		= 3;
	final static int CATEGORY_5		= 4;
    final static int CATEGORY_6		= 5;

    final static int ADVERTISEMENTS = 1;
	final static int ALCOHOL 		= 2;
	final static int ANONYMIZERS 	= 3;
	final static int ARTS 			= 4;
	final static int BUSINESS 		= 5;
	final static int TRANSPORTATION = 6;
	final static int CHAT 			= 7;
	final static int FORUMS 		= 9;
	final static int COMPROMISED 	= 10;
	final static int COMPUTERS 		= 11;
	final static int CRIMINAL 		= 12;
	final static int DATING 		= 13;
	final static int DOWNLOAD 		= 14;
	final static int EDUCATION 		= 15;
	final static int ENTERTAINMENT 	= 16;
	final static int FINANCE 		= 17;
	final static int GAMBLING 		= 18;
	final static int GAMES 			= 19;
	final static int GOVERNMENT 	= 20;
	final static int HATE 			= 21;
	final static int HEALTH 		   = 22;
	final static int DRUG 			= 23;
	final static int JOBSEARCH 		= 24;
	final static int STREAMING 		= 26;
	final static int NEWS 			= 27;
	final static int NGO 			= 28;
	final static int NUDITY 		= 29;
	final static int PERSONAL		= 30;
	final static int PHISHING		= 31;
	final static int POLITICS 		= 32;
	final static int PORNOGRAPHY 	= 33;
	final static int REALESTATE 	= 34;
	final static int RELIGION 		= 35;
	final static int RESTAURANTS 	= 36;
	final static int SEARCH 		= 37;
	final static int SHOPPING 		= 38;
	final static int SNS 			= 39;
	final static int SPAM 			= 40;
	final static int SPORTS 		= 41;
	final static int MALWARE 		= 42;
	final static int TRANSLATOR 	= 44;
	final static int TRAVEL 		= 45;
	final static int VIOLENCE 		= 46;
	final static int WEAPONS 		= 47;
	final static int EMAIL			= 48;
	final static int GENERAL	 	= 49;
	final static int LEISURE 		= 50;
	final static int BOTNETS 		= 61;
	final static int CULT 			= 62;
	final static int FASHION 		= 63;
	final static int GREETING 		= 64;
	final static int HACKING 		= 65;
	final static int SOFTWARE 		= 67;
	final static int IMAGESHARE 	= 68;
	final static int SECURITY 		= 69;
	final static int MESSASING		= 70;
	final static int ERRORS 		= 71;
	final static int DOMAINS 		= 72;
	final static int P2P 			= 73;
	final static int IP 			= 74;
	final static int CHEATING 		= 75;
	final static int SEXEDUCATION 	= 76;
	final static int TASTELESS 		= 77;
	final static int CHILDABUSE 	= 78;

    final static int[][] CATEGORY ={
        {ARTS, BUSINESS, TRANSPORTATION, FORUMS,COMPROMISED,COMPUTERS,EDUCATION,FINANCE,GOVERNMENT,TRANSLATOR,EMAIL,SOFTWARE,SECURITY,IP},
        {ADVERTISEMENTS, JOBSEARCH, SEARCH, IMAGESHARE,NEWS},
        {HEALTH, NGO, POLITICS, GENERAL,BOTNETS,ERRORS,DOMAINS,TASTELESS },
        {CHAT, DATING, STREAMING, PERSONAL,REALESTATE,RELIGION,RESTAURANTS,SHOPPING,SNS,TRAVEL,FASHION,GREETING,MESSASING},
        {DOWNLOAD, ENTERTAINMENT, GAMBLING, GAMES,SPORTS,LEISURE,P2P},
        {ALCOHOL, ANONYMIZERS, CRIMINAL, HATE,DRUG,NUDITY,PHISHING,PORNOGRAPHY,SPAM,MALWARE,VIOLENCE,WEAPONS,CULT,HACKING,CHEATING,SEXEDUCATION,CHILDABUSE},
        {ARTS, BUSINESS, TRANSPORTATION, FORUMS,COMPROMISED,COMPUTERS,EDUCATION,FINANCE,GOVERNMENT,TRANSLATOR,EMAIL,SOFTWARE,SECURITY,IP}};
}