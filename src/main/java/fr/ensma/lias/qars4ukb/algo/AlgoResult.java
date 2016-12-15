package fr.ensma.lias.qars4ukb.algo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ensma.lias.qars4ukb.query.Query;

/**
 * Result of the algorithms to compute
 * the MFSs and XSSs for different alpha
 * @author Stéphane JEAN
 *
 */
public class AlgoResult {

    /**
     * Set of alphaMFSs for different alpha
     */
    private Map<Double, List<Query>> alphaMFSs;
    
    /**
     * Set of alphaXSSs for different alpha
     */
    private Map<Double, List<Query>> alphaXSSs; 
    
    /**
     * Initialize this result
     */
    public AlgoResult(){
	alphaMFSs = new HashMap<>();
	alphaXSSs = new HashMap<>();
    }
    
    /**
     * Add a set of alphaMFSs for a given alpha
     * @param alpha a given threshold
     * @param mfs a list of MFSs
     */
    public void addAlphaMFSs(Double alpha, List<Query> mfs) {
	alphaMFSs.put(alpha, mfs);
    }
    
    /**
     * Add a set of alphaXSSs for a given alpha
     * @param alpha a given threshold
     * @param mfs a list of XSSs
     */
    public void addAlphaXSSs(Double alpha, List<Query> xss) {
	alphaXSSs.put(alpha, xss);
    }
    
    /**
     * Get the alphaMFSs for a given alpha
     * @param alpha a given threshold
     * @return the alphaMFSs
     */
    public List<Query> getAlphaMFSs(Double alpha) {
	return alphaMFSs.get(alpha);
    }
    
    /**
     * Get the alphaXSSs for a given alpha
     * @param alpha a given threshold
     * @return the alphaXSSs
     */
    public List<Query> getAlphaXSSs(Double alpha) {
	return alphaXSSs.get(alpha);
    }
}
