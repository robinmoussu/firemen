// Description d'une case
// Dernière modification : Thibaud BACKENSTRASS, 8 novembre
public class Case {
    // Coordonnées (entières, positives) de la case
    private int ligne;
    private int colonne;
    private NatureTerrain terrain;
    
    // Constructeur de case
    public Case(int ligne, int colonne, NatureTerrain terrain) {
        this.ligne = ligne;
        this.colonne = colonne;
        this.terrain = terrain;
    }
    
    
    public Case(int ligne, int colonnen) {
        this(ligne, colonnen, NatureTerrain.TERRAIN_LIBRE);
    }

    // Accesseurs
    public int getLigne() {
        return this.ligne;
    }
    public int getColonne() {
        return this.colonne;
    }
    public NatureTerrain getTerrain() {
        return this.terrain;
    }

    // Mutateurs
    public void setLigne(int ligne) throws ConstructionException {
        if(ligne<0) { // Invariant de classe
            throw new ConstructionException("Les coordonnées d'une case ne peuvent pas être négatives !");
        }
        
        this.ligne = ligne;
    }
    public void setColonne(int colonne) throws ConstructionException {
        if(colonne<0) { // Invariant de classe
            throw new ConstructionException("Les coordonnées d'une case ne peuvent pas être négatives !");
        }
        
        this.colonne = colonne;
    }
    public void setTerrain(NatureTerrain terrain) {
        this.terrain = terrain;
    }

    // On redéfinit un comparateur de Case "propre" depuis la superclasse Object
    @Override
    public boolean equals(Object o) {
        if(this==o) {
            return true;
        }
        if(!(o instanceof Case)) {
            return false;
        }

        Case c = (Case) o; // downcasting
        // Deux cases sont égales si et seulement si elles sont à la même place sur la carte
        return this.ligne == c.getLigne() && this.colonne == c.getColonne();
    }
    
    // On ajoute une méthode permettant de savoir facilement si deux cases sont voisines ou non
    public boolean estVoisine(Case c) {
        if(    (c.getLigne()-this.ligne==1 && c.getColonne()-this.colonne==0)
            || (c.getLigne()-this.ligne==-1 && c.getColonne()-this.colonne==0)
            || (c.getLigne()-this.ligne==0 && c.getColonne()-this.colonne==1)
            || (c.getLigne()-this.ligne==0 && c.getColonne()-this.colonne==-1)
          ) {
            return true;
        }
        else {
            return false;
        }
    }
    
    ////////////////////////////
    
    
    public Case up() {
        return new Case(this.ligne - 1, this.colonne);
    }
    
    public Case down() {
        return new Case(this.ligne + 1, this.colonne);
    }
    
    public Case left() {
        return new Case(this.ligne, this.colonne - 1);
    }
    
    public Case right() {
        return new Case(this.ligne, this.colonne + 1);
    }
    
    public String toString() {
        return "Case " + this.ligne + ", " + this.colonne;
    }
    
    /** Retourne la direction global permettant d'atteindre une autre case
     * 
     * @param other La case de destination
     * @return La direction pour se diriger vers other
     */
    public Direction getDirection(Case other) {
        if ((this.colonne <  other.colonne) && (this.ligne == other.ligne)) {
            return Direction.right;
        } else if ((this.colonne >  other.colonne) && (this.ligne == other.ligne)) {
            return Direction.left;
        } else if ((this.colonne == other.colonne) && (this.ligne >  other.ligne)) {
            return Direction.up;
        } else if ((this.colonne == other.colonne) && (this.ligne <  other.ligne)) {
            return Direction.down;
        } else if ((this.colonne <  other.colonne) && (this.ligne >  other.ligne)) {
            return Direction.topRight;
        } else if ((this.colonne <  other.colonne) && (this.ligne <  other.ligne)) {
            return Direction.bottomRight;
        } else if ((this.colonne >  other.colonne) && (this.ligne >  other.ligne)) {
            return Direction.topLeft;
        } else if ((this.colonne >  other.colonne) && (this.ligne <  other.ligne)) {
            return Direction.bottomLeft;
        } else {
            return Direction.same;
        }
    }
    
    public enum Direction {
        up,
        down,
        left,
        right,
        topLeft,
        topRight,
        bottomLeft,
        bottomRight,
        same,
    };
}
