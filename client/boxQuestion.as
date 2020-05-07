﻿import mx.controls.TextInput;

//Avant tout, mettre tous les boutons invisibles
//_level0.loader.contentHolder.box_question.swapDepths(_level0.loader.contentHolder.menuPointages);

bugText = _level0.loader.contentHolder.texteSource_xml.firstChild.attributes.buttonBug;
bugTextRoll = _level0.loader.contentHolder.texteSource_xml.firstChild.attributes.buttonBug;


if(_level0.loader.contentHolder.langue == "en")
{
	cadreLoading.texte = "Loading ...";	
}


// to stop drag the game table
Mouse.removeListener(_level0.loader.contentHolder.mouseListener);
_level0.loader.contentHolder.objGestionnaireEvenements.getOurPerso().setMinigameLoade(true);


_root.prob_txt._visible = false;
btn_ok_erreur._visible = false;

btn_a._visible = false;
btn_b._visible = false;
btn_c._visible = false;
btn_d._visible = false;
btn_e._visible = false;

btn_vrai._visible = false;
btn_faux._visible = false;
//_root.vrai_txt._visible = false;
//_root.faux_txt._visible = false;

rep._visible = false;
btn_ok._visible = false;



// pour savoir si la question a ete loadee
var loaded:Boolean = false;

// pour garder l'indice du livre en cas d'utilisation
//var livreIndice:Number;

// pour garder l'indice de la boule en cas d'utilisation
//var bouleIndice:Number;

/*
if(_level0.loader.contentHolder.objGestionnaireEvenements.typeDeJeu == "MathEnJeu"){ 
   this.btnSendErreur._visible = false;
} else if(_level0.loader.contentHolder.objGestionnaireEvenements.typeDeJeu == "Tournament"){
   this.btnSendErreur._visible = true;
   this.question_txt._visible = false;
   this.textBoxNumQues._visible = false;
}
*/

this.btnSendError._visible = true;
this.question_txt._visible = false;
this.textBoxNumQues_txt._visible = false;


// Decompose l'url pour afficher le num de la question
var parties_url:Array = _level0.loader.contentHolder.url_question.split("/");
var parties_nom:Array = parties_url[parties_url.length-1].split("-");
textBoxNumQues_txt = parties_nom[1];

// add possibilty to use keyboard 
var oUserKey:Object = new Object();
oUserKey.onKeyDown = function():Void {
      if( Key.getCode() == 65){
	      if(_level0.loader.contentHolder.type_question == "MULTIPLE_CHOICE"
			 || _level0.loader.contentHolder.type_question == "MULTIPLE_CHOICE_5")
		  {
		        _level0.loader.contentHolder.objGestionnaireEvenements.repondreQuestion("1");
	            _level0.loader.contentHolder.box_question.enleverBoutons();
		  }else if(_level0.loader.contentHolder.type_question == "TRUE_OR_FALSE")
		  {
			  _level0.loader.contentHolder.objGestionnaireEvenements.repondreQuestion("1");
	          _level0.loader.contentHolder.box_question.enleverBoutons();
		  }
       
      } else if( Key.getCode() == 66)
	  {
		  if(_level0.loader.contentHolder.type_question == "MULTIPLE_CHOICE"
							  || _level0.loader.contentHolder.type_question == "MULTIPLE_CHOICE_5")
		  {
             _level0.loader.contentHolder.objGestionnaireEvenements.repondreQuestion("2");
	         _level0.loader.contentHolder.box_question.enleverBoutons();
		  }else if(_level0.loader.contentHolder.type_question == "TRUE_OR_FALSE")
		  {
			  _level0.loader.contentHolder.objGestionnaireEvenements.repondreQuestion("0");
	          _level0.loader.contentHolder.box_question.enleverBoutons();
		  }
	        
      } else if( Key.getCode() == 67 && (_level0.loader.contentHolder.type_question == "MULTIPLE_CHOICE"
							  || _level0.loader.contentHolder.type_question == "MULTIPLE_CHOICE_5") ) {
          _level0.loader.contentHolder.objGestionnaireEvenements.repondreQuestion("3");
	      _level0.loader.contentHolder.box_question.enleverBoutons();
       
      } else if( Key.getCode() == 68 && (_level0.loader.contentHolder.type_question == "MULTIPLE_CHOICE"
							  || _level0.loader.contentHolder.type_question == "MULTIPLE_CHOICE_5")) {
          _level0.loader.contentHolder.objGestionnaireEvenements.repondreQuestion("4");
	      _level0.loader.contentHolder.box_question.enleverBoutons();
       
      } else if( Key.getCode() == 69 && (_level0.loader.contentHolder.type_question == "MULTIPLE_CHOICE"
							  || _level0.loader.contentHolder.type_question == "MULTIPLE_CHOICE_5")) {
          _level0.loader.contentHolder.objGestionnaireEvenements.repondreQuestion("5");
	      _level0.loader.contentHolder.box_question.enleverBoutons();
       
      } else {
           
      }
   };
