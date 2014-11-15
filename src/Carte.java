// Description de la carte
// Dernière modification : Thibaud BACKENSTRASS, 9 novembre
public class Carte {
    // Attributs
    private int nbLignes; // >=0
    private int nbColonnes; // >=0
    private int tailleCases; // >=0

    private Case[][] carte;

    // Constructeur
    public Carte(int nbLignes, int nbColonnes, int tailleCases) throws ConstructionException {
        if(nbLignes<=0 || nbColonnes<=0 || tailleCases<=0) {
            throw new ConstructionException("Les paramètres de la carte doivent être positifs !");
        }
        
        this.nbLignes = nbLignes;
        this.nbColonnes = nbColonnes;
        this.tailleCases = tailleCases;
        this.carte = new Case[nbLignes][nbColonnes];
        // On n'initialise pas les cases ici, on dispose d'une méthode plus bas pour le faire
    }

    // Accesseurs
    public int getNbLignes() {
        return this.nbLignes;
    }
    public int getNbColonnes() {
        return this.nbColonnes;
    }
    public int getTailleCases() {
        return this.tailleCases;
    }

    // Pas de mutateurs : la carte est chargée une fois pour toutes en mémoire

    // Initialisation d'une case à la place (i, j)
    public void setCase(int ligne, int colonne, NatureTerrain t) throws ConstructionException {
        if(ligne<0 || ligne>=nbLignes || colonne<0 || colonne>=nbColonnes) {
            throw new ConstructionException("Création d'une case en-dehors des limites de la carte !");
        }
        
        carte[ligne][colonne] = new Case(ligne, colonne, t); // On crée une case (composition) qui sera détruite à la destruction de la Carte
    }

    // Récupérer une référence sur une case à partir de ses coordonnées
    public Case getCase(int ligne, int colonne) throws SimulationException {
        if(ligne<0 || ligne>=nbLignes || colonne<0 || colonne>=nbColonnes) {
            throw new SimulationException("Accès à une case en-dehors des limites de la carte !");
        }
        
        return carte[ligne][colonne];
    }

    // Rechercher si un voisin existe
    public boolean voisinExiste(Case src, Direction dir) {
        switch(dir) {
            case NORD:  // Il faut pouvoir atteindre la ligne 0
                        if(src.getLigne()>0 && src.getLigne()<nbLignes && src.getColonne()>=0 && src.getColonne()<nbColonnes) {
                           return true;
                        }
                        break;
            case SUD:   // Il faut pouvoir atteindre la ligne nbLignes-1
                        if(src.getLigne()>=0 && src.getLigne()<nbLignes-1 && src.getColonne()>=0 && src.getColonne()<nbColonnes) {
                            return true;
                        }
                        break;
            case EST:   // Il faut pouvoir atteindre la colonne nbColonnes-1
                        if(src.getLigne()>=0 && src.getLigne()<nbLignes && src.getColonne()>=0 && src.getColonne()<nbColonnes-1) {
                            return true;
                        }
                        break;
            case OUEST: // Il faut pouvoir atteindre la colonne 0
                        if(src.getLigne()>=0 && src.getLigne()<nbLignes && src.getColonne()>0 && src.getColonne()<nbColonnes) {
                            return true;
                        }
                        break;
            default: return false;
        }
        return false;
    }
    
    // Renvoyer une référence sur la case du voisin
    public Case getVoisin(Case src, Direction dir) throws SimulationException {
        if(this.voisinExiste(src, dir) != true) {
			throw new SimulationException("Pas de voisin dans la direction spécifiée !");
		}
	
        switch(dir) {
            case NORD:  return carte[src.getLigne()-1][src.getColonne()];
            case SUD:   return carte[src.getLigne()+1][src.getColonne()];
            case EST:   return carte[src.getLigne()][src.getColonne()+1];
            case OUEST: return carte[src.getLigne()][src.getColonne()+1];
        }

        return null;
    }

    // Savoir si une case est en bordure de l'eau ou non
    public boolean estBordEau(Case c) {
        try {
            if(     (voisinExiste(c, Direction.NORD)==true && getVoisin(c, Direction.NORD).getTerrain() == NatureTerrain.EAU)
                 || (voisinExiste(c, Direction.SUD)==true && getVoisin(c, Direction.SUD).getTerrain() == NatureTerrain.EAU)
                 || (voisinExiste(c, Direction.EST)==true && getVoisin(c, Direction.EST).getTerrain() == NatureTerrain.EAU)
                 || (voisinExiste(c, Direction.OUEST)==true && getVoisin(c, Direction.OUEST).getTerrain() == NatureTerrain.EAU)
            ) {
                return true;
            }
            else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

}
