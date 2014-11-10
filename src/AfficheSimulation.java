// Classe d'affichage des données de la simulation depuis le fichier
// Dernière modification : Thibaud BACKENSTRASS, 10 novembre
import ihm.*;
import java.awt.Color;
import java.util.LinkedList;
import java.io.FileNotFoundException;

public class AfficheSimulation {
	public static void main(String[] args) {
        if(args.length < 1) {
            System.out.println("Syntaxe: java AfficheSimulation <nomDeFichier>");
            System.exit(1);
        }
        try {
            DonneesSimulation simulation = LecteurDonnees.creeDonnees(args[0]);
            Firemen firement = new Firemen(simulation);
		} catch (FileNotFoundException e) {
			System.out.println("fichier " + args[0] + " inconnu ou illisible");
		} catch (ExceptionFormatDonnees e) {
			System.out.println("\n\t**format du fichier " + args[0] + " invalide: " + e.getMessage());
        }
	}
}

class Firemen implements Simulable {
    private DonneesSimulation simulation;
	private int nbLignes;
	private int nbColonnes;
    private IGSimulateur ihm;  // l'IHM associee a ce simulateur
    private long date = 0;
    
	public Firemen(DonneesSimulation data) {
		// cree l'IHM et l'associe a ce simulateur (this), qui en tant que
		// Simulable recevra les evenements suite aux actions de l'utilisateur
        nbLignes = data.getNbLignes();
        nbColonnes = data.getNbColonnes();
        simulation = data;
		ihm = new IGSimulateur(nbColonnes, nbLignes, this);
		dessine();    // mettre a jour l'affichage
	}
	
	@Override
	public void next() {
		date++;
		System.out.println("TODO: avancer la simulation \"d'un pas de temps\": " + date);
		dessine();    // mettre a jour l'affichage
	}

	@Override
	public void restart() {
		System.out.println("TODO: remettre le simulateur dans son état initial");
		date = 0;
		dessine();    // mettre a jour l'affichage
	}

	private void dessine() {
        // Afficher les donnees 		
		try {
            // Affichage des données sur la nature du terrain
            for(int i=0; i<nbLignes; i++) {
                for(int j=0; j<nbColonnes; j++) {
                    Case c = (simulation.getCarte()).getCase(i, j);
                    // ATTENTION !
                    // i désigne la ligne, j la colonne de la case
                    // Il faut inverser les données pour les méthodes de ihm, qui veulent d'abord la colonne, puis la ligne
                    // Sinon problème de transposée...
                    switch(c.getTerrain()) {
                        case EAU:             ihm.paintImage(j, i, "images/eau.png", 1, 1);
                                              break;
                        case FORET:           ihm.paintImage(j, i, "images/foret.png", 1, 1);
                                              break;
                        case ROCHE:           ihm.paintImage(j, i, "images/roche.png", 1, 1);
                                              break;
                        case TERRAIN_LIBRE:   ihm.paintImage(j, i, "images/terrain_libre.png", 1, 1);
                                              break;
                        case HABITAT:         ihm.paintImage(j, i, "images/habitat.png", 1, 1);
                                              break;
                        default:    ihm.paintBox(j, i, Color.GRAY); // Erreur
                                    break;
                    }
			        //ihm.paintImage(4, 15, "images/feu.png", 0.8, 0.8);
        			//ihm.paintString(7, 15, Color.YELLOW, "I");
                }
            }

            // Affichage des incendies avec une taille croissante avec l'intensité
            LinkedList<Incendie> incendies = simulation.getIncendies();
            for(Incendie i : incendies) {
                if(i.getIntensite()>0) {
                    if(i.getIntensite()<1000) {
                        ihm.paintImage((i.getPosition()).getColonne(), (i.getPosition()).getLigne(), "images/incendie.png", 0.5, 0.5);
                    }
                    else if(i.getIntensite()<10000) {
                        ihm.paintImage((i.getPosition()).getColonne(), (i.getPosition()).getLigne(), "images/incendie.png", 0.7, 0.7);
                    }
                    else {
                        ihm.paintImage((i.getPosition()).getColonne(), (i.getPosition()).getLigne(), "images/incendie.png", 0.9, 0.9);
                    }
                }
            }

            // Affichage des robots
            LinkedList<Robot> robots = simulation.getRobots();
            for(Robot r : robots) {
                switch(r.getTypeRobot()) {
                    case DRONE:     ihm.paintImage((r.getPosition()).getColonne(), (r.getPosition()).getLigne(), "images/drone.png", 0.9, 0.9);
                                    break;
                    case ROUES:     ihm.paintImage((r.getPosition()).getColonne(), (r.getPosition()).getLigne(), "images/roues.png", 0.9, 0.9);
                                    break;
                    case CHENILLES: ihm.paintImage((r.getPosition()).getColonne(), (r.getPosition()).getLigne(), "images/chenilles.png", 0.9, 0.9);
                                    break;
                    case PATTES:    ihm.paintImage((r.getPosition()).getColonne(), (r.getPosition()).getLigne(), "images/pattes.png", 0.9, 0.9);
                                    break;
                }
            }
            
		} catch (MapIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
	}

}
