/**
 * 
 */
package ippoz.multilayer.detector.algorithm;

import ippoz.multilayer.commons.indicator.Indicator;
import ippoz.multilayer.detector.commons.data.Snapshot;
import ippoz.multilayer.detector.commons.service.ServiceCall;
import ippoz.multilayer.detector.commons.service.ServiceStat;
import ippoz.multilayer.detector.commons.support.AppUtility;
import ippoz.multilayer.detector.configuration.AlgorithmConfiguration;

import java.util.Date;

/**
 * The Class RemoteCallChecker.
 * Checks if the duration of a service call is compliant with the expectations.
 *
 * @author Tommy
 */
public class RemoteCallChecker extends DetectionAlgorithm {
	
	/** The weight tag. */
	private static String WEIGHT_TAG = "rcc_weight";

	/** The remote call checker weight. */
	private double weight;
	
	/**
	 * Instantiates a new remote call checker.
	 *
	 * @param weight the weight
	 */
	public RemoteCallChecker(double weight) {
		super(null);
		this.weight = weight;
	}

	/**
	 * Instantiates a new remote call checker.
	 *
	 * @param conf the configuration
	 */
	public RemoteCallChecker(AlgorithmConfiguration conf) {
		super(conf);
		weight = Double.parseDouble(conf.getItem(WEIGHT_TAG));
	}

	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.algorithm.DetectionAlgorithm#evaluateSnapshot(ippoz.multilayer.detector.data.Snapshot)
	 */
	@Override
	public double evaluateSnapshot(Snapshot sysSnapshot) {
		double evalResult = 0.0;
		for(ServiceCall call : sysSnapshot.getServiceCalls()){
			evalResult = evalResult + analyzeServiceCall(sysSnapshot.getTimestamp(), call, sysSnapshot.getServiceStatList().get(call.getServiceName()));
		}
		return evalResult / sysSnapshot.getServiceCalls().size();
	}

	/**
	 * Analyse service call.
	 *
	 * @param snapTime the snapshot time
	 * @param call the service call
	 * @param serviceStat the service stat
	 * @return the double
	 */
	private double analyzeServiceCall(Date snapTime, ServiceCall call, ServiceStat serviceStat) {
		if(call.getEndTime().compareTo(snapTime) == 0){
			if(!call.getResponseCode().equals("200"))
				return weight;
			else return evaluateAbsDiff(AppUtility.getSecondsBetween(call.getEndTime(), call.getStartTime()), serviceStat.getTimeStat(), 1.0);
		} else return evaluateOverDiff(AppUtility.getSecondsBetween(snapTime, call.getStartTime()), serviceStat.getTimeStat());
	}

	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.algorithm.DetectionAlgorithm#printImageResults(java.lang.String, java.lang.String)
	 */
	@Override
	protected void printImageResults(String outFolderName, String expTag) {
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.algorithm.DetectionAlgorithm#printTextResults(java.lang.String, java.lang.String)
	 */
	@Override
	protected void printTextResults(String outFolderName, String expTag) {
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.algorithm.DetectionAlgorithm#getWeight()
	 */
	@Override
	public Double getWeight() {
		return weight;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.algorithm.DetectionAlgorithm#getDataType()
	 */
	@Override
	public String getDataType() {
		return null;
	}

	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.algorithm.DetectionAlgorithm#getIndicator()
	 */
	@Override
	public Indicator getIndicator() {
		return null;
	}
	
}
