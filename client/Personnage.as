﻿
/*******************************************************************
Math en jeu
Copyright (C) 2007 Projet SMAC

Ce programme est un logiciel libre ; vous pouvez le
redistribuer et/ou le modifier au titre des clauses de la
Licence Publique Generale Affero (AGPL), telle que publiee par
Affero Inc. ; soit la version 1 de la Licence, ou (a
votre discretion) une version ulterieure quelconque.

Ce programme est distribue dans l'espoir qu'il sera utile,
mais SANS AUCUNE GARANTIE ; sans meme une garantie implicite de
COMMERCIABILITE ou DE CONFORMITE A UNE UTILISATION
PARTICULIERE. Voir la Licence Publique
Generale Affero pour plus de details.

Vous devriez avoir recu un exemplaire de la Licence Publique
Generale Affero avec ce programme; si ce n'est pas le cas,
ecrivez a Affero Inc., 510 Third Street - Suite 225,
San Francisco, CA 94107, USA.

Changed 2009 Oloieri Lilian
*********************************************************************/

import mx.transitions.Tween;
import mx.transitions.easing.*;
import flash.filters.*;
import mx.controls.Loader;
//import flash.filters.ColorMatrixFilter;

class Personnage implements IPersonnage
{
	private var role:Number;             // role of user if 1 - simple user , if 2 - master(admin)
	private var image:MovieClip;
	private var position:Point;
	private var numero:Number;           // ????
	private var prochainePosition:Point;
	private var l:Number;
	private var c:Number;
	private var pointage:Number;
	private var argent:Number;
	private var listeDesObjets:Object;
	private var faireCollision:String;   // sert a savoir s'il y a eu collision apres un deplacement et avec quoi
	private var nom:String;               // name of user that is master of pers
		
	private var clothesColor:String;
	private var colorFilter:ColorMatrixFilter; // filter to color our perso 
	private var brainiacState:Boolean;
	private var brainiacRestedTime:Number;
	private var bananaId:Number;	
	private var bananaState:Boolean;
	private var bananaRestedTime:Number;	
	
	private var idClip:Number;           // number used to identify the movie used for perso - from 1 to 12
	private var orient:String;           // orientation ... right or left
	
	private static var BRAINIAC_TIME:Number = 60;    //  constant for the time that brainiac is active
	private var planche:PlancheDeJeu;
	
	////////////////////////////////////////////////////////////
	public function getIdPersonnage():Number
	{
		return numero;
	}
	
	////////////////////////////////////////////////////////////
	public function setIdPersonnage(n:Number)
	{
		numero = n;
	}
	
	function getBananaState():Boolean
	{
		return this.bananaState;
	}
	
	function setBananaState(bananaState:Boolean)
	{
		this.bananaState = bananaState;
	}
	
	function setBananaTime(bananaTime:Number)
	{
		this.bananaRestedTime = bananaTime;
	}
	
	function addBananaTime(bananaTime:Number)
	{
		this.bananaRestedTime += bananaTime;
	}
	
	function decreaseBananaTime()
	{
		if(this.bananaRestedTime > 0)
		   this.bananaRestedTime--;
	}
	
	function getBananaTime():Number
	{
		return this.bananaRestedTime;
	}
	
	
	function setUsedBook(bool:Boolean)
	{
		this.usedBook = bool;
	}
	
	function getUsedBook():Boolean
	{
		return this.usedBook;
	}
	
	function setBananaId(idNumber:Number)
	{
		this.bananaId = idNumber;
	}
	
	function getBananaId():Number
	{
		return this.bananaId;
	}
	
	public function setMinigameLoade(bool:Boolean)
	{
		this.minigameLoade = bool;
	}
		
	function getMinigameLoade()
	{
		return this.minigameLoade;
	}
	function setDirection(dir:String)
	{
		this.orient = dir;
	}
	
	function getDirection():String
	{
		return this.orient;
	}
	
	function decreaseBrainiacTime()
	{
		if(this.brainiacRestedTime > 0)
		   this.brainiacRestedTime--;
	}
	
	function getBrainiacTime():Number
	{
		return this.brainiacRestedTime;
	}
	
	function setBrainiacTime(time:Number)
	{
		this.brainiacRestedTime = time;
	}
	
