package de.uni_erlangen.lstm.convert;

import de.uni_erlangen.lstm.inputs.Basic;
import de.uni_erlangen.lstm.models.adm1.StateVariables;

/**
 * Converts basic measurements to ADM1 state variables, based on the paper:
 * 
 * Kleerebezem, R., and M. C. M. van Loosdrecht. “Waste Characterization for Implementation in ADM1.” 
 * Water Science and Technology : A Journal of the International Association on Water Pollution Research 54, 
 * no. 4 (2006): 167–74. doi:10.2166/wst.2006.538.
 * 
 * @author liampetti
 *
 */
public class ConvertBasicADM1 {
	private Basic inputs;
	private StateVariables outputs;
	
	private double nPR;
	private double chVFA;
	private double yPRO;
	private double yCHO;
	private double yLIP;
	private double yVFA;
	private double yCH4;
		
	private double T;
	private double Kh;
	private double Ka;
	
	private double R;	
	private double mwO2;
	
	/*
	 * Default settings
	 */
	public ConvertBasicADM1() {
		this.inputs = new Basic();
		this.outputs = new StateVariables();
		
		this.nPR = 0.26;
		this.chVFA = -0.5;
		this.yPRO = 4;
		this.yCHO = 4;
		this.yLIP = 5.68;
		this.yVFA = 4;
		this.yCH4 = 65;
		
		this.T = 298;
		this.Kh = 29.8;
		this.Ka = 4.40e-7;
		
		this.R = 0.082;
		this.mwO2 = 31.9988;
	}
	
	public void setInputs(Basic inputs) {
		this.inputs = inputs;
	}
	
	public void runConversion() {
		double[] coeff = getStoichiometric();
		double[] frac = getMolFractions(coeff);
		double[] conc = getCODConc(frac);
		double[] comp = getGasComp(conc);
		
		outputs.setQ_D(inputs.getFlow());
		
		outputs.setX_ch(conc[3]);
		outputs.setX_li(conc[2]);
		outputs.setX_pr(conc[1]);
		// Acetate is assumed to be the main VFA in the influent
		outputs.setS_ac(conc[0]);
		
		// TODO: Check if better way of extracting S_cat and S_an from alkalinity measurements
		// S_cat - S_an = alkVFA + alkIC
		// Assume S_an is 1/2 of S_cat (always cations minus anions for S_H_ion calc)
		outputs.setS_cat(inputs.getAlkVFA()+inputs.getAlkIC()*1.5);
		outputs.setS_an(inputs.getAlkVFA()+inputs.getAlkIC()*0.5);
		
		// Assumptions:
		//   H2CO3 < HCO3, CO2_gas
		//   HCO3 = Alk + Norg
		// 	 CH4_gas = CH4
		outputs.setS_gas_ch4(comp[0]);
		outputs.setS_IC(comp[1]);
		outputs.setS_gas_co2(comp[3]);		
		outputs.setPh(comp[7]);		
	}
	
	public StateVariables getOutputs() {
		return outputs;
	}
	
	/*
	 * Step 1
	 */
	public double[] getStoichiometric() {
		double[] coeff = new double[5];
		
		//x
		coeff[0] = 1;
		
		//y
		coeff[1] = (2*inputs.getCod()+inputs.getAlkVFA()-2*inputs.getnOrg())/inputs.getToc();
		
		//z
		coeff[2] = 2-((inputs.getCod()+0.5*inputs.getnOrg())/inputs.getToc());
		
		//v
		coeff[3] = inputs.getnOrg()/inputs.getToc();
		
		//u
		coeff[4] = -inputs.getAlkVFA()/inputs.getToc();
		
		return coeff;		
	}
	
	/*
	 * Step 2a
	 */
	public double[] getMolFractions(double[] coeff) {
		double[] frac = new double[4];
		
		//VFA
		frac[0] = coeff[4]/chVFA;
		
		//PRO
		frac[1] = coeff[3]/nPR;
		
		//LIP
		frac[2] = (coeff[1]-2*coeff[2]-3*coeff[3]-coeff[4])/(yLIP-4);
		
		//CHO
		frac[3] = 1-frac[2]-frac[0]-frac[1];
		
		return frac;		
	}
	
	/*
	 * Step 2b
	 */
	public double[] getCODConc(double[] frac) {
		double[] conc = new double[4];
		
		//VFA
		conc[0] = inputs.getToc()*frac[0]*(yVFA/4)*mwO2;
		
		//PRO
		conc[1] = inputs.getToc()*frac[1]*(yPRO/4)*mwO2;
		
		//LIP
		conc[2] = inputs.getToc()*frac[2]*(yLIP/4)*mwO2;
		
		//CHO
		conc[3] = inputs.getToc()*frac[3]*(yCHO/4)*mwO2;
		
		return conc;
	}
	
	/*
	 * Step 3 
	 */
	public double[] getGasComp(double[] conc) {
		double[] comp = new double[8];
		
		//CH4
		//comp[0] = inputs.getCod()/yCH4;
		comp[0] = (conc[0]+conc[1]+conc[2]+conc[3])/yCH4;
		
		//IC
		comp[1] = inputs.getToc()-comp[0]+inputs.getAlkIC();

		//Alk
		comp[2] = inputs.getAlkVFA()+inputs.getAlkIC();
		
		//CO2
		comp[3] = comp[1] -comp[2] - inputs.getnOrg();
		
		//pCO2
		comp[4] = comp[3]/(comp[3]+comp[0]);
		
		//vGas
		comp[5] = (comp[3] + comp[0])*R*T;
		
		//S_H_ion
		comp[6] = (Ka*comp[4])/(Kh*(comp[2]+inputs.getnOrg()));
		
		//pH
		comp[7] = -Math.log10(comp[6]);
		
		//Ideally they should be estimated from measurements on the actual wastewater. If
		//no information is available then the variables may be estimated from Sic and Sin.
		//Scat = Sic
		//San = Sin
		
		return comp;
	}

}
