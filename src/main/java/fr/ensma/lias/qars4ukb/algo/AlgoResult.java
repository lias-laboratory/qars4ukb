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

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((alphaMFSs == null) ? 0 : alphaMFSs.hashCode());
	result = prime * result + ((alphaXSSs == null) ? 0 : alphaXSSs.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	AlgoResult other = (AlgoResult) obj;
	if (alphaMFSs == null) {
	    if (other.alphaMFSs != null)
		return false;
	} else if (!alphaMFSs.equals(other.alphaMFSs))
	    return false;
	if (alphaXSSs == null) {
	    if (other.alphaXSSs != null)
		return false;
	} else if (!alphaXSSs.equals(other.alphaXSSs))
	    return false;
	return true;
    }
    
//    @Override
//    public String toString() {
//	StringBuffer res = new StringBuffer();
//	
//	for(Double alpha : alphaMFSs.keySet()) {
//	    res.append(alpha + ": ");
//	    List<Query> 
//	}
//	return res.toString();
//    }
    
    
}
