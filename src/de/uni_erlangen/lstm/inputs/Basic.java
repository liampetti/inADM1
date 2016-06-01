package de.uni_erlangen.lstm.inputs;

/**
 * Basic measurements as described by the paper:
 * 
 * Kleerebezem, R., and M. C. M. van Loosdrecht. “Waste Characterization for Implementation in ADM1.” 
 * Water Science and Technology : A Journal of the International Association on Water Pollution Research 54, 
 * no. 4 (2006): 167–74. doi:10.2166/wst.2006.538.
 * 
 * @author liampetti
 *
 */
public class Basic {
	double flow; // Influent Flow Rate [m3/d]
	double cod; // Chemical Oxygen Demand (COD) [kg/m3]
	double toc; // Total Organic Carbon (TOC) [kg/m3]
	double nOrg; // Organic nitrogen (Norg) [kg/m3]
	// Alkalinity (Alk), consisting of:
	double alkIC; // Bicarbonate Alkalinity [kg CaCO3/m3]
	double alkVFA;	// Neutralized Fatty Acids [kg CaCO3/m3]
	
	// Molar masses
	private double mO2 = 31.9988;
	private double mC = 12.0107;
	private double mN = 14.0067;
	
	public double getFlow() {
		return flow;
	}
	public void setFlow(double flow) {
		this.flow = flow;
	}
	public double getCod() {
		return cod;
	}	
	public void setCod(double cod) {
		this.cod = cod/(mO2); // Convert to molar concentrations
	}
	public double getToc() {
		return toc;
	}
	public void setToc(double toc) {
		this.toc = toc/mC; // Convert to molar concentrations
	}
	public double getnOrg() {
		return nOrg;
	}
	public void setnOrg(double nOrg) {
		this.nOrg = nOrg/mN; // Convert to molar concentrations
	}
	public double getAlkIC() {
		return alkIC;
	}
	public void setAlkIC(double alkIC) {
		this.alkIC = alkIC*10; // Convert to molar concentrations
	}
	public double getAlkVFA() {
		return alkVFA;
	}
	public void setAlkVFA(double alkVFA) {
		this.alkVFA = alkVFA*10; // Convert to molar concentrations
	}	
}