Key.addListener(oUserKey);

var monContent:MovieClip;
var loadListener:Object = new Object();
loadListener.complete = function(eventObject)
{
	
	// on vérifie si on a chargé quelque chose en regardant la taille chargée
	if(monScroll.getBytesLoaded()!=0)
	{
		loaded = true;
		cadreLoading._visible = false;
			
		//FAIRE LA MEME CHOSE DANS GUI_RETRO
		
		monScroll.content._width = 360;
		monScroll.content._yscale = monScroll.content._xscale;
		monScroll.content.enabled = true;
		
		// to get functions inside the flash swf content
		monContent = monScroll.content;
		Selection.setFocus(monScroll);
	    
		this.onEnterFrame = null;
	 	
	switch (_level0.loader.contentHolder.type_question)
	{
		
		case "MULTIPLE_CHOICE" ://"ChoixReponse" :
			btn_a._visible = true;
			btn_b._visible = true;
			btn_c._visible = true;
			btn_d._visible = true;
			//btn_e._visible = true;
			break;
			
		case "MULTIPLE_CHOICE_5" ://"ChoixReponse" :
			btn_a._visible = true;
			btn_b._visible = true;
			btn_c._visible = true;
			btn_d._visible = true;
			btn_e._visible = true;
			break;
			
		case "TRUE_OR_FALSE" :
			
			btn_vrai._visible = true;
			btn_faux._visible = true;
			//Key.removeListener(oUserKey);
			trace(" TF - " + btn_faux._visible + " " + btn_vrai._visible);
			break;
			
		case "SHORT_ANSWER" :
			rep._visible = true;
			Selection.setFocus(rep);
			
			btn_ok._visible = true;
			Key.removeListener(oUserKey);
						
			btn_ok.onRelease = function()
			{
				//trace("btnOK tappé");
				traiterReponseCourte();
			}
			
			onKeyDown = function()
			{
				if(Key.getCode() == Key.ENTER)
				{
					//trace("enter tappé");
					traiterReponseCourte();
				}
				else
				{
					//trace("pas ENTER tappé");
				}
				
			}			
		    break;
		
		case "MINI_DOKU" :
		btn_ok._visible = true;
		btn_ok._x = 230;
		btn_ok._y = 340;
			Key.removeListener(oUserKey);
						
			btn_ok.onRelease = function()
			{
				trace("btnOK tappé dans MiniDoku");
				traiterReponseMiniDoku();
			}
		break;
			
		default :
			trace("Type de question inconnu");
		break;
		}
	
	}
	else
	{
		//trace("Chargement d'une question : 0 byte chargé => erreur");
		var horsService:MovieClip;
				
				cadreLoading._visible = false;
		
				horsService = _level0.loader.contentHolder.attachMovie("GUI_erreur", "HorsService", 9999);
				
				horsService.textGUI_erreur.text = _level0.loader.contentHolder.texteSource_xml.firstChild.attributes.GUIerreurQuestion;
				//horsService.linkGUI_erreur._visible = true;
				//horsService.linkGUI_erreur.text = _level0.loader.contentHolder.url_question;//_root.texteSource_xml.firstChild.attributes.GUIhorsService2;
				//horsService.linkGUI_erreur.html = true;
				
				horsService.btn_ok.onRelease = function()
				{						
					_level0.loader.contentHolder.erreurConnexion = true;
					// envoyer une réponse assurement mauvaise
					_level0.loader.contentHolder.objGestionnaireEvenements.repondreQuestion("-e+b*x");
					_level0.loader.contentHolder.box_question.gotoAndPlay(9);
					_level0.loader.contentHolder.objGestionnaireEvenements.getOurPerso().setMinigameLoade(false);

					horsService.removeMovieClip();
				}
				
				_level0.loader.contentHolder.type_question = "erreur connexion";
				monScroll.contentPath = null;
				loaded = true;
				
				Mouse.removeListener(_level0.loader.contentHolder.mouseListener);
	}
	
};

var sendReport:MovieClip;

this.btnSendError.onPress = function(){
	
	sendReport = _level0.loader.contentHolder.attachMovie("GUI_bugReport", "bugReport", 9999);
	Key.removeListener(oUserKey);
}

