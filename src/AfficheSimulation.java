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
            String filename = args[0];
            LecteurDonnees lecteur = new LecteurDonnees(filename);
            DonneesSimulation simulation = lecteur.creeDonnees();
            Simulateur simulateur = new Simulateur(0); // Création du simulateur
            Manager manager = new ManagerScenario0(simulateur, simulation); // Création du manager
            simulateur.setManager(manager);

            Firemen firemen = new Firemen(simulation, lecteur, simulateur); // Création de l'IHM
		} catch (FileNotFoundException e) {
			System.out.println("fichier " + args[0] + " inconnu ou illisible");
		} catch (ExceptionFormatDonnees e) {
			System.out.println("\n\t**format du fichier " + args[0] + " invalide: " + e.getMessage());
        }
	}
}

class Firemen implements Simulable {
	private int nbLignes;
	private int nbColonnes;
    private IGSimulateur ihm;  // l'IHM associee a ce simulateur
    private Date date = new Date(); // On utilise l'objet Date
    private DonneesSimulation simulation;
    private LecteurDonnees lecteur;
    private Simulateur simulateur;
    
	public Firemen(DonneesSimulation data, LecteurDonnees lecteur, Simulateur simulateur) {
		// cree l'IHM et l'associe a ce simulateur (this), qui en tant que
		// Simulable recevra les evenements suite aux actions de l'utilisateur
        this.simulation = data;
        nbLignes = data.getNbLignes();
        nbColonnes = data.getNbColonnes();
		ihm = new IGSimulateur(nbColonnes, nbLignes, this);
        this.lecteur = lecteur;
        this.simulateur = simulateur;
		dessine();    // mettre a jour l'affichage
	}
	
	@Override
	public void next() {
		try {
            simulateur.incrementeDate(1); // Incrémenter la date courante et gérer les événements
            dessine();
        }
        catch (SimulationException e) {
            System.out.println("[ERR] Erreur lors de l'exécution de la simulation : " + e.getMessage());
        }
	}

	@Override
	public void restart() {
        try {
            this.simulation = this.lecteur.creeDonnees();
            date.resetDate(); // Réinitialiser la date courante
            dessine(); // Mettre à jour l'affichage
            System.out.println("[OK] Redémarrage de la simulation depuis son état initial.");
        } catch (Exception e) {
            System.out.println("[ERR] Erreur lors de la remise à jour des données de simulation");
        }
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
        			//ihm.paintString(7, 15, Color.YELLOW, "I");
                }
            }

            // Affichage des incendies avec une taille croissante avec l'intensité
            for(Incendie i : simulation.getIncendies()) {
                if(i.getIntensite()>0) {
                    double tailleImage;
                    if(i.getIntensite()<1000) {
                        tailleImage = 0.5;
                    }
                    else if(i.getIntensite()<10000) {
                        tailleImage = 0.7;
                    }
                    else {
                        tailleImage = 0.9;
                    }
                    ihm.paintImage((i.getPosition()).getColonne(), (i.getPosition()).getLigne(), "images/incendie.png", tailleImage, tailleImage);
                }
            }

            // Affichage des robots
            for(Robot r : simulation.getRobots()) {
                ihm.paintImage((r.getPosition()).getColonne(), (r.getPosition()).getLigne(), r.getImage(), 0.9, 0.9);
            }
		} catch (MapIndexOutOfBoundsException e) {
			e.printStackTrace();
		} catch (SimulationException e) {
            System.out.println("[ERR] Echec de l'affichage de la carte sur l'IHM (parcours de cases hors-carte)");
        }
	}

}

