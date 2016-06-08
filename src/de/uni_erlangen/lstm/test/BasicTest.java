package de.uni_erlangen.lstm.test;

import de.uni_erlangen.lstm.convert.ConvertBasicADM1;
import de.uni_erlangen.lstm.inputs.Basic;
import de.uni_erlangen.lstm.models.adm1.StateVariables;

public class BasicTest {
	// Inputs
	private static double flow = 170.0; // m3/d
	private static double cod = 15.0;//15.0; // kg/m3
	private static double toc = cod/3.0;//5.0; // kg/m3
	private static double nOrg = cod*0.02;//0.3; // kg/m3
	private static double alkVFA = cod*0.00033;//0.005; // kg CaCO3/m3
	private static double alkIC = alkVFA/5.0; //0.001; // kg CaCO3/m3

	public static void main(String[] args) {
		Basic inputs = new Basic();
		
		inputs.setFlow(flow);
		inputs.setCod(cod);
		inputs.setToc(toc);
		inputs.setnOrg(nOrg);
		inputs.setAlkVFA(alkVFA);
		inputs.setAlkIC(alkIC);
		
		System.out.println("Inputs -> \n\tCOD: "
				+ inputs.getCod() + "\n\tTOC: " + inputs.getToc() + "\n\tNorg: "
				+ inputs.getnOrg() + "\n\tAlkVFA: " + inputs.getAlkVFA() + "\n\tAlkIC: " 
				+ inputs.getAlkIC());	
		
		ConvertBasicADM1 converter = new ConvertBasicADM1();
		converter.setInputs(inputs);
		converter.runConversion();
		StateVariables outputs = converter.getOutputs();
		
		outputs.writeVar("outputs.csv");
		String outputText = "Outputs -> \n\t";
		for (int i=0;i<outputs.getVar().length;i++) {
			outputText += outputs.getVar()[i] + "\n\t";
		}
		System.out.println(outputText);
	}
}
