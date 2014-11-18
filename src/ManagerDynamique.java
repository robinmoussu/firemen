
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.PriorityQueue;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author robin
 */
public class ManagerDynamique extends Manager {
    
    ArrayList<Managed> managed;

    public ManagerDynamique(Simulateur simu, DonneesSimulation simuData)
            throws SimulationException {
        super(simu, simuData);
        
        ArrayList<Robot> robots = this.simuData.getRobots();
        
        managed = new ArrayList<>(robots.size());
        for(Robot robot: robots) {
            managed.add(new DoNothing(robot));
        }
    }
    
    @Override
    public void manage()
            throws SimulationException {
        if (this.managed.size() != this.simuData.getRobots().size()) {
            throw new SimulationException("Le nombre de robots présent dans "
                    + "la simulation ne correspond pas avec le nombre de "
                    + "robots managés. Peut être que vous avez creer ce "
                    + "manager avant d'avoir fini d'ajouter touts les robots à "
                    + "la simulation ?");
        }
        ListIterator<Managed> itr = this.managed.listIterator();
        while (itr.hasNext()) {
            Managed m = itr.next();
            if (m.actionFinie()) {
                // Il faut lui trouver un nouvel objectif

                if (m.robot.estVide()) {
                    m = new ChercheEau(this.simuData, m.getRobot());
                    itr.set(m);
                } else {
                    m = new EteindreIncendie(this.simuData, m.getRobot());
                    itr.set(m);
                }
            }
            
            m.doAction();
        }
    }

    @Override
    public void signaleSuccessEvent(Evenement e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void signaleFailEvent(Evenement e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

abstract class Managed {
    protected Robot robot;
    protected boolean finished;

    public Managed(Robot robot) {
        this.robot = robot;
        this.finished = false;
    }
    
    public boolean actionFinie() {
        return this.finished;
    }

    public Robot getRobot() {
        return robot;
    }
    
    abstract void doAction() throws SimulationException;
}

class DoNothing extends Managed {

    public DoNothing(Robot robot) {
        super(robot);
        this.finished = true;
    }

    @Override
    void doAction() {
        // Rien à faire
        System.err.println("Le robot " + this.robot + " n'a plus rien à faire");
    }

}

class ChercheEau extends Managed {
    protected Astar parcourt;
    protected DonneesSimulation data;

    public ChercheEau(DonneesSimulation data, Robot robot)
            throws SimulationException {
        super(robot);
        this.data = data;
        
        // On rempli le robot avec l'eau la plus proche
        Astar astar;
        PriorityQueue<Astar> eauProche;

        eauProche = new PriorityQueue<>();
        for (Case eau : data.getCaseEau()) {
            astar = new Astar(data.getCarte(),
                    this.robot.getPosition(), eau, this.robot);
            eauProche.add(astar);
        }
        
        this.parcourt = eauProche.peek();
        if (this.parcourt != null) {
            finished = true;
        }
    }

    @Override
    void doAction() throws SimulationException {
        System.out.println("Recherche d'eau…");
        if (this.robot.estRemplissable(this.data.getCarte())) {
            System.out.println("On remplie le robot…");
            this.robot.remplirReservoir(this.data.getCarte());
        } else {
            System.out.println("On rapproche le robot");
            this.robot.deplacer(this.parcourt.next(
                    this.robot.getPosition()));
        }
    }
    
}

class EteindreIncendie extends Managed {
    protected Astar parcourt;
    protected DonneesSimulation data;

    public EteindreIncendie(DonneesSimulation data, Robot robot)
            throws SimulationException {
        super(robot);
        this.data = data;
        
        // On cherche l'incendie le plus proche
        Astar astar;
        PriorityQueue<Astar> feuProche;
        
        feuProche = new PriorityQueue<>();
        for (Incendie feu : data.getIncendies()) {
            astar = new Astar(data.getCarte(),
                    this.robot.getPosition(), feu.getPosition(), this.robot);
            feuProche.add(astar);
        }
        
        this.parcourt = feuProche.peek();
        if (this.parcourt != null) {
            finished = true;
        }
    }

    @Override
    void doAction() throws SimulationException {
        System.out.println("Recherche d'incendies…");
        if (this.robot.peutEteindreFeu(this.data)) {
            System.out.println("On éteind l'incendie…");
            this.robot.deverserEau(this.data, 1);
        } else {
            System.out.println("On rapproche le robot");
            this.robot.deplacer(this.parcourt.next(
                    this.robot.getPosition()));
        }
    }
    
}