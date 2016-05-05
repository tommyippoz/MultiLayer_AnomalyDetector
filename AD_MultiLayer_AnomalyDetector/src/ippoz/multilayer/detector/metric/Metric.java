/**
 * 
 */
package ippoz.multilayer.detector.metric;

import ippoz.multilayer.detector.algorithm.DetectionAlgorithm;
import ippoz.multilayer.detector.data.ExperimentData;
import ippoz.multilayer.detector.data.Snapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * The Class Metric.
 * Defines a base metric. Needs to be extended from concrete metrics' classes.
 *
 * @author Tommy
 */
public abstract class Metric {
	
	/**
	 * Evaluates the experiment using the chosen metric.
	 *
	 * @param alg the algorithm
	 * @param expData the experiment data
	 * @return the anomaly evaluation
	 */
	public double evaluateMetric(DetectionAlgorithm alg, ExperimentData expData){
		Snapshot sysSnapshot;
		HashMap<Date, Double> anomalyEvaluations = new HashMap<Date, Double>();
		expData.resetIterator();
		while(expData.hasNextSnapshot()){
			sysSnapshot = expData.nextSnapshot();
			anomalyEvaluations.put(sysSnapshot.getTimestamp(), alg.snapshotAnomalyRate(sysSnapshot));
		}
		expData.resetIterator();
		return evaluateAnomalyResults(expData, anomalyEvaluations);
	}

	/**
	 * Evaluates anomaly results coming from evaluations of all the snapshot of an experiment.
	 *
	 * @param expData the experiment data
	 * @param anomalyEvaluations the anomaly evaluations
	 * @return the global anomaly evaluation
	 */
	public abstract double evaluateAnomalyResults(ExperimentData expData, HashMap<Date, Double> anomalyEvaluations);

	/**
	 * Returns the anomaly evaluation for the given input data.
	 *
	 * @param expData the experiment data
	 * @param voting the voting results for each snapshot
	 * @param anomalyTreshold the anomaly threshold
	 * @return the global anomaly evaluation
	 */
	public double evaluateAnomalyResults(ExperimentData expData, TreeMap<Date, Double> voting, double anomalyTreshold) {
		HashMap<Date, Double> convertedMap = new HashMap<Date, Double>(); 
		for(Date date : voting.keySet()){
			convertedMap.put(date, voting.get(date)/anomalyTreshold*1.0);
		}
		return evaluateAnomalyResults(expData, convertedMap);
	}
	
	/**
	 * Compares metric results.
	 *
	 * @param currentMetricValue the current metric value
	 * @param bestMetricValue the best metric value
	 * @return the comparison result
	 */
	public abstract int compareResults(double currentMetricValue, double bestMetricValue);
	
	/**
	 * Converts numeric into boolean anomaly evaluation.
	 *
	 * @param anomalyValue the anomaly value
	 * @return true if anomaly value is over 1.0
	 */
	public static boolean anomalyTrueFalse(double anomalyValue){
		return anomalyValue >= 1.0;
	}

	/**
	 * Gets the metric name.
	 *
	 * @return the metric name
	 */
	public abstract String getMetricName();

	

}
