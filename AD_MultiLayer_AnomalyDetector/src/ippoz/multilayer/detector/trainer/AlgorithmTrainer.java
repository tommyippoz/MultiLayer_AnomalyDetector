/**
 * 
 */
package ippoz.multilayer.detector.trainer;

import ippoz.multilayer.detector.algorithm.DetectionAlgorithm;
import ippoz.multilayer.detector.configuration.AlgorithmConfiguration;
import ippoz.multilayer.detector.data.ExperimentData;
import ippoz.multilayer.detector.data.Indicator;
import ippoz.multilayer.detector.metric.Metric;
import ippoz.multilayer.detector.reputation.Reputation;
import ippoz.multilayer.detector.support.AppLogger;
import ippoz.multilayer.detector.support.AppUtility;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * The Class AlgorithmTrainer.
 * Base class for each algorithm scorer. Extends Thread.
 *
 * @author Tommy
 */
public class AlgorithmTrainer extends Thread implements Comparable<AlgorithmTrainer>{
	
	/** The algorithm tag. */
	private String algTag;	
	
	/** The involved indicator. */
	private Indicator indicator;
	
	/** The data category tag. */
	private String categoryTag;
	
	/** The used metric. */
	private Metric metric;
	
	/** The used reputation metric. */
	private Reputation reputation;
	
	/** The possible configurations. */
	private HashMap<String, LinkedList<AlgorithmConfiguration>> configurations;
	
	/** The experiments' list. */
	private LinkedList<ExperimentData> expList;
	
	/** The best configuration. */
	private AlgorithmConfiguration bestConf;
	
	/** The metric score. */
	private double metricScore;
	
	/** The reputation score. */
	private double reputationScore;
	
	/**
	 * Instantiates a new algorithm trainer.
	 *
	 * @param algTag the algorithm tag
	 * @param indicator the involved indicator
	 * @param categoryTag the data category tag
	 * @param metric the used metric
	 * @param reputation the used reputation metric
	 * @param trainData the considered train data
	 * @param configurations the possible configurations
	 */
	public AlgorithmTrainer(String algTag, Indicator indicator, String categoryTag, Metric metric, Reputation reputation, LinkedList<ExperimentData> trainData, HashMap<String, LinkedList<AlgorithmConfiguration>> configurations) {
		this.algTag = algTag;
		this.indicator = indicator;
		this.categoryTag = categoryTag;
		this.metric = metric;
		this.reputation = reputation;
		this.configurations = configurations;
		expList = deepClone(trainData);
	}

	/**
	 * Deep clone of the experiment list.
	 *
	 * @param trainData the train data
	 * @return the cloned experiment list
	 */
	private LinkedList<ExperimentData> deepClone(LinkedList<ExperimentData> trainData) {
		LinkedList<ExperimentData> list = new LinkedList<ExperimentData>();
		try {
			for(ExperimentData eData : trainData){
				list.add(eData.clone());
			}
		} catch (CloneNotSupportedException ex) {
			AppLogger.logException(getClass(), ex, "Unable to clone Experiment");
		}
		return list;
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		Double bestMetricValue = Double.NaN;
		Double currentMetricValue;
		LinkedList<Double> metricResults;
		DetectionAlgorithm algorithm;
		for(AlgorithmConfiguration conf : configurations.get(algTag.toUpperCase())){
			metricResults = new LinkedList<Double>();
			algorithm = DetectionAlgorithm.buildAlgorithm(algTag, categoryTag, indicator, conf);
			for(ExperimentData expData : expList){
				metricResults.add(metric.evaluateMetric(algorithm, expData));
			}
			currentMetricValue = AppUtility.calcAvg(metricResults.toArray(new Double[metricResults.size()]));
			if(bestMetricValue.isNaN() || metric.compareResults(currentMetricValue, bestMetricValue) == 1){
				bestMetricValue = currentMetricValue;
				bestConf = conf;
			}
		}
		metricScore = evaluateMetricScore(expList);
		reputationScore = evaluateReputationScore(expList);
		bestConf.addItem(AlgorithmConfiguration.WEIGHT, String.valueOf(reputationScore));
		bestConf.addItem(AlgorithmConfiguration.SCORE, String.valueOf(metricScore));
	}
	
	/**
	 * Evaluates metric score on a specified set of experiments.
	 *
	 * @param trainData the train data
	 * @return the metric score
	 */
	private double evaluateMetricScore(LinkedList<ExperimentData> trainData){
		LinkedList<Double> metricResults = new LinkedList<Double>();
		DetectionAlgorithm algorithm = DetectionAlgorithm.buildAlgorithm(algTag, categoryTag, indicator, bestConf);
		for(ExperimentData expData : trainData){
			metricResults.add(metric.evaluateMetric(algorithm, expData));
		}
		return AppUtility.calcAvg(metricResults.toArray(new Double[metricResults.size()]));
	}
	
	/**
	 * Evaluate reputation score on a specified set of experiments.
	 *
	 * @param trainData the train data
	 * @return the reputation score
	 */
	private double evaluateReputationScore(LinkedList<ExperimentData> trainData){
		LinkedList<Double> reputationResults = new LinkedList<Double>();
		DetectionAlgorithm algorithm = DetectionAlgorithm.buildAlgorithm(algTag, categoryTag, indicator, bestConf);
		for(ExperimentData expData : trainData){
			reputationResults.add(reputation.evaluateReputation(algorithm, expData));
		}
		return AppUtility.calcAvg(reputationResults.toArray(new Double[reputationResults.size()]));
	}

	/**
	 * Gets the metric score.
	 *
	 * @return the metric score
	 */
	public double getMetricScore() {
		return metricScore;
	}
	
	/**
	 * Gets the reputation score.
	 *
	 * @return the reputation score
	 */
	public double getReputationScore() {
		return reputationScore;
	}
	
	/**
	 * Gets the best configuration.
	 *
	 * @return the best configuration
	 */
	public AlgorithmConfiguration getBestConfiguration(){
		return bestConf;
	}
	
	/**
	 * Gets the indicator name.
	 *
	 * @return the indicator name
	 */
	public String getIndicatorName(){
		if(indicator != null)
			return indicator.getName();
		else return null;
	}
	
	/**
	 * Gets the data type.
	 *
	 * @return the data type
	 */
	public String getDataType(){
		return categoryTag;
	}

	/**
	 * Gets the algorithm type.
	 *
	 * @return the algorithm type
	 */
	public String getAlgType(){
		return algTag;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(AlgorithmTrainer other) {
		return Double.compare(other.getMetricScore(), getMetricScore());
	}
	
}