	function setBoardCentre(centre:Boolean)
	{
		this.boardCentre = centre;
	}
	
	function getBoardCentre():Boolean
	{
		return this.boardCentre;
	}
	
	function setColor(n:String)
	{
		clothesColor = n;
	}
	
	function getColor():String
	{
		return clothesColor;
	}
	
	function setRole(n:Number)
	{
		role = n;
	}
	
	function getRole():Number
	{
		return role;
	}
	
	function setBrainiac(stateVar:Boolean)
	{
		brainiacState = stateVar;
	}
	
	function getBrainiac():Boolean
	{
		return brainiacState;
	}
   
	////////////////////////////////////////////////////////////
	function obtenirNom():String
	{
		return this.nom;
	}
	       
	////////////////////////////////////////////////////////////
	function acheterObjet(id:Number, objectName:String)
	{
		listeDesObjets[objectName].push(id);
		ajouterImageBanque(objectName);
	}
	
	////////////////////////////////////////////////////////////    
	function afficherObjets()
	{
		var i:Number;
		//trace("debut afficherObjets\n**********")
		for(var nom:String in listeDesObjets)
		{
			for(i in listeDesObjets[nom]) 
			trace(listeDesObjets[nom][i] + " - id : ");
		}
		//trace("**********\nfin afficherObjets")   
	}
	
	////////////////////////////////////////////////////////////   
	function obtenirNombreObjet()
	{
		var total:Number = 0;
		for(var nom:String in listeDesObjets)
		{
			total += listeDesObjets[nom].length; 			
		}
		
		//trace("total : " + total);
		return total;
	}
	
	////////////////////////////////////////////////////////////
	function ajouterObjet(id:Number, objectName:String)
	{
		listeDesObjets[objectName].push(id);
		ajouterImageBanque(objectName);
	}
	
	////////////////////////////////////////////////////////////
	function obtenirObjets():Object
	{
		return this.listeDesObjets;
	}
	
	
	function objectInListe(nomObj:String):Boolean
	{
		return this.listeDesObjets[nomObj].length >= 1;
		
	}
		
	////////////////////////////////////////////////////////////
	function zoomer(valeur:Number)
	{		
		image._xscale += valeur;
		image._yscale += valeur;
	}
	
	////////////////////////////////////////////////////////////
	function translater(la:Number, ha:Number)
	{
		image._x += la;
		image._y += ha;
		
		this.position.definirX(position.obtenirX() + la);
		this.position.definirY(position.obtenirY() + ha);
		this.prochainePosition.definirX(prochainePosition.obtenirX() + la);
		this.prochainePosition.definirY(prochainePosition.obtenirY() + ha);
	}
	
	////////////////////////////////////////////////////////////
	function obtenirL():Number
	{
		return l;
	}
	
	////////////////////////////////////////////////////////////
	function definirL(ll:Number)
	{
		l = ll;
	}
	
	////////////////////////////////////////////////////////////
	function obtenirC():Number
	{
		return c;
	}
	
	////////////////////////////////////////////////////////////
	function definirC(cc:Number)
	{
		c = cc;
	}
	
	////////////////////////////////////////////////////////////
	function obtenirNumero():Number
	{
		return numero;
	}
	
	////////////////////////////////////////////////////////////
	function definirNumero(n:Number)
	{
		numero = n;
	}
	
	////////////////////////////////////////////////////////////
	function obtenirPointage():Number
	{
		return pointage;
	}
	
	////////////////////////////////////////////////////////////
	function modifierPointage(x:Number)
	{
		pointage = x;
	}
	
	////////////////////////////////////////////////////////////
	function obtenirArgent():Number
	{
		return argent;
	}
	
	
	////////////////////////////////////////////////////////////
	function modifierArgent(x:Number)
	{
		argent = x;
		_level0.loader.contentHolder.objectMenu["piece"].countTxt = x;
	}
	
	////////////////////////////////////////////////////////////
	function obtenirImage():MovieClip
	{
		return image;
	}
	
	////////////////////////////////////////////////////////////
	function definirImage(i:MovieClip)
	{
		image = i;
	}
	
