﻿// import the Delegate class
import mx.utils.Delegate;
import mx.controls.TextInput;

var dokuGame:MathDoku = new MathDoku();

// declare a new XML instance
var groupsxml:XML = new XML();

// ignore tabs, returns, and other whitespace between nodes
groupsxml.ignoreWhite = true;

// set the scope of the onLoad function to the MathDoku timeline, not configxml
groupsxml.onLoad = Delegate.create(this, onXmlLoaded);

//create arrays needed for groups and laws
var unseenLines:Array = new Array();
var groupLaw:Array = new Array();
var path:String = _level0.loader.contentHolder.url_retro;
var version:String = "";
var nCase:Number = 4; // 


this.onEnterFrame = loadXML;

function loadXML()
{
	if(path == undefined)
       groupsxml.load("mathdoku.xml");
	else
	   groupsxml.load(path);
   delete this.onEnterFrame;	
}

function onXmlLoaded(boole:Boolean)
{	
  if(boole)
  {	
	trace('reading mathdoku XML');
	treatXML();
  }
  else
  {
	 // if an XML read error occurred
     trace('error reading XML de config');
  }
}




function treatXML()
{
	version = groupsxml.firstChild.attributes.caseVersion;
	
	var groupsChildNodes:Array = groupsxml.firstChild.childNodes;
	for(var i in groupsChildNodes)
	{
		// create new group
		var group:DokuGroup = new DokuGroup(dokuGame);
		
	   //trace(groupsChildNodes[i].nodeName);
	   var groupChildNodes:Array = groupsChildNodes[i].childNodes;
	   for(var j in groupChildNodes)
	   {
	      //trace(groupChildNodes[j].nodeName);
		  if(groupChildNodes[j].nodeName == "unseen")
		  {
			  var unseenChildNodes:Array = groupChildNodes[j].childNodes;
			  for(var s in unseenChildNodes)
			  {
				  unseenLines.push(unseenChildNodes[s].firstChild.nodeValue);				  
			  }   
		  }else if(groupChildNodes[j].nodeName == "law")
		  {
			  groupLaw.push(new Object());
			  groupLaw[groupLaw.length - 1].law = groupChildNodes[j].firstChild.nodeValue;
			  groupLaw[groupLaw.length - 1].lawCase = groupChildNodes[j].attributes.id;
			  group.setGroupLaw(groupChildNodes[j].firstChild.nodeValue, groupChildNodes[j].attributes.id, this[groupChildNodes[j].attributes.id]);
			  //trace(groupLaw[groupLaw.length - 1].law + " " + groupLaw[groupLaw.length - 1].lawCase)
		  }else if(groupChildNodes[j].nodeName == "cases")
		  {
			  var casesChildNodes:Array = groupChildNodes[j].childNodes;
			  for(var s in casesChildNodes)
			  {
				  var caseStr:String = casesChildNodes[s].attributes.id;
				  var solution:String = casesChildNodes[s].firstChild.nodeValue;
				  //group.addCase(caseStr, this[caseStr]);
				  group.addCase(caseStr, this[caseStr].NB);	
				  this[caseStr].NB.text = solution;
			  }   
		  }		  
	   }
	   // add created group to game proccesor
	   dokuGame.addGroup(group);
	}
	
	initGroups();
}

//initGroups();

function initGroups()
{	
     // mathdoku version
	if(version != undefined)
       test_mc.NX.text = " MathDoku " + version + " x " + version;	
	else
	   test_mc.NX.text = " MathDoku ";	
	   
	 trace(unseenLines.length);
	 for(var i in unseenLines)
	    this[unseenLines[i]]._visible = false;
		
	 for(var i in groupLaw)
	 {
		 this[groupLaw[i].lawCase].text = groupLaw[i].law;
	 }
	 	
}

for(var i = 1; i <= 4; i++)
{
	for(var j = 1; j <= 4; j++)
	{
	  this["N" + i + j].RC._alpha = 3;	  
	}
}// end for's

