/***************************************************************************************
	@author: Jimmy Yuan
	@date: April 2017
 *****************************************************************************************/


import java.util.ArrayList;
import java.util.List;


public class NBClassifierImpl implements NBClassifier {

	private int nFeatures; 		// The number of features including the class 
	private int[] featureSize;	// Size of each features
	private List<ArrayList<Double>> logPosProbs;
	//private List<List<Double[]>> logPosProbs;	// parameters of Naive Bayes

	/**
	 * Constructs a new classifier without any trained knowledge.
	 */
	public NBClassifierImpl() {

	}

	/**
	 * Construct a new classifier 
	 * 
	 * @param int[] sizes of all attributes
	 */
	public NBClassifierImpl(int[] features) {
		this.nFeatures = features.length;

		// initialize feature size
		this.featureSize = features.clone();
		//System.out.println(nFeatures);
		this.logPosProbs = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> foo = new ArrayList<Double>();
		for(int i = 0; i < nFeatures; i++){
			this.logPosProbs.add(foo);
		}
		//this.logPosProbs = new ArrayList<List<Double[]>>(this.nFeatures);
		//this.logPosProbs = new ArrayList<Double>(this.nFeatures);
	}


	/**
	 * Read training data and learn parameters
	 * 
	 * @param int[][] training data
	 */
	@Override
	public void fit(int[][] data) {
		double rows = data.length;
		double totalcount = 0;//total number of positive classes
		for(int i = 0; i < rows; i++){//go through each row


			//System.out.println(data[i][nFeatures-1]);
			//System.out.println("i = " + i);
			//System.out.println("nFeatures = " + nFeatures);
			//System.out.println("====");
			if(data[i][nFeatures - 1] == 1){//if the last column i.e. class is positive
				totalcount++;
			}

		}
		double num = totalcount + 1;
		double den = rows + featureSize[featureSize.length-1];
		double numerator = rows-totalcount + 1;
		double denominator = rows + featureSize[featureSize.length-1];
		/*
		System.out.println(num);
		System.out.println(den);
		System.out.println(numerator);
		System.out.println(denominator);
		*/
		double posprob = num/den;
		double negprob = numerator/denominator;
		posprob = Math.log(posprob);
		negprob = Math.log(negprob);
		ArrayList<Double> newlist = new ArrayList<Double>();
		newlist.add(posprob);
		newlist.add(negprob);
		this.logPosProbs.set(nFeatures - 1, newlist);//add to end of list
		/*
		double count1 = 0;
		double count2 = 0;
		for(int i = 0; i < rows; i++){//for each row
			for(int j = 0; j < nFeatures - 1; j++){//for each column
				if(data[i][nFeatures-1] == 1){//if it belongs to positive class
					if(data[i][j] == 1){//count up all
						count1++;
					}
				}
				else{
					if(data[i][j] == 1){
						count2++;
					}
				}
			}
		}
		System.out.println(count1 + " " + count2);
		 */
		for(int j = 0; j < nFeatures - 1; j++){//go through each column except last one which is class
			int[] counts = new int[2*(featureSize[j])];
			for(int runner=0;runner<counts.length;runner++){
				counts[runner] = 0;
			}
			/*
			int poscount1 = 0;
			int poscount2 = 0;
			int negcount1 = 0;
			int negcount2 = 0;
			*/
			for(int i = 0; i < rows; i++){//go through each row
				if(data[i][nFeatures - 1] == 1){//if positive class
					
					counts[(data[i][j]*2)]++;
					/*
					if(data[i][j] == 1){//if for this instance it is positive, and belongs to positive class add to poscount
						//poscount1++;
						counts[featureSize[j] - 2]++;
					}
					else counts[featureSize[j] - 1]++;//else if the value is negative but belongs to positive class
					*/
				}
				else{//else is negative class

					counts[(data[i][j]*2)+1]++;

				}
			}
			/*
			double x = Math.log((poscount1+1)/(totalcount + featureSize[j]));
			double num1 = poscount + 1;
			double den1 = totalcount + featureSize[j];
			double featprob = num1/den1;
			//System.out.println(num1 + " " + den1);
			//featprob = Math.log(featprob);
			double y = Math.log((negcount1+1)/((rows-totalcount) + featureSize[j]));
			double num2 = negcount + 1;
			double den2 = (rows - totalcount) + featureSize[j];
			double featprob2 = num2/den2;
			featprob2 = Math.log(featprob2);
			double a = Math.log((poscount2+1)/(totalcount + featureSize[j]));
			double b = Math.log((negcount2+1)/((rows-totalcount) + featureSize[j]));
			newlist2.add(x);
			newlist2.add(y);
			newlist2.add(a);
			newlist2.add(b);
			*/
			ArrayList<Double> newlist2 = new ArrayList<Double>();
			for(int runner=0;runner<counts.length;runner++){
				double addme = 0;
				if(runner%2 == 0){
					addme = Math.log((counts[runner]+1)/(totalcount + featureSize[j]));
				}
				else{
					addme = Math.log((counts[runner]+1)/((rows-totalcount) + featureSize[j]));
				}
				newlist2.add(addme);
			}
			//System.out.println(featprob + " " + featprob2);
			this.logPosProbs.set(j, newlist2);
			/*
			System.out.println("=====");
			System.out.println(num1);
			System.out.println(den1);
			System.out.println(num2);
			System.out.println(den2);
			*/
		}
	}

	/**
	 * Classify new dataset
	 * 
	 * @param int[][] test data
	 * @return Label[] classified labels
	 */
	@Override
	public Label[] classify(int[][] instances) {
		/*
		for(int i = 0; i < nFeatures; i++){
			System.out.println(i);
			System.out.println(this.logPosProbs.get(i).get(0));
			System.out.println(this.logPosProbs.get(i).get(1));
			System.out.println("===");
		}
		*/
		int nrows = instances.length;
		Label[] yPred = new Label[nrows]; // predicted data
		for(int i = 0; i < instances.length; i++){//go through each row
			double totalpos = 0;
			double totalneg = 0;
			for(int j = 0; j < nFeatures - 1; j++){//go through each instance
				totalpos = totalpos + this.logPosProbs.get(j).get(instances[i][j]*2);
				totalneg = totalneg + this.logPosProbs.get(j).get((instances[i][j]*2)+1);
				
				
				
				
				/*
				if(instances[i][j] == 1){
					//System.out.println(j + " " + i);
					totalpos = totalpos + this.logPosProbs.get(j).get(0);//if its marked as present, add log probabilty
					totalneg = totalneg + this.logPosProbs.get(j).get(1);
				}
				else{
					totalpos = totalpos + this.logPosProbs.get(j).get(2);
					totalneg = totalneg + this.logPosProbs.get(j).get(3);
				}
				*/
			}
			totalpos = totalpos+this.logPosProbs.get(nFeatures-1).get(0);//add total probability
			totalneg = totalneg+this.logPosProbs.get(nFeatures-1).get(1);
			/*
			System.out.println(i);
			System.out.println("totalpos = " + totalpos);
			System.out.println("totalneg = " + totalneg);
			System.out.println("====");
			*/
			if(totalpos >= totalneg){
				yPred[i] = Label.Positive;
			}
			else{
				yPred[i] = Label.Negative;
			}
		}

		return yPred;
	}
}