	////////////////////////////////////////////////////////////
	function obtenirPosition():Point
	{
		return position;
	}
	
	
	////////////////////////////////////////////////////////////
	function definirPosition(p:Point, ll:Number, cc:Number)
	{
		//trace("test drift :" + image._x + " " + image._y);
		image._x = p.obtenirX();
		image._y = p.obtenirY();
		//trace("test drift after :" + image._x + " " + image._y);
		this.position.definirX(p.obtenirX());
		this.position.definirY(p.obtenirY());
		this.l = ll;
		this.c = cc;
	}

	////////////////////////////////////////////////////////////
	function obtenirProchainePosition():Point
	{
		return prochainePosition;
	}
	
		
	//////////////////////////////////////////////////////////////////////////////////////
	//	CONSTRUCTEUR
	//////////////////////////////////////////////////////////////////////////////////////
	function Personnage(idPers:Number, nom:String, role:Number, nomClip:Number, ll:Number, cc:Number, cloColor:String, planches:PlancheDeJeu)
	{
		trace(" idPers : " + idPers + " nom : "+ nom + " role : " + role + " nomClip : " + nomClip + " ll : " + ll + " cc : " + cc + " cloColor : " + cloColor + "planches : " + planches);
		this.planche = planches;
		var niveau:Number = 5 * planche.obtenirTableauDesCases().length * planche.obtenirTableauDesCases()[0].length + 2 * idPers;
		var xx:Number = planche.obtenirTableauDesCases()[ll][cc].obtenirClipCase()._x;
		var yy:Number = planche.obtenirTableauDesCases()[ll][cc].obtenirClipCase()._y;
				
		////////////
		this.l = ll;
		this.c = cc;
		this.numero = idPers;
		this.position = new Point(xx,yy);
		this.prochainePosition = new Point(xx,yy);
		this.clothesColor = cloColor;
		this.idClip = nomClip;
        this.colorFilter = _level0.loader.contentHolder.objGestionnaireEvenements.colorMatrixPerso(this.clothesColor, this.idClip);
		var filterC:ColorMatrixFilter = this.colorFilter;
		
		this.brainiacState = false;
		this.brainiacRestedTime = 0; 
		
		this.bananaState = false;
	    this.bananaRestedTime = 0;
		
		// to load the perso .. use ClipLoader to know the moment of complet load
		var myLoader:MovieClipLoader = new MovieClipLoader();
	    //myLoader.addListener(image);
		
		var mclListener:Object = new Object();
        mclListener.onLoadComplete = function(target_mc:MovieClip) {
            target_mc.filterC = filterC;
		    target_mc.nom = nom;
						
			target_mc.gotoAndPlay("bored");
			//target_mc.gotoAndStop(1);
			
			// assure que le clip a la bonne orientation
			//target_mc._xscale = - Math.abs(target_mc._xscale);
			//target_mc.dtNom._xscale = - Math.abs(target_mc._xscale);
			//target_mc.dtNom._x = 42;
			target_mc._visible = true;
        };
		myLoader.addListener(mclListener);
		
		this.orient = "right";
       
		// on use if to not see master in tournament game
		if(!(role == 2 && _level0.loader.contentHolder.objGestionnaireEvenements.typeDeJeu == "Tournament")){  
  
           image =  _level0.loader.contentHolder.referenceLayer.createEmptyMovieClip("Personnage" + idPers, niveau);
		    myLoader.loadClip("Perso/perso" + nomClip + ".swf", image); 
					
		}
					
		this.pointage = 0;
		this.argent = 0;
		this.listeDesObjets = new Object();
		
		
		// each array will contain the ID's of objects in possession
		//this.listeDesObjets["piece"] = new Array();
		this.listeDesObjets["Banane"] = new Array();
		this.listeDesObjets["Livre"] = new Array();
		this.listeDesObjets["Boule"] = new Array();
		
		this.faireCollision = null;
		this.nom = nom;
		//this.listeSurMagasin = mag;
		this.minigameLoade = false;
		this.role = role;
		this.boardCentre = false;
	}// end constr
	
	
	////////////////////////////////////////////////////////////
	function deplacePersonnage()
	{
		var pourcent:Number;
		var dx:Number = 0;
		var dy:Number = 0;
		var reafficher1:Boolean = true;
		var reafficher2:Boolean = false;
		
		var isOurName:Boolean = (this.nom == _level0.loader.contentHolder.objGestionnaireEvenements.obtenirNomUtilisateur());
		
		
		dx = this.prochainePosition.obtenirX() - this.position.obtenirX();  
		dy = this.prochainePosition.obtenirY() - this.position.obtenirY();
		
		//trace("ds deplacePersonnage " + this.prochainePosition.obtenirX() + " " + this.position.obtenirX());
		//trace("ds deplacePersonnage " + dx + " " + dy);

		//if(isOurName)
		  //trace("Test sur retour!!! " + boardCentre);
		
		if( boardCentre && planche.getRepostCases() && (isOurName))
		{
			planche.effacerCasesPossibles(_level0.loader.contentHolder.planche.obtenirPerso());
			planche.afficherCasesPossibles(_level0.loader.contentHolder.planche.obtenirPerso());
			planche.setRepostCases(false);
			// trace("Test sur retour 1 " + boardCentre);

			return;
			
		}
		else if( boardCentre  &&  !planche.getShowCases() && (isOurName)) // to consider the case that we don't have possibility to move 
		{
			planche.afficherCasesPossibles(_level0.loader.contentHolder.planche.obtenirPerso());
			//trace("Le test de deplacement!!!!")
            //trace("Test sur retour 2 " + boardCentre);

			return;
		}
		else if( boardCentre ) //dx == 0 && dy == 0 && image._currentFrame == 1) 
		{
					//  trace("Test sur retour 3 " + boardCentre);

			return;
		}
		
		if ((dx == 0) && (dy == 0))
		{
			if(image._currentFrame != 1 && image._currentFrame < 90)
			{
				// place le personnage au repos et de face
				image.gotoAndStop(1);
		
				switch(this.faireCollision)
				{
					case "piece":
						// l'argent est ajoute au personnage dans les fichiers gestEve et gestComm
						// en gros, ici, on enleve l'image du board
    					planche.enleverPiece(this.l, this.c);
						planche.modifierNumeroCase(this.l, this.c, -10000);
						_level0.loader.contentHolder.array_sons[9].start(0,1);
						this.faireCollision = null;
					break;
				
					case "magasin":
						//si notre personnage tombe sur un magasin, on charge le GUI_magasin
						if(isOurName)
						{
							this.minigameLoade = true;
							reafficher1 = false;
							planche.effacerCasesPossibles(_level0.loader.contentHolder.planche.obtenirPerso());
							_root.objGestionnaireInterface.effacerBoutons(1);			
							
							// on charge le magasin : on ajuste sa position a l'ecran
							// on indique qu'il ne s'agit pas d'un mini-game.
						
							var minigame:MovieClip;   
							minigame = _level0.loader.contentHolder.miniGameLayer.attachMovie("GUI_magasin", "magasin", 9997);
							minigame._x = 0;
							minigame._y = 0;
							minigame._xscale = 100;
							minigame._yscale = 100;
							//minigame._width = 550;
							//minigame._height= 400;
							minigame._visible = false;
						
						
							//variables qui servent pour un fade-in/fade-out a l'entree du magasin
							var oListener:Object = new Object();  
							var twMove:Tween;
							var twMove2:Tween;
							var mClip1:MovieClip;
	
							mClip1 = _level0.loader.contentHolder.attachMovie("masque", "masqueA", 1,{_x:270, _y: 200});
							mClip1._alpha = 0;
						
							twMove = new Tween(mClip1, "_alpha", Regular.easeOut, 0, 100, 1, true);
							twMove.addListener(oListener);
	
							oListener.onMotionFinished = function():Void 
							{ 
								twMove2 = new Tween(mClip1, "_alpha", Regular.easeOut, 100, 0, 1, true);
								_level0.loader.contentHolder.menuOutils._visible = false;
								//_level0.loader.contentHolder.enteteHolder._visible = false;
								_level0.loader.contentHolder.horloge._visible = false;// ?????  a think is not correct any more all 3 statements
								minigame._visible = true;
							}
						
							this.faireCollision = null;
						}
						else
						{
							faireCollision = null;
						}
					break;
				
					// Pour tous les objets, on se comporte de la meme maniere :
					// on enleve l'objet (meme si on en a 10 : parce que le serveur nous dit qu'on l'enleve)
					// si necessaire, on l'ajoute a nos objets
					
					case "Livre":
						_level0.loader.contentHolder.planche.enleverObjet(this.l, this.c);
						_level0.loader.contentHolder.planche.modifierNumeroCase(this.l, this.c, -30000);
						this.faireCollision = null;
				       
					break;
				
					case "Telephone":
						_level0.loader.contentHolder.planche.enleverObjet(this.l, this.c);
						_level0.loader.contentHolder.planche.modifierNumeroCase(this.l, this.c, -30000);
						this.faireCollision = null;
                        
					break;
				
					case "Boule":
						_level0.loader.contentHolder.planche.enleverObjet(this.l, this.c);
						_level0.loader.contentHolder.planche.modifierNumeroCase(this.l, this.c, -30000);
						this.faireCollision = null;
                      
					break;
				
					case "PotionGros":
						_level0.loader.contentHolder.planche.enleverObjet(this.l, this.c);
						_level0.loader.contentHolder.planche.modifierNumeroCase(this.l, this.c, -30000);
						this.faireCollision = null;
                       
					break;
				
					case "PotionPetit":
						_level0.loader.contentHolder.planche.enleverObjet(this.l, this.c);
						_level0.loader.contentHolder.planche.modifierNumeroCase(this.l, this.c, -30000);
						this.faireCollision = null;
                       
					break;
				
					case "Banane":
						_level0.loader.contentHolder.planche.enleverObjet(this.l, this.c);
						_level0.loader.contentHolder.planche.modifierNumeroCase(this.l, this.c, -30000);
						this.faireCollision = null;
                        
					break;
					
					case "Brainiac":
					   	_level0.loader.contentHolder.planche.enleverObjet(this.l, this.c);
						_level0.loader.contentHolder.planche.modifierNumeroCase(this.l, this.c, -30000);
						this.faireCollision = null;
						getBrainiacAnimaton(BRAINIAC_TIME);
                        
					break;

				
					default :
						//trace("pas de collision");	
					break;
				}// switch(this.faireCollision)
			
				if(isOurName)
				{
					if(_level0.loader.contentHolder.planche.estCaseSpeciale(this.l, this.c) &&  _level0.loader.contentHolder.sortieDunMinigame == false)
					{
						this.minigameLoade = true;
						 _level0.loader.contentHolder.planche.effacerCasesPossibles(_level0.loader.contentHolder.planche.obtenirPerso());
						_level0.objGestionnaireInterface.effacerBoutons(1);
					
						_level0.loader.contentHolder.minigameToLoad = _level0.loader.contentHolder.url_Minigames[Math.floor(Math.random() * _level0.loader.contentHolder.url_Minigames.length)];

						//variables qui servent pour un fade-in/fade-out a l'entree du magasin
						var oListener:Object = new Object();  
						var twMove:Tween;
						var twMove2:Tween;
						var mClip1:MovieClip;
	
						mClip1 = _level0.loader.contentHolder.attachMovie("masque", "masqueA", 9998,{_x:270, _y: 200});
						mClip1._alpha = 0;
						
						var minigame:MovieClip;
						minigame = _level0.loader.contentHolder.miniGameLayer.attachMovie("GUI_minigame", "Minigame", 9997);
						minigame._visible = false;
					
						switch(_level0.loader.contentHolder.minigameToLoad)
						{
							case "ileTresor.swf":
								minigame._x = 40;
								minigame._y = -40;
								minigame._width = 550;
								minigame._height= 400;
							break;
							
							case "balleAuMur2.swf":
								minigame._x = 0;
								minigame._y = 40;
								minigame._width = 550;
								minigame._height= 400;
							break;
							
							default:
								minigame._x = 15;
								minigame._y = - 40;
								minigame._width = 485;
								minigame._height= 385;
							break;
						}
						
						twMove = new Tween(mClip1, "_alpha", Regular.easeOut, 0, 100, 1, true);
						twMove.addListener(oListener);
	
						oListener.onMotionFinished = function():Void 
						{ 
							twMove2 = new Tween(mClip1, "_alpha", Regular.easeOut, 100, 0, 1, true);
							minigame._visible = true;
						}
					} // if(_level0.loader.contentHolder.planche.estCaseSpeciale(this.l, this.c) &&  _level0.loader.contentHolder.sortieDunMinigame == false)
					else reafficher2 = true;
				}// if(this.nom == _level0.loader.contentHolder.planche.obtenirNomDeMonPersonnage())
			}// if(image._currentFrame != 1 && image._currentFrame < 90)
			
			if(reafficher1 && reafficher2) this.minigameLoade = false;
			
			//Si le perso est le mien et qu'il est au repos, mais que le board n'est pas centre
			if(isOurName)
			{
				if(_level0.loader.contentHolder.planche.recentrerBoard(this.l, this.c, false))
				{
					if(!this.minigameLoade)
					{
						_level0.loader.contentHolder.planche.afficherCasesPossibles(_level0.loader.contentHolder.planche.obtenirPerso());
						_level0.loader.contentHolder.planche.setRepostCases(false);
						
					}
					boardCentre = true;
					//trace(_level0.loader.contentHolder.horlogeNum + "temps restant");
					
				}
			} 
			
			return; // pour ne pas faire le reste des verifications inutilement si dx == dy == 0
		} // if ((dx == 0) && (dy == 0))
		
		//pour le reste, ((dx == 0)&&(dy == 0) != 1)
		
		if(Math.abs(dx) > Math.abs(dy))
		{
			if(Math.abs(dx) > 10)
			{
				pourcent = 10/Math.abs(dx);
				dx *= pourcent;
				dy *= pourcent;
				dy = Math.round(dy);
			}
		}
		else(Math.abs(dy) > Math.abs(dx))
		{
			if(Math.abs(dy) > 10)
			{
				pourcent = 10/Math.abs(dy);
				dx *= pourcent;
				dy *= pourcent;
			}
		}
		
				
		//trace("trace drift dx : " + dx + " dy : " + dy + " control : " + dx/dy);
		if (dy < 0)
		{
			if(image._currentFrame < 70)
			{
				image.gotoAndPlay(70);
				//trace("perso " + image._currentFrame);
			}
			
		}
		else
		{
			if(image._currentFrame > 69)
			{
				image.gotoAndPlay(10);
			}
		}
				
		if (dx >= 0)
		{
			// assure que le clip a la bonne orientation
			image._xscale = - Math.abs(image._xscale);
			image.dtNom._xscale = - Math.abs(image._xscale);
			image.dtNom._x = 42;
			this.orient = "right";
		}
		else
		{
			// flip le clip pour aller vers la gauche
			image._xscale =  Math.abs(image._xscale);
			image.dtNom._xscale =   Math.abs(image._xscale);
			image.dtNom._x = - 42;
			this.orient = "left";
		}
		
		
		// deplace le clip
		image._x += dx;  
		image._y += dy;
		position.definirX(position.obtenirX()+ dx);
		position.definirY(position.obtenirY()+ dy);
	
		// Si le deplacement voulu n'est pas nul mais que le personnage est au repos
		//trace("avant le if le frame  :   "+this.image._currentFrame);
		if (((dx != 0) || (dy != 0)) && (this.image._currentFrame == 1 || this.image._currentFrame > 89))
		{
			// place le clip du personnage au debut de la sequence de deplacement
			image.gotoAndPlay(10);
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////
	function afficher()
	{
		image._visible = true;
		image._x = position.obtenirX();  
		image._y = position.obtenirY();
	}
	
	function afficherAutreDir()
	{
	       // assure que le clip a la bonne orientation
			image._xscale = - Math.abs(image._xscale);
			image.dtNom._xscale = - Math.abs(image._xscale);
			image.dtNom._x = 42;
			this.orient = "right";
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	function cachePersonnage()
	{
		image._visible = false;
	}	
	
	//////////////////////////////////////////////////////////////////////////////////////
	////  pt contient la ligne et la colonne PAS LES X et Y
	function definirProchainePosition(pt:Point, str:String)
	{
		_level0.loader.contentHolder.planche.tableauDesCases[this.l][this.c].retirerPersonnage(this);
		
		this.l = pt.obtenirX();
		this.c = pt.obtenirY();
		//trace("definirProchainePosition : pt " + pt.obtenirY());
  
		_level0.loader.contentHolder.planche.tableauDesCases[this.l][this.c].ajouterPersonnage(this);
		
		this.prochainePosition.definirX(_level0.loader.contentHolder.planche.tableauDesCases[this.l][this.c].obtenirClipCase()._x);
		this.prochainePosition.definirY(_level0.loader.contentHolder.planche.tableauDesCases[this.l][this.c].obtenirClipCase()._y);
		
		//trace("definirProchainePosition : def Y " + _level0.loader.contentHolder.planche.tableauDesCases[this.l][this.c].obtenirClipCase()._y);
		
		this.faireCollision = str;
		this.boardCentre = false;
		
		trace("board " + this.boardCentre);
	}

	//////////////////////////////////////////////////////////////////////////////////////
	function obtenirX():Number
	{
		return position.obtenirX();
	}

	//////////////////////////////////////////////////////////////////////////////////////
	function obtenirY():Number
	{
		return position.obtenirY();
	}
		
	//////////////////////////////////////////////////////////////////////////////////////
	function rire()
	{
		image.gotoAndPlay("rire");
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	function tossBanana()
	{
		this.image.gotoAndPlay("tossing");
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	function slippingBanana()
	{
		//this.image.gotoAndStop("rest");
		this.image.gotoAndPlay("slipping");
		
		 // in this 3 lines we transfer player from Brainiac state to Banana state
		 this.setBrainiac(false);
		 this.setBrainiacTime(0);
		 this.setBananaState(true);
		//trace("slipping !!!!!!!!!!!!!!!!!");
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	function rest()
	{
		this.image.gotoAndStop("rest");
	}
	
	// used to put the Braniac animation on the player  for the specified time
	function getBrainiacAnimaton(brainiacTime:Number)
	{		
	   var playerUnder:String = this.nom;
	   // to color our perso
	   
	   // to set off Banana effects on the player
	   this.cancelBanana();
	   
	   var filterC:ColorMatrixFilter = this.colorFilter;
						
	   if(this.brainiacRestedTime == 0)
	   {
	      //******************************** new one *************
		  // to load the perso .. use ClipLoader to know the moment of complet load
		  var myLoader:MovieClipLoader = new MovieClipLoader();
	  	
		  if(this.orient == "right")
		  {
				image._xscale = Math.abs(image._xscale);
			    image.dtNom._xscale = Math.abs(image._xscale);
			    image.dtNom._x = - 42;
				this.orient == "left";
				
		  } 
		  var nameX:String = this.nom;
		  var orientDir:String = this.orient;
		  var mclListener:Object = new Object();
          mclListener.onLoadComplete = function(target_mc:MovieClip) {
            /*
			trace("orient ::: " + orientDir);
			if(orientDir == "Est")
			{
				target_mc._xscale = - Math.abs(target_mc._xscale);
			    target_mc.dtNom._xscale = - Math.abs(target_mc._xscale);
			    target_mc.dtNom._x = 42;
			}*/
			//target_mc.clothesCol = col;
			target_mc.filterC = filterC; 
			target_mc.nom = nameX;
			target_mc.gotoAndPlay("grow");
			
			
          };
		  myLoader.addListener(mclListener);

          myLoader.loadClip("Perso/perso" + this.idClip + "brainiac.swf", image); 
		  
		
		 
		  if(_level0.loader.contentHolder.objGestionnaireEvenements.obtenirNomUtilisateur() == playerUnder)
		     _level0.loader.contentHolder.objGestionnaireEvenements.setBrainiacTimer(playerUnder);
			 
		  endOnBrainiac();
		  
	   }else if(this.brainiacRestedTime > 0)
	   {
		   
	   }
	   
	   this.brainiacRestedTime += brainiacTime;
	   
	   _level0.loader.contentHolder.planche.setRepostCases(true);
	
	} // end of getBraniacAnimation
	
	// used to put the Braniac animation on the player  for the specified time
	function getReconnectionBrainiacAnimaton(brainiacTime:Number)
	{		
	   var playerUnder:String = this.nom;
	   // to color our perso
	   
	   // to set off Banana effects on the player
	   this.cancelBanana();
	   
	   var filterC:ColorMatrixFilter = this.colorFilter;
						
	   if(this.brainiacRestedTime == 0)
	   {
	      //******************************** new one *************
		  // to load the perso .. use ClipLoader to know the moment of complet load
		  var myLoader:MovieClipLoader = new MovieClipLoader();
	  	
		  if(this.orient == "right")
		  {
				image._xscale = Math.abs(image._xscale);
			    image.dtNom._xscale = Math.abs(image._xscale);
			    image.dtNom._x = - 42;
				this.orient == "left";
				
		  } 
		  var nameX:String = this.nom;
		  var orientDir:String = this.orient;
		  var mclListener:Object = new Object();
          mclListener.onLoadComplete = function(target_mc:MovieClip) {
            /*
			trace("orient ::: " + orientDir);
			if(orientDir == "Est")
			{
				target_mc._xscale = - Math.abs(target_mc._xscale);
			    target_mc.dtNom._xscale = - Math.abs(target_mc._xscale);
			    target_mc.dtNom._x = 42;
			}*/
			//target_mc.clothesCol = col;
			target_mc.filterC = filterC; 
			target_mc.nom = nameX;
			target_mc.gotoAndPlay("bored");
			
			
          };
		  myLoader.addListener(mclListener);

          myLoader.loadClip("Perso/perso" + this.idClip + "brainiac.swf", image); 
		  
		
		 
		  if(_level0.loader.contentHolder.objGestionnaireEvenements.obtenirNomUtilisateur() == playerUnder)
		     _level0.loader.contentHolder.objGestionnaireEvenements.setBrainiacTimer(playerUnder);
			 
		  endOnBrainiac();
		  
	   }else if(this.brainiacRestedTime > 0)
	   {
		   
	   }
	   
	   this.brainiacRestedTime += brainiacTime;
	   
	   _level0.loader.contentHolder.planche.setRepostCases(true);
	
	} // end of getReconnectionBraniacAnimation
	
	function endOnBrainiac()
	{
		
		var playerUnder:String = this.nom;
		var id:Number = this.idClip;
		var restedTime:Number;
		var filterC:ColorMatrixFilter = this.colorFilter;
			   	  	    
	    var intervalIDEndBrain = setInterval(etapeEndBrain, 1000, playerUnder);	
		
		function etapeEndBrain():Void
		{    
		   var image:MovieClip = _level0.loader.contentHolder.planche.getPersonnageByName(playerUnder).obtenirImage(); 
		   _level0.loader.contentHolder.planche.getPersonnageByName(playerUnder).decreaseBrainiacTime();
		   restedTime = _level0.loader.contentHolder.planche.getPersonnageByName(playerUnder).getBrainiacTime(); 
		   //trace("test brainiac2 " + image._currentFrame)
           		  
		   if( restedTime == 1 && (image._currentFrame == 1 || image._currentFrame == 90))
		   {
			  _level0.loader.contentHolder.planche.getPersonnageByName(playerUnder).obtenirImage().gotoAndPlay("down");
			  
		   }
		   else if( restedTime == 0 && (image._currentFrame == 1 ||	image._currentFrame == 90 || image._currentFrame == 96))
	       {
			  //_level0.loader.contentHolder.planche.getPersonnageByName(playerUnder).obtenirImage().gotoAndPlay("down");
			  _level0.loader.contentHolder.planche.getPersonnageByName(playerUnder).setBrainiac(false);
			  if(_level0.loader.contentHolder.planche.getPersonnageByName(playerUnder).getDirection() == "right")
			  {
				image._xscale = Math.abs(image._xscale);
			    image.dtNom._xscale = Math.abs(image._xscale);
			    image.dtNom._x = - 42;
				_level0.loader.contentHolder.planche.getPersonnageByName(playerUnder).setDirection("left");
			  } 
			  myLoader.loadClip("Perso/perso" + id + ".swf", image); 
			  clearInterval(intervalIDEndBrain);
			  
		   }  
		  			
		}// end method etapeEndBrain   
	   
	    var myLoader:MovieClipLoader = new MovieClipLoader();
		var mclListener:Object = new Object();
		
        mclListener.onLoadComplete = function(target_mc:MovieClip) {
            
			target_mc.filterC = filterC;
			target_mc.nom = playerUnder;
			target_mc.gotoAndPlay("bored");
						
        };
		myLoader.addListener(mclListener);
		
	}//end endOnBrainiac
	
	private function cancelBanana()
	{
		bananaState = false;
	    bananaRestedTime = 1;
		//_level0.loader.contentHolder.objGestionnaireEvenements.setBrainiacTimer(playerUnder); 
	}
	
}