monScroll.addEventListener("complete", loadListener);
if(_level0.loader.contentHolder.type_question == "MINI_DOKU")
   monScroll.contentPath = "MINI/mathDoku.swf";
else
   monScroll.contentPath = _level0.loader.contentHolder.url_question; //"MINI/mathDoku.swf";//
var timerInit = getTimer();
var nbEssais = 1;

this.onEnterFrame = this.checkLoadProgress;


function checkLoadProgress()
{
	if(loaded == false) // car "this.onEnterFrame = null;" dans le "complete" ne semble pas marcher...???
	{
		var tempsPasse = (getTimer() -  timerInit)/1000;
	
		if(tempsPasse > 5)	// ca fait plus de 5 secondes qu'on essaye de loader la question
		{
			trace("Plus de 5 sec...");
			if(nbEssais < 3)
			{
				//renvoyer une requête
				if(_level0.loader.contentHolder.type_question == "MINI_DOKU")
                   monScroll.contentPath = "MINI/mathDoku.swf";
                else
                   monScroll.contentPath = _level0.loader.contentHolder.url_question; //"MINI/mathDoku.swf";//
				//monScroll.contentPath = "Q-1-en.swf";
				//reset le timer
				timerInit = getTimer();
				nbEssais++;
			}
			else
			{
				//_root.prob_txt._visible = true;
				//btn_ok_erreur._visible = true;

				var horsService:MovieClip;
				
				cadreLoading._visible = false;
		
				horsService = _level0.loader.contentHolder.attachMovie("GUI_erreur", "HorsService", 9999);
				
				horsService.textGUI_erreur.text = _level0.loader.contentHolder.texteSource_xml.firstChild.attributes.GUIerreurQuestion;
				//horsService.linkGUI_erreur._visible = false;
				//horsService.linkGUI_erreur.text = _level0.loader.contentHolder.url_question;//_root.texteSource_xml.firstChild.attributes.GUIhorsService2;
				//horsService.linkGUI_erreur.html = true;
				
				horsService.btn_ok.onRelease = function()
				{						
					_level0.loader.contentHolder.erreurConnexion = true;
					// envoyer une réponse assurement mauvaise
					_level0.loader.contentHolder.objGestionnaireEvenements.repondreQuestion("-e+b*x");
					_level0.loader.contentHolder.box_question.gotoAndPlay(9);
					_level0.loader.contentHolder.objGestionnaireEvenements.getOurPerso().setMinigameLoade(false);

					horsService.removeMovieClip();
				}
				/*
				var formatLink = new TextFormat();
				formatLink.url = _root.texteSource_xml.firstChild.attributes.GUIhorsServiceURL;
				formatLink.target = "_blank";
				formatLink.font = "Arial";
				formatLink.size = 12;
				formatLink.color = 0xFFFFFF;
				formatLink.bold = true;
				formatLink.underline = true;
				formatLink.align = "Center";
				
				horsService.linkGUI_erreur.setTextFormat(formatLink);
				*/
				_level0.loader.contentHolder.type_question = "erreur connexion";
				
				
				
				monScroll.contentPath = null;
				loaded = true;
				
				Mouse.removeListener(_level0.loader.contentHolder.mouseListener);
			}
		}
	}
}



function traiterReponseCourte()
{
	rep.text._visible = false;
	if(rep.text != "")
	{
		// faire valider la réponse=rep.text
		 _level0.loader.contentHolder.objGestionnaireEvenements.repondreQuestion(rep.text);		
	}	
	Key.removeListener(oUserKey);
}

function traiterReponseMiniDoku()
{
	var reponse:String = String(monContent.verifyIfDid());
   	
	_level0.loader.contentHolder.objGestionnaireEvenements.repondreQuestion(reponse);		
		
	Key.removeListener(oUserKey);
	btn_ok._visible = false;
}


function enleverBoutons()
{
	btn_a._visible = false;
	btn_b._visible = false;
	btn_c._visible = false;
	btn_d._visible = false;
	btn_e._visible = false;
	btn_ok._visible = false;
	rep._visible = false;
	btn_vrai._visible = false;
	btn_faux._visible = false;
	Key.removeListener(oUserKey);
	Mouse.addListener(_level0.loader.contentHolder.mouseListener);
	monScroll.contentPath = null;
	_level0.loader.contentHolder.objGestionnaireEvenements.getOurPerso().setUsedBook(false);
	_level0.loader.contentHolder.objGestionnaireEvenements.getOurPerso().setMinigameLoade(false);

	//btnSendError._visible = false;
}