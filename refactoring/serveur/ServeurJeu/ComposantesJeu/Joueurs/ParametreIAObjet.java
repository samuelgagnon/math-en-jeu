package ServeurJeu.ComposantesJeu.Joueurs;

// Cette classe contient tous les param�tres des objets que
// les joueurs virtuels ont besoin
public class ParametreIAObjet {
	
	// La valeur en points de cet objet
    public int intValeurPoints;
    
    // Une valeur al�atoire pour calculer des points un peu diff�rents
    public int intValeurAleatoire;
    
    // Distance maximal pour ne pas enlever trop de points
    public int intMaxDistance;
    
    // Points � enlever par coup de distance
    public int intPointsEnleverDistance;   
    
    // Points � enlever par quantit� d�j� en possession
    public int intPointsEnleverQuantite;
    
    // Points � donner lors du calcul des points d'un chemin
    public int intPointsChemin;
    
    // Points � enlever � l'objet dans le calcul des points d'un chemin
    // selon la quantit� poss�d�
    public int intPointsCheminEnleverQuantite;
    
    // Nombre d'objets maximum � poss�der par ce joueur virtuel
    public int intQuantiteMax;
    
    // Temps de s�ret� de fin de partie (on ne ramasse pas l'objet lorsqu'il
    // reste peu de temps en fin de partie (en secondes)
    public int intTempsSureteRamasser;
    
    // Constructeur de la classe, on passe tous les variables en param�tres
    public ParametreIAObjet(int valeurPoints, int valeurAleatoire, 
        int maxDistance, int pointsEnleverDistance, 
        int pointsEnleverQuantite, int pointsChemin,
        int pointsCheminEnleverQuantite, int quantiteMax,
        int tempsSureteRamasser)
    {
        intValeurPoints = valeurPoints;
	    intValeurAleatoire = valeurAleatoire;
	    intMaxDistance = maxDistance;
	    intPointsEnleverDistance = pointsEnleverDistance;   
	    intPointsEnleverQuantite = pointsEnleverQuantite;
	    intPointsChemin = pointsChemin;
	    intPointsCheminEnleverQuantite = pointsCheminEnleverQuantite;
	    intQuantiteMax = quantiteMax;
	    intTempsSureteRamasser = tempsSureteRamasser;
    }
    
}
